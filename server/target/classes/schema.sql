CREATE TABLE IF NOT EXISTS hibernate_sequences (
    sequence_name VARCHAR(255) PRIMARY KEY,
    next_val BIGINT
);
-- Initialize sequences
INSERT OR IGNORE INTO hibernate_sequences (sequence_name, next_val) VALUES
('transaction_seq', 1),
('book_seq', 1),
('user_seq', 1);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Books table
CREATE TABLE IF NOT EXISTS books (
    book_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    isbn TEXT UNIQUE NOT NULL,
    available_copies INTEGER NOT NULL DEFAULT 1,
    total_copies INTEGER NOT NULL DEFAULT 1,
    borrower_id INTEGER REFERENCES users(user_id)
);

-- Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    type VARCHAR(20) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(transaction_date);
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);
CREATE INDEX IF NOT EXISTS idx_books_borrower ON books(borrower_id);

-- Sample Data
-- 1. Users
INSERT OR REPLACE INTO users (username, password, email, role)
VALUES
('admin', 'admin', 'admin@studyshare.com', 'ADMIN'),
('user', 'password', 'user1@studyshare.com', 'USER'),
('me', 'myself', 'me@studyshare.com', 'USER');

-- 2. Books
INSERT OR REPLACE INTO books (title, author, isbn, available_copies, total_copies)
VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 2, 3),
('To Kill a Mockingbird', 'Harper Lee', '9780446310789', 1, 2),
('1984', 'George Orwell', '9780451524935', 3, 3),
('Pride and Prejudice', 'Jane Austen', '9780141439518', 2, 2);

-- 3. Transactions
INSERT OR REPLACE INTO transactions (transaction_id, book_id, user_id, type, transaction_date)
VALUES (1, 1, 1, 'BORROW', CURRENT_TIMESTAMP);
