package com.romanimazione.dao;

import com.romanimazione.entity.User;
import com.romanimazione.entity.Animatore;
import com.romanimazione.entity.Amministratore;
import com.romanimazione.exception.DAOException;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOMySQL implements UserDAO {

    @Override
    public User findUserByIdentifier(String identifier) throws DAOException {
        try {
            Connection conn = MySQLDAOFactory.createConnection();
            String query = "SELECT id, username, password, role, nome, cognome, email FROM users WHERE username = ? OR email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            ResultSet rs = stmt.executeQuery();

            User user = null;
            if (rs.next()) {
                String roleStr = rs.getString("role");
                com.romanimazione.entity.Role roleEnum = com.romanimazione.entity.Role.fromString(roleStr);

                if (roleEnum == com.romanimazione.entity.Role.ANIMATORE) {
                    user = new Animatore();
                } else if (roleEnum == com.romanimazione.entity.Role.AMMINISTRATORE) {
                    user = new Amministratore();
                } else {
                    user = new User(); // Generic fallback
                }
                
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(roleStr);
                user.setNome(rs.getString("nome"));
                user.setCognome(rs.getString("cognome"));
                user.setEmail(rs.getString("email"));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            return user;
        } catch (SQLException e) {
            throw new DAOException("Error searching user: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveUser(User user) throws DAOException {
        try {
            Connection conn = MySQLDAOFactory.createConnection();
            String query = "INSERT INTO users (username, password, role, nome, cognome, email) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getNome());
            stmt.setString(5, user.getCognome());
            stmt.setString(6, user.getEmail());
            
            stmt.executeUpdate();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
             throw new DAOException("Error saving user: " + e.getMessage(), e);
        }
    }
}
