-- Atualiza a tabela users para incluir os campos faltantes
-- Verifica se cada coluna existe antes de adicionar
SET @dbname = DATABASE();
SET @tablename = 'users';
SET @columnname = 'avatar';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(255) AFTER role;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'resume';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(255) AFTER avatar;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'phone';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(20) AFTER resume;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'company_name';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(255) AFTER phone;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'company_description';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' TEXT AFTER company_name;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'company_logo';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'SELECT 1',
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(255) AFTER company_description;')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Atualiza a tabela jobs para incluir os campos faltantes
-- Primeiro, verifique se a chave estrangeira existe antes de removê-la
SET @dbname = DATABASE();
SET @tablename = 'jobs';
SET @constraintname = 'fk_job_recruiter';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM information_schema.TABLE_CONSTRAINTS 
    WHERE (CONSTRAINT_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (CONSTRAINT_NAME = @constraintname)
  ),
  CONCAT('ALTER TABLE ', @tablename, ' DROP FOREIGN KEY ', @constraintname, ';'),
  'SELECT 1'
));
PREPARE dropIfExists FROM @preparedStatement;
EXECUTE dropIfExists;
DEALLOCATE PREPARE dropIfExists;

-- Adicione as novas colunas se não existirem
SET @columnname = 'location';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(255) AFTER requirements;'),
  'SELECT 1'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'category';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(255) AFTER location;'),
  'SELECT 1'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'type';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'ALTER TABLE jobs ADD COLUMN type ENUM("Full-Time", "Part-Time", "Contract", "Remote", "Internship") DEFAULT "Full-Time" AFTER category;',
  'SELECT 1'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'salary_min';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' INT AFTER type;'),
  'SELECT 1'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'salary_max';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' INT AFTER salary_min;'),
  'SELECT 1'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Altera a coluna status para is_closed se ela existir
SET @columnname = 'status';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'ALTER TABLE jobs CHANGE COLUMN status is_closed BOOLEAN DEFAULT FALSE;',
  'SELECT 1'
));
PREPARE alterIfExists FROM @preparedStatement;
EXECUTE alterIfExists;
DEALLOCATE PREPARE alterIfExists;

-- Altera o nome da coluna recruiter_id para company_id se ela existir
SET @columnname = 'recruiter_id';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'ALTER TABLE jobs CHANGE COLUMN recruiter_id company_id BIGINT NOT NULL;',
  'SELECT 1'
));
PREPARE alterIfExists FROM @preparedStatement;
EXECUTE alterIfExists;
DEALLOCATE PREPARE alterIfExists;

-- Adicione a nova chave estrangeira se não existir
SET @constraintname = 'fk_job_company';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM information_schema.TABLE_CONSTRAINTS 
    WHERE (CONSTRAINT_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (CONSTRAINT_NAME = @constraintname)
  ),
  'ALTER TABLE jobs ADD CONSTRAINT fk_job_company FOREIGN KEY (company_id) REFERENCES users(id) ON DELETE CASCADE;',
  'SELECT 1'
));
PREPARE addIfNotExists FROM @preparedStatement;
EXECUTE addIfNotExists;
DEALLOCATE PREPARE addIfNotExists;

-- Atualiza a tabela applications para incluir os campos faltantes
-- Primeiro, verifique se a chave estrangeira existe antes de removê-la
SET @dbname = DATABASE();
SET @tablename = 'applications';
SET @constraintname = 'fk_application_candidate';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM information_schema.TABLE_CONSTRAINTS 
    WHERE (CONSTRAINT_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (CONSTRAINT_NAME = @constraintname)
  ),
  CONCAT('ALTER TABLE ', @tablename, ' DROP FOREIGN KEY ', @constraintname, ';'),
  'SELECT 1'
));
PREPARE dropIfExists FROM @preparedStatement;
EXECUTE dropIfExists;
DEALLOCATE PREPARE dropIfExists;

-- Adicione as novas colunas se não existirem
SET @columnname = 'resume';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' VARCHAR(255) AFTER id;'),
  'SELECT 1'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

SET @columnname = 'cover_letter';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  CONCAT('ALTER TABLE ', @tablename, ' ADD COLUMN ', @columnname, ' TEXT AFTER resume;'),
  'SELECT 1'
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Altera o tipo da coluna status se ela existir
SET @columnname = 'status';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'ALTER TABLE applications MODIFY COLUMN status ENUM("Applied", "In Review", "Rejected", "Accepted") DEFAULT "Applied";',
  'SELECT 1'
));
PREPARE alterIfExists FROM @preparedStatement;
EXECUTE alterIfExists;
DEALLOCATE PREPARE alterIfExists;

-- Altera o nome da coluna candidate_id para applicant_id se ela existir
SET @columnname = 'candidate_id';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'ALTER TABLE applications CHANGE COLUMN candidate_id applicant_id BIGINT NOT NULL;',
  'SELECT 1'
));
PREPARE alterIfExists FROM @preparedStatement;
EXECUTE alterIfExists;
DEALLOCATE PREPARE alterIfExists;

-- Adicione a nova chave estrangeira se não existir
SET @constraintname = 'fk_application_applicant';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM information_schema.TABLE_CONSTRAINTS 
    WHERE (CONSTRAINT_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (CONSTRAINT_NAME = @constraintname)
  ),
  'ALTER TABLE applications ADD CONSTRAINT fk_application_applicant FOREIGN KEY (applicant_id) REFERENCES users(id) ON DELETE CASCADE;',
  'SELECT 1'
));
PREPARE addIfNotExists FROM @preparedStatement;
EXECUTE addIfNotExists;
DEALLOCATE PREPARE addIfNotExists;

-- Atualiza a tabela saved_jobs para refletir as mudanças de nomenclatura
-- Primeiro, verifique se a chave estrangeira existe antes de removê-la
SET @dbname = DATABASE();
SET @tablename = 'saved_jobs';
SET @constraintname = 'fk_saved_job_candidate';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM information_schema.TABLE_CONSTRAINTS 
    WHERE (CONSTRAINT_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (CONSTRAINT_NAME = @constraintname)
  ),
  CONCAT('ALTER TABLE ', @tablename, ' DROP FOREIGN KEY ', @constraintname, ';'),
  'SELECT 1'
));
PREPARE dropIfExists FROM @preparedStatement;
EXECUTE dropIfExists;
DEALLOCATE PREPARE dropIfExists;

-- Altera o nome da coluna candidate_id para jobseeker_id se ela existir
SET @columnname = 'candidate_id';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'ALTER TABLE saved_jobs CHANGE COLUMN candidate_id jobseeker_id BIGINT NOT NULL;',
  'SELECT 1'
));
PREPARE alterIfExists FROM @preparedStatement;
EXECUTE alterIfExists;
DEALLOCATE PREPARE alterIfExists;

-- Adicione a nova chave estrangeira se não existir
SET @constraintname = 'fk_saved_job_jobseeker';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM information_schema.TABLE_CONSTRAINTS 
    WHERE (CONSTRAINT_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (CONSTRAINT_NAME = @constraintname)
  ),
  'ALTER TABLE saved_jobs ADD CONSTRAINT fk_saved_job_jobseeker FOREIGN KEY (jobseeker_id) REFERENCES users(id) ON DELETE CASCADE;',
  'SELECT 1'
));
PREPARE addIfNotExists FROM @preparedStatement;
EXECUTE addIfNotExists;
DEALLOCATE PREPARE addIfNotExists;
