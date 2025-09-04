# Recruiting Platform - Database

This directory contains all database-related files for the Recruiting Platform.

## Structure

```
database/
├── SCHEMA.md           # Database schema documentation
├── init-db.sh          # Script to initialize the database
├── commit-db-changes.sh # Helper script for git commits
└── migrations/         # Database migration files
    ├── V1__create_users_table.sql
    ├── V2__create_jobs_table.sql
    ├── V3__create_applications_table.sql
    ├── V4__create_saved_jobs_table.sql
    └── V5__create_analytics_table.sql
```

## Setup Instructions

### Prerequisites
- MySQL Server
- MySQL client tools

### Initial Setup

1. Edit the `init-db.sh` script and update the database credentials:
   ```bash
   DB_USER="root"
   DB_PASS="yourpassword"  # Change this to your MySQL root password
   ```

2. On Windows, you can use Git Bash or WSL to run the initialization script:
   ```bash
   # Make the script executable (in Git Bash or WSL)
   chmod +x init-db.sh
   
   # Run the initialization script
   ./init-db.sh
   ```

   Or manually execute the SQL files in order using a MySQL client like MySQL Workbench.

## Managing Database Changes

1. **For new changes**:
   - Create a new migration file with the next version number (e.g., `V6__description_of_change.sql`)
   - Test the migration locally
   - Use the provided script to commit your changes:
     ```bash
     ./commit-db-changes.sh "Add new feature X to the database"
     ```

2. **For existing databases**:
   - The migration scripts are designed to be idempotent
   - You can safely run the initialization script multiple times

## Best Practices

- Always document schema changes in `SCHEMA.md`
- Keep migrations small and focused on a single change
- Test migrations in a development environment first
- Never modify a migration that has already been committed to version control
- Always back up your database before running migrations in production

## Schema Documentation

For detailed schema documentation, see [SCHEMA.md](./SCHEMA.md).
