package io.stxkxs.bedrock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class TextExtractor {

  private final ObjectMapper objectMapper;

  public TextExtractor(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public String extractTextFromFile(MultipartFile file) throws IOException {
    log.info("extracting text from file: {}", file.getOriginalFilename());

    var filename = Optional
      .ofNullable(file.getOriginalFilename())
      .orElseGet(() -> UUID.randomUUID().toString())
      .toLowerCase();
    var contentType = file.getContentType() != null
      ? MediaType.parseMediaType(file.getContentType())
      : null;

    var fileType = determineFileType(filename, contentType);
    log.debug("determined file type: {}", fileType);

    var fileContent = file.getBytes();
    log.debug("file size: {} bytes", fileContent.length);

    try {
      return switch (fileType) {
        case TEXT -> extractFromTextFile(fileContent);
        case MARKDOWN -> extractFromMarkdownFile(fileContent);
        case CSV -> extractFromCsvFile(fileContent);
        case JSON -> extractFromJsonFile(fileContent);
        default -> {
          log.warn("unsupported file type: {}", fileType);
          yield "this file type is not supported for text extraction. " +
            "supported types: txt, md, csv, json";
        }
      };
    } catch (Exception e) {
      log.error("error extracting text: {}", e.getMessage(), e);
      throw new IOException("error extracting text from file: " + e.getMessage(), e);
    }
  }

  private FileType determineFileType(String filename, MediaType contentType) {
    if (filename.endsWith(".txt")) {
      return FileType.TEXT;
    } else if (filename.endsWith(".md") || filename.endsWith(".markdown")) {
      return FileType.MARKDOWN;
    } else if (filename.endsWith(".csv")) {
      return FileType.CSV;
    } else if (filename.endsWith(".json")) {
      return FileType.JSON;
    } else if (contentType != null) {
      if (contentType.includes(MediaType.TEXT_PLAIN)) {
        return FileType.TEXT;
      } else if (contentType.toString().equals("text/markdown")) {
        return FileType.MARKDOWN;
      } else if (contentType.toString().equals("text/csv")) {
        return FileType.CSV;
      } else if (contentType.includes(MediaType.APPLICATION_JSON)) {
        return FileType.JSON;
      }
    }

    return FileType.UNSUPPORTED;
  }

  private String extractFromTextFile(byte[] content) {
    return new String(content, StandardCharsets.UTF_8);
  }

  private String extractFromMarkdownFile(byte[] content) {
    return extractFromTextFile(content);
  }

  private String extractFromCsvFile(byte[] content) throws IOException {
    try (var reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)))) {
      var builder = new StringBuilder();
      var line = "";

      while ((line = reader.readLine()) != null) {
        builder.append(line).append("\n");
      }

      var text = builder.toString();
      log.debug("extracted {} characters from csv", text.length());

      return text;
    } catch (Exception e) {
      log.error("error extracting text from csv: {}", e.getMessage(), e);
      throw new IOException("failed to extract text from csv", e);
    }
  }

  private String extractFromJsonFile(byte[] content) throws IOException {
    try {
      var jsonString = new String(content, StandardCharsets.UTF_8);
      var jsonNode = objectMapper.readTree(jsonString);
      var prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);

      log.debug("extracted {} characters from json", prettyJson.length());

      return prettyJson;
    } catch (Exception e) {
      log.error("error extracting text from json: {}", e.getMessage(), e);
      throw new IOException("failed to parse json content", e);
    }
  }

  private enum FileType {
    TEXT,
    MARKDOWN,
    CSV,
    JSON,
    UNSUPPORTED
  }
}