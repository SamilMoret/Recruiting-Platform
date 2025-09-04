#!/bin/bash

# Commit database schema changes with a descriptive message
# Usage: ./commit-db-changes.sh "Your commit message"

if [ $# -eq 0 ]; then
    echo "Please provide a commit message"
    echo "Usage: ./commit-db-changes.sh \"Your commit message\""
    exit 1
fi

# Add all database related files
git add database/SCHEMA.md
git add database/migrations/*.sql
git add database/migrations/README.md
git add database/init-db.sh

# Commit with the provided message
git commit -m "$1"

echo "Database changes have been committed with message: $1"
