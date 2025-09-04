-- Backup do esquema do banco de dados - Gerado em 2025-09-03
-- Este arquivo contém as definições de tabelas e dados iniciais do sistema

-- Desativa temporariamente as verificações de chave estrangeira
SET FOREIGN_KEY_CHECKS = 0;

-- Tabela de usuários
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    company_name VARCHAR(255),
    company_description TEXT,
    phone VARCHAR(20),
    avatar VARCHAR(255),
    resume TEXT,
    company_logo VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices para melhorar o desempenho das consultas
CREATE INDEX idx_user_email ON `user`(email);
CREATE INDEX idx_user_role ON `user`(role);

-- Inserção do usuário administrador
-- Senha: Admin@123 (criptografada com BCrypt)
INSERT IGNORE INTO `user` (
    name,
    email,
    password,
    role,
    active,
    created_at,
    updated_at,
    company_name,
    company_description
) VALUES (
    'Admin',
    'admin@recruiting.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
    'ADMIN',
    1,
    NOW(),
    NOW(),
    'Recruiting Platform',
    'Administrador do sistema'
);

-- Reativa as verificações de chave estrangeira
SET FOREIGN_KEY_CHECKS = 1;

-- Comentários sobre a estrutura do banco
-- Esta é uma versão consolidada das migrações do Flyway
-- Inclui apenas as tabelas essenciais do sistema
-- Para restaurar o banco de dados, execute este script em um banco de dados MySQL vazio
