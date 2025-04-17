# aws-bedrock-api

<div align="center">

*A Spring Boot application for interacting with AWS Bedrock models for AI conversations, document embedding, and semantic search*

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![AWS Bedrock](https://img.shields.io/badge/AWS-Bedrock-orange.svg)](https://aws.amazon.com/bedrock/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![OpenTelemetry](https://img.shields.io/badge/OpenTelemetry-Enabled-blueviolet.svg)](https://opentelemetry.io/)
[![Cassandra](https://img.shields.io/badge/Cassandra-Vector%20DB-4DB6AC.svg)](https://cassandra.apache.org/)
[![Grafana](https://img.shields.io/badge/Grafana-Observability-F46800.svg)](https://grafana.com/)

</div>

## Overview

This API provides a streamlined interface to AWS Bedrock services, offering:

- **Conversational AI**: Connect to multiple models including Claude 3.7 Sonnet, Claude 3.5 Haiku, Llama 3, and Amazon's models (Nova Pro, Titan Text)
- **Document Processing**: Upload and embed documents to enable semantic search capabilities
- **Vector Database Integration**: Stores embeddings in Cassandra for efficient similarity search
- **Observability**: Built-in OpenTelemetry integration with Grafana Cloud

## Tech Stack

- **Spring Boot 3.4.4** with Java 21
- **Spring AI** framework with Bedrock integration
- **Apache Cassandra** for vector storage
- **OpenTelemetry** for metrics, tracing, and logging
- **Docker** and Docker Compose for containerization
- **AWS SDK** for Bedrock service integration

## Features

### Conversational AI

- Chat with multiple models through a unified API
- Session-based conversation history
- Request rate limiting (global and per-user)

### Document Processing

- Upload various document formats
- Automatic embedding generation using Titan Embedding models
- Vector storage in Cassandra for similarity search

### Performance and Monitoring

- Configurable timeouts and model parameters
- OpenTelemetry integration with Grafana Cloud
- Health and metrics endpoints for monitoring

## Configuration

### Available Models

The system supports multiple language models:

- `claude-sonnet`: Anthropic Claude 3.7 Sonnet
- `claude-haiku`: Anthropic Claude 3.5 Haiku
- `nova-pro`: Amazon Nova Pro
- `titan-text`: Amazon Titan Text Express
- `llama3`: Meta Llama 3 70B

### Enabling AWS Bedrock Model Access

Before using these models, you need to enable access to them in AWS Bedrock:

1. **Sign in to the AWS Management Console** and navigate to
   the [Amazon Bedrock console](https://console.aws.amazon.com/bedrock/).

2. **Request model access**:
    - In the Bedrock console, select "Model access" from the navigation pane
    - Click "Manage model access"
    - Select the models you want to enable (Anthropic Claude, Meta Llama, etc.)
    - Click "Save changes"
    - Wait for the request to be processed (usually takes a few minutes)

3. **Configure AWS credentials** with appropriate permissions:
    - Ensure your AWS credentials have the `AmazonBedrockFullAccess` policy attached
    - Use these credentials in your application configuration

4. **Region availability**:
    - Note that not all models are available in all AWS regions
    - Check the [AWS Bedrock documentation](https://docs.aws.amazon.com/bedrock/latest/userguide/models-regions.html)
      for the latest region availability
    - Configure your application to use a supported region for your chosen models

### Customization Options

The application can be customized through the following configuration options:

#### Model Configuration

```yaml
spring:
  ai:
    bedrock:
      converse:
        chat:
          options:
            temperature: 0.7    # Controls randomness (0.0-1.0)
            max-tokens: 4096    # Maximum request length
            top-p: 0.9          # Nucleus sampling parameter
```

#### History Retention

```yaml
app:
  conversation:
    history: 30    # conversation messages to use
    documents: 30  # number of documents to use for context
```

## Getting Started

### Prerequisites

- Docker and Docker Compose
- AWS account with Bedrock access
- AWS credentials configured locally
- Grafana Cloud account (for observability)

### Running Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/aws-bedrock-api.git
   cd aws-bedrock-api
   ```

2. Configure AWS credentials:
   Ensure you have AWS credentials set up in `~/.aws/credentials` with access to Bedrock services.

3. Configure Grafana Cloud for OpenTelemetry:
   See the [Grafana Cloud Configuration](#grafana-cloud-configuration) section below for details.

4. Start the application with Docker Compose:
   ```bash
   docker-compose up -d
   ```

   This will start:
    - The Spring Boot application on port 8080
    - Cassandra database on port 9042
    - OpenTelemetry collector

5. Verify the application is running:
   ```bash
   curl http://localhost:8080/api/conversation/models
   ```

### Grafana Cloud Configuration

The application uses Grafana Cloud for observability via OpenTelemetry. To set up this integration:

1. **Create a Grafana Cloud Account**:
   If you don't already have one, sign up at [Grafana Cloud](https://grafana.com/products/cloud/).

2. **Access Your Grafana Cloud Instance**:
   After logging in, navigate to your Grafana Cloud portal.

3. **Get Your OTLP Endpoint**:
    - Go to "Settings" in your Grafana Cloud instance
    - Look for "Data Sources" or "Connections"
    - Find the "OpenTelemetry" or "OTLP" section
    - Copy the OTLP Endpoint URL (it should look like `https://otlp-gateway-prod-us-west-0.grafana.net/otlp`)

4. **Get Your Instance ID**:
    - This can typically be found in your account settings or in the URL of your Grafana dashboard
    - The instance ID is a numeric value (e.g., `123456`)

5. **Create an API Key**:
    - Navigate to "Security" or "API Keys" in your Grafana Cloud settings
    - Click "Add API Key" or "Create API Key"
    - Provide a name (e.g., "otel-collector")
    - Select appropriate permissions (typically "MetricsPublisher" and "TracesPublisher")
    - Click "Create" and copy the generated API key

6. **Update Your `.env` File**:
   Create or modify the `.env` file in your project root with the following values:
   ```
   GRAFANA_CLOUD_OTLP_ENDPOINT="https://otlp-gateway-prod-us-west-0.grafana.net/otlp"
   GRAFANA_CLOUD_INSTANCE_ID="YOUR_INSTANCE_ID"
   GRAFANA_CLOUD_API_KEY="YOUR_API_KEY"
   DEPLOYMENT_ENV=development
   ```

7. **Verify OpenTelemetry Collector Configuration**:
   The `otel-collector-config.yaml` file should be configured to use these environment variables to connect to Grafana
   Cloud.

### Using Example Scripts

The repository includes example scripts to demonstrate API usage:

#### Document Upload

To upload domain-specific documents:

```bash
./example/upload.sh example/{domain}/docs
```

This script will process all documents in the specified domain folder and create embeddings for them in the vector
database.

#### Example Conversations

After uploading documents, you can test simple and complex conversations:

```bash
# Run a simple conversation example
./example/{domain}/simple.sh

# Run a more complex conversation example
./example/{domain}/complex.sh
```

These scripts demonstrate how to:

1. Create a new conversation session
2. Send prompts to the AI models
3. Retrieve and continue conversations
4. Leverage uploaded documents for context-aware responses

## API Endpoints

### Conversation API

- `POST /api/conversation/stream` - Send a message to an AI model
  ```json
  {
    "prompt": "Your question here",
    "sessionId": "uuid-session-identifier",
    "modelId": "claude-sonnet"
  }
  ```

- `GET /api/conversation/models` - Get a list of available models
- `GET /api/conversation/history/{sessionId}` - Get conversation history for a session

### Document API

- `POST /api/document/upload` - Upload and embed a document
    - Multipart form data with `file` and optional `title` parameters

## Environment Configuration

The application uses environment variables for configuration:

- `AWS_PROFILE` - AWS profile to use for Bedrock access
- `AWS_REGION` - AWS region for Bedrock services (default: us-west-2)
- `GRAFANA_CLOUD_OTLP_ENDPOINT` - The endpoint URL for Grafana Cloud OTLP
- `GRAFANA_CLOUD_INSTANCE_ID` - Your Grafana Cloud instance ID
- `GRAFANA_CLOUD_API_KEY` - API key for Grafana Cloud authentication
- `DEPLOYMENT_ENV` - Environment name (development, staging, production)

### AWS Configuration Details

#### AWS Credentials

The application uses AWS credentials to access Bedrock services. You have several options:

1. **AWS Profile** (recommended for development):
    - Create an AWS profile in your `~/.aws/credentials` file with appropriate Bedrock permissions
    - Set the `AWS_PROFILE` environment variable to the profile name
    - The Docker setup mounts your local AWS credentials into the container

2. **IAM Role** (recommended for production):
    - Configure an IAM role with appropriate Bedrock permissions
    - Attach the role to your deployment environment (e.g., EC2, ECS)

3. **Direct Credentials** (not recommended except for testing):
    - Set `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` environment variables
    - Never commit these values to your repository

#### AWS Region Selection

Choose an AWS region that:

1. Supports all the Bedrock models you need to use
2. Is geographically close to your users for lower latency
3. Complies with your data residency requirements

Common regions with good Bedrock model support include:

- `us-west-2` (US West - Oregon)
- `us-east-1` (US East - N. Virginia)

Review the [AWS Bedrock documentation](https://docs.aws.amazon.com/bedrock/latest/userguide/models-regions.html) for the
most current region support information.

## Development

### Building from Source

```bash
mvn clean package
```

### Running Tests

```bash
mvn test
```

### Extending the Application

This API can be extended in several ways:

1. **Additional Document Types**: Implement new document processing strategies
2. **Custom Prompt Templates**: Create domain-specific prompts for better results
3. **Integration with Other Systems**: Add webhooks or event-driven architectures
4. **User Authentication**: Implement authentication and authorization

## Monitoring and Observability

The application exposes metrics and health endpoints:

- `GET /actuator/health` - Application health status
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/prometheus` - Prometheus-formatted metrics

Traces and logs are sent to the configured OpenTelemetry collector, which forwards them to Grafana Cloud. You can access
your application metrics, traces, and logs in your Grafana Cloud dashboard.

## Working with Cassandra

The application uses Apache Cassandra for storing conversation history and vector embeddings. The database runs in a
Docker container as part of the docker-compose setup.

### Database Schema

The Cassandra database uses the following schema:

```cql
-- Keyspace
CREATE KEYSPACE IF NOT EXISTS stxkxs WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

-- Conversations table - stores individual messages
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

-- Conversation sessions table - stores metadata about conversations
CREATE TABLE IF NOT EXISTS conversation_sessions
(
    id          uuid PRIMARY KEY,
    created_at  timestamp,
    updated_at  timestamp,
    title       text,
    message_ids list<uuid>,
    model_id    text
);

-- Vector documents table - stores document embeddings for semantic search
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

-- Vector search index
CREATE CUSTOM INDEX IF NOT EXISTS vector_documents_embedding_idx
    ON vector_documents (embedding)
    USING 'StorageAttachedIndex' WITH OPTIONS = {'similarity_function': 'cosine'};
```

### Accessing Cassandra

You can interact with the Cassandra database using CQL (Cassandra Query Language) through the `cqlsh` utility:

1. **Connect to the Cassandra container**:
   ```bash
   docker exec -it cassandra cqlsh
   ```

2. **Switch to the application keyspace**:
   ```sql
   USE stxkxs;
   ```

3. **View available tables**:
   ```sql
   DESCRIBE TABLES;
   ```

### Querying Data

Here are some useful queries for inspecting your data:

#### View Conversation Sessions

```sql
SELECT id, created_at, title, model_id
FROM conversation_sessions LIMIT 10;
```

#### View Conversation Messages for a Session

```sql
SELECT timestamp, role, content
FROM conversations
WHERE session_id = < session -uuid>
ORDER BY timestamp ASC;
```

Replace `<session-uuid>` with an actual UUID from the conversation_sessions table.

#### View Embedded Documents

```sql
SELECT id, title, created_at
FROM vector_documents LIMIT 10;
```

#### View Document Content

```sql
SELECT id, title, content
FROM vector_documents
WHERE id = '<document-id>';
```

Replace `<document-id>` with an actual document ID from the vector_documents table.

#### Perform Vector Similarity Search

```sql
SELECT id, title
FROM vector_documents
ORDER BY embedding ANN OF [<vector-values>] LIMIT 5;
```

Replace `<vector-values>` with a comma-separated list of 1024 float values.

### Monitoring Database

To check the status of your Cassandra database:

```bash
docker exec cassandra nodetool status
```

### Common Operations

#### Export Data

To export data from a table:

```bash
docker exec -it cassandra cqlsh -e "COPY stxkxs.vector_documents (id, title, created_at) TO 'documents.csv' WITH HEADER=true;"
```

#### Import Data

To import data into a table:

```bash
# First copy the CSV file to the container
docker cp data.csv cassandra:/tmp/data.csv

# Then import the data
docker exec -it cassandra cqlsh -e "COPY stxkxs.vector_documents (id, title, content) FROM '/tmp/data.csv' WITH HEADER=true;"
```

#### Truncate a Table

To clear all data from a table:

```bash
docker exec -it cassandra cqlsh -e "TRUNCATE stxkxs.vector_documents;"
```

### Persistence

The Cassandra data is stored within the Docker container and will persist across container restarts as long as you don't
remove the container. To ensure long-term data persistence, consider modifying the docker-compose.yml to add a volume
mount for Cassandra data.

## License

This project is licensed under the [MIT License](LICENSE).

For your convenience, you can find the full MIT License text at:

- [https://opensource.org/license/mit/](https://opensource.org/license/mit/) (Official OSI website)
- [https://choosealicense.com/licenses/mit/](https://choosealicense.com/licenses/mit/) (Choose a License website)

To apply this license to your project, create a file named `LICENSE` in your project's root directory with the MIT
License text, replacing `[year]` with the current year and `[fullname]` with your name or organization.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.