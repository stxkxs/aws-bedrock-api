#!/bin/zsh

# Script to generate synthetic sales data CSV files for zsh
# Creates monthly, quarterly, product comparison, and regional sales data

# Create docs directory if it doesn't exist
mkdir -p docs

echo "Generating synthetic sales data..."

# Variables for data generation
START_YEAR=2022
NUM_YEARS=3
products=("Product A" "Product B" "Product C" "Product D")
regions=("North" "South" "East" "West")

# Base values for each product
PRODUCT_A_BASE=50000
PRODUCT_B_BASE=75000
PRODUCT_C_BASE=30000
PRODUCT_D_BASE=20000

# Growth rates for each product (annual)
PRODUCT_A_GROWTH=0.15
PRODUCT_B_GROWTH=0.05
PRODUCT_C_GROWTH=-0.02
PRODUCT_D_GROWTH=0.25

# Seasonality factors for each product
PRODUCT_A_SEASONALITY=0.3
PRODUCT_B_SEASONALITY=0.1
PRODUCT_C_SEASONALITY=0.5
PRODUCT_D_SEASONALITY=0.2

# Regional factors
NORTH_FACTOR=1.1
SOUTH_FACTOR=0.9
EAST_FACTOR=1.0
WEST_FACTOR=1.2

# Create CSV headers
echo "date,month,year,product,region,sales,units,margin" > docs/monthly_sales.csv
echo "year,quarter,product,region,sales,units,margin" > docs/quarterly_sales.csv
echo "year,month,product,sales,units,margin" > docs/product_comparison.csv
echo "year,region,sales,units,margin" > docs/regional_performance.csv

# Create temporary files to store aggregated data
TEMP_QUARTERLY=$(mktemp)
TEMP_PRODUCT=$(mktemp)
TEMP_REGIONAL=$(mktemp)

