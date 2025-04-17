package io.stxkxs.bedrock.conf;

import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.bedrock.titan.BedrockTitanEmbeddingModel;
import org.springframework.ai.bedrock.titan.api.TitanEmbeddingBedrockApi;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.time.Duration;

@Configuration
public class BedrockConf {

  @Value("${spring.ai.bedrock.aws.region:us-west-2}")
  private String awsRegion;

  @Value("${spring.ai.bedrock.aws.profile-name:default}")
  private String awsProfileName;

  @Value("${app.bedrock.timeout:10}")
  private Long timeout;

  @Value("${spring.ai.bedrock.converse.chat.options.temperature:0.7}")
  private Double temperature;

  @Value("${spring.ai.bedrock.converse.chat.options.max-tokens:4096}")
  private Integer maxTokens;

  @Value("${spring.ai.bedrock.converse.chat.options.top-p:0.9}")
  private Double topP;

  @Value("${spring.ai.bedrock.converse.chat.options.models.claude-sonnet:us.anthropic.claude-3-7-sonnet-20250219-v1:0}")
  private String claudeSonnetModelId;

  @Value("${spring.ai.bedrock.converse.chat.options.models.claude-haiku:us.anthropic.claude-3-5-haiku-20241022-v1:0}")
  private String claudeHaikuModelId;

  @Value("${spring.ai.bedrock.converse.chat.options.models.nova-pro:amazon.nova-pro-v1:0}")
  private String novaProModelId;

  @Value("${spring.ai.bedrock.converse.chat.options.models.titan-text:amazon.titan-text-express-v1}")
  private String titanTextModelId;

  @Value("${spring.ai.bedrock.converse.chat.options.models.llama3:meta.llama3-70b-instruct-v1:0}")
  private String llama3ModelId;

  @Value("${spring.ai.bedrock.embedding.model-id:amazon.titan-embed-text-v2:0}")
  private String embeddingModelId;

  @Bean
  public BedrockRuntimeClient bedrockRuntimeClient() {
    return BedrockRuntimeClient.builder()
      .region(Region.of(awsRegion))
      .credentialsProvider(ProfileCredentialsProvider.builder()
        .profileName(awsProfileName)
        .build())
      .httpClient(ApacheHttpClient.builder()
        .socketTimeout(Duration.ofMinutes(timeout))
        .build())
      .overrideConfiguration(c -> c.apiCallTimeout(Duration.ofMinutes(timeout)))
      .build();
  }

  @Bean
  @Primary
  @Qualifier("anthropic.claude-sonnet")
  public BedrockProxyChatModel claudeSonnetModel() {
    return BedrockProxyChatModel.builder()
      .bedrockRuntimeClient(bedrockRuntimeClient())
      .region(Region.of(awsRegion))
      .timeout(Duration.ofMinutes(timeout))
      .defaultOptions(ToolCallingChatOptions.builder()
        .model(claudeSonnetModelId)
        .topP(topP)
        .temperature(temperature)
        .maxTokens(maxTokens)
        .build())
      .build();
  }

  @Bean
  @Qualifier("anthropic.claude-haiku")
  public BedrockProxyChatModel claudeHaikuModel() {
    return BedrockProxyChatModel.builder()
      .bedrockRuntimeClient(bedrockRuntimeClient())
      .region(Region.of(awsRegion))
      .timeout(Duration.ofMinutes(timeout))
      .defaultOptions(ToolCallingChatOptions.builder()
        .model(claudeHaikuModelId)
        .topP(topP)
        .temperature(temperature)
        .maxTokens(maxTokens)
        .build())
      .build();
  }

  @Bean
  @Qualifier("amazon.nova-pro")
  public BedrockProxyChatModel novaProModel() {
    return BedrockProxyChatModel.builder()
      .bedrockRuntimeClient(bedrockRuntimeClient())
      .region(Region.of(awsRegion))
      .timeout(Duration.ofMinutes(timeout))
      .defaultOptions(ToolCallingChatOptions.builder()
        .model(novaProModelId)
        .topP(topP)
        .temperature(temperature)
        .maxTokens(maxTokens)
        .build())
      .build();
  }

  @Bean
  @Qualifier("amazon.titan-text")
  public BedrockProxyChatModel titanTextModel() {
    return BedrockProxyChatModel.builder()
      .bedrockRuntimeClient(bedrockRuntimeClient())
      .region(Region.of(awsRegion))
      .timeout(Duration.ofMinutes(timeout))
      .defaultOptions(ToolCallingChatOptions.builder()
        .model(titanTextModelId)
        .topP(topP)
        .temperature(temperature)
        .maxTokens(maxTokens)
        .build())
      .build();
  }

  @Bean
  @Qualifier("meta.llama3")
  public BedrockProxyChatModel llamaModel() {
    return BedrockProxyChatModel.builder()
      .bedrockRuntimeClient(bedrockRuntimeClient())
      .region(Region.of(awsRegion))
      .timeout(Duration.ofMinutes(timeout))
      .defaultOptions(ToolCallingChatOptions.builder()
        .model(llama3ModelId)
        .topP(topP)
        .temperature(temperature)
        .maxTokens(maxTokens)
        .build())
      .build();
  }

  @Bean
  public EmbeddingModel embeddingModel() {
    return new BedrockTitanEmbeddingModel(new TitanEmbeddingBedrockApi(embeddingModelId, awsRegion, Duration.ofMinutes(timeout)));
  }
}