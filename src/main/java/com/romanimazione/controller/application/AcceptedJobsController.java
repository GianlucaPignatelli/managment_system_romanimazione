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
        dao.checkTimeouts(); // Clean up states if necessary
        
        List<Party> accepted = dao.findAcceptedJobs(animator.getUsername(), startDate, endDate);
        List<PartyBean> beans = new ArrayList<>();
        
        for (Party p : accepted) {
            PartyBean pb = PartyBean.fromEntity(p);
            // Include status (which is definitely ACCEPTED)
            pb.getAssignmentStatuses().put(animator.getUsername(), com.romanimazione.entity.AssignmentStatus.ACCEPTED);
            
            // Bring timestamps as well
            java.time.LocalDateTime ts = dao.getAssignmentTimestamp(p.getId(), animator.getUsername());
            if (ts != null) {
                pb.getAssignmentTimestamps().put(animator.getUsername(), ts);
            }
            
            beans.add(pb);
        }
        return beans;
    }
}
