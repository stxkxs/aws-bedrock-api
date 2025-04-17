#!/bin/bash

# This script contains simple questions for sales data analysis
# Uses session management for maintaining conversation context

echo "Running Simple Sales Analysis Questions..."
echo -e "\n\n-------------------------------------------\n\n"

# Start a new conversation session
SESSION_ID=""

# Prompt 1: Total sales by product
echo "Running Question 1: Total sales by product"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Based on the monthly_sales.csv data, what is the total sales amount by product over the entire period? Which product generated the most revenue and which had the lowest?"}')

# Extract and display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

# Extract the session ID for subsequent requests
SESSION_ID=$(echo "$RESPONSE" | jq -r '.sessionId')
echo -e "\nSession ID: $SESSION_ID\n"

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 2: Regional performance
echo "Running Question 2: Regional performance comparison"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using the regional_performance.csv data, which region had the highest sales, and how did it compare to other regions? Create a visualization showing the sales distribution by region.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 3: Quarterly trends
echo "Running Question 3: Quarterly sales trends"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Analyze quarterly_sales.csv to identify how quarterly sales changed over time. Show a visualization of the trend and explain any notable patterns or anomalies.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 4: Product comparison
echo "Running Question 4: Product A and B comparison"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using product_comparison.csv, compare the performance of Product A and Product B. Which one shows better growth? Create a visualization showing their sales trends over time.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 5: Seasonal patterns
echo "Running Question 5: Seasonal sales patterns"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using monthly_sales.csv, identify if there's a seasonal pattern in our sales data. Which months typically have the highest sales? Create a visualization showing monthly sales patterns.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 6: Year over year comparison
echo "Running Question 6: Year-over-year sales comparison"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Compare sales in 2023 to sales in 2022 using monthly_sales.csv. Calculate the percentage change for each product and create a visualization showing year-over-year growth by product.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 7: Profit margin analysis
echo "Running Question 7: Profit margin analysis"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Analyze monthly_sales.csv to determine which product has the highest profit margin percentage. Create a visualization comparing margins across products.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 8: Product performance by region
echo "Running Question 8: Regional product performance"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using monthly_sales.csv, create a breakdown of how each product performs in each region. Visualize this using an appropriate chart type to show regional product performance clearly.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 9: Monthly sales trend
echo "Running Question 9: Monthly sales trend visualization"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Create a visualization showing the monthly sales trend over the entire period using monthly_sales.csv. Highlight any significant events or anomalies in the data.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 10: Simple forecast
echo "Running Question 10: Sales forecast for next quarter"
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Based on historical data in quarterly_sales.csv, create a simple forecast for each product for the next quarter. Visualize the historical data and the forecast in a line chart.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"