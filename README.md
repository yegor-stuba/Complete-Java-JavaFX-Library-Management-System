# StudyShare Library Management System

A collaborative platform for students to share and manage their book collections.

## Project Overview
StudyShare is designed to help students track their book collections and discover books owned by their peers. The system facilitates book borrowing and lending, promoting resource sharing within the academic community.

the project description:
Library Management System designed to help students keep track of their book collections and know which books their friends possess. This system will allow users to register, view, and manage their books effectively. You can name the application, reflecting its purpose of promoting collaboration and resource sharing among students.

The primary goal of this library management system is to enable students to borrow and lend books with ease, fostering a supportive academic community. By sharing resources, students can save money and enhance their learning experience through greater access to diverse literature.

ARCHITECTURE OVERVIEW
CLIENT-SERVER MODEL
ROLES OF CLIENT AND
SERVER

Client-Server Architecture
• The architecture follows a client-server model, where the client is responsible for the user interface and interacts with the server to perform operations.
• The client application, built with JavaFX, provides an interactive interface for users to log in, register, and manage their book collections.

Client (JavaFX)
• Handles user interactions, such as logging in, registering users, adding books, and viewing available books.
• Communicates with the server to request and send data using RESTful
APIs.

Server (Spring Boot)
• Processes client requests, manages the business logic, and interacts with the database.
• Exposes endpoints via RESTful APls to allow the client to perform CRUD(CREATE, READ, UPDATE and DELETE) operations on the book and user data.

TECHNOLOGY STACK
Client side: JavaFX
Used to create the user interface of the application.
Provides modern Ul components and supports user interactions.

Server side: Spring Boot
Used for the server side, process the incoming requests from the client
Database: SQLite
For storing the info about books, users, role and etc
Communication: RESTful
APIs
Enables efficient client-server communication using standard HTTP methods.
Supports operations like retrieving
(GET), creating (POST), updating (PUT), and deleting (DELETE) resources.

KEY FEATURES OF THE APPLICATION
USER ROLES
User
Can view available books, borrow and lend books.
Interacts with the system by searching for books and managing their borrowed items.

Library Administrator
Responsible for adding, updating, and removing books from the inventory.
Manages user accounts and oversees borrowing/lending activities.

FUNCTIONALITY
Book Registration
• Users can register new books, providing necessary details.
Viewing Books
• Users can browse and search for books within the library system.

Managing User Accounts:
• Administrators can create, edit, and delete user accounts as needed.
Error Handling
Emphasizes the importance of preventing unauthorized actions and input errors.
Implements measures like SQL injection prevention to enhance security.

DATABASE DESIGN:
USERS TABLE:

Columns
• user_id, username, password, role_id, email
Description
• Stores user information including unique identifiers, usernames, hashed passwords, and associated roles.
Relationships
• Linked to the Roles table via role_id, indicating the user's permissions.

BOOKS TABLE:

Columns
• book_id, title, author, isbn, available_copies
Description
• Contains details about each book in the library, including title, author, ISBN number, and the number of available copies.
Relationships
• Not directly linked to users but related to lending/borrowing actions via a Transactions table.

ROLES TABLE:

Columns
• role_id, role_name
Description
• Defines user roles (e.g., User, Library Administrator), determining access levels and functionalities within the application.
Relationships
• Associated with the Users table through role_ _id.

TRANSACTIONS TABLE:

Columns
• transaction _id, user_id, book_id, action, date
Description
• Tracks actions taken by users (e.g., borrowing or returning books).
Relationships
• Links Users and Books tables through user_id and book_id.