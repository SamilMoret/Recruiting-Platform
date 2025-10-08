# Recruiting Platform

Plataforma de Recrutamento e Seleção que conecta candidatos a vagas de emprego e empresas a talentos.

## 🚀 Tecnologias

### Frontend
- **React** - Biblioteca JavaScript para interfaces
- **TypeScript** - Tipagem estática para JavaScript
- **Vite** - Build tool e dev server
- **Axios** - Cliente HTTP
- **React Router** - Navegação
- **Tailwind CSS** - Estilização
- **React Hook Form** - Gerenciamento de formulários
- **React Query** - Gerenciamento de estado e cache
- **Zod** - Validação de esquemas


### Backend
- **Java 17** - Linguagem principal
- **Spring Boot 3.x** - Framework para aplicações Java
- **Spring Security** - Autenticação e autorização
- **JWT** - Autenticação stateless
- **JPA/Hibernate** - Mapeamento objeto-relacional
- **MySQL** - Banco de dados
- **Maven** - Gerenciamento de dependências
- **Lombok** - Redução de boilerplate
- **Flyway** - Migrações de banco de dados

## 📦 Estrutura do Projeto

```
recruiting-platform/
├── client/                  # Frontend (React + TypeScript)
│   ├── public/             # Arquivos estáticos
│   └── src/                # Código-fonte
│       ├── components/     # Componentes reutilizáveis
│       ├── pages/          # Páginas da aplicação
│       ├── services/       # Serviços de API
│       ├── styles/         # Estilos globais
│       └── utils/          # Utilitários e helpers
│
└── server/                 # Backend (Spring Boot)
    ├── src/main/java/br/com/one/jobportal/
    │   ├── config/        # Configurações do Spring
    │   ├── controller/    # Controladores REST
    │   ├── dto/           # Objetos de Transferência de Dados
    │   ├── entity/        # Entidades JPA
    │   ├── repository/    # Repositórios JPA
    │   ├── security/      # Configurações de segurança
    │   ├── service/       # Lógica de negócios
    │   └── util/          # Utilitários
    └── src/main/resources/
        └── application.properties  # Configurações da aplicação
```

## 🛠️ Como Executar

### Pré-requisitos

- Java 22 ou superior
- MySQL 8.0+
- Maven 3.8+

### Backend

1. **Configuração do banco de dados**:
   - Crie um banco de dados MySQL chamado `recruiting_platform`
   - Atualize as credenciais no arquivo `server/src/main/resources/application.properties`

2. **Iniciar o servidor**:
   ```bash
   cd server
   mvn spring-boot:run
   ```

   O servidor estará disponível em: `http://localhost:8080`

### Frontend

1. **Instalar dependências**:
   ```bash
   cd client
   npm install
   ```

2. **Iniciar o cliente**:
   ```bash
   npm run dev
   ```

   O frontend estará disponível em: `http://localhost:5173`

## 🔐 Credenciais de Acesso

### Usuário Administrador
- **Email**: admin@jobportal.com
- **Senha**: Admin@123

### Usuário Empregador
- **Email**: empregador@exemplo.com
- **Senha**: Senha@123

### Usuário Candidato
- **Email**: candidato@exemplo.com
- **Senha**: Senha@123

## 📚 Documentação da API

### Principais Endpoints

#### Autenticação
- `POST /api/auth/register` - Registrar novo usuário
- `POST /api/auth/authenticate` - Fazer login
- `POST /api/auth/refresh-token` - Atualizar token JWT

#### Vagas
- `GET /api/jobs` - Listar vagas
- `POST /api/jobs` - Criar vaga (apenas empregadores)
- `GET /api/jobs/{id}` - Detalhes da vaga
- `PUT /api/jobs/{id}` - Atualizar vaga
- `DELETE /api/jobs/{id}` - Excluir vaga

#### Candidaturas
- `POST /api/applications` - Candidatar-se a vaga
- `GET /api/applications` - Listar candidaturas
- `GET /api/applications/{id}` - Detalhes da candidatura

#### Usuários
- `GET /api/users/me` - Perfil do usuário logado
- `PUT /api/users/me` - Atualizar perfil

#### Administração
- `GET /api/admin/users` - Listar usuários (apenas admin)
- `PUT /api/admin/users/{id}/status` - Ativar/desativar usuário
- `POST /api/admin/promote` - Promover usuário a admin

## 🌟 Recursos

### Para Candidatos
- Busca de vagas por palavras-chave, localização e tipo
- Criação de perfil profissional
- Acompanhamento de candidaturas
- Favoritar vagas

### Para Empresas
- Publicação de vagas
- Gerenciamento de candidaturas
- Busca de candidatos
- Dashboard de vagas

### Para Administradores
- Gerenciamento de usuários
- Estatísticas da plataforma
- Moderação de conteúdo


## 👥 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas alterações (`git commit -m 'Add some AmazingFeature'`)
4. Faça o push da branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 🤝 Feito por:
Welinton Nascimeto
Samil Moret

