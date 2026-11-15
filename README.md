# HelpDesk – Full Stack Ticket Management System

A full-stack Help Desk application built using **Spring Boot, React, MySQL, and Spring Security**. It provides a centralized platform where users can raise support tickets, support agents can manage and resolve assigned tickets, and administrators can manage users, agents, and tickets.

## 🚀 Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT Authentication
- MySQL
- Maven
- Lombok
- Bean Validation

### Frontend
- React.js
- JavaScript
- HTML5
- CSS3
- Axios 
- Vite

### DevOps & Tools
- Docker
- Docker Compose
- Postman
- Git & GitHub

## ✨ Features

### Authentication & Authorization
- User registration and login.
- JWT-based authentication.
- Password encryption using BCrypt.
- Role-Based Access Control (RBAC).

### Role-Based Access

**ADMIN**
- View all tickets.
- Create and view support agents.
- Assign tickets to support agents.
- Manage users.
- Monitor ticket status and priority.

**SUPPORT_AGENT**
- View assigned tickets.
- Update ticket status.
- Update ticket priority.
- Add comments to tickets.
- Track ticket history.

**USER**
- Create support tickets.
- View personal tickets.
- Track ticket status.
- Add comments to tickets.
- View ticket history.

### Ticket Management
- Create, retrieve, update, and delete tickets according to permissions.
- Assign tickets to support agents.
- Filter tickets by status and priority.
- Pagination and sorting.
- Automatic ticket timestamps.

### Comments & Ticket History
- Add comments to tickets.
- Retrieve ticket conversations.
- Maintain ticket status change history.
- Track who performed each status update.

### Dockerization
- Dockerized Spring Boot application.
- MySQL container with persistent storage.
- Docker Compose for running the application and database together.

## 📁 Project Structure

```text
HelpDesk/
│
├── backend/
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── .env
│
├── front/
│   ├── public/
│   ├── src/
│   ├── package.json
│   └── vite.config.js
│
├── .gitignore
└── README.md
```

## ⚙️ Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/Ductblue/HelpDesk-Ticket-Management-System.git
cd HelpDesk-Ticket-Management-System
```

### 2. Run the Backend

Navigate to the backend directory:

```bash
cd backend
```

Configure your environment variables and MySQL database connection.

Example environment variables:

```env
MYSQL_ROOT_PASSWORD=your_mysql_password
JWT_SECRET=your_base64_encoded_jwt_secret
```

Run the application using Maven:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend runs at:

```text
http://localhost:8080
```

### 3. Run the Frontend

Open another terminal and navigate to the frontend directory:

```bash
cd front
```

Install dependencies:

```bash
npm install
```

Start the React development server:

```bash
npm run dev
```

Frontend runs at the URL displayed by Vite, usually:

```text
http://localhost:5173
```

### 4. Run Using Docker

From the backend directory (where the Docker Compose file is located):

```bash
docker compose up --build -d
```

Stop the containers:

```bash
docker compose down
```

The Docker setup uses a persistent MySQL volume so database records remain available when containers are restarted.

## 🔗 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a user |
| POST | `/api/auth/login` | Login |
| POST | `/api/tickets` | Create a ticket |
| GET | `/api/tickets/my` | Get personal tickets |
| GET | `/api/tickets` | Get tickets accessible to admin/agent |
| GET | `/api/tickets/{id}` | Get ticket details |
| PUT | `/api/tickets/{id}/assign/{agentId}` | Assign ticket |
| PUT | `/api/tickets/{id}/status` | Update ticket status |
| PUT | `/api/tickets/{id}/priority` | Update ticket priority |
| POST | `/api/comments/ticket/{ticketId}` | Add a comment |
| GET | `/api/comments/ticket/{ticketId}` | Get ticket comments |
| GET | `/api/ticket-history/ticket/{ticketId}` | Get ticket history |
| POST | `/api/admin/agents` | Create support agent |
| GET | `/api/admin/agents` | Get all support agents |

Protected endpoints require a valid JWT access token and appropriate role permissions.

## 🔐 Security

- Stateless authentication using JWT.
- BCrypt password hashing.
- Role-based endpoint authorization.
- Server-side validation.
- Access control for tickets, comments, and ticket history.
- Sensitive configuration managed through environment variables.

## 🛠️ Future Improvements

- Email notifications for ticket updates.
- File attachments in tickets.
- Real-time notifications.
- Dashboard analytics.
- Advanced ticket search.

## 👨‍💻 Author

**Shreyansh Tripathi**

GitHub: https://github.com/Ductblue

---

If you find this project useful, consider giving the repository a ⭐.
