package io.stxkxs.bedrock.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

/** Request payload for initiating or continuing a conversation. */
@Builder
public record ConversationRequest(
    UUID sessionId,
    @Size(max = 50, message = "Model ID must be less than 50 characters") String modelId,
    @NotBlank(message = "Prompt is required")
        @Size(min = 1, max = 10000, message = "Prompt must be between 1 and 10,000 characters")
        String prompt) {}
