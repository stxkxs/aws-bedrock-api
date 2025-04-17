CREATE KEYSPACE IF NOT EXISTS stxkxs WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE stxkxs;

CREATE TABLE IF NOT EXISTS conversations
(
    id         uuid,
    session_id uuid,
    parent_id  uuid,
    timestamp  timestamp,
    role       text,
    content    text,
    PRIMARY KEY (session_id, timestamp, id)
) WITH CLUSTERING ORDER BY (timestamp ASC, id ASC);

CREATE TABLE IF NOT EXISTS conversation_sessions
(
    id          uuid PRIMARY KEY,
    created_at  timestamp,
    updated_at  timestamp,
    title       text,
    message_ids list<uuid>,
    model_id    text
);

CREATE TABLE IF NOT EXISTS vector_documents
(
    id          text PRIMARY KEY,
    title       text,
    content     text,
    embedding   VECTOR<FLOAT, 1024>,
    created_at  timestamp,
    updated_at  timestamp,
    source_type text,
    metadata    text
);

CREATE CUSTOM INDEX IF NOT EXISTS vector_documents_embedding_idx
    ON vector_documents (embedding)
    USING 'StorageAttachedIndex' WITH OPTIONS = {'similarity_function': 'cosine'};