-- Altera a coluna role de ENUM para VARCHAR(50)
ALTER TABLE `user` 
MODIFY COLUMN `role` VARCHAR(50) NOT NULL;
