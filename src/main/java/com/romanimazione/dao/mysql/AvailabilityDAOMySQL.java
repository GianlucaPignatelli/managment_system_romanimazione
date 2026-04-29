package com.romanimazione.dao.mysql;

import com.romanimazione.dao.AvailabilityDAO;

import com.romanimazione.entity.Availability;
import com.romanimazione.exception.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityDAOMySQL implements AvailabilityDAO {

    private static final String USERNAME_COL = "username";
    private static final String DATE_COL = "availability_date";
    private static final String START_COL = "start_time";
    private static final String END_COL = "end_time";
    private static final String FULL_COL = "is_full_day";

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
    public List<Availability> findAllAvailabilities() throws DAOException {
        List<Availability> list = new ArrayList<>();
        String query = "SELECT id, username, availability_date, start_time, end_time, is_full_day FROM availability";

        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Availability a = mapRow(rs);
                    list.add(a);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving availabilities: " + e.getMessage(), e);
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

    private Availability mapRow(ResultSet rs) throws SQLException {
        Availability a = new Availability();
        a.setId(rs.getInt("id"));
        a.setUsername(rs.getString(USERNAME_COL));
        a.setDate(rs.getDate(DATE_COL).toLocalDate());
        a.setStartTime(rs.getTime(START_COL).toLocalTime());
        a.setEndTime(rs.getTime(END_COL).toLocalTime());
        a.setFullDay(rs.getBoolean(FULL_COL));
        return a;
    }
}