package io.stxkxs.bedrock.model;

import lombok.Builder;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.UUID;

@Builder
public record ChatResponseWithSession(
  UUID sessionId,
  ChatResponse chatResponse
) {}
