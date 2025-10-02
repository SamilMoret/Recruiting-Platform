# Esquema do Banco de Dados - Recruiting Platform

## Tabelas

### 1. users
Armazena informações dos usuários do sistema.

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| name | VARCHAR(255) | NOT NULL | Nome completo |
| email | VARCHAR(255) | UNIQUE, NOT NULL | E-mail do usuário |
| password | VARCHAR(255) | NOT NULL | Senha (hash) |
| role | ENUM('JOB_SEEKER', 'EMPLOYER', 'ADMIN') | DEFAULT 'JOB_SEEKER' | Perfil do usuário |
| avatar | VARCHAR(255) |  | URL da foto de perfil |
| resume | VARCHAR(255) |  | URL do currículo (para candidatos) |
| phone | VARCHAR(20) |  | Telefone para contato |
| company_name | VARCHAR(255) |  | Nome da empresa (para empregadores) |
| company_description | TEXT |  | Descrição da empresa |
| company_logo | VARCHAR(255) |  | Logo da empresa |
| active | BOOLEAN | DEFAULT TRUE | Conta ativa |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Data de criação |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Última atualização |

**Índices:**
- `uk_email` (email) - Índice único para e-mail
- `idx_user_role` (role) - Índice para busca por perfil

### 2. jobs
Armazena as vagas de emprego.

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| title | VARCHAR(255) | NOT NULL | Título da vaga |
| description | TEXT | NOT NULL | Descrição detalhada |
| requirements | TEXT | NOT NULL | Requisitos |
| location | VARCHAR(255) |  | Localização |
| category | VARCHAR(255) |  | Categoria da vaga |
| type | ENUM('Full-Time', 'Part-Time', 'Contract', 'Remote', 'Internship') | DEFAULT 'Full-Time' | Tipo de contrato |
| salary_min | INT |  | Faixa salarial mínima |
| salary_max | INT |  | Faixa salarial máxima |
| is_closed | BOOLEAN | DEFAULT FALSE | Vaga encerrada |
| recruiter_id | BIGINT | FK(users.id), NOT NULL | Recrutador responsável |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Data de criação |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Última atualização |

**Chaves Estrangeiras:**
- `recruiter_id` referencia `users(id)` ON DELETE CASCADE

### 3. applications
Registra as candidaturas às vagas.

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| resume | VARCHAR(255) |  | URL do currículo (opcional, pode herdar do usuário) |
| cover_letter | TEXT |  | Carta de apresentação |
| status | ENUM('PENDING', 'APPROVED', 'REJECTED') | DEFAULT 'PENDING' | Status da candidatura |
| job_id | BIGINT | FK(jobs.id), NOT NULL | Vaga |
| candidate_id | BIGINT | FK(users.id), NOT NULL | Candidato |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Data da candidatura |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Última atualização |

**Chaves Estrangeiras:**
- `job_id` referencia `jobs(id)` ON DELETE CASCADE
- `candidate_id` referencia `users(id)` ON DELETE CASCADE

**Restrições Únicas:**
- `unique_application` (job_id, candidate_id) - Impede múltiplas candidaturas à mesma vaga

### 4. saved_jobs
Registra as vagas salvas pelos candidatos.

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| id | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| jobseeker_id | BIGINT | FK(users.id), NOT NULL | Candidato que salvou |
| job_id | BIGINT | FK(jobs.id), NOT NULL | Vaga salva |
| created_at | DATETIME | DEFAULT CURRENT_TIMESTAMP | Data do salvamento |
| updated_at | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | Última atualização |

**Chaves Estrangeiras:**
- `jobseeker_id` referencia `users(id)` ON DELETE CASCADE
- `job_id` referencia `jobs(id)` ON DELETE CASCADE

**Restrições Únicas:**
- `unique_saved_job` (jobseeker_id, job_id) - Impede duplicação de vagas salvas

## Relacionamentos

1. **users** (1) → **jobs** (muitos)
   - Um usuário (recrutador) pode publicar várias vagas
   - Uma vaga pertence a um único recrutador

2. **users** (1) → **applications** (muitos)
   - Um candidato pode se inscrever em várias vagas
   - Uma candidatura pertence a um único candidato

3. **jobs** (1) → **applications** (muitos)
   - Uma vaga pode receber várias candidaturas
   - Uma candidatura é para uma única vaga

4. **users** (1) → **saved_jobs** (muitos)
   - Um candidato pode salvar várias vagas
   - Um registro de vaga salva pertence a um candidato

5. **jobs** (1) → **saved_jobs** (muitos)
   - Uma vaga pode ser salva por vários candidatos
   - Um registro de vaga salva referencia uma vaga

## Índices Adicionais (V2__Add_indexes.sql)

### Tabela jobs:
- `idx_jobs_recruiter_id` (recruiter_id)
- `idx_jobs_is_closed` (is_closed)
- `idx_jobs_type` (type)
- `idx_jobs_location` (location)
- `idx_jobs_category` (category)
- `idx_jobs_created_at` (created_at)

### Tabela applications:
- `idx_applications_job_id` (job_id)
- `idx_applications_candidate_id` (candidate_id)
- `idx_applications_status` (status)
- `idx_applications_created_at` (created_at)

### Tabela saved_jobs:
- `idx_saved_jobs_jobseeker_id` (jobseeker_id)
- `idx_saved_jobs_job_id` (job_id)

