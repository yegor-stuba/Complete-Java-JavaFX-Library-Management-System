# StudyShare Library Management System

A collaborative platform for students to share and manage their book collections.

## Project Overview
StudyShare is designed to help students track their book collections and discover books owned by their peers. The system facilitates book borrowing and lending, promoting resource sharing within the academic community.

## Architecture
### Client-Server Model
- **Client (JavaFX)**
  - User interface and interactions
  - Book management views
  - Authentication flows
  - Real-time updates

- **Server (Spring Boot)**
  - RESTful API endpoints
  - Business logic
  - Database operations
  - Security implementation

### Technology Stack
- Client: JavaFX 21
- Server: Spring Boot 3.2.0
- Database: SQLite
- Authentication: JWT
- Communication: RESTful APIs

## Core Features
- [x] User Authentication
- [x] Book Management
- [x] Transaction System
- [x] Role-based Access Control
- [x] Search Functionality

## Database Schema
- Users (user_id, username, password, email, role)
- Books (book_id, title, author, isbn, available_copies, owner_id)
- Transactions (transaction_id, user_id, book_id, type, date, due_date)
- Roles (role_id, role_name)

## Security Features
- JWT Authentication
- Role-based Authorization
- Input Validation
- Error Handling
- SQL Injection Prevention

## Current Status
- [x] Basic Architecture
- [x] Database Setup
- [x] User Authentication
- [x] Book Management
- [x] Transaction System
- [ ] Advanced Search (In Progress)
- [ ] Analytics Dashboard (Planned)