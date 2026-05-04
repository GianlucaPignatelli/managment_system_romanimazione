package com.romanimazione.controller.application;

import com.romanimazione.bean.PartyBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.dao.DAOFactory;
import com.romanimazione.dao.PartyDAO;
import com.romanimazione.entity.Party;
import com.romanimazione.exception.DAOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AcceptedJobsController extends Subject {

    public List<PartyBean> getAcceptedJobs(UserBean animator, LocalDate startDate, LocalDate endDate) throws DAOException {
        if (animator == null || animator.getUsername() == null) {
             throw new IllegalArgumentException("Invalid animator");
        }
        
        PartyDAO dao = DAOFactory.getDAOFactory().getPartyDAO();
        List<Party> allParties = dao.findAllParties();
        List<PartyBean> beans = new ArrayList<>();
        
        for (Party p : allParties) {
            com.romanimazione.entity.AssignmentStatus status = p.getAssignmentStatuses().get(animator.getUsername());
            if (status == com.romanimazione.entity.AssignmentStatus.ACCEPTED) {
                boolean isBeforeStart = startDate != null && p.getDate().isBefore(startDate);
                boolean isAfterEnd = endDate != null && p.getDate().isAfter(endDate);
                
                if (!isBeforeStart && !isAfterEnd) {
                    PartyBean pb = PartyController.mapToBean(p);
                    beans.add(pb);
                }
            }
        }
        
        // Ensure chronological ordering since the DB is no longer doing the SORT BY!
        beans.sort((b1, b2) -> {
            int dateCmp = b1.getDate().compareTo(b2.getDate());
            if (dateCmp != 0) return dateCmp;
            return b1.getStartTime().compareTo(b2.getStartTime());
        });
        
        return beans;
    }
}
