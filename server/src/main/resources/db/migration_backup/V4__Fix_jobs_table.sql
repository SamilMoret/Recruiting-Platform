-- =============================================
-- FIX: Corrige a estrutura da tabela jobs
-- =============================================
SET @dbname = DATABASE();
SET @tablename = 'jobs';

-- Remove a chave estrangeira fk_job_company se existir
SET @constraintname = 'fk_job_company';
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

-- Renomeia a coluna company_id para recruiter_id se ela existir
SET @columnname = 'company_id';
SET @preparedStatement = (SELECT IF(
  EXISTS(
    SELECT * FROM INFORMATION_SCHEMA.COLUMNS
    WHERE (TABLE_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (COLUMN_NAME = @columnname)
  ),
  'ALTER TABLE jobs CHANGE COLUMN company_id recruiter_id BIGINT NOT NULL;',
  'SELECT 1'
));
PREPARE alterIfExists FROM @preparedStatement;
EXECUTE alterIfExists;
DEALLOCATE PREPARE alterIfExists;

-- Adiciona a chave estrangeira correta se não existir
SET @constraintname = 'fk_job_recruiter';
SET @preparedStatement = (SELECT IF(
  NOT EXISTS(
    SELECT * FROM information_schema.TABLE_CONSTRAINTS 
    WHERE (CONSTRAINT_SCHEMA = @dbname)
    AND (TABLE_NAME = @tablename)
    AND (CONSTRAINT_NAME = @constraintname)
  ),
  'ALTER TABLE jobs ADD CONSTRAINT fk_job_recruiter FOREIGN KEY (recruiter_id) REFERENCES users(id) ON DELETE CASCADE;',
  'SELECT 1'
));
PREPARE addIfNotExists FROM @preparedStatement;
EXECUTE addIfNotExists;
DEALLOCATE PREPARE addIfNotExists;
