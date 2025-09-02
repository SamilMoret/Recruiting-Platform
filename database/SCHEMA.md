# Database Schema Documentation

## Tables

### 1. User
Stores user account information.

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for the user |
| name | VARCHAR(100) | NOT NULL | User's full name |
| email | VARCHAR(100) | UNIQUE, NOT NULL | User's email address |
| password | VARCHAR(255) | NOT NULL | Hashed password |
| role | ENUM('CANDIDATE', 'RECRUITER', 'ADMIN') | NOT NULL | User role in the system |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last update timestamp |

### 2. Job
Stores job postings.

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for the job |
| title | VARCHAR(100) | NOT NULL | Job title |
| description | TEXT | NOT NULL | Detailed job description |
| requirements | TEXT | NOT NULL | Required qualifications |
| location | VARCHAR(100) | | Job location |
| salary | DECIMAL(10,2) | | Salary information |
| status | ENUM('OPEN', 'CLOSED', 'DRAFT') | DEFAULT 'DRAFT' | Current status of the job posting |
| created_by | BIGINT | FOREIGN KEY (User.id) | User who created the job |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last update timestamp |

### 3. Application
Tracks job applications.

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier for the application |
| user_id | BIGINT | FOREIGN KEY (User.id) | Applicant |
| job_id | BIGINT | FOREIGN KEY (Job.id) | Applied job |
| status | ENUM('PENDING', 'REVIEW', 'ACCEPTED', 'REJECTED') | DEFAULT 'PENDING' | Application status |
| cover_letter | TEXT | | Applicant's cover letter |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Application timestamp |
| updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Last update timestamp |

### 4. SavedJob
Tracks jobs saved by users.

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| user_id | BIGINT | FOREIGN KEY (User.id) | User who saved the job |
| job_id | BIGINT | FOREIGN KEY (Job.id) | Saved job |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | When the job was saved |

### 5. Analytics
Stores analytics data.

| Column Name | Data Type | Constraints | Description |
|-------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| user_id | BIGINT | FOREIGN KEY (User.id) | Related user |
| metric_name | VARCHAR(50) | NOT NULL | Name of the metric |
| metric_value | TEXT | NOT NULL | Value of the metric |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | When the metric was recorded |

## Relationships

1. **User** has many **Job** (One-to-Many)
   - A user can post multiple jobs
   - A job is posted by one user

2. **User** has many **Application** (One-to-Many)
   - A user can submit multiple applications
   - An application is submitted by one user

3. **Job** has many **Application** (One-to-Many)
   - A job can receive multiple applications
   - An application is for one job

4. **User** has many **SavedJob** (One-to-Many)
   - A user can save multiple jobs
   - A saved job belongs to one user

5. **Job** has many **SavedJob** (One-to-Many)
   - A job can be saved by multiple users
   - A saved job references one job

6. **User** has one **Analytics** (One-to-One)
   - A user has one analytics record
   - An analytics record belongs to one user
