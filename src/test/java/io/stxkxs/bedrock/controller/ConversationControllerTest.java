package io.stxkxs.bedrock.controller;

import io.stxkxs.bedrock.model.ChatResponseWithSession;
import io.stxkxs.bedrock.service.ConversationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ConversationControllerTest {

  private MockMvc mockMvc;

  @Mock
  private ConversationService conversationService;

  private ConversationController conversationController;

  @BeforeEach
  void setUp() {
    conversationController = new ConversationController(conversationService);
    mockMvc = MockMvcBuilders.standaloneSetup(conversationController).build();
  }

  @Test
  @DisplayName("Should process chat request successfully with existing session ID")
  void shouldProcessChatRequestSuccessfullyWithExistingSessionId() throws Exception {
    var prompt = "Hello, AI!";
    var sessionId = UUID.randomUUID();
    var modelId = "claude-sonnet";

    var chatResponse = mock(ChatResponse.class);
    var generation = mock(Generation.class);
    var message = new AssistantMessage("Hello, human!");

    when(generation.getOutput()).thenReturn(message);
    when(chatResponse.getResult()).thenReturn(generation);
    when(conversationService.chat(sessionId, modelId, prompt))
      .thenReturn(ChatResponseWithSession.builder()
        .sessionId(sessionId)
        .chatResponse(chatResponse)
        .build());

    mockMvc.perform(post("/api/conversation/stream")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
              "prompt": "Hello, AI!",
              "sessionId": "%s",
              "modelId": "claude-sonnet"
          }
          """.formatted(sessionId)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.sessionId", is(sessionId.toString())));
  }

  @Test
  @DisplayName("Should process chat request successfully with new session ID")
  void shouldProcessChatRequestSuccessfullyWithNewSessionId() throws Exception {
    var prompt = "Hello, AI!";
    var generatedSessionId = UUID.randomUUID();
    var modelId = "claude-sonnet";

    var chatResponse = mock(ChatResponse.class);
    var generation = mock(Generation.class);
    var message = new AssistantMessage("Hello, human!");

    when(generation.getOutput()).thenReturn(message);
    when(chatResponse.getResult()).thenReturn(generation);
    when(conversationService.chat(null, modelId, prompt))
      .thenReturn(ChatResponseWithSession.builder()
        .chatResponse(chatResponse)
        .sessionId(generatedSessionId)
        .build());

    mockMvc.perform(post("/api/conversation/stream")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {
              "prompt": "Hello, AI!",
              "modelId": "claude-sonnet"
          }
          """))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.sessionId", is(generatedSessionId.toString())));
  }

  @Test
  @DisplayName("Should get available models")
  void shouldGetAvailableModels() throws Exception {
    var models = List.of("claude-sonnet", "claude-haiku", "titan", "nova", "llama");
    when(conversationService.getAvailableModels()).thenReturn(models);

    mockMvc.perform(get("/api/conversation/models"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(5)))
      .andExpect(jsonPath("$[0]", is("claude-sonnet")))
      .andExpect(jsonPath("$[1]", is("claude-haiku")))
      .andExpect(jsonPath("$[2]", is("titan")))
      .andExpect(jsonPath("$[3]", is("nova")))
      .andExpect(jsonPath("$[4]", is("llama")));
  }
}