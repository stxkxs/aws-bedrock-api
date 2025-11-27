package io.stxkxs.bedrock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  private DocumentService documentService;

  @Mock private VectorStore vectorStore;

  @Mock private TextExtractor textExtractor;

  @Captor private ArgumentCaptor<List<Document>> documentsCaptor;

  @BeforeEach
  void setUp() {
    documentService = new DocumentService(vectorStore, textExtractor);
  }

  @Test
  @DisplayName("Should embed document and add to vector store")
  void shouldEmbedDocumentAndAddToVectorStore() throws IOException {
    var title = "Test Document";
    var fileName = "test.txt";
    var content =
        "This is a test document with sufficient content to be split into chunks for proper embedding in the vector store.";
    var file =
        new MockMultipartFile("file", fileName, MediaType.TEXT_PLAIN_VALUE, content.getBytes());

    when(textExtractor.extractTextFromFile(file)).thenReturn(content);

    var result = documentService.embed(file, title);

    verify(vectorStore).add(documentsCaptor.capture());
    var capturedDocuments = documentsCaptor.getValue();

    assertThat(capturedDocuments).isNotEmpty();
    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    assertThat(result.getMetadata()).containsEntry("title", title);
    assertThat(result.getMetadata()).containsEntry("source", fileName);
  }

  @Test
  @DisplayName("Should search for documents in vector store")
  void shouldSearchForDocumentsInVectorStore() {
    var query = "test query";
    var limit = 5;
    var expectedDocuments =
        List.of(createTestDocument("doc1", "content1"), createTestDocument("doc2", "content2"));

    when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(expectedDocuments);

    var result = documentService.search(query, limit);

    assertThat(result).isEqualTo(expectedDocuments);

    var requestCaptor = ArgumentCaptor.forClass(SearchRequest.class);
    verify(vectorStore).similaritySearch(requestCaptor.capture());

    var capturedRequest = requestCaptor.getValue();
    assertThat(capturedRequest.getQuery()).isEqualTo(query);
    assertThat(capturedRequest.getTopK()).isEqualTo(limit);
  }

  @Test
  @DisplayName("Should return empty list when search returns null")
  void shouldReturnEmptyListWhenSearchReturnsNull() {
    var query = "test query";
    var limit = 5;

    when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(null);

    var result = documentService.search(query, limit);

    assertThat(result).isEmpty();
  }

  private Document createTestDocument(String id, String content) {
    return Document.builder().id(id).text(content).metadata(Map.of("source", "test")).build();
  }
}
