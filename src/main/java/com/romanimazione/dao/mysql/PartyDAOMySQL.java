package com.romanimazione.dao.mysql;

import com.romanimazione.dao.PartyDAO;
import com.romanimazione.entity.Party;
import com.romanimazione.exception.DAOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartyDAOMySQL implements PartyDAO {

    @Override
    public void saveParty(Party party) throws DAOException {
        String query = "INSERT INTO party (name, type, address, party_date, client_name, client_phone, start_time, end_time, children_count, animators_required, description, cost, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, party.getName());
            stmt.setString(2, party.getType());
            stmt.setString(3, party.getAddress());
            stmt.setDate(4, Date.valueOf(party.getDate()));
            
            stmt.setString(5, party.getClientName());
            stmt.setString(6, party.getClientPhone());
            stmt.setTime(7, Time.valueOf(party.getStartTime()));
            stmt.setTime(8, Time.valueOf(party.getEndTime()));
            
            if (party.getChildrenCount() != null) {
                stmt.setInt(9, party.getChildrenCount());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }
            
            stmt.setInt(10, party.getAnimatorsRequired());
            stmt.setString(11, party.getDescription());
            stmt.setDouble(12, party.getCost());
            stmt.setString(13, party.getStatus().name());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error saving party: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Party> findAllParties() throws DAOException {
        List<Party> list = new ArrayList<>();
        String query = "SELECT id, name, type, address, party_date, client_name, client_phone, start_time, end_time, children_count, animators_required, description, cost, status FROM party";
        
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapRowToParty(rs));
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding parties: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void assignAnimator(int partyId, String animatorUsername) throws DAOException {
        String query = "INSERT INTO party_assignments (party_id, animator_username, status, assigned_at) " +
                       "VALUES (?, ?, 'PENDING', CURRENT_TIMESTAMP) " +
                       "ON DUPLICATE KEY UPDATE status='PENDING', assigned_at=CURRENT_TIMESTAMP";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, partyId);
            stmt.setString(2, animatorUsername);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new DAOException("Error assigning animator: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getAssignedAnimators(int partyId) throws DAOException {
        List<String> list = new ArrayList<>();
        String query = "SELECT animator_username FROM party_assignments WHERE party_id = ?";
        
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, partyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("animator_username"));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving assigned animators: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void updateStatus(int partyId, com.romanimazione.entity.PartyStatus status) throws DAOException {
        String query = "UPDATE party SET status = ? WHERE id = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, partyId);
            
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                 throw new DAOException("Party with ID " + partyId + " not found.");
            }
            
        } catch (SQLException e) {
            throw new DAOException("Error updating party status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Party> findJobOffers(String animatorUsername) throws DAOException {
        List<Party> list = new ArrayList<>();
        // Join Party and PartyAssignments to get details of pending offers
        String query = "SELECT p.*, pa.status as assign_status " +
                       "FROM party p " +
                       "JOIN party_assignments pa ON p.id = pa.party_id " +
                       "WHERE pa.animator_username = ? AND pa.status = 'PENDING'";
        
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, animatorUsername);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Party p = mapRowToParty(rs);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding job offers: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public List<Party> findAcceptedJobs(String animatorUsername, java.time.LocalDate startDate, java.time.LocalDate endDate) throws DAOException {
        List<Party> list = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT p.*, pa.status as assign_status ");
        queryBuilder.append("FROM party p ");
        queryBuilder.append("JOIN party_assignments pa ON p.id = pa.party_id ");
        queryBuilder.append("WHERE pa.animator_username = ? AND pa.status = 'ACCEPTED' ");
        
        if (startDate != null) {
            queryBuilder.append("AND p.party_date >= ? ");
        }
        if (endDate != null) {
            queryBuilder.append("AND p.party_date <= ? ");
        }
        queryBuilder.append("ORDER BY p.party_date ASC, p.start_time ASC");

        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            
            int paramIndex = 1;
            stmt.setString(paramIndex++, animatorUsername);
            
            if (startDate != null) {
                stmt.setDate(paramIndex++, Date.valueOf(startDate));
            }
            if (endDate != null) {
                stmt.setDate(paramIndex++, Date.valueOf(endDate));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Party p = mapRowToParty(rs);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding accepted jobs: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public void updateAssignmentStatus(int partyId, String animatorUsername, com.romanimazione.entity.AssignmentStatus status) throws DAOException {
        String query = "UPDATE party_assignments SET status = ? WHERE party_id = ? AND animator_username = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, partyId);
            stmt.setString(3, animatorUsername);
            
            int affected = stmt.executeUpdate();
            if (affected == 0) {
                 throw new DAOException("Assignment not found for party " + partyId + " and user " + animatorUsername);
            }
            
        } catch (SQLException e) {
            throw new DAOException("Error updating assignment status: " + e.getMessage(), e);
        }
    }

    @Override
    public com.romanimazione.entity.AssignmentStatus getAssignmentStatus(int partyId, String animatorUsername) throws DAOException {
        String query = "SELECT status FROM party_assignments WHERE party_id = ? AND animator_username = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, partyId);
            stmt.setString(2, animatorUsername);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return com.romanimazione.entity.AssignmentStatus.valueOf(rs.getString("status"));
                }
            }
            return null; // No assignment found
        } catch (SQLException e) {
            throw new DAOException("Error getting assignment status: " + e.getMessage(), e);
        }
    }  

    @Override
    public int getProposalCount(int partyId) throws DAOException {
        String query = "SELECT COUNT(*) FROM party_assignments WHERE party_id = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, partyId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new DAOException("Error count proposals: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void removeAssignment(int partyId, String animatorUsername) throws DAOException {
        String sql = "DELETE FROM party_assignments WHERE party_id = ? AND animator_username = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, partyId);
            stmt.setString(2, animatorUsername);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error removing assignment", e);
        }
    }

    // Extraction helper to reduce duplication
    
    @Override
    public java.time.LocalDateTime getAssignmentTimestamp(int partyId, String animatorUsername) throws DAOException {
        String query = "SELECT assigned_at FROM party_assignments WHERE party_id = ? AND animator_username = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, partyId);
            stmt.setString(2, animatorUsername);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("assigned_at");
                    if (ts != null) {
                        return ts.toLocalDateTime();
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DAOException("Error getting assignment timestamp: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void checkTimeouts() throws DAOException {
        // Automatically updates assignments that have been pending for > 24 hours
        String query = "UPDATE party_assignments SET status = 'TIMEOUT' WHERE status = 'PENDING' AND assigned_at < (NOW() - INTERVAL 24 HOUR)";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error checking timeouts: " + e.getMessage(), e);
        }
    }

    private Party mapRowToParty(ResultSet rs) throws SQLException {
        Party p = new Party();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setType(rs.getString("type"));
        p.setAddress(rs.getString("address"));
        p.setDate(rs.getDate("party_date").toLocalDate());
        
        p.setClientName(rs.getString("client_name"));
        p.setClientPhone(rs.getString("client_phone"));
        p.setStartTime(rs.getTime("start_time").toLocalTime());
        p.setEndTime(rs.getTime("end_time").toLocalTime());
        
        int children = rs.getInt("children_count");
        if (!rs.wasNull()) {
            p.setChildrenCount(children);
        }
        
        p.setAnimatorsRequired(rs.getInt("animators_required"));
        p.setDescription(rs.getString("description"));
        p.setCost(rs.getDouble("cost"));
        p.setStatus(com.romanimazione.entity.PartyStatus.valueOf(rs.getString("status")));
        return p;
    }
}
