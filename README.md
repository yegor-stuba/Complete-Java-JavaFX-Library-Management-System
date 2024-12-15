# 📚 StudyShare: Collaborative Library Management System

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
    - Add, edit, or delete user accounts.
- 📚 **Book Inventory Management**
    - Add new books, update book details, and track their availability.
- 🖥 **Admin Dashboard with Statistics**
    - View the total number of books in the library.
    - Track the total number of registered users.
    - View detailed statistics of borrowed and returned books.
- 📈 **Detailed Transaction Reports**
    - Monitor which user borrowed or returned specific books.
    - Access transaction details available only for admins.

---

## 🛠 Tech Stack

- **Backend**: Java, Spring Boot
- **Frontend**: JavaFX (FXML for UI design)
- **Database**: SQLite
- **Build Tool**: Maven
- **IDE**: IntelliJ IDEA

---

## 🚀 Getting Started

### Prerequisites
1. Install **Java 17+**.
2. Install **Maven**.
3. Set up **MySQL** and create the database using `schema.sql` in `server/src/main/resources`.

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/studyshare.git
   cd studyshare
   ```
2. Navigate to the server directory and build the backend:
   ```bash
   cd server
   mvn clean install
   ```
3. Configure the database connection in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/studyshare
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```
5. Launch the frontend application by running the JavaFX client.

---

## 📥 Login Information

### User Login
- **Username**: `user`
- **Password**: `123456`

### Admin Login
- **Username**: `admin`
- **Password**: `admin`

---

## 📷 Screenshots

### Login Page
A clean and intuitive login interface for both users and admins.
<img width="797" alt="image" src="https://github.com/user-attachments/assets/7d66100b-d703-4fd1-9072-2245ad211ed3" />

### Registration Page
Easily register new users with a simple and efficient form.
<img width="798" alt="image" src="https://github.com/user-attachments/assets/c8c98642-d146-4b72-b0ab-77b544ce8125" />

### Admin Dashboard
Get an overview of library statistics and manage books and users seamlessly.
![Admin Dashboard](https://github.com/user-attachments/assets/fd66d67a-b290-49dc-9ca8-7d791164108a)

### Book Management
Manage book inventory, add new books, and update details with ease.

### Transactions
Monitor user transactions, including who borrowed or returned specific books.


---

## 🤝 Contributing

We welcome contributions! Please fork the repository and submit a pull request with detailed explanations of your changes.

---

## ✉️ Contact
