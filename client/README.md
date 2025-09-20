# Recruiting Platform

A modern, full-stack recruiting platform for job seekers, employers, and administrators. Built with React (Vite), Java Spring Boot backend, JWT authentication, and a responsive, user-friendly UI.

# Demo

Watch a walkthrough of the platform:

<div align="center">
  <a href="https://youtu.be/1QEMu4EPbTg" target="_blank">
    <img src="https://img.youtube.com/vi/1QEMu4EPbTg/0.jpg" alt="Recruiting Platform Demo" width="480"/>
  </a>
</div>

## Features

- **Job Seeker Portal**
  - Browse and search jobs
  - Save jobs and manage applications
  - Profile management and resume upload

- **Employer Portal**
  - Post and manage job listings
  - View and manage applicants
  - Company profile management

- **Admin Dashboard**
  - Platform analytics and statistics
  - User management (job seekers, employers)
  - Job and application oversight

- **Authentication & Security**
  - JWT-based login and refresh
  - Role-based protected routes
  - Secure profile and data management

- **Modern UI/UX**
  - Responsive design for desktop and mobile
  - Dashboard layouts for each user type
  - Toast notifications and loading spinners

## Tech Stack

- **Frontend:** React (Vite), Tailwind CSS, React Router, Axios
- **Backend:** Java Spring Boot (not included in this repo)
- **API:** RESTful endpoints, JWT authentication
- **Deployment:** Nginx reverse proxy, HTTPS

## Getting Started

### Prerequisites

- Node.js (v22+ recommended)
- npm or yarn

### Installation

```bash
# Clone the repository
git clone https://github.com/SamilMoret/Recruiting-Platform.git
cd Recruiting-Platform/client

# Install dependencies
npm install
# or
yarn install
```

### Environment Variables

Create a `.env` file in the root of the client directory and add the following line:

```
VITE_API_BASE_URL=https://your-api-url.com/npm 
run dev
# or
yarn dev
npm run lint
```

### Project Structure

```
client/
  src/
    components/         # Reusable UI components
    context/            # React context (Auth, etc.)
    pages/              # Page components (JobSeeker, Employer, Admin, Auth)
    routes/             # ProtectedRoute and routing logic
    utils/              # API paths, helpers, axios instance
    assets/             # Images and static assets
  public/               # Static files
  .env                  # Environment variables
# Project metadata and scripts


#Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

License
MIT
```

