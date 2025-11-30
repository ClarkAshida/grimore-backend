-- Create disciplines table
CREATE TABLE disciplines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255),
    location VARCHAR(255),
    nature VARCHAR(50) NOT NULL DEFAULT 'OBLIGATORY' CHECK (nature IN ('OBLIGATORY', 'OPTIONAL')),
    semester INTEGER NOT NULL CHECK (semester > 0),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'PASSED', 'FAILED', 'LOCKED')),
    total_hours VARCHAR(50) NOT NULL DEFAULT 'H30' CHECK (total_hours IN ('H0', 'H30', 'H60', 'H90', 'H120')),
    absences_count INTEGER NOT NULL DEFAULT 0 CHECK (absences_count >= 0),
    class_schedules VARCHAR(255) NOT NULL,
    CONSTRAINT fk_disciplines_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_disciplines_student_id ON disciplines(student_id);
CREATE INDEX idx_disciplines_status ON disciplines(status);
CREATE INDEX idx_disciplines_semester ON disciplines(semester);

