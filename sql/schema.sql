CREATE DATABASE IF NOT EXISTS managment_system_romanimazione;
USE managment_system_romanimazione;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role ENUM('ANIMATORE', 'AMMINISTRATORE') NOT NULL,
    nome VARCHAR(50),
    cognome VARCHAR(50),
    email VARCHAR(100)
);

-- Example Insert (Optional, but useful for testing if user runs it)
-- INSERT INTO users (username, password, role, nome, cognome, email) VALUES ('admin', 'admin', 'AMMINISTRATORE', 'Admin', 'User', 'admin@example.com');
