# Plataforma de Recrutamento - Banco de Dados

Este diretório contém todos os arquivos relacionados ao banco de dados da Plataforma de Recrutamento.

## Estrutura do Banco de Dados

O banco de dados da aplicação é gerenciado pelo Flyway e consiste nas seguintes tabelas:

### Tabelas Principais

1. **users**
   - Armazena informações de todos os usuários do sistema (candidatos, empregadores e administradores)
   - Campos principais: id, name, email, password, role, avatar, resume, phone, company_name, company_description, company_logo

2. **jobs**
   - Armazena as vagas de emprego publicadas pelos empregadores
   - Campos principais: id, title, description, requirements, location, type, salary_min, salary_max, recruiter_id

3. **applications**
   - Registra as candidaturas dos usuários às vagas
   - Campos principais: id, resume, cover_letter, status, job_id, candidate_id

4. **saved_jobs**
   - Armazena as vagas salvas pelos candidatos
   - Campos principais: id, jobseeker_id, job_id

## Migrações

As migrações estão localizadas no diretório `migrations/` e são executadas na seguinte ordem:

1. **V1__Create_users_table.sql**
   - Cria a tabela de usuários com os perfis: JOB_SEEKER, EMPLOYER, ADMIN

2. **V2__Create_jobs_table.sql**
   - Cria a tabela de vagas com tipos: Full-Time, Part-Time, Contract, Remote, Internship
   - Inclui índices para otimização de consultas

3. **V3__Create_applications_table.sql**
   - Cria a tabela de candidaturas
   - Status possíveis: PENDING (Pendente), APPROVED (Aprovado), REJECTED (Rejeitado)

4. **V4__Create_saved_jobs_table.sql**
   - Cria a tabela de vagas salvas
   - Mantém o relacionamento entre candidatos e vagas salvas

## Configuração

### Pré-requisitos
- MySQL 5.7 ou superior
- Java 11 ou superior (para execução do Flyway)
- Maven (para gerenciamento de dependências)

### Inicialização do Banco de Dados

1. Configure as credenciais do banco de dados no arquivo `application.properties`
2. Execute as migrações usando o comando:
   ```bash
   mvn flyway:migrate
   ```

## Gerenciamento de Migrações

- Todas as alterações no esquema do banco devem ser feitas através de migrações
- As migrações são versionadas e executadas em ordem numérica
- Nunca modifique migrações já aplicadas ao banco
- Para criar uma nova migração, adicione um novo arquivo seguindo o padrão: `V[versão]__Descricao_da_migracao.sql`

## Status Atual

- O banco de dados está configurado para executar apenas a migração V1 (tabela de usuários)
- As migrações V2, V3 e V4 estão disponíveis, mas não serão executadas automaticamente
- Para aplicar as migrações adicionais, é necessário executá-las manualmente
   - Use o script fornecido para fazer o commit das alterações:
     ```bash
     ./commit-db-changes.sh "Add new feature X to the database"
     ```

2. **Para bancos de dados existentes**:
   - Os scripts de migração são idempotentes
   - Você pode executar o script de inicialização várias vezes com segurança

## Boas Práticas

- Sempre documente as alterações no esquema no arquivo `SCHEMA.md`
- Mantenha as migrações pequenas e focadas em uma única alteração
- Teste as migrações primeiro em um ambiente de desenvolvimento
- Nunca modifique uma migração que já foi enviada para o controle de versão
- Sempre faça backup do banco de dados antes de executar migrações em produção

## Documentação do Esquema

Para documentação detalhada do esquema, consulte [SCHEMA.md](./SCHEMA.md).
