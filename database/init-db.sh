#!/bin/bash

# Database configuration
DB_NAME="recruiting_platform"
DB_USER="root"
DB_PASS="yourpassword" # Change this to your MySQL root password
MYSQL_CMD="mysql -u$DB_USER"

if [ -n "$DB_PASS" ]; then
    MYSQL_CMD="$MYSQL_CMD -p$DB_PASS"
fi

# Create database
echo "Creating database $DB_NAME..."
$MYSQL_CMD -e "CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Run migrations
echo "Running migrations..."
for migration in $(ls -v migrations/V*.sql); do
    echo "Applying migration: $migration"
    $MYSQL_CMD $DB_NAME < $migration
    if [ $? -ne 0 ]; then
        echo "Error applying migration $migration"
        exit 1
    fi
done

echo "Database setup completed successfully!"
