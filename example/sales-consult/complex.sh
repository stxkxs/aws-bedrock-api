#!/bin/bash

# This script contains complex questions for sales data analysis
# Uses session management for maintaining conversation context

echo "Running Complex Sales Analysis Questions..."
echo -e "\n\n-------------------------------------------\n\n"

# Start a new conversation session
SESSION_ID=""

# Prompt 1: Time series decomposition
echo "Running Prompt 1: Time Series Decomposition Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d '{"prompt":"Using monthly_sales.csv, perform a detailed time series decomposition to separate trend, seasonal, and residual components. Identify which products show the strongest growth trends and which are most affected by seasonality. Create visualizations showing these components, including trend components by product, seasonal patterns by product, residual analysis to identify anomalies, and growth rate comparison across products."}')

# Extract and display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

# Extract the session ID for subsequent requests
SESSION_ID=$(echo "$RESPONSE" | jq -r '.sessionId')
echo -e "\nSession ID: $SESSION_ID\n"

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 2: Product B anomaly analysis
echo "Running Prompt 2: Product B Anomaly Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using monthly_sales.csv, analyze the spike in Product B sales in the West region during July 2023. How statistically significant was this anomaly? What was its impact on overall regional performance and company-wide sales? Create visualizations that highlight this anomaly in context, including statistical significance calculation, before/after impact analysis, and company-wide impact assessment.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 3: Correlation analysis
echo "Running Prompt 3: Multi-factor Correlation Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using monthly_sales.csv, conduct a correlation analysis between sales, units sold, margins, products, regions, and time periods. Identify which factors most strongly influence overall performance. Visualize these relationships using correlation matrices and other appropriate charts, including key factor identification, scatter plots of important relationships, and statistical significance of correlations.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 4: Economic downturn impact
echo "Running Prompt 4: Economic Downturn Impact Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using monthly_sales.csv, model the impact of the economic downturn that began in October 2023. Calculate the elasticity of demand for each product during this period. Which products were most resistant to the downturn? How effective was the recovery starting in February 2024? Create visualizations showing performance relative to expected trends, including price elasticity calculations by product, resistance/vulnerability rankings, recovery effectiveness metrics, and counterfactual analysis.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 5: Advanced forecasting
echo "Running Prompt 5: Advanced Sales Forecasting..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using monthly_sales.csv and quarterly_sales.csv, develop a sophisticated forecasting model for each product's sales over the next 12 months. Include prediction intervals to show uncertainty. How do forecasts change if we account for seasonal patterns, growth trends, and the residual patterns from our historical data? Visualize these forecasts with prediction intervals showing uncertainty, multiple scenario forecasts, and interactive forecast visualizations.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 6: Product-region interaction analysis
echo "Running Prompt 6: Product-Region Interaction Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using monthly_sales.csv, perform an analysis of variance (ANOVA) to determine how product performance varies by region, and whether there are significant interaction effects between product types and regional factors. Which product-region combinations show the strongest positive interactions? Visualize these interactions with ANOVA results table, interaction effect calculations, heat map of product-region performance, and opportunity matrix for underperforming combinations.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 7: Margin optimization
echo "Running Prompt 7: Margin Optimization Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using monthly_sales.csv, create a model that shows the relationship between sales volume and profit margins for each product. Is there evidence of price elasticity affecting our margins? Where is the optimal balance point between volume and margin for each product? Visualize these relationships and identify optimization opportunities including volume-margin curves by product, elasticity calculations, optimal price point recommendations, and revenue/profit maximization scenarios.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 8: Product mix evolution
echo "Running Prompt 8: Product Mix Evolution Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using monthly_sales.csv, analyze how our product mix has evolved over time. Which products are gaining or losing share? Create a visualization that shows the changing composition of our product portfolio's contribution to total sales over time, including stacked area charts showing evolving product mix, share gain/loss metrics, trend analysis for each product's contribution, and future mix projections.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 9: Advanced anomaly detection
echo "Running Prompt 9: Advanced Anomaly Detection..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using monthly_sales.csv, implement an unsupervised anomaly detection algorithm to identify unusual patterns in our sales data that may not be obvious through visual inspection. Consider using isolation forests, DBSCAN, or autoencoders. Visualize and explain the anomalies detected and their potential business impact, including machine learning model explanation, visualization of detected anomalies, business impact assessment, and root cause hypotheses.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"
sleep 2

# Prompt 10: Sales driver regression analysis
echo "Running Prompt 10: Sales Driver Regression Analysis..."
RESPONSE=$(curl -s -N -X POST http://localhost:8080/api/conversation/stream \
  -H "Content-Type: application/json" \
  -d "{\"prompt\":\"Using all available data files, build a regression model to identify the key drivers of sales performance. Consider factors like seasonality, product type, region, pricing strategy, and external economic events. Quantify the relative importance of each factor. Visualize how these drivers interact to influence overall performance, including regression model results, feature importance rankings, interactive driver visualization, and actionable insights for leveraging key drivers.\",\"sessionId\":\"$SESSION_ID\"}")

# Display the AI response text
echo "$RESPONSE" | jq -r '.chatResponse.result.output.text'

echo -e "\n\n-------------------------------------------\n\n"