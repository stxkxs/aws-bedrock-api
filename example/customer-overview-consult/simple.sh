#!/bin/bash

# This script contains simple questions for a Databricks-like platform analyzing real customer data
# Now with session management for maintaining conversation context

echo "Running Simple Customer Analysis Questions..."
echo -e "\n\n-------------------------------------------\n\n"

# Start a new conversation session
SESSION_ID=""

# Prompt 1: Highest platform usage
echo "Running Question 1: Highest platform usage"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Which customer processes the highest volume of data per month on our platform, and what is their primary use case?"}')

# Extract and display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

# Extract the session ID for subsequent requests
SESSION_ID=$(echo "$RESPONSE" | jq -r '.sessionId')
echo -e "\nSession ID: $SESSION_ID\n"

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 2: Common compliance requirements (using session ID)
echo "Running Question 2: Common compliance requirements"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"What are the most common data compliance requirements across our enterprise customers, and which industries have the most stringent requirements?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 3: Compare subscription tiers (using session ID)
echo "Running Question 3: Subscription tiers analysis"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"What are the different subscription tiers we offer, and how are our enterprise customers distributed across these tiers? Which tier shows the highest growth rate?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 4: Support ticket analysis (using session ID)
echo "Running Question 4: Support ticket analysis"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Which customer has opened the most technical support tickets in the past quarter, and what categories of issues have they most frequently reported?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 5: Cloud provider distribution (using session ID)
echo "Running Question 5: Cloud provider distribution"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"How many of our enterprise customers are deployed on AWS versus Azure or GCP? What is the average monthly compute spend for customers on each cloud platform?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 6: AI/ML adoption (using session ID)
echo "Running Question 6: AI/ML adoption"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Which customers have implemented production machine learning pipelines using our platform, and what specific ML use cases are they pursuing?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 7: Seasonal usage patterns (using session ID)
echo "Running Question 7: Seasonal usage patterns"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Which customers show notable seasonal patterns in their platform usage, and what are the characteristics of these patterns?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 8: Growth potential assessment (using session ID)
echo "Running Question 8: Growth potential assessment"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Based on current usage trends and industry benchmarks, which customer has the highest growth potential for expanding their data analytics workloads on our platform?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 9: Multi-cloud strategy assessment (using session ID)
echo "Running Question 9: Multi-cloud strategy assessment"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Which enterprise customers are implementing a multi-cloud data strategy, and what specific cloud providers are they using for different workloads?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 10: Customer satisfaction analysis (using session ID)
echo "Running Question 10: Customer satisfaction analysis"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Compare the Net Promoter Scores across our enterprise customers. Which industries show the highest satisfaction rates, and which specific customers might need additional attention from our customer success team?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"