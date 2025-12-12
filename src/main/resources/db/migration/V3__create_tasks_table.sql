-- Create tasks table
CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    discipline_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL CHECK (type IN ('PROVA', 'TRABALHO', 'SEMINARIO', 'LISTA', 'PROJETO', 'OUTRO')),
    due_date TIMESTAMP NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_discipline FOREIGN KEY (discipline_id) REFERENCES disciplines(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_tasks_discipline_id ON tasks(discipline_id);
CREATE INDEX idx_tasks_completed ON tasks(completed);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);

