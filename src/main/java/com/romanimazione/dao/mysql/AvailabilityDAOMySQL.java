package com.romanimazione.dao.mysql;

import com.romanimazione.dao.AvailabilityDAO;

import com.romanimazione.entity.Availability;
import com.romanimazione.exception.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityDAOMySQL implements AvailabilityDAO {

    @Override
    public void saveAvailability(Availability availability) throws DAOException {
        String query = "INSERT INTO availability (username, availability_date, start_time, end_time, is_full_day) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, availability.getUsername());
            stmt.setDate(2, Date.valueOf(availability.getDate()));
            stmt.setTime(3, Time.valueOf(availability.getStartTime()));
            stmt.setTime(4, Time.valueOf(availability.getEndTime()));
            stmt.setBoolean(5, availability.isFullDay());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error saving availability: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Availability> findByUsername(String username) throws DAOException {
        List<Availability> list = new ArrayList<>();
        String query = "SELECT id, username, availability_date, start_time, end_time, is_full_day FROM availability WHERE username = ?";

        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Availability a = new Availability();
                    a.setId(rs.getInt("id"));
                    a.setUsername(rs.getString("username"));
                    a.setDate(rs.getDate("availability_date").toLocalDate());
                    a.setStartTime(rs.getTime("start_time").toLocalTime());
                    a.setEndTime(rs.getTime("end_time").toLocalTime());
                    a.setFullDay(rs.getBoolean("is_full_day"));
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error searching availability: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void updateAvailability(Availability availability) throws DAOException {
        String query = "UPDATE availability SET availability_date = ?, start_time = ?, end_time = ?, is_full_day = ? WHERE id = ? AND username = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(availability.getDate()));
            stmt.setTime(2, Time.valueOf(availability.getStartTime()));
            stmt.setTime(3, Time.valueOf(availability.getEndTime()));
            stmt.setBoolean(4, availability.isFullDay());
            // Where clauses
            stmt.setInt(5, availability.getId());
            stmt.setString(6, availability.getUsername());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                throw new DAOException("Update failed: Availability not found or permission denied.");
            }
        } catch (SQLException e) {
            throw new DAOException("Error updating availability: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAvailability(Availability availability) throws DAOException {
        // Delete by ID is safest if valid, otherwise by composite key
        String query;
        if (availability.getId() > 0) {
            query = "DELETE FROM availability WHERE id = ?";
        } else {
            // Fallback if no ID (should rely on controller to provide ID though)
            query = "DELETE FROM availability WHERE username = ? AND availability_date = ?";
        }

        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (availability.getId() > 0) {
                stmt.setInt(1, availability.getId());
            } else {
                stmt.setString(1, availability.getUsername());
                stmt.setDate(2, Date.valueOf(availability.getDate()));
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting availability: " + e.getMessage(), e);
        }
    }
}