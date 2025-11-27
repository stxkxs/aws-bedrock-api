package io.stxkxs.bedrock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import io.stxkxs.bedrock.model.Conversation;
import io.stxkxs.bedrock.model.ConversationSession;
import io.stxkxs.bedrock.repository.ConversationRepository;
import io.stxkxs.bedrock.repository.ConversationSessionRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.bedrock.converse.BedrockProxyChatModel;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

  private ConversationService conversationService;

  @Mock private BedrockProxyChatModel claudeSonnetModel;

  @Mock private BedrockProxyChatModel claudeHaikuModel;

  @Mock private BedrockProxyChatModel titanTextModel;

  @Mock private BedrockProxyChatModel novaProModel;

  @Mock private BedrockProxyChatModel llamaModel;

  @Mock private DocumentService documentService;

  @Mock private ConversationRepository conversationRepository;

  @Mock private ConversationSessionRepository sessionRepository;

  @Captor private ArgumentCaptor<Conversation> conversationCaptor;

  @Captor private ArgumentCaptor<ConversationSession> sessionCaptor;

  @Captor private ArgumentCaptor<Prompt> promptCaptor;

  @BeforeEach
  void setUp() {
    conversationService =
        new ConversationService(
            claudeSonnetModel,
            claudeHaikuModel,
            titanTextModel,
            novaProModel,
            llamaModel,
            documentService,
            conversationRepository,
            sessionRepository);

    ReflectionTestUtils.setField(conversationService, "maxDocuments", 5);
    ReflectionTestUtils.setField(conversationService, "maxConversationHistory", 10);
  }

  @Test
  @DisplayName("Should throw exception when prompt is null")
  void shouldThrowExceptionWhenPromptIsNull() {
    assertThatThrownBy(() -> conversationService.chat(UUID.randomUUID(), "claude-sonnet", null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Chat prompt cannot be null");
  }

  @Test
  @DisplayName("Should create new session when session ID is null")
  void shouldCreateNewSessionWhenSessionIdIsNull() {
    var prompt = "Hello, AI!";
    var savedMessage = createConversation(UUID.randomUUID());
    var chatResponse = mock(ChatResponse.class);
    var responseResult = mock(Generation.class);
    var responseOutput = mock(AssistantMessage.class);

    when(conversationRepository.save(any(Conversation.class))).thenReturn(savedMessage);
    when(sessionRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    when(sessionRepository.save(any(ConversationSession.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(documentService.search(anyString(), anyInt())).thenReturn(Collections.emptyList());
    when(claudeSonnetModel.call(any(Prompt.class))).thenReturn(chatResponse);
    when(chatResponse.getResult()).thenReturn(responseResult);
    when(responseResult.getOutput()).thenReturn(responseOutput);
    when(responseOutput.getText()).thenReturn("Hello, human!");

    var result = conversationService.chat(null, "claude-sonnet", prompt);

    verify(sessionRepository).save(sessionCaptor.capture());
    var capturedSession = sessionCaptor.getValue();

    assertThat(capturedSession.id()).isNotNull();
    assertThat(capturedSession.messageIds()).isNotNull().isEmpty();
    assertThat(result.chatResponse()).isEqualTo(chatResponse);
    assertThat(result.sessionId()).isNotNull();
  }

  @Test
  @DisplayName("Should use existing session when session ID is provided")
  void shouldUseExistingSessionWhenSessionIdIsProvided() {
    var sessionId = UUID.randomUUID();
    var prompt = "Hello, AI!";
    var existingSession =
        ConversationSession.builder().id(sessionId).messageIds(new ArrayList<>()).build();
    var savedMessage = createConversation(sessionId);
    var chatResponse = mock(ChatResponse.class);
    var responseResult = mock(Generation.class);
    var responseOutput = mock(AssistantMessage.class);

    when(conversationRepository.save(any(Conversation.class))).thenReturn(savedMessage);
    when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));
    when(sessionRepository.save(any(ConversationSession.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(documentService.search(anyString(), anyInt())).thenReturn(Collections.emptyList());
    when(claudeSonnetModel.call(any(Prompt.class))).thenReturn(chatResponse);
    when(chatResponse.getResult()).thenReturn(responseResult);
    when(responseResult.getOutput()).thenReturn(responseOutput);
    when(responseOutput.getText()).thenReturn("Hello, human!");

    var result = conversationService.chat(sessionId, "claude-sonnet", prompt);

    verify(claudeSonnetModel).call(any(Prompt.class));
    assertThat(result.chatResponse()).isEqualTo(chatResponse);
    assertThat(result.sessionId()).isEqualTo(sessionId);
  }

  @Test
  @DisplayName("Should retrieve relevant documents for context")
  void shouldRetrieveRelevantDocumentsForContext() {
    var sessionId = UUID.randomUUID();
    var prompt = "Tell me about embeddings";
    var savedMessage = createConversation(sessionId);
    var existingSession =
        ConversationSession.builder().id(sessionId).messageIds(new ArrayList<>()).build();
    var documents =
        List.of(
            Document.builder().text("Document about embeddings").build(),
            Document.builder().text("Vector embeddings information").build());
    var chatResponse = mock(ChatResponse.class);
    var responseResult = mock(Generation.class);
    var responseOutput = mock(AssistantMessage.class);

    when(conversationRepository.save(any(Conversation.class))).thenReturn(savedMessage);
    when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));
    when(sessionRepository.save(any(ConversationSession.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(documentService.search(eq(prompt), anyInt())).thenReturn(documents);
    when(claudeSonnetModel.call(any(Prompt.class))).thenReturn(chatResponse);
    when(chatResponse.getResult()).thenReturn(responseResult);
    when(responseResult.getOutput()).thenReturn(responseOutput);
    when(responseOutput.getText()).thenReturn("Embeddings are vector representations...");
    when(conversationRepository.findBySessionIdOrderByTimestampAsc(sessionId))
        .thenReturn(Collections.emptyList());

    var result = conversationService.chat(sessionId, "claude-sonnet", prompt);

    verify(documentService).search(eq(prompt), eq(5));
    verify(claudeSonnetModel).call(promptCaptor.capture());

    var capturedPrompt = promptCaptor.getValue();
    var messages = capturedPrompt.getInstructions();

    assertThat(messages).isNotEmpty();
    var systemMessage =
        messages.stream()
            .filter(m -> m instanceof SystemMessage)
            .map(m -> (SystemMessage) m)
            .findFirst()
            .orElseThrow();

    assertThat(systemMessage.getText()).contains("Document about embeddings");
    assertThat(systemMessage.getText()).contains("Vector embeddings information");
    assertThat(result.chatResponse()).isEqualTo(chatResponse);
    assertThat(result.sessionId()).isEqualTo(sessionId);
  }

  @Test
  @DisplayName("Should use different model when specified")
  void shouldUseDifferentModelWhenSpecified() {
    var sessionId = UUID.randomUUID();
    var prompt = "Hello from Llama";
    var savedMessage = createConversation(sessionId);
    var existingSession =
        ConversationSession.builder().id(sessionId).messageIds(new ArrayList<>()).build();
    var chatResponse = mock(ChatResponse.class);
    var responseResult = mock(Generation.class);
    var responseOutput = mock(AssistantMessage.class);

    when(conversationRepository.save(any(Conversation.class))).thenReturn(savedMessage);
    when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));
    when(sessionRepository.save(any(ConversationSession.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(documentService.search(anyString(), anyInt())).thenReturn(Collections.emptyList());
    when(llamaModel.call(any(Prompt.class))).thenReturn(chatResponse);
    when(chatResponse.getResult()).thenReturn(responseResult);
    when(responseResult.getOutput()).thenReturn(responseOutput);
    when(responseOutput.getText()).thenReturn("Hello from Llama model!");
    when(conversationRepository.findBySessionIdOrderByTimestampAsc(sessionId))
        .thenReturn(Collections.emptyList());

    var result = conversationService.chat(sessionId, "llama", prompt);

    verify(llamaModel).call(any(Prompt.class));
    assertThat(result.chatResponse()).isEqualTo(chatResponse);
    assertThat(result.sessionId()).isEqualTo(sessionId);
  }

  @Test
  @DisplayName("Should save assistant response")
  void shouldSaveAssistantResponse() {
    var sessionId = UUID.randomUUID();
    var prompt = "Hello, AI!";
    var savedUserMessage = createConversation(sessionId);
    var existingSession =
        ConversationSession.builder().id(sessionId).messageIds(new ArrayList<>()).build();
    var assistantContent = "Hello, human!";
    var chatResponse = mock(ChatResponse.class);
    var responseResult = mock(Generation.class);
    var responseOutput = mock(AssistantMessage.class);

    when(conversationRepository.save(any(Conversation.class)))
        .thenReturn(savedUserMessage)
        .thenAnswer(invocation -> invocation.getArgument(0, Conversation.class));
    when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));
    when(sessionRepository.save(any(ConversationSession.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(documentService.search(anyString(), anyInt())).thenReturn(Collections.emptyList());
    when(claudeSonnetModel.call(any(Prompt.class))).thenReturn(chatResponse);
    when(chatResponse.getResult()).thenReturn(responseResult);
    when(responseResult.getOutput()).thenReturn(responseOutput);
    when(responseOutput.getText()).thenReturn(assistantContent);
    when(conversationRepository.findBySessionIdOrderByTimestampAsc(sessionId))
        .thenReturn(Collections.emptyList());

    var result = conversationService.chat(sessionId, "claude-sonnet", prompt);

    var messageCaptor = ArgumentCaptor.forClass(Conversation.class);
    verify(conversationRepository, times(2)).save(messageCaptor.capture());
    var savedMessages = messageCaptor.getAllValues();

    // First saved message should be the user message
    assertThat(savedMessages.get(0).role()).isEqualTo("user");
    assertThat(savedMessages.get(0).content()).isEqualTo(prompt);

    // Second saved message should be the assistant response
    assertThat(savedMessages.get(1).role()).isEqualTo("assistant");
    assertThat(savedMessages.get(1).content()).isEqualTo(assistantContent);
    assertThat(savedMessages.get(1).parentId()).isEqualTo(savedUserMessage.id());

    // Verify the response contains both the chat response and session ID
    assertThat(result.chatResponse()).isEqualTo(chatResponse);
    assertThat(result.sessionId()).isEqualTo(sessionId);
  }

  @Test
  @DisplayName("Should include conversation history in prompt")
  void shouldIncludeConversationHistoryInPrompt() {
    var sessionId = UUID.randomUUID();
    var prompt = "What's the weather like?";
    var savedMessage = createConversation(sessionId);
    var existingSession =
        ConversationSession.builder().id(sessionId).messageIds(new ArrayList<>()).build();
    var chatResponse = mock(ChatResponse.class);
    var responseResult = mock(Generation.class);
    var responseOutput = mock(AssistantMessage.class);

    var history =
        List.of(
            Conversation.builder()
                .id(UUID.randomUUID())
                .sessionId(sessionId)
                .timestamp(Instant.now().minusSeconds(60))
                .role("user")
                .content("Hello")
                .build(),
            Conversation.builder()
                .id(UUID.randomUUID())
                .sessionId(sessionId)
                .timestamp(Instant.now().minusSeconds(45))
                .role("assistant")
                .content("Hi there!")
                .build());

    when(conversationRepository.save(any(Conversation.class))).thenReturn(savedMessage);
    when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(existingSession));
    when(sessionRepository.save(any(ConversationSession.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(documentService.search(anyString(), anyInt())).thenReturn(Collections.emptyList());
    when(claudeSonnetModel.call(any(Prompt.class))).thenReturn(chatResponse);
    when(chatResponse.getResult()).thenReturn(responseResult);
    when(responseResult.getOutput()).thenReturn(responseOutput);
    when(responseOutput.getText()).thenReturn("It's sunny today!");
    when(conversationRepository.findBySessionIdOrderByTimestampAsc(sessionId)).thenReturn(history);

    var result = conversationService.chat(sessionId, "claude-sonnet", prompt);

    verify(claudeSonnetModel).call(promptCaptor.capture());

    var capturedPrompt = promptCaptor.getValue();
    var messages = capturedPrompt.getInstructions();

    // Should contain: 1 system message + 2 history messages + 1 new user message
    assertThat(messages).hasSize(4);

    assertThat(messages.get(0)).isInstanceOf(SystemMessage.class);
    assertThat(messages.get(1)).isInstanceOf(UserMessage.class);
    assertThat((messages.get(1)).getText()).isEqualTo("Hello");
    assertThat(messages.get(2)).isInstanceOf(AssistantMessage.class);
    assertThat((messages.get(2)).getText()).isEqualTo("Hi there!");
    assertThat(messages.get(3)).isInstanceOf(UserMessage.class);
    assertThat((messages.get(3)).getText()).isEqualTo(prompt);

    // Verify the response contains both the chat response and session ID
    assertThat(result.chatResponse()).isEqualTo(chatResponse);
    assertThat(result.sessionId()).isEqualTo(sessionId);
  }

  private Conversation createConversation(UUID sessionId) {
    return Conversation.builder()
        .id(UUID.randomUUID())
        .sessionId(sessionId)
        .timestamp(Instant.now())
        .role("user")
        .content("Test content")
        .build();
  }
}
