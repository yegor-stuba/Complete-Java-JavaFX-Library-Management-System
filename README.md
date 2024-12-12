📚 StudyShare: Collaborative Library Management System



StudyShare is a robust library management system designed to simplify book lending, user management, and transaction tracking for administrators and users. It ensures efficient operation, secure authentication, and a smooth user experience.

✨ Features

For Users:

🛠 User Registration and Login

📖 Browse Available Books

🔄 Borrow and Return Books

📊 View Personal Transaction History

For Admins:

👤 User Management

Add, edit, or delete user accounts.

📚 Book Inventory Management

Add new books, update book details, and track their availability.

📈 Admin Dashboard with Statistics

View the total number of books in the library.

Track the total number of registered users.

View detailed statistics of borrowed and returned books.

🖥 Transaction Reports

Monitor which user borrowed or returned specific books.

Access transaction details available only for admins.

🛠 Tech Stack

Backend: Java, Spring Boot

Frontend: JavaFX (FXML for UI design)

Database: SQLite

Build Tool: Maven

IDE: IntelliJ IDEA

🚀 Getting Started

Prerequisites

Install Java 17+.

Install Maven.

Set up MySQL and create the database using schema.sql in server/src/main/resources.

Installation

Clone the repository:

git clone https://github.com/your-repo/studyshare.git
cd studyshare

Navigate to the server directory and build the backend:

cd server
mvn clean install

Configure the database connection in application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/studyshare
spring.datasource.username=your_username
spring.datasource.password=your_password

Run the application:

mvn spring-boot:run

Launch the frontend application by running the JavaFX client.

📥 Login Information

To access the system, use the following credentials:

User Login

Username: user

Password: password

Admin Login

Username: admin

Password: admin

📷 Screenshots

Login Page
<img width="796" alt="image" src="https://github.com/user-attachments/assets/be1a85b4-ba29-4728-a9e9-b62eb939c6b2" />




Registration Page
<img width="797" alt="image" src="https://github.com/user-attachments/assets/281f93a7-7428-4c2d-8f75-7336c6ca52df" />




Admin Dashboard (admin part)
<img width="1430" alt="image" src="https://github.com/user-attachments/assets/3f18bb90-8375-48f3-9ce7-af61361ac1af" />




Book Mangment
<img width="1440" alt="image" src="https://github.com/user-attachments/assets/59475f43-b92f-4a05-8cdb-956cdea267ac" />



Statistics (admin part)
<img width="1440" alt="image" src="https://github.com/user-attachments/assets/82b7571c-5453-4588-90da-2dbed348a473" />



Transactions 
<img width="1440" alt="image" src="https://github.com/user-attachments/assets/5fac0af1-106a-49a5-865d-fc85156a1c04" />



📖 Additional Features

Borrow and return books seamlessly.

Users can only view their own transaction history.

Admins can:

Track who borrowed or returned books.

Monitor library usage statistics.

View total books in the library and total users registered.

🤝 Contributing

We welcome contributions! Please fork the repository and submit a pull request with detailed explanations of your changes.

✉️ Contact

For any inquiries or feedback, please reach out to Veres Group at veres.group@example.com.Stay connected for updates and new features!
