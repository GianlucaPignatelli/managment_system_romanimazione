package com.romanimazione.dao.file;

import com.romanimazione.dao.EquipmentDAO;
import com.romanimazione.entity.Equipment;
import com.romanimazione.exception.DAOException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class EquipmentDAOFile extends GenericFileDAO<Equipment> implements EquipmentDAO {
    
    public EquipmentDAOFile() {
        super("equipment.json");
    }

    @Override
    public void saveEquipment(Equipment equipment) throws DAOException {
        List<Equipment> list = load(new TypeReference<List<Equipment>>(){});
        int maxId = list.stream().mapToInt(Equipment::getId).max().orElse(0);
        equipment.setId(maxId + 1);
        list.add(equipment);
        save(list);
    }

    @Override
    public void updateEquipment(Equipment equipment) throws DAOException {
        List<Equipment> list = load(new TypeReference<List<Equipment>>(){});
        boolean updated = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == equipment.getId()) {
                list.set(i, equipment);
                updated = true;
                break;
            }
        }
        if (!updated) throw new DAOException("Equipment not found");
        save(list);
    }

    @Override
    public void deleteEquipment(int id) throws DAOException {
        List<Equipment> list = load(new TypeReference<List<Equipment>>(){});
        if (list.removeIf(e -> e.getId() == id)) {
            save(list);
        } else {
            throw new DAOException("Equipment not found");
        }
    }


    @Override
    public List<Equipment> findAllEquipment() throws DAOException {
        return load(new TypeReference<List<Equipment>>(){});
    }
}
