package io.stxkxs.bedrock.error;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.stxkxs.bedrock.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(TooManyRequestsException.class)
  public ResponseEntity<ApiError> handleTooManyRequestsException(TooManyRequestsException ex, ServerWebExchange exchange) {
    var span = Span.current();
    span.setStatus(StatusCode.ERROR, ex.getMessage());
    span.recordException(ex);

    log.warn("Rate limit exceeded: {}", ex.getMessage());

    var error = new ApiError(
      "TOO_MANY_REQUESTS",
      ex.getMessage(),
      HttpStatus.TOO_MANY_REQUESTS.value(),
      exchange.getRequest().getPath().value(),
      Instant.now()
    );

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<ApiError> handleValidationErrors(WebExchangeBindException ex, ServerWebExchange exchange) {
    var span = Span.current();
    span.setStatus(StatusCode.ERROR, "Validation error");
    span.recordException(ex);

    var errorDetails = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(err -> err.getField() + ": " + err.getDefaultMessage())
      .collect(Collectors.joining(", "));

    log.warn("Validation error: {}", errorDetails);

    var error = new ApiError(
      "VALIDATION_ERROR",
      "validation failed: " + errorDetails,
      HttpStatus.BAD_REQUEST.value(),
      exchange.getRequest().getPath().value(),
      Instant.now()
    );

    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex, ServerWebExchange exchange) {
    var span = Span.current();
    span.setStatus(StatusCode.ERROR, ex.getMessage());
    span.recordException(ex);

    log.warn("Resource not found: {}", ex.getMessage());

    var error = new ApiError(
      "RESOURCE_NOT_FOUND",
      ex.getMessage(),
      HttpStatus.NOT_FOUND.value(),
      exchange.getRequest().getPath().value(),
      Instant.now()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex, ServerWebExchange exchange) {
    var span = Span.current();
    span.setStatus(StatusCode.ERROR, ex.getReason());
    span.recordException(ex);

    log.warn("http status error: {} - {}", ex.getStatusCode(), ex.getReason());

    var error = new ApiError(
      "HTTP_ERROR",
      ex.getReason(),
      ex.getStatusCode().value(),
      exchange.getRequest().getPath().value(),
      Instant.now()
    );

    return ResponseEntity.status(ex.getStatusCode()).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleGenericException(Exception ex, ServerWebExchange exchange) {
    var span = Span.current();
    span.setStatus(StatusCode.ERROR, ex.getMessage());
    span.recordException(ex);

    log.error("unhandled exception", ex);

    var error = new ApiError(
      "INTERNAL_SERVER_ERROR",
      "an unexpected error occurred",
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      exchange.getRequest().getPath().value(),
      Instant.now()
    );

    var errorDetails = Map.of(
      "exception", ex.getClass().getName(),
      "message", ex.getMessage(),
      "path", exchange.getRequest().getPath().value());

    log.error("unhandled exception: {}", errorDetails, ex);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}