#!/bin/bash

# This script contains complex questions for a Databricks-like platform analyzing real customer data
# Now with session management for maintaining conversation context

echo "Running Complex Customer Analysis Questions..."
echo -e "\n\n-------------------------------------------\n\n"

# Start a new conversation session
SESSION_ID=""

# Prompt 1: Identify high-value AWS customers with potential for AI/ML integration
echo "Running Prompt 1: High-value AWS customers with AI/ML potential..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Based on our customer data, which healthcare and financial services customers are spending over $75,000 monthly on cloud resources and have shown interest in ML or generative AI technologies? Provide specific examples of their current ML/AI initiatives and recommend personalized next steps for expanding their usage of our advanced analytics platform."}')

# Extract and display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

# Extract the session ID for subsequent requests
SESSION_ID=$(echo "$RESPONSE" | jq -r '.sessionId')
echo -e "\nSession ID: $SESSION_ID\n"

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 2: Cross-cloud customer analysis (using session ID)
echo "Running Prompt 2: Cross-cloud data strategy analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Which of our enterprise customers are using a multi-cloud strategy spanning AWS, Azure, and GCP? Compare their data processing volumes, resource allocation patterns, and cost efficiency metrics. Based on this analysis, identify specific opportunities where our unified data platform could help them optimize their lakehouse architecture and reduce data silos across cloud providers.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 3: Support efficiency and customer satisfaction (using session ID)
echo "Running Prompt 3: Support efficiency and customer health analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Analyze our enterprise support ticket resolution times against customer satisfaction scores and renewal likelihood. Is there a correlation between our response time to critical data pipeline issues and customer health scores? Which customers have experienced the most complex integration challenges? For customers with below-average satisfaction ratings, what specific technical support interactions appear to have negatively impacted their experience?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 4: Industry-specific usage patterns (using session ID)
echo "Running Prompt 4: Industry-specific usage patterns and benchmarks..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Compare data processing workloads and platform usage patterns across different industry verticals in our customer base. How do cluster configurations, job scheduling patterns, and analytics use cases differ between healthcare, financial services, retail, and technology companies? What industry-specific solution accelerators could we develop based on these patterns to increase time-to-value for new customers in these sectors?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 5: Customer churn risk analysis (using session ID)
echo "Running Prompt 5: Customer churn risk analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Based on platform usage metrics, support interactions, and contract history, identify early warning signs of potential churn in our enterprise customer base. Which customers show decreasing active users or declining workload volume? For our transportation technology customer specifically, create a detailed health assessment focusing on their platform adoption challenges and recommend specific technical enablement strategies to improve their time-to-value.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 6: Seasonal workload impact analysis (using session ID)
echo "Running Prompt 6: Seasonal workload optimization strategies..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Several of our customers experience highly seasonal data processing needs. Compare our retail and education sector customers in terms of how their seasonal patterns affect cluster utilization, job concurrency, and support requirements. What proactive recommendations could our customer success team provide to help these customers implement auto-scaling policies, workload scheduling, and cost optimization strategies during their peak and off-peak periods?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 7: Growth opportunity identification (using session ID)
echo "Running Prompt 7: Account expansion opportunities..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Identify the top 3 enterprise customers with the highest expansion potential based on current platform utilization, data volumes, and industry growth trends. What specific upsell opportunities exist for each regarding advanced analytics features, additional workload migrations, or enterprise-wide deployments? For our healthcare customer specifically, create a detailed expansion plan addressing their current ML initiatives, compliance requirements, and potential integration with our new managed MLOps capabilities.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 8: Compliance requirement optimization (using session ID)
echo "Running Prompt 8: Compliance requirement optimization..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Compare the data governance and compliance requirements across our regulated industry customers. Which security and compliance features are most utilized? For our financial services and healthcare customers with the most stringent requirements, what specific deployment patterns or platform configurations could streamline their compliance efforts while enabling more agile data science workflows? How can our enterprise security features be better leveraged to address their specific regulatory challenges?\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 9: Performance optimization across customer tiers (using session ID)
echo "Running Prompt 9: Performance optimization opportunities..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Analyze query performance and job completion times across different customer subscription tiers and deployment sizes. Is there a correlation between cluster configuration decisions and performance metrics? Which customers are experiencing suboptimal performance relative to others in their industry vertical or workload type? For customers with resource-intensive workloads, analyze their current architecture and recommend specific optimizations to improve cost-performance ratios.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 10: Enterprise AI adoption roadmap (using session ID)
echo "Running Prompt 10: Enterprise AI adoption roadmap..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Create a 6-month enterprise AI adoption roadmap for our key accounts across different industry verticals. Based on current data maturity and use cases, identify industry-specific implementation paths, prioritizing customers by ML readiness and business impact potential. Include specific recommendations for our healthcare, retail, and financial services customers transitioning from traditional analytics to production AI/ML, addressing their data governance requirements, skills gaps, and integration needs with existing systems.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"