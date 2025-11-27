package io.stxkxs.bedrock.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.stxkxs.bedrock.service.DocumentService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

  private MockMvc mockMvc;

  @Mock private DocumentService documentService;

  private DocumentController documentController;

  @BeforeEach
  void setUp() {
    documentController = new DocumentController(documentService);
    mockMvc = MockMvcBuilders.standaloneSetup(documentController).build();
  }

  @Test
  @DisplayName("Should upload document successfully")
  void shouldUploadDocumentSuccessfully() throws Exception {
    var fileName = "test.txt";
    var title = "Test Document";
    var content = "Test content";
    var documentId = UUID.randomUUID().toString();

    var file =
        new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, content.getBytes());

    var document =
        Document.builder()
            .id(documentId)
            .text(content)
            .metadata(Map.of("title", title, "source", fileName))
            .build();

    when(documentService.embed(any(), eq(title))).thenReturn(document);

    mockMvc
        .perform(multipart("/api/document/upload").file(file).param("title", title))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("document processed successfully")))
        .andExpect(jsonPath("$.documentId", is(documentId)));

    verify(documentService).embed(file, title);
  }

  @Test
  @DisplayName("Should use file name as title when title is not provided")
  void shouldUseFileNameAsTitleWhenTitleIsNotProvided() throws Exception {
    var fileName = "test.txt";
    var content = "Test content";
    var documentId = UUID.randomUUID().toString();

    var file =
        new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, content.getBytes());

    var document =
        Document.builder()
            .id(documentId)
            .text(content)
            .metadata(Map.of("title", fileName, "source", fileName))
            .build();

    when(documentService.embed(any(), eq(fileName))).thenReturn(document);

    mockMvc
        .perform(multipart("/api/document/upload").file(file))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is("document processed successfully")))
        .andExpect(jsonPath("$.documentId", is(documentId)));

    verify(documentService).embed(file, fileName);
  }

  @Test
  @DisplayName("Should handle document processing error")
  void shouldHandleDocumentProcessingError() throws Exception {
    var fileName = "test.txt";
    var content = "Test content";
    var errorMessage = "Invalid file format";

    var file =
        new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, content.getBytes());

    when(documentService.embed(any(), anyString())).thenThrow(new RuntimeException(errorMessage));

    mockMvc
        .perform(multipart("/api/document/upload").file(file))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error", is("failed to process document: " + errorMessage)));
  }
}
