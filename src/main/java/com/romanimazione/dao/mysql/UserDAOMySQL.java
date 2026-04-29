package com.romanimazione.dao.mysql;

import com.romanimazione.dao.UserDAO;

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
    public void updateUser(User user) throws DAOException {
        String query = "UPDATE users SET password = ?, role = ?, nome = ?, cognome = ?, email = ?, is_super_admin = ? WHERE username = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getPassword());
            stmt.setString(2, user.getRole());
            stmt.setString(3, user.getNome());
            stmt.setString(4, user.getCognome());
            stmt.setString(5, user.getEmail());
            stmt.setBoolean(6, user.isSuperAdmin());
            stmt.setString(7, user.getUsername());
            
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DAOException("User not found: " + user.getUsername());
            }
        } catch (SQLException e) {
             throw new DAOException("Error updating user", e);
        }
    }
    
    @Override
    public void deleteUser(String username) throws DAOException {
        String query = "DELETE FROM users WHERE username = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting user: " + e.getMessage(), e);
        }
    }

    @Override
    public java.util.List<User> findAllUsers() throws DAOException {
        java.util.List<User> list = new java.util.ArrayList<>();
        String query = "SELECT id, username, password, role, nome, cognome, email, is_super_admin FROM users";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
             
             while (rs.next()) {
                 User user = mapRow(rs);
                 list.add(user);
             }
        } catch (SQLException e) {
            throw new DAOException("Error finding all users", e);
        }
        return list;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        String roleStr = rs.getString("role");
        User user = null;
        
        if ("ANIMATORE".equalsIgnoreCase(roleStr)) {
            user = new Animatore();
        } else if ("AMMINISTRATORE".equalsIgnoreCase(roleStr)) {
            user = new Amministratore();
        } else {
            // Fallback anonimo in caso di dati db inconsistenti
            user = new User() {}; 
        }
        
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(roleStr);
        user.setNome(rs.getString("nome"));
        user.setCognome(rs.getString("cognome"));
        user.setEmail(rs.getString("email"));
        
        // Handle super admin if column exists (try/catch for schema compatibility)
        try {
            user.setSuperAdmin(rs.getBoolean("is_super_admin"));
        } catch (SQLException e) {
            // Column might not exist yet
            user.setSuperAdmin(false);
        }
        return user;
    }


    @Override
    public void saveUser(User user) throws DAOException {
        String query = "INSERT INTO users (username, password, role, nome, cognome, email, is_super_admin) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getNome());
            stmt.setString(5, user.getCognome());
            stmt.setString(6, user.getEmail());
            stmt.setBoolean(7, user.isSuperAdmin());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
             System.err.println("UserDAOMySQL: INSERT FAILED. " + e.getMessage());
             throw new DAOException("Error saving user (Strict Mode): " + e.getMessage(), e);
        }
    }
}
