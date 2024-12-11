# 📚 StudyShare: Collaborative Library Management System

<<<<<<< HEAD
Architecture Requirements (5 points)
Client-server architecture: Properly implement client-server communication using RESTful APIs.
Client (JavaFX): Create a functional user interface for all interactions, including login, registration, viewing books, and managing book data.
Server (Spring Boot): Implement server-side business logic, handle requests, and interact with the database.
Communication: Ensure efficient RESTful communication between client and server.
Database Design and Implementation (4 points)
Implement the following tables with proper relationships:
Users Table: Includes columns for user_id, username, password , role_id, and email.
Roles Table: Includes role_id and role_name to define user roles (e.g., User, Library Administrator).
Books Table: Includes book_id, title, author, isbn, and available_copies.
Transactions Table: Includes transaction_id, user_id, book_id, action, and date.
Establish relationships between:
Users and Roles via role_id.
Transactions with Users and Books.
Login and Registration (4 points)
Registration: Allow new users to register with details such as username, password, email, and role.
Login: Enable users to log in with proper authentication 
Authorization: Implement role-based access control for user and administrator functionalities.
User Interface (2 points)
Design a user-friendly interface in JavaFX:
Include forms for login and registration.
Provide clear navigation for borrowing, lending, and managing books.
Ensure an intuitive design for administrators to manage user accounts and books.
Role Implementation and Functionalities (6 points)
User Role (3 points):

Viewing Books: Browse and search the catalog.
Borrowing Books: Select and borrow books, reducing available copies.
Lending Books: Return borrowed books to increase available copies.
Library Administrator Role (3 points):

Managing Books: Add, update, and remove books.
Managing Users: Create, edit, and delete user accounts.
Overseeing Transactions: Monitor borrowing and lending activities.
Overall Functionality and GitHub Project Management (5 points)
Functionality:
Ensure all CRUD operations are implemented for books and users.
Maintain robust error handling (e.g., preventing unauthorized actions, avoiding SQL injection).
GitHub Usage:
Regular commits reflecting progress.
Use branches, pull requests, and issue tracking for collaboration.
Document project details in the repository (e.g., README with setup instructions, features, and architecture overview).
Key Deliverables:
Fully functional client-server application with JavaFX UI and Spring Boot backend.
Database implementation covering all specified tables and relationships.
Features for user login, registration, and role-specific functionalities.
Proper use of GitHub for version control and project management.
=======
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)

StudyShare is a robust library management system designed to simplify book lending, user management, and transaction tracking for administrators and users. It ensures efficient operation, secure authentication, and a smooth user experience.

---

## ✨ Features

### For Users:
- 🛠 **User Registration and Login**
- 📖 **Browse Available Books**
- 🔄 **Borrow and Return Books**
- 📊 **View Transaction History**

### For Admins:
- 👤 **User Management**
- 📚 **Book Inventory Management**
- 🖥 **Admin Dashboard**
- 📈 **Detailed Transaction Reports**

---

## 🛠 Tech Stack

- **Backend**: Java, Spring Boot
- **Frontend**: JavaFX (FXML for UI design)
- **Database**: MySQL
- **Build Tool**: Maven
- **Authentication**: JWT (JSON Web Token)

---

## 🚀 Getting Started

### Prerequisites
1. Install **Java 17+**.
2. Install **Maven**.
3. Set up **MySQL** and create the database using `schema.sql` in `server/src/main/resources`.
>>>>>>> 1e6513ae29a67684a41f7a3ea7f2686f5d9e11ad
