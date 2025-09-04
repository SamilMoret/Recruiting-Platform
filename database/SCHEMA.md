# Database Schema Documentation

## Tables

### 1. User
Armazena informações da conta do usuário.

| Nome da Coluna | Tipo de Dados | Restrições | Descrição |
|----------------|---------------|------------|------------|
| id | BIGINT | CHAVE PRIMÁRIA, AUTO_INCREMENT | Identificador único do usuário |
| name | VARCHAR(100) | NOT NULL | Nome completo do usuário |
| email | VARCHAR(100) | ÚNICO, NOT NULL | Endereço de e-mail do usuário |
| password | VARCHAR(255) | NOT NULL | Senha criptografada |
| role | ENUM('CANDIDATE', 'RECRUITER', 'ADMIN') | NOT NULL | Papel do usuário no sistema |
| created_at | TIMESTAMP | PADRÃO CURRENT_TIMESTAMP | Data de criação da conta |
| updated_at | TIMESTAMP | PADRÃO CURRENT_TIMESTAMP ATUALIZAÇÃO CURRENT_TIMESTAMP | Data da última atualização |

### 2. Job
Armazena as vagas de emprego.

| Nome da Coluna | Tipo de Dados | Restrições | Descrição |
|----------------|---------------|------------|------------|
| id | BIGINT | CHAVE PRIMÁRIA, AUTO_INCREMENT | Identificador único da vaga |
| title | VARCHAR(100) | NOT NULL | Título da vaga |
| description | TEXT | NOT NULL | Descrição detalhada da vaga |
| requirements | TEXT | NOT NULL | Qualificações necessárias |
| location | VARCHAR(100) |  | Localização da vaga |
| salary | DECIMAL(10,2) |  | Informações salariais |
| status | ENUM('OPEN', 'CLOSED', 'DRAFT') | PADRÃO 'DRAFT' | Status atual da vaga |
| created_by | BIGINT | CHAVE ESTRANGEIRA (User.id) | Usuário que criou a vaga |
| created_at | TIMESTAMP | PADRÃO CURRENT_TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | PADRÃO CURRENT_TIMESTAMP ATUALIZAÇÃO CURRENT_TIMESTAMP | Data da última atualização |

### 3. Application
Acompanha as candidaturas às vagas.

| Nome da Coluna | Tipo de Dados | Restrições | Descrição |
|----------------|---------------|------------|------------|
| id | BIGINT | CHAVE PRIMÁRIA, AUTO_INCREMENT | Identificador único da candidatura |
| user_id | BIGINT | CHAVE ESTRANGEIRA (User.id) | Candidato |
| job_id | BIGINT | CHAVE ESTRANGEIRA (Job.id) | Vaga candidatada |
| status | ENUM('PENDING', 'REVIEW', 'ACCEPTED', 'REJECTED') | PADRÃO 'PENDING' | Status da candidatura |
| cover_letter | TEXT |  | Carta de apresentação do candidato |
| created_at | TIMESTAMP | PADRÃO CURRENT_TIMESTAMP | Data da candidatura |
| updated_at | TIMESTAMP | PADRÃO CURRENT_TIMESTAMP ATUALIZAÇÃO CURRENT_TIMESTAMP | Data da última atualização |

### 4. SavedJob
Acompanha as vagas salvas pelos usuários.

| Nome da Coluna | Tipo de Dados | Restrições | Descrição |
|----------------|---------------|------------|------------|
| id | BIGINT | CHAVE PRIMÁRIA, AUTO_INCREMENT | Identificador único |
| user_id | BIGINT | CHAVE ESTRANGEIRA (User.id) | Usuário que salvou a vaga |
| job_id | BIGINT | CHAVE ESTRANGEIRA (Job.id) | Vaga salva |
| created_at | TIMESTAMP | PADRÃO CURRENT_TIMESTAMP | Data em que a vaga foi salva |

### 5. Analytics
Armazena dados analíticos.

| Nome da Coluna | Tipo de Dados | Restrições | Descrição |
|----------------|---------------|------------|------------|
| id | BIGINT | CHAVE PRIMÁRIA, AUTO_INCREMENT | Identificador único |
| user_id | BIGINT | CHAVE ESTRANGEIRA (User.id) | Usuário relacionado |
| metric_name | VARCHAR(50) | NOT NULL | Nome da métrica |
| metric_value | TEXT | NOT NULL | Valor da métrica |
| created_at | TIMESTAMP | PADRÃO CURRENT_TIMESTAMP | Data de registro da métrica |

## Relacionamentos

1. **User** tem muitos **Job** (Um-para-Muitos)
   - Um usuário pode publicar várias vagas
   - Uma vaga é publicada por um usuário

2. **User** tem muitos **Application** (Um-para-Muitos)
   - Um usuário pode enviar várias candidaturas
   - Uma candidatura é enviada por um usuário

3. **Job** tem muitos **Application** (Um-para-Muitos)
   - Uma vaga pode receber várias candidaturas
   - Uma candidatura é para uma vaga

4. **User** tem muitos **SavedJob** (Um-para-Muitos)
   - Um usuário pode salvar várias vagas
   - Uma vaga salva pertence a um usuário

5. **Job** tem muitos **SavedJob** (Um-para-Muitos)
   - Uma vaga pode ser salva por vários usuários
   - Uma vaga salva referencia uma vaga

6. **User** tem um **Analytics** (Um-para-Um)
   - Um usuário tem um registro de análise
   - Um registro de análise pertence a um usuário
