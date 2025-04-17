#!/bin/bash

# Script to recursively find and upload all files to an API endpoint
# Usage: ./upload_files.sh <root_directory>

# Enable debug mode for verbose output
set -x

# Check if a directory was provided
if [ $# -ne 1 ]; then
    echo "Usage: $0 <root_directory>"
    exit 1
fi

ROOT_DIR="$1"

# Check if the provided directory exists
if [ ! -d "$ROOT_DIR" ]; then
    echo "Error: Directory '$ROOT_DIR' does not exist."
    exit 1
fi

# API endpoint
API_ENDPOINT="http://localhost:8080/api/document/upload"

# Function to upload a file
upload_file() {
    local file_path="$1"
    local file_name=$(basename "$file_path")

    echo "--------------------------------"
    echo "Uploading: $file_path"
    echo "Using title: $file_name"
    echo "--------------------------------"

    # Execute the curl command with verbose option
    curl -v -X POST "$API_ENDPOINT" \
      -H "Content-Type: multipart/form-data" \
      -F "file=@$file_path" \
      -F "title=$file_name"

    # Capture the exit code
    local status=$?
    if [ $status -ne 0 ]; then
        echo "ERROR: Upload failed with status $status for file: $file_path"
    else
        echo "SUCCESS: Uploaded file: $file_path"
    fi

    # Add a newline for better readability in the output
    echo -e "\n"
}

# Function to process directories recursively
process_directory() {
    local current_dir="$1"

    echo "=========================================="
    echo "PROCESSING DIRECTORY: $current_dir"
    echo "=========================================="

    # First, process all files in the current directory
    echo "Looking for files in: $current_dir"
    file_count=0

    # Get files in the current directory
    files=("$current_dir"/*)

    # Process each file sequentially
    for file in "${files[@]}"; do
        # Skip if it's not a file
        if [ ! -f "$file" ]; then
            continue
        fi

        file_count=$((file_count + 1))
        echo "Found file ($file_count): $file"
        upload_file "$file"

        # Add a small delay between uploads (optional)
        sleep 1
    done

    if [ $file_count -eq 0 ]; then
        echo "No files found in this directory."
    else
        echo "Processed $file_count files in $current_dir"
    fi

    # Then, process all subdirectories
    echo "Looking for subdirectories in: $current_dir"
    dir_count=0

    # Get subdirectories in the current directory
    for dir in "$current_dir"/*/; do
        # Skip if it's not a directory
        if [ ! -d "$dir" ]; then
            continue
        fi

        dir_count=$((dir_count + 1))
        echo "Found subdirectory ($dir_count): $dir"
        echo "Entering directory: $dir"
        process_directory "$dir"
        echo "Leaving directory: $dir"
    done

    if [ $dir_count -eq 0 ]; then
        echo "No subdirectories found."
    else
        echo "Processed $dir_count subdirectories in $current_dir"
    fi
}

echo "****************************************"
echo "Starting file upload from: $ROOT_DIR"
echo "API Endpoint: $API_ENDPOINT"
echo "****************************************"

process_directory "$ROOT_DIR"
echo "Upload process completed."