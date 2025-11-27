package io.stxkxs.bedrock.model;

import java.util.UUID;
import lombok.Builder;
import org.springframework.ai.chat.model.ChatResponse;

@Builder
public record ChatResponseWithSession(UUID sessionId, ChatResponse chatResponse) {}
