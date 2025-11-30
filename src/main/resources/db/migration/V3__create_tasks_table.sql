-- Create tasks table
CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    discipline_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    task_type VARCHAR(50) NOT NULL CHECK (task_type IN ('EXAM', 'HOMEWORK', 'SEMINAR', 'STUDY', 'PROJECT', 'OTHER')),
    status VARCHAR(50) NOT NULL DEFAULT 'TODO' CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE', 'STAND_BY')),
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    due_date TIMESTAMP NOT NULL,
    grade_weight DOUBLE PRECISION CHECK (grade_weight >= 0 AND grade_weight <= 10),
    grade_obtained DOUBLE PRECISION CHECK (grade_obtained >= 0 AND grade_obtained <= 10),
    CONSTRAINT fk_tasks_discipline FOREIGN KEY (discipline_id) REFERENCES disciplines(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_tasks_discipline_id ON tasks(discipline_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_tasks_priority ON tasks(priority);

