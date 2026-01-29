package com.romanimazione.controller.graphic;

import com.romanimazione.bean.AvailabilityBean;
import com.romanimazione.bean.CredentialsBean;
import com.romanimazione.bean.SessionBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.AvailabilityController;
import com.romanimazione.controller.application.LoginController;
import com.romanimazione.controller.application.PartyController;
import com.romanimazione.controller.application.RegisterController;
import com.romanimazione.view.cli.AvailabilityCLIView;
import com.romanimazione.view.cli.LoginCLIView;
import com.romanimazione.view.cli.MainCLIView;
import com.romanimazione.view.cli.PartyCLIView;

import java.io.IOException;
import java.util.List;

public class CLIController {

    // Views
    private final MainCLIView mainView;
    private final LoginCLIView loginView;
    private final AvailabilityCLIView availabilityView;
    private final PartyCLIView partyView;

    // Controllers
    private final LoginController loginController;
    private final AvailabilityController availabilityController;
    private final RegisterController registerController;

    private static final String MSG_INVALID = "Invalid choice.";

    public CLIController() {
        this.mainView = new MainCLIView();
        this.loginView = new LoginCLIView();
        this.availabilityView = new AvailabilityCLIView();
        this.partyView = new PartyCLIView();
        
        this.loginController = new LoginController();
        this.availabilityController = new AvailabilityController();
        this.registerController = new RegisterController();
    }

    public void start() {
        mainView.showWelcomeMessage();
        boolean running = true;
        try {
            while (running) {
                String input = mainView.showMainMenuAndGetChoice();

                if ("1".equals(input)) {
                    handleLogin();
                } else if ("2".equals(input)) {
                    handleRegistration();
                } else if ("3".equals(input)) {
                    running = false;
                    mainView.showGoodbyeMessage();
                } else {
                   mainView.showMessage(MSG_INVALID);
                }
            }
        } catch (Exception e) {
            mainView.showError("System Error: " + e.getMessage());
        }
    }

    private void handleLogin() {
        try {
            CredentialsBean creds = loginView.getLoginCredentials();
            UserBean user = loginController.login(creds);
            SessionBean.getInstance().setCurrentUser(user);
            
            mainView.showMessage("Login successful. Role: " + user.getRole());
            
            if ("ANIMATORE".equalsIgnoreCase(user.getRole())) {
                animatorLoop();
            } else if ("AMMINISTRATORE".equalsIgnoreCase(user.getRole())) {
                 adminLoop();
            } else {
                 mainView.showMessage("Unknown role menu.");
            }

        } catch (Exception e) {
            mainView.showError(e.getMessage());
        }
    }
    
    private void animatorLoop() throws IOException {
        boolean loggedIn = true;
        while (loggedIn) {
            String subInput = mainView.showAnimatorMenuAndGetChoice();
            
            if ("1".equals(subInput)) {
                manageAvailability();
            } else if ("2".equals(subInput)) {
                loggedIn = false;
                SessionBean.getInstance().setCurrentUser(null);
                mainView.showMessage("Logged out.");
            } else {
                mainView.showMessage(MSG_INVALID);
            }
        }
    }

    private void adminLoop() throws IOException {
        boolean loggedIn = true;
        PartyController partyController = new PartyController();

        while (loggedIn) {
            String input = mainView.showAdminMenuAndGetChoice();

            try {
                if ("1".equals(input)) {
                    createPartyCLI(partyController);
                } else if ("2".equals(input)) {
                    listPartiesCLI(partyController);
                } else if ("3".equals(input)) {
                    loggedIn = false;
                    SessionBean.getInstance().setCurrentUser(null);
                    mainView.showMessage("Logged out.");
                } else {
                    mainView.showMessage(MSG_INVALID);
                }
            } catch (Exception e) {
                mainView.showError(e.getMessage());
            }
        }
    }

    private void createPartyCLI(PartyController controller) throws Exception {
        com.romanimazione.bean.PartyBean bean = partyView.getPartyDetails(controller.getPartyTypes());
        controller.createParty(bean);
        mainView.showMessage("Party created successfully!");
    }

    private void listPartiesCLI(PartyController controller) throws Exception {
        partyView.showPartyList(controller.getAllParties());
    }

    private void manageAvailability() throws IOException {
        boolean back = false;
        while (!back) {
            String input = availabilityView.showAvailabilityMenuAndGetChoice();

            try {
                switch (input) {
                    case "1": addAvailabilityCLI(); break;
                    case "2": listAvailabilityCLI(); break;
                    case "3": updateAvailabilityCLI(); break;
                    case "4": deleteAvailabilityCLI(); break;
                    case "5": back = true; break;
                    default: mainView.showMessage(MSG_INVALID);
                }
            } catch (Exception e) {
                mainView.showError(e.getMessage());
            }
        }
    }

    private void addAvailabilityCLI() throws Exception {
        AvailabilityBean bean = availabilityView.getAvailabilityDetails();
        bean.setUsername(SessionBean.getInstance().getCurrentUser().getUsername());
        availabilityController.addAvailability(bean);
        mainView.showMessage("Availability added successfully.");
    }
    
    private void updateAvailabilityCLI() throws Exception {
        int id = availabilityView.getIdInput("update");
        AvailabilityBean bean = availabilityView.getAvailabilityDetails();
        bean.setId(id);
        bean.setUsername(SessionBean.getInstance().getCurrentUser().getUsername());
        
        availabilityController.updateAvailability(bean);
        mainView.showMessage("Availability updated successfully.");
    }
    
    private void deleteAvailabilityCLI() throws Exception {
         int id = availabilityView.getIdInput("delete");
         AvailabilityBean bean = new AvailabilityBean();
         bean.setId(id);
         bean.setUsername(SessionBean.getInstance().getCurrentUser().getUsername());
         
         availabilityController.deleteAvailability(bean);
         mainView.showMessage("Availability deleted successfully.");
    }

    private void listAvailabilityCLI() throws Exception {
        String user = SessionBean.getInstance().getCurrentUser().getUsername();
        List<AvailabilityBean> list = availabilityController.getAvailabilities(user);
        availabilityView.showAvailabilityList(list);
    }

    private void handleRegistration() {
        try {
            UserBean userBean = loginView.getRegistrationDetails();
            registerController.register(userBean);
            mainView.showMessage("Registration successful! You can now login.");
        } catch (Exception e) {
            mainView.showError(e.getMessage());
        }
    }
}
