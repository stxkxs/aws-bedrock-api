package io.stxkxs.bedrock.conf;

import io.micrometer.observation.ObservationRegistry;
import java.time.Duration;
import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.bedrock.titan.BedrockTitanEmbeddingModel;
import org.springframework.ai.bedrock.titan.api.TitanEmbeddingBedrockApi;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@Configuration
public class BedrockConf {

  @Value("${spring.ai.bedrock.aws.region:us-west-2}")
  private String awsRegion;

  @Value("${spring.ai.bedrock.aws.profile-name:default}")
  private String awsProfileName;

  @Value("${app.bedrock.timeout:10}")
  private Long timeout;

  @Value("${spring.ai.bedrock.embedding.model-id:amazon.titan-embed-text-v2:0}")
  private String embeddingModelId;

  @Autowired private ModelConfiguration modelConfiguration;

  @Bean
  public BedrockRuntimeClient bedrockRuntimeClient() {
    return BedrockRuntimeClient.builder()
        .region(Region.of(awsRegion))
        .credentialsProvider(
            ProfileCredentialsProvider.builder().profileName(awsProfileName).build())
        .httpClient(ApacheHttpClient.builder().socketTimeout(Duration.ofMinutes(timeout)).build())
        .overrideConfiguration(c -> c.apiCallTimeout(Duration.ofMinutes(timeout)))
        .build();
  }

  @Bean
  @Primary
  @Qualifier("anthropic.claude-sonnet")
  public BedrockProxyChatModel claudeSonnetModel() {
    return createChatModel("claude-sonnet");
  }

  @Bean
  @Qualifier("anthropic.claude-opus")
  public BedrockProxyChatModel claudeOpusModel() {
    return createChatModel("claude-opus");
  }

  @Bean
  @Qualifier("amazon.nova-premiere")
  public BedrockProxyChatModel novaPremiereModel() {
    return createChatModel("nova-premiere");
  }

  @Bean
  @Qualifier("amazon.titan-text")
  public BedrockProxyChatModel titanTextModel() {
    return createChatModel("titan-text");
  }

  @Bean
  @Qualifier("meta.llama4")
  public BedrockProxyChatModel llama4Model() {
    return createChatModel("llama4");
  }

  private BedrockProxyChatModel createChatModel(String modelKey) {
    String modelId = modelConfiguration.getModelId(modelKey);
    if (modelId == null) {
      throw new IllegalArgumentException("Model ID not found for key: " + modelKey);
    }

    return BedrockProxyChatModel.builder()
        .bedrockRuntimeClient(bedrockRuntimeClient())
        .region(Region.of(awsRegion))
        .timeout(Duration.ofMinutes(timeout))
        .defaultOptions(
            ToolCallingChatOptions.builder()
                .model(modelId)
                .topP(modelConfiguration.getTopP())
                .temperature(modelConfiguration.getTemperature())
                .maxTokens(modelConfiguration.getMaxTokens())
                .build())
        .build();
  }

  @Bean
  public EmbeddingModel embeddingModel() {
    return new BedrockTitanEmbeddingModel(
        new TitanEmbeddingBedrockApi(embeddingModelId, awsRegion, Duration.ofMinutes(timeout)),
        ObservationRegistry.NOOP);
  }
}
