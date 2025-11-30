-- Create students table
CREATE TABLE students (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    university_name VARCHAR(255),
    course_name VARCHAR(255),
    current_semester INTEGER NOT NULL CHECK (current_semester > 0)
);

-- Create index for email lookup
CREATE INDEX idx_students_email ON students(email);

