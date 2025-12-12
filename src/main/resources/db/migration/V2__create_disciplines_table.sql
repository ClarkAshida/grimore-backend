-- Create disciplines table
CREATE TABLE disciplines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(20),
    schedule_code VARCHAR(20),
    location VARCHAR(255),
    color_hex VARCHAR(7),
    workload_hours VARCHAR(10) NOT NULL DEFAULT 'H30' CHECK (workload_hours IN ('H30', 'H45', 'H60', 'H75', 'H90', 'H120')),
    absences_hours INTEGER NOT NULL DEFAULT 0 CHECK (absences_hours >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_disciplines_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_disciplines_student_id ON disciplines(student_id);
CREATE INDEX idx_disciplines_active ON disciplines(active);

