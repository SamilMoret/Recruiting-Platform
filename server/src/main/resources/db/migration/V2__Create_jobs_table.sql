-- Criação da tabela jobs
CREATE TABLE IF NOT EXISTS jobs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  requirements TEXT NOT NULL,
  location VARCHAR(255),
  category VARCHAR(255),
  type ENUM('Full-Time', 'Part-Time', 'Contract', 'Remote', 'Internship') DEFAULT 'Full-Time',
  salary_min INT,
  salary_max INT,
  is_closed BOOLEAN DEFAULT FALSE,
  is_saved BOOLEAN DEFAULT FALSE,
  recruiter_id BIGINT NOT NULL,
  application_status VARCHAR(50) DEFAULT NULL,
  application_count INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_job_recruiter FOREIGN KEY (recruiter_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Adiciona índice para melhorar consultas por recrutador
CREATE INDEX idx_job_recruiter ON jobs(recruiter_id);
