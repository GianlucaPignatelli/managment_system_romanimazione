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
        String query = "INSERT INTO party (name, type, address, party_date, client_name, client_phone, start_time, end_time, children_count, animators_required, description, cost, status, equipment_category) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            stmt.setString(14, party.getEquipmentCategory());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error saving party: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Party> findAllParties() throws DAOException {
        List<Party> list = new ArrayList<>();
        String query = "SELECT id, name, type, address, party_date, client_name, client_phone, start_time, end_time, children_count, animators_required, description, cost, status, equipment_category FROM party";
        
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Party p = mapRowToParty(rs);
                loadAssignments(p, conn);
                list.add(p);
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding parties: " + e.getMessage(), e);
        }
        return list;
    }

    @Override
    public Party getPartyById(int id) throws DAOException {
        String query = "SELECT id, name, type, address, party_date, client_name, client_phone, start_time, end_time, children_count, animators_required, description, cost, status, equipment_category FROM party WHERE id = ?";
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
             stmt.setInt(1, id);
             try (ResultSet rs = stmt.executeQuery()) {
                 if (rs.next()) {
                     Party p = mapRowToParty(rs);
                     loadAssignments(p, conn);
                     return p;
                 }
                 return null;
             }
        } catch (SQLException e) {
            throw new DAOException("Error getting party: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void update(Party party) throws DAOException {
        String query = "UPDATE party SET name=?, type=?, address=?, party_date=?, client_name=?, client_phone=?, start_time=?, end_time=?, children_count=?, animators_required=?, description=?, cost=?, status=?, equipment_category=? WHERE id=?";
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
            stmt.setString(14, party.getEquipmentCategory());
            stmt.setInt(15, party.getId());
            
            stmt.executeUpdate();
            
            // Mantiene il mapping dell'Application Controller!
            syncAssignments(party, conn);
            
        } catch (SQLException e) {
            throw new DAOException("Error updating party: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteParty(int id) throws DAOException {
       try (Connection conn = MySQLDAOFactory.createConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM party WHERE id=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
       } catch (SQLException e) {
            throw new DAOException("Error deleting party", e);
       }
    }
    
    private void loadAssignments(Party p, Connection conn) throws SQLException {
        String query = "SELECT animator_username, status, assigned_at FROM party_assignments WHERE party_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, p.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String user = rs.getString("animator_username");
                    com.romanimazione.entity.AssignmentStatus status = com.romanimazione.entity.AssignmentStatus.valueOf(rs.getString("status"));
                    Timestamp ts = rs.getTimestamp("assigned_at");
                    
                    p.getAssignmentStatuses().put(user, status);
                    if (ts != null) {
                        p.getAssignmentTimestamps().put(user, ts.toLocalDateTime());
                    }
                }
            }
        }
    }
    
    private void syncAssignments(Party p, Connection conn) throws SQLException {
        // Distrugge le vecchie relazioni e ricrea quelle calcolate dal Controller (Mapping ORM Puro)
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM party_assignments WHERE party_id = ?")) {
            stmt.setInt(1, p.getId());
            stmt.executeUpdate();
        }
        
        String insert = "INSERT INTO party_assignments (party_id, animator_username, status, assigned_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insert)) {
            for (String user : p.getAssignmentStatuses().keySet()) {
                stmt.setInt(1, p.getId());
                stmt.setString(2, user);
                stmt.setString(3, p.getAssignmentStatuses().get(user).name());
                
                java.time.LocalDateTime ldt = p.getAssignmentTimestamps().get(user);
                if (ldt != null) {
                    stmt.setTimestamp(4, Timestamp.valueOf(ldt));
                } else {
                    stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                }
                stmt.addBatch();
            }
            stmt.executeBatch();
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
        p.setEquipmentCategory(rs.getString("equipment_category"));
        return p;
    }
}
