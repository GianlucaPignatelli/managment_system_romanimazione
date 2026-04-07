package com.romanimazione.view.fx;

import com.romanimazione.view.MainApp;
import java.io.IOException;

/**
 * Boundary class for Main Navigation and Dashboards.
 */
public class MainFXView {

    public void showHome() throws IOException {
        MainApp.setRoot("home");
    }

    public void showAdminDashboard() throws IOException {
        MainApp.setRoot("admin_dashboard");
    }
    
    public void showAnimatorDashboard() throws IOException {
        MainApp.setRoot("animator_dashboard");
    }
    
    public void showJobOffers() throws IOException {
        MainApp.setRoot("job_offers");
    }

    public void showAcceptedJobs() throws IOException {
        MainApp.setRoot("animator_accepted_jobs");
    }

    public void showManageUsers() throws IOException {
        new UserManagementFXView().show();
    }
}