# Generate monthly data
for ((year=START_YEAR; year<START_YEAR+NUM_YEARS; year++)); do
    for ((month=1; month<=12; month++)); do
        # Calculate month index (0-35 for 3 years)
        month_index=$(( (year-START_YEAR)*12 + month - 1 ))

        # Determine quarter
        quarter=$(( (month-1)/3 + 1 ))

        # Format date as YYYY-MM
        if [ $month -lt 10 ]; then
            date_str="${year}-0${month}"
        else
            date_str="${year}-${month}"
        fi

        # Special events
        event_factor=1.0

        # Holiday season boost (December)
        if [ $month -eq 12 ]; then
            event_factor=1.2
        fi

        # New product launch (March 2023)
        if [ $year -eq 2023 ] && [ $month -eq 3 ]; then
            # Only applies to Product D
            product_d_event_factor=1.5
        else
            product_d_event_factor=1.0
        fi

        # Economic downturn (Oct-Dec 2023)
        if [ $year -eq 2023 ] && [ $month -eq 10 ]; then
            event_factor=0.85
        elif [ $year -eq 2023 ] && [ $month -eq 11 ]; then
            event_factor=0.8
        elif [ $year -eq 2023 ] && [ $month -eq 12 ]; then
            event_factor=0.75
        fi

        # Recovery (Feb-Apr 2024)
        if [ $year -eq 2024 ] && [ $month -eq 2 ]; then
            event_factor=0.9
        elif [ $year -eq 2024 ] && [ $month -eq 3 ]; then
            event_factor=0.95
        elif [ $year -eq 2024 ] && [ $month -eq 4 ]; then
            event_factor=1.0
        fi

        # Generate data for each product and region
        for product in "${products[@]}"; do
            # Set product-specific parameters
            this_event_factor=$event_factor  # Default

            case "$product" in
                "Product A")
                    base_value=$PRODUCT_A_BASE
                    seasonality=$PRODUCT_A_SEASONALITY
                    growth=$PRODUCT_A_GROWTH
                    ;;
                "Product B")
                    base_value=$PRODUCT_B_BASE
                    seasonality=$PRODUCT_B_SEASONALITY
                    growth=$PRODUCT_B_GROWTH
                    ;;
                "Product C")
                    base_value=$PRODUCT_C_BASE
                    seasonality=$PRODUCT_C_SEASONALITY
                    growth=$PRODUCT_C_GROWTH
                    ;;
                "Product D")
                    base_value=$PRODUCT_D_BASE
                    seasonality=$PRODUCT_D_SEASONALITY
                    growth=$PRODUCT_D_GROWTH
                    # Apply special event for Product D launch
                    if [ $year -eq 2023 ] && [ $month -eq 3 ]; then
                        this_event_factor=$product_d_event_factor
                    fi
                    ;;
            esac

            # Calculate growth factor based on months since start
            growth_factor=$(echo "scale=4; 1 + ($growth * $month_index / 12)" | bc)

            # Calculate seasonal factor (higher in Q4, lower in Q1)
            # Using simplified approach for seasonality with some randomness
            case $month in
                1|2|3) seasonal_factor=$(echo "scale=4; 1 - $seasonality * 0.5 + (($RANDOM % 100) / 1000)" | bc);;
                4|5|6) seasonal_factor=$(echo "scale=4; 1 - $seasonality * 0.1 + (($RANDOM % 100) / 1000)" | bc);;
                7|8|9) seasonal_factor=$(echo "scale=4; 1 + $seasonality * 0.2 + (($RANDOM % 100) / 1000)" | bc);;
                10|11|12) seasonal_factor=$(echo "scale=4; 1 + $seasonality * 0.6 + (($RANDOM % 100) / 1000)" | bc);;
            esac

            for region in "${regions[@]}"; do
                # Set region-specific factor
                case "$region" in
                    "North") regional_factor=$NORTH_FACTOR;;
                    "South") regional_factor=$SOUTH_FACTOR;;
                    "East") regional_factor=$EAST_FACTOR;;
                    "West") regional_factor=$WEST_FACTOR;;
                esac

                # Add volatility (random variation between 0.9 and 1.1)
                volatility_factor=$(echo "scale=4; 0.9 + (($RANDOM % 20) / 100)" | bc)

                # Calculate final sales value - need to be careful with floating point in shell
                sales_value=$(echo "scale=0; $base_value * $seasonal_factor * $growth_factor * $volatility_factor * $this_event_factor * $regional_factor / 1" | bc)

                # Add specific anomalies

                # Unexpected spike for Product B in West region in July 2023
                if [ "$product" = "Product B" ] && [ "$region" = "West" ] && [ $year -eq 2023 ] && [ $month -eq 7 ]; then
                    sales_value=$(echo "scale=0; $sales_value * 2.5 / 1" | bc)
                fi

                # Supply chain issues for Product A in all regions in September 2023
                if [ "$product" = "Product A" ] && [ $year -eq 2023 ] && [ $month -eq 9 ]; then
                    sales_value=$(echo "scale=0; $sales_value * 0.6 / 1" | bc)
                fi

                # Marketing campaign for Product C in East region in May 2024
                if [ "$product" = "Product C" ] && [ "$region" = "East" ] && [ $year -eq 2024 ] && [ $month -eq 5 ]; then
                    sales_value=$(echo "scale=0; $sales_value * 1.8 / 1" | bc)
                fi

                # Calculate units approximately (scaled by base value)
                units=$(echo "scale=0; $sales_value / ($base_value / 100) / 1" | bc)

                # Calculate margin (random between 20-40% of sales)
                margin_pct=$(echo "scale=4; 0.2 + (($RANDOM % 20) / 100)" | bc)
                margin=$(echo "scale=0; $sales_value * $margin_pct / 1" | bc)

                # Write to monthly sales CSV
                echo "$date_str,$month,$year,$product,$region,$sales_value,$units,$margin" >> docs/monthly_sales.csv

                # Write to aggregation files - these will be processed with awk later
                # Quarterly data
                echo "${year},${quarter},${product},${region},${sales_value},${units},${margin}" >> "$TEMP_QUARTERLY"

                # Product comparison data
                echo "${year},${month},${product},${sales_value},${units},${margin}" >> "$TEMP_PRODUCT"

                # Regional yearly data
                echo "${year},${region},${sales_value},${units},${margin}" >> "$TEMP_REGIONAL"
            done
        done
    done
done

