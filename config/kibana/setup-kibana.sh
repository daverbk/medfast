#!/bin/bash

# Function to log messages with a timestamp
log_message() {
  echo "$(date '+%Y-%m-%d %H:%M:%S') - $1"
}

# Start Kibana with root permissions
log_message "Starting Kibana..."
kibana --allow-root &

# Wait for Kibana to initialize
log_message "Waiting for Kibana to start (30 seconds)..."
sleep 30

# Set up the index pattern
log_message "Setting up the index pattern from index.json..."
curl -X POST "http://localhost:5601/api/index_patterns/index_pattern" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d @/usr/share/kibana/config/index.json

# Wait before proceeding to the next step
log_message "Waiting for the index pattern to be fully registered (5 seconds)..."
sleep 5

# Import the dashboard
log_message "Importing the dashboard from dashboard.json..."
curl -X POST "http://localhost:5601/api/kibana/dashboards/import" \
  -H 'kbn-xsrf: true' \
  -H 'Content-Type: application/json' \
  -d @/usr/share/kibana/config/dashboard.json

# Wait for any background processes to complete
log_message "Waiting for background processes to complete..."
wait

log_message "Script execution completed successfully."
