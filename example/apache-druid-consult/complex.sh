#!/bin/bash

# This script contains 10 deep technical questions to test Apache Druid's capabilities
# Now with session management for maintaining conversation context

echo "Running Apache Druid Questions..."
echo -e "\n\n-------------------------------------------\n\n"

# Start a new conversation session
SESSION_ID=""

# Prompt 1: Real-time ingestion capabilities
echo "Running Question 1: Real-time ingestion with Kafka..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Can you explain how to set up a real-time ingestion pipeline from Kafka to Apache Druid? I need details about supervisor configuration, exactly-once semantics, offset management, and how to ensure my data is immediately queryable upon arrival. Also, what are the performance implications of different batch sizes and segment granularity choices?"}')

# Extract and display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

# Extract the session ID for subsequent requests
SESSION_ID=$(echo "$RESPONSE" | jq -r '.sessionId')
echo -e "\nSession ID: $SESSION_ID\n"

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 2: Complex multi-table join performance (using session ID)
echo "Running Question 2: Multi-table join optimization..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"I need to implement a star schema in Druid with multiple dimension tables. Can you analyze the performance tradeoffs between pre-joining tables during ingestion versus using query-time joins? Specifically, how does Druid handle broadcast hash joins, what are the memory implications, and in what scenarios would each approach be most efficient? Please include examples with SQL syntax for both approaches.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 3: Time-series aggregation strategies (using session ID)
echo "Running Question 3: Time-series aggregation strategies..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"I have time-series data with billions of events per day and need to perform both real-time and historical analysis. What strategies should I use for rollup, retention, and multi-level aggregation in Druid? How do I balance query performance with storage costs when implementing different time-based aggregation techniques? Please provide concrete examples of how to implement this using Druid SQL.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 4: Query optimization for high concurrency (using session ID)
echo "Running Question 4: Query optimization for high concurrency..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Our Druid cluster needs to handle 10,000+ concurrent queries per second with sub-second response times. What architectural and configuration optimizations should we implement? Please explain how Druid's query processing, caching mechanisms, and resource allocation work together. I'm especially interested in understanding how to tune broker/historical/middleManager ratios, memory settings, and query context parameters for maximum performance.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 5: Geo-spatial analytics (using session ID)
echo "Running Question 5: Geo-spatial analytics..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"We need to implement geo-spatial analytics on Apache Druid using latitude/longitude coordinates and polygon regions. What are the optimal approaches for: 1) Storing spatial data in Druid, 2) Implementing point-in-polygon queries, 3) Calculating distances between points, and 4) Creating heat maps or density visualizations? Please provide specific SQL examples and explain any extensions or configurations needed.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 6: Security and multi-tenancy (using session ID)
echo "Running Question 6: Security and multi-tenancy..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"We need to implement a multi-tenant environment with Apache Druid where different clients can access only their own data. Can you explain how to configure authentication, authorization, and row-level security in Druid? What are the best practices for encryption (both in-transit and at-rest), audit logging, and preventing query resource contention between tenants? How does this impact overall cluster performance?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 7: Advanced data modeling (using session ID)
echo "Running Question 7: Advanced data modeling..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"We're trying to model complex event data with nested structures, arrays, and variable schemas in Druid. Can you explain the best approaches for handling: 1) Nested JSON structures, 2) Arrays of objects, 3) Schema evolution over time, and 4) High-cardinality dimensions? For each approach, explain the query performance implications and provide examples of the ingestion spec and corresponding queries.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 8: Integration with machine learning pipelines (using session ID)
echo "Running Question 8: Integration with machine learning pipelines..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"We want to integrate Apache Druid into our machine learning workflow. Can you explain: 1) How to efficiently export training data from Druid to ML systems, 2) How to import ML predictions back into Druid for real-time serving, 3) Techniques for feature engineering within Druid using SQL, and 4) How to implement anomaly detection directly within Druid queries? Please include specific architecture diagrams and code examples where possible.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 9: Retention policies and data lifecycle (using session ID)
echo "Running Question 9: Retention policies and data lifecycle..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"We need to implement sophisticated data retention in our Druid cluster that balances performance, compliance, and storage costs. Can you explain how to: 1) Configure tiered storage for hot/warm/cold data, 2) Implement automatic segment compaction strategies, 3) Set up granular retention rules based on data importance, and 4) Handle GDPR/CCPA deletion requests efficiently? What are the performance implications of each approach?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 10: Cluster scaling and operational monitoring (using session ID)
echo "Running Question 10: Cluster scaling and operational monitoring..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"How should we approach monitoring, scaling, and troubleshooting a production Druid cluster? Please explain: 1) Key metrics we should monitor for early problem detection, 2) How to properly size and scale different node types based on workload patterns, 3) Effective backup and disaster recovery strategies, and 4) Techniques for identifying and resolving slow queries or ingestion bottlenecks. Include examples of specific monitoring dashboards and alerting thresholds.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"