package io.stxkxs.bedrock.repository;

import io.stxkxs.bedrock.model.Conversation;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends CassandraRepository<Conversation, UUID> {
  List<Conversation> findBySessionIdOrderByTimestampAsc(UUID sessionId);
}