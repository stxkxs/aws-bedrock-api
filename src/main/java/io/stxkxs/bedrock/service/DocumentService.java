package io.stxkxs.bedrock.service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class DocumentService {

  private final VectorStore vectorStore;
  private final TextExtractor textExtractor;
  private final TokenTextSplitter tokenTextSplitter;

  public DocumentService(VectorStore store, TextExtractor textExtractor) {
    this.vectorStore = store;
    this.textExtractor = textExtractor;
    this.tokenTextSplitter =
        TokenTextSplitter.builder()
            .withChunkSize(2000)
            .withMinChunkSizeChars(100)
            .withMinChunkLengthToEmbed(10)
            .withMaxNumChunks(100)
            .withKeepSeparator(true)
            .build();
  }

  public Document embed(MultipartFile file, String title) throws IOException {
    log.info("processing document: {}", title);

    var content = textExtractor.extractTextFromFile(file);
    log.debug("text extracted, creating document with {} characters", content.length());

    var document =
        Document.builder()
            .id(UUID.randomUUID().toString())
            .metadata(
                Map.of(
                    "title",
                    title,
                    "source",
                    file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown"))
            .text(content)
            .build();

    var documents = tokenTextSplitter.apply(List.of(document));
    log.info("document split into {} chunks", documents.size());

    if (!documents.isEmpty()) {
      vectorStore.add(documents);
      log.info("added document chunks to vector store");
      return documents.getFirst();
    } else {
      log.warn("no document chunks were created");
      return document;
    }
  }

  public List<Document> search(String query, int limit) {
    return Optional.ofNullable(
            vectorStore.similaritySearch(SearchRequest.builder().topK(limit).query(query).build()))
        .orElse(Collections.emptyList());
  }
}
