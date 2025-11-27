package io.stxkxs.bedrock.conf;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.ai.bedrock.converse.chat.options")
public class ModelConfiguration {

  private Map<String, String> models;
  private Double temperature = 0.7;
  private Integer maxTokens = 4096;
  private Double topP = 0.9;

  public Map<String, String> getModels() {
    return models;
  }

  public void setModels(Map<String, String> models) {
    this.models = models;
  }

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }

  public Integer getMaxTokens() {
    return maxTokens;
  }

  public void setMaxTokens(Integer maxTokens) {
    this.maxTokens = maxTokens;
  }

  public Double getTopP() {
    return topP;
  }

  public void setTopP(Double topP) {
    this.topP = topP;
  }

  public String getModelId(String modelKey) {
    return models != null ? models.get(modelKey) : null;
  }
}
