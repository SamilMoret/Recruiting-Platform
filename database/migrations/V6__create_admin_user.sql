-- Senha: Admin@123 (criptografada com BCrypt)
INSERT INTO `user` (
    name,
    email,
    password,
    role,
    active,
    created_at,
    updated_at
)
VALUES (
    'Admin',
    'admin@recruiting.com',
    '$2a$10$XURPShQNCsLjp1ESc7la/.1lFhz/KFjGJp.6Y2cmBz.XimjID5DXi',
    'ADMIN',
    true,
    NOW(),
    NOW()
);