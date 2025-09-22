-- Corrige os valores de enum na tabela users para corresponder ao esperado
UPDATE users SET role = 'JOB_SEEKER' WHERE role = 'candidate' OR role = 'CANDIDATE';
UPDATE users SET role = 'EMPLOYER' WHERE role = 'recruiter' OR role = 'RECRUITER';

-- Atualiza o tipo da coluna role para garantir que use os valores corretos
ALTER TABLE users 
MODIFY COLUMN role ENUM('JOB_SEEKER', 'EMPLOYER', 'ADMIN') NOT NULL DEFAULT 'JOB_SEEKER';
