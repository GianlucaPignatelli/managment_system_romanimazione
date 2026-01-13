module com.romanimazione {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;

    opens com.romanimazione.controller.graphic to javafx.fxml;

    exports com.romanimazione.view;
    exports com.romanimazione.controller.graphic;
    exports com.romanimazione.controller.application;
    exports com.romanimazione.bean;
    exports com.romanimazione.entity;
    exports com.romanimazione.exception;
}
