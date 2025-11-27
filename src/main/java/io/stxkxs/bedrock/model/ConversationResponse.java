package io.stxkxs.bedrock.model;

import java.util.UUID;
import lombok.Builder;
import org.springframework.ai.chat.model.ChatResponse;

@Builder
public record ConversationResponse(ChatResponse chatResponse, UUID sessionId) {}
