package io.stxkxs.bedrock.conf;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Request logging filter that adds request context to MDC for structured logging.
 *
 * <p>Extracts client IP from various proxy headers and adds request metadata to the logging
 * context.
 */
@Configuration
@Order(-999)
public class LoggingConf extends OncePerRequestFilter {

  private static final String UNKNOWN_IP = "unknown";

  private static final List<String> IP_HEADERS =
      List.of(
          "X-Forwarded-For",
          "Proxy-Client-IP",
          "WL-Proxy-Client-IP",
          "HTTP_X_FORWARDED_FOR",
          "HTTP_X_FORWARDED",
          "HTTP_X_CLUSTER_CLIENT_IP",
          "HTTP_CLIENT_IP",
          "HTTP_FORWARDED_FOR",
          "HTTP_FORWARDED");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String requestId = UUID.randomUUID().toString();

    try {
      MDC.put("requestId", requestId);
      MDC.put("method", request.getMethod());
      MDC.put("path", request.getRequestURI());
      MDC.put("clientIp", resolveClientIp(request));

      response.addHeader("X-Request-ID", requestId);
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

  private String resolveClientIp(HttpServletRequest request) {
    return IP_HEADERS.stream()
        .map(request::getHeader)
        .filter(this::isValidIp)
        .findFirst()
        .map(this::extractFirstIp)
        .orElseGet(() -> getRemoteAddressOrUnknown(request));
  }

  private boolean isValidIp(String ip) {
    return ip != null && !ip.isEmpty() && !UNKNOWN_IP.equalsIgnoreCase(ip);
  }

  private String extractFirstIp(String headerValue) {
    if (headerValue.contains(",")) {
      int commaIndex = headerValue.indexOf(',');
      return headerValue.substring(0, commaIndex).trim();
    }
    return headerValue;
  }

  private String getRemoteAddressOrUnknown(HttpServletRequest request) {
    return Objects.requireNonNullElse(request.getRemoteAddr(), UNKNOWN_IP);
  }
}
