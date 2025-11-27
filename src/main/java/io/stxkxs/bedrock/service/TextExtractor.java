package io.stxkxs.bedrock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Extracts text content from various file formats.
 *
 * <p>Supports plain text, Markdown, CSV, and JSON file formats. Files are identified by extension
 * first, then by content type.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TextExtractor {

  private static final String UNSUPPORTED_TYPE_MESSAGE =
      "This file type is not supported for text extraction. Supported types: txt, md, csv, json";

  private static final Map<String, FileType> EXTENSION_MAP =
      Map.of(
          ".txt", FileType.TEXT,
          ".md", FileType.MARKDOWN,
          ".markdown", FileType.MARKDOWN,
          ".csv", FileType.CSV,
          ".json", FileType.JSON);

  private static final Set<String> MARKDOWN_CONTENT_TYPES = Set.of("text/markdown");
  private static final Set<String> CSV_CONTENT_TYPES = Set.of("text/csv");

  private final ObjectMapper objectMapper;

  /**
   * Extracts text content from the uploaded file.
   *
   * @param file the uploaded file
   * @return extracted text content
   * @throws IOException if file reading or parsing fails
   */
  public String extractTextFromFile(MultipartFile file) throws IOException {
    String filename = resolveFilename(file);
    log.info("Extracting text from file: {}", filename);

    MediaType contentType = parseContentType(file);
    FileType fileType = determineFileType(filename, contentType);
    log.debug("Determined file type: {}", fileType);

    byte[] content = file.getBytes();
    log.debug("File size: {} bytes", content.length);

    return extractContent(fileType, content);
  }

  private String resolveFilename(MultipartFile file) {
    return Optional.ofNullable(file.getOriginalFilename())
        .orElseGet(() -> UUID.randomUUID().toString())
        .toLowerCase(Locale.ROOT);
  }

  private MediaType parseContentType(MultipartFile file) {
    return Optional.ofNullable(file.getContentType()).map(MediaType::parseMediaType).orElse(null);
  }

  private FileType determineFileType(String filename, MediaType contentType) {
    return determineByExtension(filename)
        .orElseGet(() -> determineByContentType(contentType).orElse(FileType.UNSUPPORTED));
  }

  private Optional<FileType> determineByExtension(String filename) {
    return EXTENSION_MAP.entrySet().stream()
        .filter(entry -> filename.endsWith(entry.getKey()))
        .map(Map.Entry::getValue)
        .findFirst();
  }

  private Optional<FileType> determineByContentType(MediaType contentType) {
    if (contentType == null) {
      return Optional.empty();
    }

    String contentTypeString = contentType.toString();

    if (contentType.includes(MediaType.TEXT_PLAIN)) {
      return Optional.of(FileType.TEXT);
    }
    if (MARKDOWN_CONTENT_TYPES.contains(contentTypeString)) {
      return Optional.of(FileType.MARKDOWN);
    }
    if (CSV_CONTENT_TYPES.contains(contentTypeString)) {
      return Optional.of(FileType.CSV);
    }
    if (contentType.includes(MediaType.APPLICATION_JSON)) {
      return Optional.of(FileType.JSON);
    }

    return Optional.empty();
  }

  private String extractContent(FileType fileType, byte[] content) throws IOException {
    try {
      return switch (fileType) {
        case TEXT, MARKDOWN, CSV -> extractAsText(content);
        case JSON -> extractAsJson(content);
        case UNSUPPORTED -> {
          log.warn("Unsupported file type requested");
          yield UNSUPPORTED_TYPE_MESSAGE;
        }
      };
    } catch (IOException e) {
      log.error("Error extracting text: {}", e.getMessage(), e);
      throw new IOException("Error extracting text from file: " + e.getMessage(), e);
    }
  }

  private String extractAsText(byte[] content) {
    String text = new String(content, StandardCharsets.UTF_8);
    log.debug("Extracted {} characters as text", text.length());
    return text;
  }

  private String extractAsJson(byte[] content) throws IOException {
    String jsonString = new String(content, StandardCharsets.UTF_8);
    var jsonNode = objectMapper.readTree(jsonString);
    String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

    log.debug("Extracted {} characters from JSON", prettyJson.length());
    return prettyJson;
  }

  private enum FileType {
    TEXT,
    MARKDOWN,
    CSV,
    JSON,
    UNSUPPORTED
  }
}
