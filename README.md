# Phonebook Application

A full-stack application for managing phone contacts. This project uses Java Spring Boot for the backend and React with TypeScript for the frontend, all containerized with Docker.

## Getting Started

### Prerequisites

- Docker and Docker Compose installed on your machine.

### Running the Application

1. Clone the repository.
2. Navigate to the project root directory.
3. Run the following command to build and start the services:

   ```bash
   docker-compose up --build
   ```

This command will start:

- **MySQL Database** on port `3306`
- **Backend API** on port `8080`
- **Frontend Application** on port `5173`

Access the application in your browser at `http://localhost:5173`.
