package io.stxkxs.bedrock.service;

import io.stxkxs.bedrock.model.ChatResponseWithSession;
import io.stxkxs.bedrock.model.Conversation;
import io.stxkxs.bedrock.model.ConversationSession;
import io.stxkxs.bedrock.repository.ConversationRepository;
import io.stxkxs.bedrock.repository.ConversationSessionRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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

/**
 * Service for managing AI conversations with multiple model support.
 *
 * <p>Handles conversation sessions, context retrieval from documents, and message persistence.
 */
@Slf4j
@Service
public class ConversationService {

  private static final String ROLE_USER = "user";
  private static final String ROLE_ASSISTANT = "assistant";
  private static final String DEFAULT_SYSTEM_PROMPT = "You are a helpful AI assistant. ";

  private final Map<String, BedrockProxyChatModel> modelMap;
  private final BedrockProxyChatModel defaultModel;
  private final DocumentService documentService;
  private final ConversationRepository conversationRepository;
  private final ConversationSessionRepository sessionRepository;

  @Value("${app.conversation.history:30}")
  private int maxDocuments;

  @Value("${app.conversation.documents:30}")
  private int maxConversationHistory;

  public ConversationService(
      @Qualifier("anthropic.claude-sonnet") BedrockProxyChatModel claudeSonnetModel,
      @Qualifier("anthropic.claude-opus") BedrockProxyChatModel claudeOpusModel,
      @Qualifier("amazon.titan-text") BedrockProxyChatModel titanTextModel,
      @Qualifier("amazon.nova-premiere") BedrockProxyChatModel novaPremiereModel,
      @Qualifier("meta.llama4") BedrockProxyChatModel llama4Model,
      DocumentService documentService,
      ConversationRepository conversationRepository,
      ConversationSessionRepository sessionRepository) {

    this.defaultModel = claudeSonnetModel;
    this.documentService = documentService;
    this.conversationRepository = conversationRepository;
    this.sessionRepository = sessionRepository;

    this.modelMap =
        Map.of(
            "claude-sonnet", claudeSonnetModel,
            "claude-opus", claudeOpusModel,
            "titan", titanTextModel,
            "nova", novaPremiereModel,
            "llama", llama4Model);
  }

  /**
   * Process a chat request with the specified model.
   *
   * @param sessionId optional session ID for continuing a conversation
   * @param modelId optional model identifier
   * @param prompt the user's message
   * @return response containing the AI reply and session information
   */
  public ChatResponseWithSession chat(UUID sessionId, String modelId, String prompt) {
    Objects.requireNonNull(prompt, "Chat prompt cannot be null");

    UUID activeSessionId = Optional.ofNullable(sessionId).orElseGet(UUID::randomUUID);
    log.debug(
        "Starting chat - model: {}, session: {}",
        Optional.ofNullable(modelId).orElse("default"),
        activeSessionId);

    Conversation savedUserMessage = saveUserMessage(activeSessionId, prompt);
    ensureSessionExists(activeSessionId);

    String contextText = retrieveRelevantContext(prompt);
    List<Message> messages = buildPromptMessages(activeSessionId, contextText, prompt);

    var response = resolveModel(modelId).call(new Prompt(messages));
    String assistantReply = response.getResult().getOutput().getText();

    saveAssistantMessage(activeSessionId, savedUserMessage.id(), assistantReply);

    return ChatResponseWithSession.builder()
        .sessionId(activeSessionId)
        .chatResponse(response)
        .build();
  }

  public List<Conversation> getConversationHistory(UUID sessionId) {
    return conversationRepository.findBySessionIdOrderByTimestampAsc(sessionId);
  }

  public List<String> getAvailableModels() {
    return List.copyOf(modelMap.keySet());
  }

  private Conversation saveUserMessage(UUID sessionId, String prompt) {
    return conversationRepository.save(
        Conversation.builder()
            .id(UUID.randomUUID())
            .timestamp(Instant.now())
            .sessionId(sessionId)
            .role(ROLE_USER)
            .content(prompt)
            .build());
  }

  private void saveAssistantMessage(UUID sessionId, UUID parentId, String content) {
    Conversation savedMessage =
        conversationRepository.save(
            Conversation.builder()
                .id(UUID.randomUUID())
                .timestamp(Instant.now())
                .sessionId(sessionId)
                .parentId(parentId)
                .role(ROLE_ASSISTANT)
                .content(content)
                .build());

    updateSessionWithMessage(sessionId, savedMessage.id());
  }

  private void updateSessionWithMessage(UUID sessionId, UUID messageId) {
    sessionRepository
        .findById(sessionId)
        .ifPresent(
            session -> {
              List<UUID> updatedMessageIds =
                  new ArrayList<>(
                      Optional.ofNullable(session.messageIds()).orElseGet(ArrayList::new));
              updatedMessageIds.add(messageId);

              sessionRepository.save(
                  ConversationSession.builder()
                      .id(session.id())
                      .messageIds(updatedMessageIds)
                      .build());
            });
  }

  private void ensureSessionExists(UUID sessionId) {
    if (sessionRepository.findById(sessionId).isEmpty()) {
      sessionRepository.save(
          ConversationSession.builder().id(sessionId).messageIds(new ArrayList<>()).build());
    }
  }

  private String retrieveRelevantContext(String query) {
    List<Document> documents = documentService.search(query, maxDocuments);
    String context = documents.stream().map(Document::getText).reduce("", String::concat);

    if (!context.isEmpty()) {
      log.debug("Retrieved {} documents for context", documents.size());
    }
    return context;
  }

  private List<Message> buildPromptMessages(UUID sessionId, String context, String currentPrompt) {
    List<Message> messages = new ArrayList<>();

    messages.add(new SystemMessage(buildSystemPrompt(context)));
    messages.addAll(loadConversationHistory(sessionId));
    messages.add(new UserMessage(currentPrompt));

    return messages;
  }

  private String buildSystemPrompt(String context) {
    if (context.isEmpty()) {
      return DEFAULT_SYSTEM_PROMPT;
    }
    return DEFAULT_SYSTEM_PROMPT
        + "Use the following relevant information to answer the question:\n\n"
        + context;
  }

  private List<Message> loadConversationHistory(UUID sessionId) {
    List<Conversation> history =
        conversationRepository.findBySessionIdOrderByTimestampAsc(sessionId);

    if (history.size() > maxConversationHistory) {
      history = history.subList(history.size() - maxConversationHistory, history.size());
    }

    return history.stream().map(this::toMessage).filter(Objects::nonNull).toList();
  }

  private Message toMessage(Conversation conversation) {
    return switch (conversation.role()) {
      case ROLE_USER -> new UserMessage(conversation.content());
      case ROLE_ASSISTANT -> new AssistantMessage(conversation.content());
      default -> null;
    };
  }

  private BedrockProxyChatModel resolveModel(String modelId) {
    if (modelId == null) {
      return defaultModel;
    }
    return modelMap.getOrDefault(modelId.toLowerCase(Locale.ROOT), defaultModel);
  }
}
