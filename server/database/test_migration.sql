-- Script para testar a estrutura do banco de dados

-- 1. Verificar as tabelas existentes
SHOW TABLES;

-- 4. Verificar as restrições de chave estrangeira
SELECT 
    TABLE_NAME, 
    COLUMN_NAME, 
    CONSTRAINT_NAME, 
    REFERENCED_TABLE_NAME, 
    REFERENCED_COLUMN_NAME
FROM 
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE 
    REFERENCED_TABLE_SCHEMA = 'recruiting_platform';

-- 5. Verificar os dados em cada tabela
SELECT 'User' as Tabela, COUNT(*) as Total FROM user UNION ALL
SELECT 'Job' as Tabela, COUNT(*) as Total FROM job UNION ALL
SELECT 'Application' as Tabela, COUNT(*) as Total FROM application UNION ALL
SELECT 'SavedJob' as Tabela, COUNT(*) as Total FROM saved_jobs UNION ALL
SELECT 'Analytics' as Tabela, COUNT(*) as Total FROM analytics;
