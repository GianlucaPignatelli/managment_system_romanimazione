CREATE TABLE IF NOT EXISTS party_assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    party_id INT NOT NULL,
    animator_username VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (party_id) REFERENCES party(id) ON DELETE CASCADE,
    FOREIGN KEY (animator_username) REFERENCES users(username) ON DELETE CASCADE,
    UNIQUE(party_id, animator_username)
);