# Generate monthly data
for ((year=START_YEAR; year<START_YEAR+NUM_YEARS; year++)); do
    for ((month=1; month<=12; month++)); do
        # Calculate month index (0-35 for 3 years)
        month_index=$(( (year-START_YEAR)*12 + month - 1 ))

        # Determine quarter
        quarter=$(( (month-1)/3 + 1 ))

        # Format date as YYYY-MM
        if [ $month -lt 10 ]; then
            date_str="${year}-0${month}"
        else
            date_str="${year}-${month}"
        fi

        # Special events
        event_factor=1.0

        # Holiday season boost (December)
        if [ $month -eq 12 ]; then
            event_factor=1.2
        fi

        # New product launch (March 2023)
        if [ $year -eq 2023 ] && [ $month -eq 3 ]; then
            # Only applies to Product D
            product_d_event_factor=1.5
        else
            product_d_event_factor=1.0
        fi

        # Economic downturn (Oct-Dec 2023)
        if [ $year -eq 2023 ] && [ $month -eq 10 ]; then
            event_factor=0.85
        elif [ $year -eq 2023 ] && [ $month -eq 11 ]; then
            event_factor=0.8
        elif [ $year -eq 2023 ] && [ $month -eq 12 ]; then
            event_factor=0.75
        fi

        # Recovery (Feb-Apr 2024)
        if [ $year -eq 2024 ] && [ $month -eq 2 ]; then
            event_factor=0.9
        elif [ $year -eq 2024 ] && [ $month -eq 3 ]; then
            event_factor=0.95
        elif [ $year -eq 2024 ] && [ $month -eq 4 ]; then
            event_factor=1.0
        fi

        # Generate data for each product and region
        for product in "${PRODUCTS[@]}"; do
            # Set product-specific parameters
            case "$product" in
                "Product A")
                    base_value=$PRODUCT_A_BASE
                    seasonality=$PRODUCT_A_SEASONALITY
                    growth=$PRODUCT_A_GROWTH
                    ;;
                "Product B")
                    base_value=$PRODUCT_B_BASE
                    seasonality=$PRODUCT_B_SEASONALITY
                    growth=$PRODUCT_B_GROWTH
                    ;;
                "Product C")
                    base_value=$PRODUCT_C_BASE
                    seasonality=$PRODUCT_C_SEASONALITY
                    growth=$PRODUCT_C_GROWTH
                    ;;
                "Product D")
                    base_value=$PRODUCT_D_BASE
                    seasonality=$PRODUCT_D_SEASONALITY
                    growth=$PRODUCT_D_GROWTH
                    # Apply special event for Product D launch
                    if [ $year -eq 2023 ] && [ $month -eq 3 ]; then
                        this_event_factor=$product_d_event_factor
                    else
                        this_event_factor=$event_factor
                    fi
                    ;;
            esac

            # Calculate growth factor based on months since start
            growth_factor=$(echo "scale=4; 1 + ($growth * $month_index / 12)" | bc)

            # Calculate seasonal factor (higher in Q4, lower in Q1)
            # Using simplified approach for seasonality with some randomness
            case $month in
                1|2|3) seasonal_factor=$(echo "scale=4; 1 - $seasonality * 0.5 + (($RANDOM % 100) / 1000)" | bc);;
                4|5|6) seasonal_factor=$(echo "scale=4; 1 - $seasonality * 0.1 + (($RANDOM % 100) / 1000)" | bc);;
                7|8|9) seasonal_factor=$(echo "scale=4; 1 + $seasonality * 0.2 + (($RANDOM % 100) / 1000)" | bc);;
                10|11|12) seasonal_factor=$(echo "scale=4; 1 + $seasonality * 0.6 + (($RANDOM % 100) / 1000)" | bc);;
            esac

            for region in "${REGIONS[@]}"; do
                # Set region-specific factor
                case "$region" in
                    "North") regional_factor=$NORTH_FACTOR;;
                    "South") regional_factor=$SOUTH_FACTOR;;
                    "East") regional_factor=$EAST_FACTOR;;
                    "West") regional_factor=$WEST_FACTOR;;
                esac

                # Add volatility (random variation between 0.9 and 1.1)
                volatility_factor=$(echo "scale=4; 0.9 + (($RANDOM % 20) / 100)" | bc)

                # Calculate final sales value
                sales_value=$(echo "scale=0; $base_value * $seasonal_factor * $growth_factor * $volatility_factor * $this_event_factor * $regional_factor / 1" | bc)

                # Add specific anomalies

                # Unexpected spike for Product B in West region in July 2023
                if [ "$product" = "Product B" ] && [ "$region" = "West" ] && [ $year -eq 2023 ] && [ $month -eq 7 ]; then
                    sales_value=$(echo "scale=0; $sales_value * 2.5 / 1" | bc)
                fi

                # Supply chain issues for Product A in all regions in September 2023
                if [ "$product" = "Product A" ] && [ $year -eq 2023 ] && [ $month -eq 9 ]; then
                    sales_value=$(echo "scale=0; $sales_value * 0.6 / 1" | bc)
                fi

                # Marketing campaign for Product C in East region in May 2024
                if [ "$product" = "Product C" ] && [ "$region" = "East" ] && [ $year -eq 2024 ] && [ $month -eq 5 ]; then
                    sales_value=$(echo "scale=0; $sales_value * 1.8 / 1" | bc)
                fi

                # Calculate units approximately (scaled by base value)
                units=$(echo "scale=0; $sales_value / ($base_value / 100) / 1" | bc)

                # Calculate margin (random between 20-40% of sales)
                margin_pct=$(echo "scale=4; 0.2 + (($RANDOM % 20) / 100)" | bc)
                margin=$(echo "scale=0; $sales_value * $margin_pct / 1" | bc)

                # Write to monthly sales CSV
                echo "$date_str,$month,$year,$product,$region,$sales_value,$units,$margin" >> docs/monthly_sales.csv

                # Write to aggregation files
                # Quarterly data
                echo "${year},${quarter},${product},${region},${sales_value},${units},${margin}" >> "$TEMP_QUARTERLY"

                # Product comparison data
                echo "${year},${month},${product},${sales_value},${units},${margin}" >> "$TEMP_PRODUCT"

                # Regional yearly data
                echo "${year},${region},${sales_value},${units},${margin}" >> "$TEMP_REGIONAL"
            done
        done
    done
