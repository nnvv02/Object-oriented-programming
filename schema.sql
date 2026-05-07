CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(32) NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    year INTEGER NOT NULL,
    pages INTEGER NOT NULL,
    genre VARCHAR(64) NOT NULL,
    format VARCHAR(64),
    file_size DOUBLE PRECISION,
    publisher VARCHAR(255),
    print_run INTEGER,
    duration_minutes INTEGER,
    narrator VARCHAR(255),
    subject VARCHAR(255),
    grade_level INTEGER,
    quantity INTEGER NOT NULL
);
