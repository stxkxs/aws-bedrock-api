#!/bin/bash

# This script demonstrates engineering project success analysis capabilities on a RAG platform
# Now with session management for maintaining conversation context

echo "Running Engineering Project Success Analysis Demonstration..."
echo -e "\n\n-------------------------------------------\n\n"

# Start a new conversation session
SESSION_ID=""

# Prompt 1: Cross-team project impact analysis
echo "Running Prompt 1: Cross-team Project Impact Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Analyze our engineering teams' most successful projects across different domains. Which projects have delivered the highest quantifiable business impact for our customers, and what common success factors can we identify across teams? Consider both direct customer value metrics and internal efficiency improvements.\"}")

# Extract and display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

# Extract the session ID for subsequent requests
SESSION_ID=$(echo "$RESPONSE" | jq -r '.sessionId')
echo -e "\nSession ID: $SESSION_ID\n"

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 2: Customer-specific technology recommendation (using session ID)
echo "Running Prompt 2: Customer-specific Technology Recommendation..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Based on our engineering project history and Mayo Clinic's specific usage patterns, what technology recommendations should we make for their upcoming expansion into personalized medicine analytics? Consider their current infrastructure, compliance requirements, and the results of similar projects we've implemented for healthcare organizations.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 3: Implementation timeline and resource planning (using session ID)
echo "Running Prompt 3: Implementation Timeline Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Generate a realistic implementation timeline and resource plan for Goldman Sachs' upcoming real-time risk analytics project. Based on our engineering project history with similar financial services customers, estimate the time required for each phase, identify potential bottlenecks, and recommend an optimal team composition.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 4: Cross-team technology integration analysis (using session ID)
echo "Running Prompt 4: Cross-team Technology Integration Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Our most successful enterprise customers are leveraging integrations between our different platform capabilities. Based on our engineering project outcomes across teams, identify the most valuable integration patterns between our data pipeline, MLOps, and governance capabilities. What specific technical implementations have delivered the highest business impact, and which customers should we target for similar integrated solutions?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 5: ROI prediction model for prospective customers (using session ID)
echo "Running Prompt 5: ROI Prediction Model for Prospects..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using our historical engineering project data and customer success metrics, create a model to predict potential ROI for prospective customers in the e-commerce sector. What key indicators from our past implementations correlate most strongly with successful outcomes? Develop a framework our sales team can use to estimate potential time-to-value, cost savings, and revenue impact for different customer profiles.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 6: Technical capability gap analysis (using session ID)
echo "Running Prompt 6: Technical Capability Gap Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Based on our engineering teams' project outcomes and customer feedback, identify the top 3 technical capability gaps in our current platform. For each gap, analyze which customers are most affected, what workarounds they're currently using, and what engineering investments would deliver the highest customer impact. Prioritize these opportunities based on potential revenue impact and technical feasibility.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 7: Success pattern analysis (using session ID)
echo "Running Prompt 7: Customer Implementation Success Pattern Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Analyze the implementation patterns across our most successful customer deployments. What technical approaches, team structures, and implementation sequences correlate most strongly with rapid time-to-value and high customer satisfaction? Compare implementations across different industry verticals and identify both universal success factors and industry-specific best practices we should recommend to new customers.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 8: Project risk analysis and mitigation (using session ID)
echo "Running Prompt 8: Project Risk Analysis and Mitigation Strategies..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Based on our engineering project history, identify the most common implementation risks and their impact on project timelines and customer satisfaction. For each risk category, analyze which mitigation strategies have been most effective, and develop a proactive risk assessment framework our Customer Success team can use during implementation planning. Provide specific examples from our project history where early risk identification prevented significant issues.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 9: Technology adoption acceleration (using session ID)
echo "Running Prompt 9: Technology Adoption Acceleration Strategies..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Our Customer Success team reports that technology adoption within customer organizations is a key challenge affecting time-to-value. Based on our engineering project outcomes and customer feedback, identify which implementation approaches, technical architectures, and enablement strategies have most effectively accelerated adoption across different customer types. Create specific recommendations for accelerating adoption of our most complex features, particularly our advanced ML capabilities.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 10: Future innovation roadmap (using session ID)
echo "Running Prompt 10: Future Innovation Roadmap Based on Project Outcomes..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Looking ahead to the next 18 months, what technical innovations should our engineering teams prioritize based on our project history, customer feedback, and emerging market trends? For each recommended focus area, analyze which customer segments would benefit most, what implementation approach would deliver value fastest, and how we should measure success. Create a phased roadmap that balances quick wins with transformative longer-term initiatives.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"