package io.stxkxs.bedrock.controller;

import io.stxkxs.bedrock.service.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/document")
public class DocumentController {

  private final DocumentService documentService;

  public DocumentController(DocumentService documentService) {
    this.documentService = documentService;
  }

  @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String, String>> uploadDocument(
    @RequestParam("file") MultipartFile file,
    @RequestParam(name = "title", required = false) String title
  ) {
    log.info("uploading document: {}", file.getOriginalFilename());

    try {
      var document = documentService.embed(file, StringUtils.defaultIfBlank(title, file.getOriginalFilename()));

      log.info("document processed successfully: {}", document.getId());

      return ResponseEntity.ok(Map.of(
        "message", "document processed successfully",
        "documentId", document.getId()));
    } catch (Exception e) {
      log.error("error processing document: {}", e.getMessage(), e);

      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "failed to process document: " + e.getMessage()));
    }
  }
}