package io.stxkxs.bedrock.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ConversationRequest(
  UUID sessionId,

  @NotBlank(message = "prompt is required")
  @Size(min = 1, max = 10000, message = "prompt must be between 1 and 10,000 characters")
  String prompt,

  @Size(max = 50, message = "model id must be less than 50 characters")
  String modelId
) {}