package io.stxkxs.bedrock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TextExtractorTest {

  private TextExtractor textExtractor;

  @Mock
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    textExtractor = new TextExtractor(objectMapper);
  }

  @Test
  @DisplayName("Should extract text from text file")
  void shouldExtractTextFromTextFile() throws IOException {
    var content = "Sample text content";
    var file = new MockMultipartFile(
      "file",
      "test.txt",
      MediaType.TEXT_PLAIN_VALUE,
      content.getBytes(StandardCharsets.UTF_8)
    );

    var result = textExtractor.extractTextFromFile(file);

    assertThat(result).isEqualTo(content);
  }

  @Test
  @DisplayName("Should extract text from markdown file")
  void shouldExtractTextFromMarkdownFile() throws IOException {
    var content = "# Markdown heading\n\nSome content";
    var file = new MockMultipartFile(
      "file",
      "test.md",
      "text/markdown",
      content.getBytes(StandardCharsets.UTF_8)
    );

    var result = textExtractor.extractTextFromFile(file);

    assertThat(result).isEqualTo(content);
  }

  @Test
  @DisplayName("Should extract text from CSV file")
  void shouldExtractTextFromCsvFile() throws IOException {
    var content = "header1,header2\nvalue1,value2\nvalue3,value4";
    var file = new MockMultipartFile(
      "file",
      "test.csv",
      "text/csv",
      content.getBytes(StandardCharsets.UTF_8)
    );

    var result = textExtractor.extractTextFromFile(file);

    assertThat(result).isEqualTo(content + "\n");
  }

  @Test
  @DisplayName("Should extract text from JSON file")
  void shouldExtractTextFromJsonFile() throws IOException {
    var jsonContent = "{\"key\":\"value\"}";
    var prettyJson = "{\n  \"key\" : \"value\"\n}";
    var file = new MockMultipartFile(
      "file",
      "test.json",
      MediaType.APPLICATION_JSON_VALUE,
      jsonContent.getBytes(StandardCharsets.UTF_8)
    );

    when(objectMapper.readTree(jsonContent)).thenReturn(new ObjectMapper().readTree(jsonContent));
    when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(new ObjectMapper().writerWithDefaultPrettyPrinter());

    var result = textExtractor.extractTextFromFile(file);

    assertThat(result).contains("key");
    assertThat(result).contains("value");
  }

  @Test
  @DisplayName("Should handle JSON parsing error")
  void shouldHandleJsonParsingError() throws IOException {
    var invalidJson = "{invalid}";
    var file = new MockMultipartFile(
      "file",
      "test.json",
      MediaType.APPLICATION_JSON_VALUE,
      invalidJson.getBytes(StandardCharsets.UTF_8)
    );

    when(objectMapper.readTree(anyString())).thenAnswer(invocation -> {
      throw new IOException("Invalid JSON");
    });

    assertThatThrownBy(() -> textExtractor.extractTextFromFile(file))
      .isInstanceOf(IOException.class)
      .hasMessageContaining("failed to parse json content");
  }

  @Test
  @DisplayName("Should handle unsupported file type")
  void shouldHandleUnsupportedFileType() throws IOException {
    var content = "Sample content";
    var file = new MockMultipartFile(
      "file",
      "test.pdf",
      "application/pdf",
      content.getBytes(StandardCharsets.UTF_8)
    );

    var result = textExtractor.extractTextFromFile(file);

    assertThat(result).contains("not supported");
    assertThat(result).contains("supported types: txt, md, csv, json");
  }

  @Test
  @DisplayName("Should handle file with missing extension but valid content type")
  void shouldHandleFileWithMissingExtensionButValidContentType() throws IOException {
    var content = "Sample text content";
    var file = new MockMultipartFile(
      "file",
      "test",
      MediaType.TEXT_PLAIN_VALUE,
      content.getBytes(StandardCharsets.UTF_8)
    );

    var result = textExtractor.extractTextFromFile(file);

    assertThat(result).isEqualTo(content);
  }

  @Test
  @DisplayName("Should handle file with missing filename")
  void shouldHandleFileWithMissingFilename() throws IOException {
    var content = "Sample text content";
    var file = new MockMultipartFile(
      "file",
      null,
      MediaType.TEXT_PLAIN_VALUE,
      content.getBytes(StandardCharsets.UTF_8)
    );

    var result = textExtractor.extractTextFromFile(file);

    assertThat(result).isEqualTo(content);
  }
}