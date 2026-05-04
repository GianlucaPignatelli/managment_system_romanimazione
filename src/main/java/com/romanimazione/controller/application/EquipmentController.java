package com.romanimazione.controller.application;

import com.romanimazione.bean.EquipmentBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.EquipmentDAO;
import com.romanimazione.entity.Equipment;
import com.romanimazione.exception.DAOException;

import java.util.ArrayList;
import java.util.List;

public class EquipmentController extends Subject {

    public void addEquipment(EquipmentBean bean) throws DAOException, IllegalArgumentException {
        bean.validateSyntax();
        EquipmentDAO dao = DAOFactory.getDAOFactory().getEquipmentDAO();
        Equipment eq = mapToEntity(bean);
        dao.saveEquipment(eq);
        bean.setId(String.valueOf(eq.getId()));
        notifyObservers("Equipment " + bean.getName() + " added successfully.");
    }

    public void updateEquipment(EquipmentBean bean) throws DAOException, IllegalArgumentException {
        if (bean.getId() == null || bean.getId().isEmpty()) {
            throw new IllegalArgumentException("Equipment ID is required for update.");
        }
        bean.validateSyntax();
        EquipmentDAO dao = DAOFactory.getDAOFactory().getEquipmentDAO();
        Equipment eq = mapToEntity(bean);
        eq.setId(Integer.parseInt(bean.getId()));
        dao.updateEquipment(eq);
        notifyObservers("Equipment " + bean.getName() + " updated successfully.");
    }

    public void deleteEquipment(EquipmentBean bean) throws DAOException {
        if (bean.getId() == null || bean.getId().isEmpty()) {
            throw new IllegalArgumentException("Equipment ID is required for deletion.");
        }
        EquipmentDAO dao = DAOFactory.getDAOFactory().getEquipmentDAO();
        dao.deleteEquipment(Integer.parseInt(bean.getId()));
        notifyObservers("Equipment with ID " + bean.getId() + " deleted successfully.");
    }

    public List<EquipmentBean> getAllEquipment() throws DAOException {
        EquipmentDAO dao = DAOFactory.getDAOFactory().getEquipmentDAO();
        List<Equipment> entities = dao.findAllEquipment();
        List<EquipmentBean> beans = new ArrayList<>();
        for (Equipment e : entities) {
            beans.add(mapToBean(e));
        }
        return beans;
    }

    private Equipment mapToEntity(EquipmentBean bean) {
        Equipment eq = new Equipment();
        if (bean.getId() != null && !bean.getId().trim().isEmpty()) {
            eq.setId(Integer.parseInt(bean.getId()));
        }
        eq.setName(bean.getName());
        eq.setCategory(bean.getCategory());
        eq.setQuantity(Integer.parseInt(bean.getQuantity()));
        eq.setCondition(bean.getCondition());
        eq.setAdminUsername(bean.getAdminUsername());
        return eq;
    }

    private EquipmentBean mapToBean(Equipment eq) {
        EquipmentBean bean = new EquipmentBean();
        bean.setId(String.valueOf(eq.getId()));
        bean.setName(eq.getName());
        bean.setCategory(eq.getCategory());
        bean.setQuantity(String.valueOf(eq.getQuantity()));
        bean.setCondition(eq.getCondition());
        bean.setAdminUsername(eq.getAdminUsername());
        return bean;
    }
}