done

# Process the temporary files to create aggregated data with awk

# Process quarterly data - aggregate by year, quarter, product, region
awk -F, '{
    key=$1","$2","$3","$4
    if (!(key in sales)) {
        keys[key_count++] = key;
        sales[key] = 0;
        units[key] = 0;
        margin[key] = 0;
    }
    sales[key] += $5;
    units[key] += $6;
    margin[key] += $7;
}
END {
    for (i = 0; i < key_count; i++) {
        key = keys[i];
        print key","sales[key]","units[key]","margin[key];
    }
}' "$TEMP_QUARTERLY" >> docs/quarterly_sales.csv

# Process product comparison data - aggregate by year, month, product
awk -F, '{
    key=$1","$2","$3
    if (!(key in sales)) {
        keys[key_count++] = key;
        sales[key] = 0;
        units[key] = 0;
        margin[key] = 0;
    }
    sales[key] += $4;
    units[key] += $5;
    margin[key] += $6;
}
END {
    for (i = 0; i < key_count; i++) {
        key = keys[i];
        print key","sales[key]","units[key]","margin[key];
    }
}' "$TEMP_PRODUCT" >> docs/product_comparison.csv

# Process regional yearly data - aggregate by year, region
awk -F, '{
    key=$1","$2
    if (!(key in sales)) {
        keys[key_count++] = key;
        sales[key] = 0;
        units[key] = 0;
        margin[key] = 0;
    }
    sales[key] += $3;
    units[key] += $4;
    margin[key] += $5;
}
END {
    for (i = 0; i < key_count; i++) {
        key = keys[i];
        print key","sales[key]","units[key]","margin[key];
    }
}' "$TEMP_REGIONAL" >> docs/regional_performance.csv

# Clean up temporary files
rm "$TEMP_QUARTERLY" "$TEMP_PRODUCT" "$TEMP_REGIONAL"

echo "Data generation complete. The following files have been created:"
echo " - docs/monthly_sales.csv: $(wc -l < docs/monthly_sales.csv) rows"
echo " - docs/quarterly_sales.csv: $(wc -l < docs/quarterly_sales.csv) rows"
echo " - docs/product_comparison.csv: $(wc -l < docs/product_comparison.csv) rows"
echo " - docs/regional_performance.csv: $(wc -l < docs/regional_performance.csv) rows"

echo "Data generation complete. The following files have been created:"
echo " - docs/monthly_sales.csv: $(wc -l < docs/monthly_sales.csv) rows"
echo " - docs/quarterly_sales.csv: $(wc -l < docs/quarterly_sales.csv) rows"
echo " - docs/product_comparison.csv: $(wc -l < docs/product_comparison.csv) rows"
echo " - docs/regional_performance.csv: $(wc -l < docs/regional_performance.csv) rows"