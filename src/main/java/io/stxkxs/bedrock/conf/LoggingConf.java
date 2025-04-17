package io.stxkxs.bedrock.conf;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Configuration
@Order(-999)
public class LoggingConf extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain
  ) throws ServletException, IOException {
    var requestId = UUID.randomUUID().toString();

    try {
      MDC.put("requestId", requestId);
      MDC.put("method", request.getMethod());
      MDC.put("path", request.getRequestURI());
      MDC.put("clientIp", getClientIpAddress(request));

      response.addHeader("X-Request-ID", requestId);
      chain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }

  private String getClientIpAddress(HttpServletRequest request) {
    var ip = request.getHeader("X-Forwarded-For");

    if (isIpMissing(ip)) ip = request.getHeader("Proxy-Client-IP");
    if (isIpMissing(ip)) ip = request.getHeader("WL-Proxy-Client-IP");
    if (isIpMissing(ip)) ip = request.getHeader("HTTP_X_FORWARDED_FOR");
    if (isIpMissing(ip)) ip = request.getHeader("HTTP_X_FORWARDED");
    if (isIpMissing(ip)) ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
    if (isIpMissing(ip)) ip = request.getHeader("HTTP_CLIENT_IP");
    if (isIpMissing(ip)) ip = request.getHeader("HTTP_FORWARDED_FOR");
    if (isIpMissing(ip)) ip = request.getHeader("HTTP_FORWARDED");
    if (isIpMissing(ip)) ip = request.getRemoteAddr();

    return ip != null && ip.contains(",") ? ip.split(",")[0].trim() : (ip != null ? ip : "unknown");
  }

  private boolean isIpMissing(String ip) {
    return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
  }
}