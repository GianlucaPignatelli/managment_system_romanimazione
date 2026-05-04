package com.romanimazione.dao.demo;

import com.romanimazione.dao.EquipmentDAO;
import com.romanimazione.entity.Equipment;
import com.romanimazione.exception.DAOException;

import java.util.ArrayList;
import java.util.List;

public class EquipmentDAODemo implements EquipmentDAO {
    private static List<Equipment> inventory = new ArrayList<>();
    private static int idCounter = 1;

    static {
        inventory.add(new Equipment(idCounter++, "Cassa Audio Sony", "Elettronica", 2, "Nuovo", "admin"));
        inventory.add(new Equipment(idCounter++, "Costume Topolino", "Costumi", 1, "Usato", "admin"));
        inventory.add(new Equipment(idCounter++, "Macchina Zucchero Filato", "Macchinari", 1, "Nuovo", "admin"));
    }

    @Override
    public void saveEquipment(Equipment equipment) throws DAOException {
        equipment.setId(idCounter++);
        inventory.add(equipment);
    }

    @Override
    public void updateEquipment(Equipment equipment) throws DAOException {
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getId() == equipment.getId()) {
                inventory.set(i, equipment);
                return;
            }
        }
        throw new DAOException("Equipment not found");
    }

    @Override
    public void deleteEquipment(int id) throws DAOException {
        if (!inventory.removeIf(e -> e.getId() == id)) {
            throw new DAOException("Equipment not found");
        }
    }


    @Override
    public List<Equipment> findAllEquipment() throws DAOException {
        return new ArrayList<>(inventory);
    }
}
