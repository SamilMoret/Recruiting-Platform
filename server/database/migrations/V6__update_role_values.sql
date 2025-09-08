-- Atualiza os valores de role na tabela User para usar as novas nomenclaturas
-- Este script deve ser executado após a atualização da estrutura da tabela

-- Atualiza 'candidate' para 'JOB_SEEKER'
UPDATE `User` SET role = 'JOB_SEEKER' WHERE role = 'candidate' OR role = 'CANDIDATE';

-- Atualiza 'recruiter' para 'EMPLOYER'
UPDATE `User` SET role = 'EMPLOYER' WHERE role = 'recruiter' OR role = 'RECRUITER';

-- Verifica se há valores inesperados
SELECT id, email, role FROM `User` WHERE role NOT IN ('JOB_SEEKER', 'EMPLOYER', 'ADMIN');
