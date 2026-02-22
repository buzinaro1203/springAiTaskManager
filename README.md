# Spring AI Task Manager

## 🚀 Overview
Spring AI Task Manager is a simple task management application with a backend API built in Java and a frontend built in React. This task manager includes an AI agent implemented using Spring AI.

## 🧠 Features
- Sign-in and sign-up system
- Create, delete, edit, and complete tasks
- Java API with a MySQL database
- Search tool
- Filter tool
- AI assistant with integrated tools. The assistant can create, complete, delete, and reschedule tasks
- The AI assistant has a context window of 20 messages

---

## 🏗 Architecture
- This project follows a layered architecture based on MVC principles.
- DTOs are used to transfer data without exposing the domain models.
- Mappers are used to transform DTOs into models and vice versa.
- A configuration layer is responsible for CORS and security settings.
- Security uses Basic Authentication with email and password.
- The AI module is separated into dedicated components.
- Controllers handle HTTP requests.
- Services contain business logic.
- Repositories extend `JpaRepository` for database access.

---

## 🛠 Tech Stack
- **Backend:** Java 17, Spring Boot ecosystem
- **Database:** MySQL
- **Frontend:** React.js (Vite)
- **AI Integration:** Spring AI with Gemini 2.5 Flash

## ⚙️ Installation
### Prerequisites
Java 17+, Maven, Node.js, MySQL, Git

1. Clone the repository.
```bash
git clone https://github.com/buzinaro1203/springAiTaskManager.git
cd springAiTaskManager
```
2. Backend setup.
- Configure `application.properties`
- Add your database credentials
- Add your Gemini API key
- Run the backend `mvn spring-boot:run`

3. Frontend setup.
- Navigate to frontend/todo-list-project
- Install dependencies
- Run the development server
  ```
  npm install
  npm run dev
  ```
4. Access the app
- Backend: http://localhost:8080
- Frontend: http://localhost:5173

---

## 🔐 Authentication
User authentication uses email and password. The email must follow a valid format (e.g., useremail@email.com), but it does not need to be a real email address. The password must contain at least 6 characters.

Endpoints are protected using Basic Authentication, which means they can only be accessed with credentials encoded from the user's email and password.

---

## 🤖 AI Agent
The AI agent can interact with the user like a common LLM. The difference is that it can also invoke backend functions to create, delete, complete, and reschedule tasks.

For the context window, I used the `ChatMemory` class from Spring AI. It stores the most recent messages exchanged between the user and the assistant. In this implementation, the assistant has a context window of 20 messages.

---

## 📌 Future Improvements
- Add more AI functionalities.
- Add notifications when a task is close to expiring.
- Replace Basic Authentication with JWT.
- Add login with Google.

---

## 👤 Author
Guilherme Henrique Barbosa Buzinaro  
Software Engineering Student  
GitHub: https://github.com/buzinaro1203  
LinkedIn: https://linkedin.com/in/guilhermebuzinaro
