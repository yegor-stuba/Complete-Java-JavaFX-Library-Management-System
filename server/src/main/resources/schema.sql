CREATE TABLE IF NOT EXISTS hibernate_sequences (
    sequence_name TEXT PRIMARY KEY,
    next_val INTEGER
);

-- Initialize sequence
INSERT OR IGNORE INTO hibernate_sequences (sequence_name, next_val)
VALUES ('users', 1);

CREATE TABLE IF NOT EXISTS roles (
    role_id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_name TEXT NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    role TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS books (
    book_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    isbn TEXT UNIQUE,
    available_copies INTEGER NOT NULL DEFAULT 1,
    owner_id INTEGER,
    FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    book_id INTEGER NOT NULL,
    type TEXT NOT NULL,
    date TIMESTAMP NOT NULL,
    due_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);



INSERT OR IGNORE INTO roles (role_name) VALUES ('USER'), ('ADMIN');


-- Clear existing admin user
DELETE FROM users WHERE username = 'admin';

-- Insert admin with BCrypt encoded password
INSERT OR IGNORE INTO users (username, password, email, role)
VALUES ('admin', '$2a$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', 'admin@studyshare.com', 'ADMIN');
