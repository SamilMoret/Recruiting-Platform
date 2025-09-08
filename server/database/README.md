# Recruiting Platform - Database

This directory contains all database-related files for the Recruiting Platform.

## Estrutura

```
database/
├── SCHEMA.md           # Documentação do esquema do banco de dados
├── init-db.sh          # Script para inicializar o banco de dados
├── commit-db-changes.sh # Script auxiliar para commits no git
├── test_migration.sql  # Script para testar a estrutura do banco
└── migrations/         # Arquivos de migração do banco de dados
    ├── V1__create_user_table.sql
    ├── V2__create_job_table.sql
    ├── V3__create_application_table.sql
    ├── V4__create_saved_jobs_table.sql
    └── V5__create_analytics_table.sql
```

## Instruções de Configuração

### Pré-requisitos
- Servidor MySQL instalado
- Ferramentas de cliente MySQL (como MySQL Workbench ou linha de comando)

### Configuração Inicial

1. Edite o arquivo `init-db.sh` e atualize as credenciais do banco de dados se necessário:
   ```bash
   DB_USER="root"
   DB_PASSWORD="sua_senha"  # Altere para a senha do seu MySQL
   ```

2. No Windows, você pode usar Git Bash ou WSL para executar o script de inicialização:
   ```bash
   # Tornar o script executável (no Git Bash ou WSL)
   chmod +x init-db.sh
   
   # Executar o script de inicialização
   ./init-db.sh
   ```

   Ou execute manualmente os arquivos SQL em ordem usando um cliente MySQL como o MySQL Workbench.

## Gerenciando Mudanças no Banco de Dados

1. **Para novas alterações**:
   - Crie um novo arquivo de migração com o próximo número de versão (ex: `V6__descricao_da_mudanca.sql`)
   - Teste a migração localmente
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
