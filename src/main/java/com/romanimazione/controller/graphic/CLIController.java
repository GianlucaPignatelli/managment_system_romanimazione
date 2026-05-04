package com.romanimazione.controller.graphic;

import com.romanimazione.bean.AvailabilityBean;
import com.romanimazione.bean.CredentialsBean;
import com.romanimazione.bean.SessionBean;
import com.romanimazione.bean.UserBean;
import com.romanimazione.controller.application.*;
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
    private final com.romanimazione.view.cli.EquipmentCLIView equipmentView;

    // Controllers
    private final LoginController loginController;
    private final AvailabilityController availabilityController;
    private final RegistrationController registerController;
    private final JobOfferController jobOfferController;
    private final AdminUserController adminUserController;
    private final AcceptedJobsController acceptedJobsController;
    private final EquipmentController equipmentController;

    private static final String MSG_INVALID = "Invalid choice.";
    private static final String MSG_ERR_UNEXPECTED = "Unexpected error: ";
    private static final String MSG_LOGGED_OUT = "Logged out.";

    public CLIController() {
        this.mainView = new MainCLIView();
        this.loginView = new LoginCLIView();
        this.availabilityView = new AvailabilityCLIView();
        this.partyView = new PartyCLIView();
        this.equipmentView = new com.romanimazione.view.cli.EquipmentCLIView();
        
        this.loginController = new LoginController();
        this.availabilityController = new AvailabilityController();
        this.registerController = new RegistrationController();
        this.jobOfferController = new JobOfferController();
        this.adminUserController = new AdminUserController();
        this.acceptedJobsController = new AcceptedJobsController();
        this.equipmentController = new EquipmentController();
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
        } catch (IOException e) {
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

        } catch (com.romanimazione.exception.DAOException | com.romanimazione.exception.UserNotFoundException | IOException e) {
             mainView.showError(e.getMessage());
        } catch (Exception e) {
             mainView.showError(MSG_ERR_UNEXPECTED + e.getMessage());
        }
    }

    private void handleRegistration() {
        try {
            long count = registerController.countAdmins();
            boolean isCodeSet = com.romanimazione.bean.SecurityManager.getInstance().isMasterCodeSet();
            boolean isFirstAdmin = (count == 0 || !isCodeSet);
            
            UserBean userBean = loginView.getRegistrationDetails(isFirstAdmin);
            
            registerController.register(userBean);
            
            mainView.showMessage("Registration successful! You can now login.");
        } catch (com.romanimazione.exception.DAOException | IOException | IllegalArgumentException e) {
            mainView.showError(e.getMessage());
        } catch (Exception e) {
             mainView.showError(MSG_ERR_UNEXPECTED + e.getMessage());
        }
    }
    
    private void animatorLoop() throws IOException {
        boolean loggedIn = true;
        while (loggedIn) {
            String subInput = mainView.showAnimatorMenuAndGetChoice();
            
            if ("1".equals(subInput)) {
                manageAvailability();
            } else if ("2".equals(subInput)) {
                viewJobOffersCLI();
            } else if ("3".equals(subInput)) {
                viewAcceptedJobsCLI();
            } else if ("4".equals(subInput)) {
                loggedIn = false;
                SessionBean.getInstance().setCurrentUser(null);
                mainView.showMessage(MSG_LOGGED_OUT);
            } else {
                mainView.showMessage(MSG_INVALID);
            }
        }
    }

    private void viewJobOffersCLI() {
        try {
            UserBean current = SessionBean.getInstance().getCurrentUser();
            List<com.romanimazione.bean.PartyBean> offers = jobOfferController.getPendingOffers(current);
            partyView.showJobOffers(offers, current.getUsername());
            
            if (offers.isEmpty()) return;
            
            int choice = partyView.askJobOfferSelection(offers.size());
            if (choice > 0) {
                com.romanimazione.bean.PartyBean selected = offers.get(choice - 1);
                String action = partyView.askAcceptOrReject();
                if ("A".equals(action)) {
                    jobOfferController.acceptOffer(selected, current);
                    mainView.showMessage("You ACCEPTED the job offer.");
                } else if ("R".equals(action)) {
                    jobOfferController.rejectOffer(selected, current);
                     mainView.showMessage("You REJECTED the job offer.");
                } else {
                     mainView.showMessage("Action cancelled.");
                }
            }
        } catch (Exception e) {
             mainView.showError(e.getMessage());
        }
    }

    private void viewAcceptedJobsCLI() {
        try {
            UserBean current = SessionBean.getInstance().getCurrentUser();
            
            System.out.println("\n--- FILTER ACCEPTED JOBS ---");
            java.time.LocalDate start = partyView.promptForDate("Start Date");
            java.time.LocalDate end = partyView.promptForDate("End Date");
            
            if (start == null && end == null) {
                start = java.time.LocalDate.now();
                System.out.println("Defaulting to all future jobs from today onwards.");
            }
            
            List<com.romanimazione.bean.PartyBean> accepted = acceptedJobsController.getAcceptedJobs(current, start, end);
            
            System.out.println("\n--- ACCEPTED JOBS ---");
            if (accepted.isEmpty()) {
                System.out.println("No accepted jobs found in the specified period.");
            } else {
                for (int i = 0; i < accepted.size(); i++) {
                    com.romanimazione.bean.PartyBean pb = accepted.get(i);
                    System.out.printf("%d. [%s] %s | %s - %s @ %s (Fee: %s)%n",
                            i + 1, pb.getDate(), pb.getName(), pb.getStartTime(), pb.getEndTime(), pb.getAddress(), pb.getCost());
                }
            }
        } catch (Exception e) {
            mainView.showError(e.getMessage());
        }
    }

    private void adminLoop() throws IOException {
        boolean loggedIn = true;
        PartyController partyController = new PartyController();
        boolean isSuperAdmin = Boolean.parseBoolean(SessionBean.getInstance().getCurrentUser().getIsSuperAdmin());

        while (loggedIn) {
            String input = mainView.showAdminMenuAndGetChoice(isSuperAdmin);
            try {
                loggedIn = handleAdminChoice(input, isSuperAdmin, partyController);
            } catch (com.romanimazione.exception.InvalidPartyException | com.romanimazione.exception.DAOException | IllegalArgumentException e) {
                 mainView.showError(e.getMessage());
            } catch (Exception e) {
                // Catch-all for unexpected runtime errors
                java.util.logging.Logger.getLogger(CLIController.class.getName()).log(java.util.logging.Level.SEVERE, e, () -> MSG_ERR_UNEXPECTED + e.getMessage());
                mainView.showError(MSG_ERR_UNEXPECTED + e.getMessage());
            }
        }
    }

    private boolean handleAdminChoice(String input, boolean isSuperAdmin, PartyController partyController) throws IOException, com.romanimazione.exception.DAOException, com.romanimazione.exception.InvalidPartyException {
        if ("1".equals(input)) {
            createPartyCLI(partyController);
        } else if ("2".equals(input)) {
            listPartiesCLI(partyController);
        } else {
            return isSuperAdmin ? handleSuperAdminChoice(input) : handleRegularAdminChoice(input);
        }
        return true;
    }

    private boolean handleSuperAdminChoice(String input) throws IOException, com.romanimazione.exception.DAOException {
         if ("3".equals(input)) {
             manageUsersCLI();
             return true;
         } else if ("4".equals(input)) {
             changeMasterCodeCLI();
             return true;
         } else if ("5".equals(input)) {
             manageEquipmentCLI();
             return true;
         } else if ("6".equals(input)) {
             SessionBean.getInstance().setCurrentUser(null);
             mainView.showMessage(MSG_LOGGED_OUT);
             return false;
         }
         mainView.showMessage(MSG_INVALID);
         return true;
    }
    
    private boolean handleRegularAdminChoice(String input) throws IOException {
        if ("3".equals(input)) {
            manageEquipmentCLI();
            return true;
        } else if ("4".equals(input)) {
            SessionBean.getInstance().setCurrentUser(null);
            mainView.showMessage(MSG_LOGGED_OUT);
            return false;
        }
        mainView.showMessage(MSG_INVALID);
        return true;
    }

    private void changeMasterCodeCLI() throws IOException {
        try {
            String newCode = mainView.getNewMasterCode();
            com.romanimazione.bean.SecurityManager.getInstance().setMasterCode(newCode);
            mainView.showMessage("Master Code updated successfully.");
        } catch (IllegalArgumentException e) {
            mainView.showError(e.getMessage());
        }
    }

    private void manageUsersCLI() throws IOException, com.romanimazione.exception.DAOException {
        com.romanimazione.view.cli.UserManagementCLIView userView = new com.romanimazione.view.cli.UserManagementCLIView();
        
        while (true) {
            List<UserBean> beans = adminUserController.getAllUsers();
            
            userView.showUserList(beans);
            int idToDelete = userView.askUserIdToDelete();
            
            if (idToDelete == 0) break;
            
            // Find target
            UserBean target = beans.stream().filter(b -> Integer.parseInt(b.getId()) == idToDelete).findFirst().orElse(null);
            
            if (target == null) {
                mainView.showError("User not found.");
            } else {
                try {
                    adminUserController.deleteUser(target);
                    mainView.showMessage("User deleted.");
                } catch (IllegalArgumentException e) {
                    mainView.showError(e.getMessage());
                }
            }
        }
    }

    private void createPartyCLI(PartyController controller) throws com.romanimazione.exception.InvalidPartyException, com.romanimazione.exception.DAOException, IOException {
        com.romanimazione.bean.PartyBean bean = partyView.getPartyDetails(controller.getPartyTypes());
        controller.createParty(bean);
        mainView.showMessage("Party created successfully!");
    }

    private void listPartiesCLI(PartyController controller) throws com.romanimazione.exception.DAOException, IOException {
        List<com.romanimazione.bean.PartyBean> parties = controller.getAllParties();
        partyView.showPartyList(parties);
        
        if (parties.isEmpty()) return;

        int partyId = partyView.askSelectPartyId();
        if (partyId > 0) {
            com.romanimazione.bean.PartyBean targetParty = parties.stream()
                    .filter(p -> Integer.parseInt(p.getId()) == partyId)
                    .findFirst()
                    .orElse(null);
            
            if (targetParty == null) {
                mainView.showError("Party ID not found.");
                return;
            }

            // Show full details
            partyView.showPartyDetails(targetParty);
            handlePartyAction(controller, targetParty);
        }
    }

    private void handlePartyAction(PartyController controller, com.romanimazione.bean.PartyBean targetParty) throws IOException, com.romanimazione.exception.DAOException {
        // Block modified actions if terminal status
        if (com.romanimazione.entity.PartyStatus.CANCELLED.name().equals(targetParty.getStatus()) ||
            com.romanimazione.entity.PartyStatus.COMPLETED.name().equals(targetParty.getStatus())) {
            mainView.showMessage("Action blocked: Party is " + targetParty.getStatus() + ".");
            return;
        }

        int action = partyView.askPartyAction();
        if (action == 1) { // Assign
            handleAssignAnimatorOperation(controller, targetParty);
        } else if (action == 2) { // Cancel
            handleCancelPartyOperation(controller, targetParty);
        }
    }

    private void handleAssignAnimatorOperation(PartyController controller, com.romanimazione.bean.PartyBean targetParty) throws IOException, com.romanimazione.exception.DAOException {
        List<UserBean> eligible = controller.findEligibleAnimators(targetParty);
        if (eligible.isEmpty() && partyView.askForceAssignment()) {
            eligible = controller.findAllAnimatorsForForce(targetParty);
        }
        partyView.showEligibleAnimators(eligible);
        
        if (!eligible.isEmpty()) {
            int choice = partyView.askAnimatorSelection(eligible.size());
            if (choice > 0) {
                UserBean selected = eligible.get(choice - 1);
                controller.assignAnimator(targetParty, selected);
                mainView.showMessage("Animator assigned successfully!");
            } else {
                mainView.showMessage("Assignment aborted.");
            }
        }
    }

    private void handleCancelPartyOperation(PartyController controller, com.romanimazione.bean.PartyBean targetParty) {
        try {
            controller.cancelParty(targetParty);
            mainView.showMessage("Party cancelled successfully.");
        } catch (Exception e) {
            mainView.showError(e.getMessage());
        }
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
            } catch (com.romanimazione.exception.InvalidAvailabilityException | com.romanimazione.exception.DAOException | IOException e) {
                mainView.showError(e.getMessage());
            } catch (Exception e) {
                mainView.showError(MSG_ERR_UNEXPECTED + e.getMessage());
            }
        }
    }

    private void addAvailabilityCLI() throws com.romanimazione.exception.DAOException, com.romanimazione.exception.InvalidAvailabilityException, IOException {
        AvailabilityBean bean = availabilityView.getAvailabilityDetails();
        bean.setUsername(SessionBean.getInstance().getCurrentUser().getUsername());
        availabilityController.addAvailability(bean);
        mainView.showMessage("Availability added successfully.");
    }
    
    private void updateAvailabilityCLI() throws com.romanimazione.exception.DAOException, com.romanimazione.exception.InvalidAvailabilityException, IOException {
        int id = availabilityView.getIdInput("update");
        AvailabilityBean bean = availabilityView.getAvailabilityDetails();
        bean.setId(String.valueOf(id));
        bean.setUsername(SessionBean.getInstance().getCurrentUser().getUsername());
        
        availabilityController.updateAvailability(bean);
        mainView.showMessage("Availability updated successfully.");
    }
    
    private void deleteAvailabilityCLI() throws com.romanimazione.exception.DAOException, IOException {
         int id = availabilityView.getIdInput("delete");
         AvailabilityBean bean = new AvailabilityBean();
         bean.setId(String.valueOf(id));
         bean.setUsername(SessionBean.getInstance().getCurrentUser().getUsername());
         
         availabilityController.deleteAvailability(bean);
         mainView.showMessage("Availability deleted successfully.");
    }

    private void listAvailabilityCLI() throws com.romanimazione.exception.DAOException {
        String user = SessionBean.getInstance().getCurrentUser().getUsername();
        List<AvailabilityBean> list = availabilityController.getAvailabilities(user);
        availabilityView.showAvailabilityList(list);
    }

    private void manageEquipmentCLI() throws IOException {
        boolean back = false;
        while (!back) {
            String input = equipmentView.showEquipmentMenuAndGetChoice();
            try {
                switch (input) {
                    case "1":
                        List<com.romanimazione.bean.EquipmentBean> list = equipmentController.getAllEquipment();
                        equipmentView.showEquipmentList(list);
                        break;
                    case "2":
                        com.romanimazione.bean.EquipmentBean newBean = equipmentView.getEquipmentDetails();
                        newBean.setAdminUsername(SessionBean.getInstance().getCurrentUser().getUsername());
                        equipmentController.addEquipment(newBean);
                        mainView.showMessage("Equipment added.");
                        break;
                    case "3":
                        String updateId = equipmentView.getIdInput("update");
                        com.romanimazione.bean.EquipmentBean updateBean = equipmentView.getEquipmentDetails();
                        updateBean.setId(updateId);
                        updateBean.setAdminUsername(SessionBean.getInstance().getCurrentUser().getUsername());
                        equipmentController.updateEquipment(updateBean);
                        mainView.showMessage("Equipment updated.");
                        break;
                    case "4":
                        String deleteId = equipmentView.getIdInput("delete");
                        com.romanimazione.bean.EquipmentBean deleteBean = new com.romanimazione.bean.EquipmentBean();
                        deleteBean.setId(deleteId);
                        equipmentController.deleteEquipment(deleteBean);
                        mainView.showMessage("Equipment deleted.");
                        break;
                    case "5":
                        back = true;
                        break;
                    default:
                        mainView.showMessage(MSG_INVALID);
                }
            } catch (com.romanimazione.exception.DAOException | IllegalArgumentException e) {
                mainView.showError(e.getMessage());
            }
        }
    }
}
