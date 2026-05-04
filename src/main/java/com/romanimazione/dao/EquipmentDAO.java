package com.romanimazione.dao;

import com.romanimazione.entity.Equipment;
import com.romanimazione.exception.DAOException;
import java.util.List;

public interface EquipmentDAO {
    void saveEquipment(Equipment equipment) throws DAOException;
    void updateEquipment(Equipment equipment) throws DAOException;
    void deleteEquipment(int id) throws DAOException;
    List<Equipment> findAllEquipment() throws DAOException;
}
