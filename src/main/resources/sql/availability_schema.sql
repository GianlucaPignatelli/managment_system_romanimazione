CREATE TABLE IF NOT EXISTS availability (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    availability_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_full_day BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);
