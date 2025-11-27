package io.stxkxs.bedrock.error;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.stxkxs.bedrock.model.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/** Global exception handler for REST API errors with OpenTelemetry integration. */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TooManyRequestsException.class)
  public ResponseEntity<ApiError> handleTooManyRequestsException(
      TooManyRequestsException ex, HttpServletRequest request) {
    recordSpanError(ex);
    log.warn("Rate limit exceeded: {}", ex.getMessage());

    ApiError error =
        new ApiError(
            "TOO_MANY_REQUESTS",
            ex.getMessage(),
            HttpStatus.TOO_MANY_REQUESTS.value(),
            request.getRequestURI(),
            Instant.now());

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidationErrors(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    recordSpanError("Validation error", ex);

    String errorDetails =
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining(", "));

    log.warn("Validation error: {}", errorDetails);

    ApiError error =
        new ApiError(
            "VALIDATION_ERROR",
            "Validation failed: " + errorDetails,
            HttpStatus.BAD_REQUEST.value(),
            request.getRequestURI(),
            Instant.now());

    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleResourceNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {
    recordSpanError(ex);
    log.warn("Resource not found: {}", ex.getMessage());

    ApiError error =
        new ApiError(
            "RESOURCE_NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            request.getRequestURI(),
            Instant.now());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiError> handleResponseStatusException(
      ResponseStatusException ex, HttpServletRequest request) {
    recordSpanError(ex.getReason(), ex);
    log.warn("HTTP status error: {} - {}", ex.getStatusCode(), ex.getReason());

    ApiError error =
        new ApiError(
            "HTTP_ERROR",
            ex.getReason(),
            ex.getStatusCode().value(),
            request.getRequestURI(),
            Instant.now());

    return ResponseEntity.status(ex.getStatusCode()).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
    recordSpanError(ex);
    log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

    ApiError error =
        new ApiError(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            request.getRequestURI(),
            Instant.now());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  private void recordSpanError(Exception ex) {
    recordSpanError(ex.getMessage(), ex);
  }

  private void recordSpanError(String message, Exception ex) {
    Span span = Span.current();
    span.setStatus(StatusCode.ERROR, message);
    span.recordException(ex);
  }
}
