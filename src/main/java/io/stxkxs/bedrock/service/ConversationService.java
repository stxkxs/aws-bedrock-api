package io.stxkxs.bedrock.service;

import io.stxkxs.bedrock.model.ChatResponseWithSession;
import io.stxkxs.bedrock.model.Conversation;
import io.stxkxs.bedrock.model.ConversationSession;
import io.stxkxs.bedrock.repository.ConversationRepository;
import io.stxkxs.bedrock.repository.ConversationSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ConversationService {

  @Value("${app.conversation.history:30}")
  private int documentSize;

  @Value("${app.conversation.documents:30}")
  private int conversationLength;

  private final Map<String, BedrockProxyChatModel> modelMap;
  private final BedrockProxyChatModel defaultModel;
  private final DocumentService documentService;
  private final ConversationRepository conversationRepository;
  private final ConversationSessionRepository sessionRepository;

  public ConversationService(
    @Qualifier("anthropic.claude-sonnet") BedrockProxyChatModel claudeSonnetModel,
    @Qualifier("anthropic.claude-haiku") BedrockProxyChatModel claudeHaikuModel,
    @Qualifier("amazon.titan-text") BedrockProxyChatModel titanTextModel,
    @Qualifier("amazon.nova-pro") BedrockProxyChatModel novaProModel,
    @Qualifier("meta.llama3") BedrockProxyChatModel llamaModel,
    DocumentService documentService,
    ConversationRepository conversationRepository,
    ConversationSessionRepository sessionRepository) {

    this.defaultModel = claudeSonnetModel;
    this.documentService = documentService;
    this.conversationRepository = conversationRepository;
    this.sessionRepository = sessionRepository;

    this.modelMap = Map.of(
      "claude-sonnet", claudeSonnetModel,
      "claude-haiku", claudeHaikuModel,
      "titan", titanTextModel,
      "nova", novaProModel,
      "llama", llamaModel
    );
  }

  public ChatResponseWithSession chat(UUID sessionId, String modelId, String prompt) {
    if (prompt == null) {
      log.error("chat prompt is null");
      throw new IllegalArgumentException("chat prompt cannot be null");
    }

    var id = sessionId != null ? sessionId : UUID.randomUUID();
    log.debug("starting chat with model: {}, sessionId: {}",
      modelId != null ? modelId : "default", id);

    try {
      var savedMessage = conversationRepository.save(
        Conversation.builder()
          .id(UUID.randomUUID())
          .timestamp(Instant.now())
          .sessionId(id)
          .parentId(null)
          .role("user")
          .content(prompt)
          .build()
      );

      ensureSession(id);

      var contextText = documentService
        .search(prompt, documentSize)
        .stream()
        .map(Document::getText)
        .collect(Collectors.joining());

      log.debug("retrieved {} relevant documents for context", contextText.isEmpty() ? 0 : documentSize);

      var messages = withContext(id, contextText);
      messages.add(new UserMessage(prompt));

      var response = getModelById(modelId).call(new Prompt(messages));

      saveAssistantResponse(id, savedMessage.id(),
        response.getResult().getOutput().getText());

      return ChatResponseWithSession.builder()
        .sessionId(id)
        .chatResponse(response)
        .build();
    } catch (Exception e) {
      log.error("error during chat: {}", e.getMessage(), e);
      throw e;
    }
  }

  private List<Message> withContext(UUID sessionId, String context) {
    var messages = new ArrayList<Message>();

    var systemPrompt = "you are a helpful ai assistant. ";
    if (!context.isEmpty()) {
      systemPrompt += "use the following relevant information to answer the question:\n\n" + context;
    }

    messages.add(new SystemMessage(systemPrompt));

    var history = conversationRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    if (history.size() > conversationLength) {
      history = history.subList(history.size() - conversationLength, history.size());
    }

    for (var msg : history) {
      if ("user".equals(msg.role())) {
        messages.add(new UserMessage(msg.content()));
      } else if ("assistant".equals(msg.role())) {
        messages.add(new AssistantMessage(msg.content()));
      }
    }

    return messages;
  }

  private void saveAssistantResponse(UUID sessionId, UUID parentId, String content) {
    var savedAssistant = conversationRepository.save(
      Conversation.builder()
        .id(UUID.randomUUID())
        .timestamp(Instant.now())
        .sessionId(sessionId)
        .parentId(parentId)
        .role("assistant")
        .content(content)
        .build()
    );

    var existingSession = sessionRepository.findById(sessionId).orElse(null);
    if (existingSession != null) {
      var messageIds = existingSession.messageIds();
      if (messageIds == null) {
        messageIds = new ArrayList<>();
      } else {
        messageIds = new ArrayList<>(messageIds);
      }

      messageIds.add(savedAssistant.id());

      var updatedSession = ConversationSession.builder()
        .id(existingSession.id())
        .messageIds(messageIds)
        .build();

      sessionRepository.save(updatedSession);
    }
  }

  private ConversationSession ensureSession(UUID sessionId) {
    return sessionRepository.findById(sessionId)
      .orElseGet(() -> sessionRepository.save(
        ConversationSession.builder()
          .id(sessionId)
          .messageIds(new ArrayList<>())
          .build()
      ));
  }

  public List<Conversation> getConversationHistory(UUID sessionId) {
    return conversationRepository.findBySessionIdOrderByTimestampAsc(sessionId);
  }

  public List<String> getAvailableModels() {
    return new ArrayList<>(modelMap.keySet());
  }

  private BedrockProxyChatModel getModelById(String modelId) {
    return modelId == null ? defaultModel : modelMap.getOrDefault(modelId.toLowerCase(), defaultModel);
  }
}