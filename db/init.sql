CREATE DATABASE IF NOT EXISTS taskdb;
USE taskdb;

CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN DEFAULT FALSE
);

-- New table for task summaries
CREATE TABLE task_summaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Primary key, auto-increment, unique identifier for each summary record',
    completed_count INT NOT NULL COMMENT 'Number of completed tasks',
    pending_count INT NOT NULL COMMENT 'Number of pending tasks',
    total_tasks INT NOT NULL COMMENT 'Total number of tasks (= completed + pending)',
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when the summary was generated'
);


-- New table for auditing
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action_type VARCHAR(50) NOT NULL,
    action_details VARCHAR(200) NOT NULL, -- The bug is here: the column is too small
    log_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert enough data to trigger the bugs
INSERT INTO tasks (id, title, description) VALUES
(1, 'Fix login page', 'The login form is broken on Safari.'),
(9, 'Refactor user service', 'The user service is too complex.'),
(10, 'Write API documentation', 'Document all public endpoints.'),
-- THIS IS THE NEW, LONGER TITLE THAT WILL TRIGGER THE BUG
(11, 'Investigate and resolve the critical performance degradation issue on the main dashboard for enterprise-level clients', 'The main dashboard is loading slowly.'),
(12, 'Deploy to staging', 'Final deployment for Q3 release.'),
(130, 'Update third-party libraries for security compliance', 'Several libraries have known vulnerabilities.');