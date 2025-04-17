package io.stxkxs.bedrock.controller;

import io.stxkxs.bedrock.model.Conversation;
import io.stxkxs.bedrock.model.ConversationRequest;
import io.stxkxs.bedrock.model.ConversationResponse;
import io.stxkxs.bedrock.service.ConversationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/conversation")
public class ConversationController {

  private final ConversationService conversationService;

  public ConversationController(ConversationService conversationService) {
    this.conversationService = conversationService;
  }

  @PostMapping(value = "/stream", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ConversationResponse> chat(@RequestBody ConversationRequest request) {
    log.info("received chat request: prompt={}, sessionId={}, modelId={}",
      request.prompt(), request.sessionId(), request.modelId());

    var sessionId = request.sessionId();
    var chatResponseWithSession = conversationService.chat(sessionId, request.modelId(), request.prompt());

    if (sessionId == null) {
      sessionId = chatResponseWithSession.sessionId();
    }

    var response = ConversationResponse.builder()
      .sessionId(sessionId)
      .chatResponse(chatResponseWithSession.chatResponse())
      .build();

    log.info("completed response generation with sessionId: {}", sessionId);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/models")
  public ResponseEntity<List<String>> getAvailableModels() {
    log.info("retrieving available models");

    return ResponseEntity.ok(conversationService.getAvailableModels());
  }

  @GetMapping("/history/{sessionId}")
  public ResponseEntity<List<Conversation>> getConversationHistory(@PathVariable UUID sessionId) {
    log.info("retrieving conversation history for session: {}", sessionId);

    return ResponseEntity.ok(conversationService.getConversationHistory(sessionId));
  }
}