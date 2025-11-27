package io.stxkxs.bedrock.model;

import java.time.Instant;

public record ApiError(String code, String message, int status, String path, Instant timestamp) {}
