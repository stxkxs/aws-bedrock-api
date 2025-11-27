package io.stxkxs.bedrock.repository;

import io.stxkxs.bedrock.model.ConversationSession;
import java.util.UUID;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationSessionRepository
    extends CassandraRepository<ConversationSession, UUID> {}
