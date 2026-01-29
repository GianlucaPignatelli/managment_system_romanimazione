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
        String query = "INSERT INTO party (name, type, address, party_date, client_name, client_phone, start_time, end_time, children_count, animators_required, description, cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error saving party: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Party> findAllParties() throws DAOException {
        List<Party> list = new ArrayList<>();
        String query = "SELECT id, name, type, address, party_date, client_name, client_phone, start_time, end_time, children_count, animators_required, description, cost FROM party";
        
        try (Connection conn = MySQLDAOFactory.createConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
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

                list.add(p);
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding parties: " + e.getMessage(), e);
        }
        return list;
    }
}
