#!/bin/bash

# This script contains 10 simple questions about Apache Druid for testing
# Now with session management for maintaining conversation context

echo "Running Simple Apache Druid Questions..."
echo -e "\n\n-------------------------------------------\n\n"

# Start a new conversation session
SESSION_ID=""

# Prompt 1: Basic overview
echo "Running Question 1: What is Apache Druid?"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d '{"prompt":"What is Apache Druid and what are its main use cases?"}')

# Extract and display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

# Extract the session ID for subsequent requests
SESSION_ID=$(echo "$RESPONSE" | jq -r '.sessionId')
echo -e "\nSession ID: $SESSION_ID\n"

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 2: Architecture (using session ID)
echo "Running Question 2: Basic architecture"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Describe the basic components of Apache Druid's architecture.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 3: Data ingestion (using session ID)
echo "Running Question 3: Data ingestion"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"What are the different ways to ingest data into Apache Druid?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 4: SQL query (using session ID)
echo "Running Question 4: SQL queries"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Show me an example of a simple SQL query in Apache Druid and explain how it works.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 5: Segments (using session ID)
echo "Running Question 5: Segments in Druid"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"What are segments in Apache Druid and why are they important?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 6: Rollup (using session ID)
echo "Running Question 6: Data rollup"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"What is data rollup in Apache Druid and when would you use it?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 7: Kafka integration (using session ID)
echo "Running Question 7: Kafka integration"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"How do you connect Apache Druid to Kafka for streaming data?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 8: Lookups (using session ID)
echo "Running Question 8: Lookups"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"What are lookups in Apache Druid and how do you use them?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 9: Performance tuning (using session ID)
echo "Running Question 9: Performance tuning"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"What are three basic ways to improve query performance in Apache Druid?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 10: Use case example (using session ID)
echo "Running Question 10: Real-world use case"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Give an example of a real-world use case for Apache Druid and why it's a good fit.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"