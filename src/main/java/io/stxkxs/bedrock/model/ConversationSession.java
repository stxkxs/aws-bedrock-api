package io.stxkxs.bedrock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Table("conversation_sessions")
@AllArgsConstructor
public class ConversationSession {
  @PrimaryKey
  private UUID id;

  @Column("created_at")
  private Instant createdAt;

  @Column("updated_at")
  private Instant updatedAt;

  private String title;

  @Column("message_ids")
  private List<UUID> messageIds;

  @Column("model_id")
  private String modelId;
}