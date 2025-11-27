package io.stxkxs.bedrock.model;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@Builder
@Table("conversations")
@AllArgsConstructor
public class Conversation {
  @PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
  private UUID id;

  @PrimaryKeyColumn(name = "session_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  private UUID sessionId;

  @PrimaryKeyColumn(
      name = "timestamp",
      ordinal = 1,
      type = PrimaryKeyType.CLUSTERED,
      ordering = Ordering.ASCENDING)
  private Instant timestamp;

  @Column("parent_id")
  private UUID parentId;

  private String role;
  private String content;
}
