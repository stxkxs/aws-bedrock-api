package io.stxkxs.bedrock.repository;

import io.stxkxs.bedrock.model.Conversation;
import java.util.List;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends CassandraRepository<Conversation, UUID> {
  List<Conversation> findBySessionIdOrderByTimestampAsc(UUID sessionId);
}
