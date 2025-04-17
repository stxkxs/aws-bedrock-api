package io.stxkxs.bedrock.model;

import lombok.Builder;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.UUID;

@Builder
public record ConversationResponse(
  ChatResponse chatResponse,
  UUID sessionId
) {}