# Database Migrations

This directory contains all database migrations for the Recruiting Platform application. Migrations are versioned and should be run in order.

## Migration Files

1. `V1__create_users_table.sql` - Creates the users table
2. `V2__create_jobs_table.sql` - Creates the jobs table
3. `V3__create_applications_table.sql` - Creates the applications table
4. `V4__create_saved_jobs_table.sql` - Creates the saved_jobs table
5. `V5__create_analytics_table.sql` - Creates the analytics table

## How to Run Migrations

### Prerequisites
- MySQL server running
- Database created
- Proper database credentials configured in your application

### Using Flyway (Recommended)

1. Install Flyway: https://flywaydb.org/documentation/usage/commandline/
2. Configure your `flyway.conf` with your database credentials
3. Run: `flyway migrate`

### Manual Execution

You can also run the SQL files manually in order using a MySQL client like MySQL Workbench or the MySQL command line client.

## Version Control

- Each migration is prefixed with a version number (V1, V2, etc.)
- Never modify a migration that has already been committed to version control
- Always create a new migration for schema changes

## Best Practices

- Keep migrations idempotent
- Include rollback scripts for production environments
- Test migrations in a development environment before applying to production
- Back up your database before running migrations in production
