package io.stxkxs.bedrock.repository;

import io.stxkxs.bedrock.model.ConversationSession;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConversationSessionRepository extends CassandraRepository<ConversationSession, UUID> {}