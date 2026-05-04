package com.romanimazione.dao.mysql;

import com.romanimazione.dao.EquipmentDAO;
import com.romanimazione.entity.Equipment;
import com.romanimazione.exception.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAOMySQL implements EquipmentDAO {

    @Override
    public void saveEquipment(Equipment equipment) throws DAOException {
        String sql = "INSERT INTO equipment (name, category, quantity, condition_status, admin_username) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setString(1, equipment.getName());
            stmt.setString(2, equipment.getCategory());
            stmt.setInt(3, equipment.getQuantity());
            stmt.setString(4, equipment.getCondition());
            stmt.setString(5, equipment.getAdminUsername());
            
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    equipment.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error saving equipment", e);
        }
    }

    @Override
    public void updateEquipment(Equipment equipment) throws DAOException {
        String sql = "UPDATE equipment SET name=?, category=?, quantity=?, condition_status=?, admin_username=? WHERE id=?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, equipment.getName());
            stmt.setString(2, equipment.getCategory());
            stmt.setInt(3, equipment.getQuantity());
            stmt.setString(4, equipment.getCondition());
            stmt.setString(5, equipment.getAdminUsername());
            stmt.setInt(6, equipment.getId());
            
            if (stmt.executeUpdate() == 0) {
                throw new DAOException("Equipment not found for update");
            }
        } catch (SQLException e) {
            throw new DAOException("Error updating equipment", e);
        }
    }

    @Override
    public void deleteEquipment(int id) throws DAOException {
        String sql = "DELETE FROM equipment WHERE id=?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new DAOException("Equipment not found for deletion");
            }
        } catch (SQLException e) {
            throw new DAOException("Error deleting equipment", e);
        }
    }


    @Override
    public List<Equipment> findAllEquipment() throws DAOException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT id, name, category, quantity, condition_status, admin_username FROM equipment";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapRowToEquipment(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error fetching all equipment", e);
        }
        return list;
    }

    private Equipment mapRowToEquipment(ResultSet rs) throws SQLException {
        return new Equipment(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getInt("quantity"),
            rs.getString("condition_status"),
            rs.getString("admin_username")
        );
    }
}
