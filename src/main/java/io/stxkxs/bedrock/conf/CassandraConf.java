package io.stxkxs.bedrock.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.cassandra.CassandraVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableReactiveCassandraRepositories;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@Slf4j
@Configuration
@EnableReactiveCassandraRepositories(basePackages = "io.stxkxs.bedrock.repository")
public class CassandraConf {

  private final BedrockRuntimeClient bedrockRuntimeClient;
  private final ObjectMapper objectMapper;

  @Value("${spring.cassandra.keyspace:stxkxs}")
  private String keyspace;

  @Value("${spring.ai.bedrock.embedding.model-id:amazon.titan-embed-text-v2:0}")
  private String embeddingModelId;

  public CassandraConf(BedrockRuntimeClient client, ObjectMapper mapper) {
    bedrockRuntimeClient = client;
    objectMapper = mapper;
  }

  @Bean
  public VectorStore vectorStore() {
    return CassandraVectorStore
      .builder(new EmbeddingModel() {
        @Override
        public EmbeddingResponse call(EmbeddingRequest request) {
          var texts = request.getInstructions();

          var embeddings = new ArrayList<Embedding>();
          for (var text : texts) {
            var embedding = generateEmbedding(text);
            embeddings.add(embedding);
          }

          return new EmbeddingResponse(embeddings);
        }

        @Override
        public float[] embed(Document document) {
          log.debug("Converting document to embedding: {}", document.getId());

          if (document.getText() == null) {
            log.warn("document text is null, cannot generate embedding");
            return new float[]{};
          }

          return embed(document.getText());
        }

        @Override
        public float[] embed(String text) {
          if (text.length() > 30000) {
            log.warn("text is very large ({} chars), may exceed token limit", text.length());
          }

          var embedding = generateEmbedding(text);
          if (embedding == null) {
            log.error("Failed to generate embedding for text");
            throw new RuntimeException("Failed to generate embedding");
          }

          var expectedDimension = 1024;
          var output = embedding.getOutput();

          var adjustedArray = new float[expectedDimension];
          var copyLength = Math.min(output.length, expectedDimension);
          System.arraycopy(output, 0, adjustedArray, 0, copyLength);

          return adjustedArray;
        }
      })
      .keyspace(keyspace)
      .table("vector_documents")
      .embeddingColumnName("embedding")
      .contentColumnName("content")
      .build();
  }

  @SneakyThrows
  public Embedding generateEmbedding(String text) {
    log.debug("Generating embedding for text of length: {}", text.length());

    var requestBody = objectMapper.createObjectNode();
    requestBody.put("inputText", text);

    var requestJson = objectMapper.writeValueAsString(requestBody);
    var requestBytes = SdkBytes.fromUtf8String(requestJson);

    var request = InvokeModelRequest.builder()
      .modelId(embeddingModelId)
      .contentType("application/json")
      .accept("application/json")
      .body(requestBytes)
      .build();

    var response = bedrockRuntimeClient.invokeModel(request);
    var responseBody = response.body().asString(StandardCharsets.UTF_8);
    var responseJson = objectMapper.readTree(responseBody);
    var embeddingNode = responseJson.get("embedding");

    var floatValues = new ArrayList<Float>();
    for (var node : embeddingNode) {
      floatValues.add(node.floatValue());
    }

    var embeddingArray = new float[floatValues.size()];
    for (int i = 0; i < floatValues.size(); i++) {
      embeddingArray[i] = floatValues.get(i);
    }

    var embedding = new Embedding(embeddingArray, 0);

    log.debug("generated embedding of size: {}", embeddingArray.length);

    return embedding;
  }
}