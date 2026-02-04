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
    public long countAdmins() throws DAOException {
        // Count users with role 'AMMINISTRATORE'
        String query = "SELECT COUNT(*) FROM users WHERE role = 'AMMINISTRATORE'";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new DAOException("Error counting admins", e);
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
        // Simple query
        String query = "SELECT * FROM users";
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
        User user = new User();
        String roleStr = rs.getString("role");
        com.romanimazione.entity.Role roleEnum = null;
        try {
            roleEnum = com.romanimazione.entity.Role.fromString(roleStr);
        } catch (IllegalArgumentException e) {
            // ignore or fallback
        }

        if (roleEnum == com.romanimazione.entity.Role.ANIMATORE) {
            user = new Animatore();
        } else if (roleEnum == com.romanimazione.entity.Role.AMMINISTRATORE) {
            user = new Amministratore();
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
    public User findUserByIdentifier(String identifier) throws DAOException {
        String query = "SELECT * FROM users WHERE username = ? OR email = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
             throw new DAOException("Error finding user", e);
        }
        return null;
    }

    @Override
    public void saveUser(User user) throws DAOException {
        // Check schema for is_super_admin. For now, try insert with it. 
        // If it fails, fallback? Or assume schema is updated.
        // Let's assume schema updated.
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
            
            stmt.setBoolean(7, user.isSuperAdmin());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
             System.err.println("UserDAOMySQL: INSERT FAILED. " + e.getMessage());
             throw new DAOException("Error saving user (Strict Mode): " + e.getMessage(), e);
        }
    }
    
    private void saveUserLegacy(User user) throws DAOException {
        String query = "INSERT INTO users (username, password, role, nome, cognome, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getNome());
            stmt.setString(5, user.getCognome());
            stmt.setString(6, user.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error saving user (legacy): " + e.getMessage(), e);
        }
    }
}
