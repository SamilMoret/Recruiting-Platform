# Plataforma de Recrutamento - Backend

Este é o backend da Plataforma de Recrutamento, desenvolvido com Spring Boot e MySQL.

## Pré-requisitos

- Java 17 ou superior
- Maven
- MySQL 8.0 ou superior

## Configuração do Banco de Dados

1. **Crie um banco de dados MySQL**
   ```sql
   CREATE DATABASE RecruitingPlatform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **Configure as credenciais do banco de dados**
   - Abra o arquivo `src/main/resources/application.properties`
   - Atualize as seguintes propriedades, se necessário:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/RecruitingPlatform?useSSL=false&serverTimezone=UTC
     spring.datasource.username=seu_usuario
     spring.datasource.password=sua_senha
     ```

## Executando a Aplicação

1. **Construa o projeto**
   ```bash
   mvn clean install
   ```

2. **Execute a aplicação**
   ```bash
   mvn spring-boot:run
   ```

   A aplicação estará disponível em: http://localhost:5001

## Migrações de Banco de Dados

O projeto utiliza o Flyway para gerenciar migrações de banco de dados. As migrações estão localizadas em `src/main/resources/db/migration/`.

- O Flyway criará automaticamente todas as tabelas necessárias na primeira execução.
- Migrações futuras devem ser adicionadas como novos arquivos SQL numerados sequencialmente (ex: V5__descricao_da_migracao.sql).

## Endpoints da API

A documentação da API estará disponível em:
- Swagger UI: http://localhost:5001/swagger-ui.html
- OpenAPI (JSON): http://localhost:5001/v3/api-docs

## Variáveis de Ambiente

| Variável | Descrição | Valor Padrão |
|----------|-----------|--------------|
| `SPRING_DATASOURCE_URL` | URL de conexão com o banco de dados | jdbc:mysql://localhost:3306/RecruitingPlatform |
| `SPRING_DATASOURCE_USERNAME` | Nome de usuário do banco de dados | root |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco de dados | - |
| `APP_JWT_SECRET` | Chave secreta para geração de tokens JWT | - |
| `APP_JWT_EXPIRATION_MS` | Tempo de expiração do token JWT (em ms) | 2592000000 (30 dias) |

## Desenvolvimento

### Estrutura do Projeto

```
src/main/java/br/com/one/jobportal/
├── config/           # Configurações do Spring
├── controller/       # Controladores da API
├── dto/              # Objetos de Transferência de Dados
├── entity/           # Entidades JPA
├── repository/       # Repositórios de dados
├── security/         # Configurações de segurança
└── service/          # Lógica de negócios
```

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.
