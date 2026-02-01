DROP TABLE IF EXISTS party;

CREATE TABLE party (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    party_date DATE NOT NULL,
    client_name VARCHAR(100) NOT NULL,
    client_phone VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    children_count INT,
    animators_required INT NOT NULL,
    description TEXT,
    cost DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'SCHEDULED'
);
