-- 1. Create sequences first
CREATE TABLE IF NOT EXISTS hibernate_sequences (
    sequence_name TEXT PRIMARY KEY,
    next_val BIGINT
);

INSERT OR REPLACE INTO hibernate_sequences (sequence_name, next_val)
VALUES ('books', 1);

-- 2. Create users table before its indexes
CREATE TABLE IF NOT EXISTS users (
                                     user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     username VARCHAR(50) UNIQUE NOT NULL,
                                     password VARCHAR(100) NOT NULL,
                                     email VARCHAR(100) UNIQUE NOT NULL,
                                     role VARCHAR(20) NOT NULL DEFAULT 'USER',
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     last_login TIMESTAMP
);
-- 3. Create indexes after table exists
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- 4. Create roles table
CREATE TABLE IF NOT EXISTS roles (
    role_id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS books (
    book_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    isbn TEXT UNIQUE,
    available_copies INTEGER NOT NULL DEFAULT 1,
    owner_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    available BOOLEAN DEFAULT TRUE,
    borrower_id INTEGER REFERENCES users(user_id),
    FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE SET NULL
);

<<<<<<< Updated upstream
=======
-- Insert initial users
INSERT OR REPLACE INTO users (user_id, username, password, email, role)
VALUES
    (1, 'admin', 'admin', 'admin@studyshare.com', 'ADMIN'),
    (2, 'user', 'password', 'user@studyshare.com', 'USER');

-- Insert sample book
INSERT OR REPLACE INTO books (title, author, isbn, available_copies, owner_id)
VALUES ('The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 3, 1),
       ('testio', 'test', '1234567890123', 1, 1),
       ('How To Make All Happen', 'John Doe', '1234569890123', 1, 1);
>>>>>>> Stashed changes



-- 6. Create book indexes
CREATE INDEX IF NOT EXISTS idx_books_title ON books(title);
CREATE INDEX IF NOT EXISTS idx_books_isbn ON books(isbn);
CREATE INDEX IF NOT EXISTS idx_books_owner ON books(owner_id);

CREATE TABLE IF NOT EXISTS transactions (
                                            transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
                                            book_id INTEGER NOT NULL,
                                            user_id INTEGER NOT NULL,
                                            active BOOLEAN DEFAULT TRUE,
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                            FOREIGN KEY (book_id) REFERENCES books(book_id),
                                            FOREIGN KEY (user_id) REFERENCES users(user_id)
);


-- 8. Create transaction indexes
CREATE INDEX IF NOT EXISTS idx_transactions_user ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_book ON transactions(book_id);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(created_at);

-- 9. Insert initial data
INSERT OR IGNORE INTO hibernate_sequences (sequence_name, next_val) VALUES ('books', 1);
INSERT OR IGNORE INTO roles (role_name) VALUES ('USER'), ('ADMIN');

-- 10. Create admin user
DELETE FROM users WHERE username = 'admin';
-- Ensure admin user exists
INSERT OR REPLACE INTO users (username, password, email, role)
VALUES ('admin', 'admin', 'admin@studyshare.com', 'ADMIN');

-- Add a test user
INSERT OR REPLACE INTO users (username, password, email, role)
VALUES ('user', 'password', 'user@studyshare.com', 'USER');


INSERT OR REPLACE INTO books (book_id, title, author, isbn, available_copies, owner_id)
VALUES (1, 'The Great Gatsby', 'F. Scott Fitzgerald', '9780743273565', 3, 2);
