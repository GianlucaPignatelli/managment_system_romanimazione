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
    public List<Availability> findByUsername(String username) throws DAOException {
        List<Availability> list = new ArrayList<>();
        String query = "SELECT id, username, availability_date, start_time, end_time, is_full_day FROM availability WHERE username = ?";

        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Availability a = mapRow(rs);
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
    @Override
    public List<String> findAvailableAnimators(java.time.LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime) throws DAOException {
        List<String> userList = new ArrayList<>();
        // Query Logic:
        // 1. Match the exact date
        // 2. Either "Full Day" is true
        // 3. OR (Start <= requestedStart AND End >= requestedEnd)
        // We use DISTINCT because a user might have entered multiple slots (though overlap checks prevent that)
        String query = "SELECT DISTINCT username FROM availability " +
                       "WHERE availability_date = ? " +
                       "AND (" +
                       "  is_full_day = TRUE " +
                       "  OR (start_time <= ? AND end_time >= ?)" +
                       ") " +
                       "AND username NOT IN (" +
                       "  SELECT pa.animator_username " +
                       "  FROM party_assignments pa " +
                       "  JOIN party p ON pa.party_id = p.id " +
                       "  WHERE p.party_date = ? " +
                       "  AND pa.status = 'ACCEPTED'" +
                       "  AND p.status != 'CANCELLED'" + 
                       ")";

        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, Date.valueOf(date));
            stmt.setTime(2, Time.valueOf(startTime));
            stmt.setTime(3, Time.valueOf(endTime));
            // Fourth parameter for the subquery date check
            stmt.setDate(4, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    userList.add(rs.getString(USERNAME_COL));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding available animators: " + e.getMessage(), e);
        }
        return userList;
    }

    @Override
    public List<Availability> findByDate(java.time.LocalDate date) throws DAOException {
        List<Availability> list = new ArrayList<>();
        String sql = "SELECT id, username, availability_date, start_time, end_time, is_full_day FROM availability WHERE availability_date = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while(rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding availability by date", e);
        }
        return list;
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