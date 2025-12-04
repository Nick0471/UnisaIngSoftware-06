package it.unisa.diem.ingsoft.biblioteca.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class HomepageController extends GuiController {
    @FXML private Button btnProfile;
    @FXML private Button btnBook;
    @FXML private Button btnUser;
    @FXML private Button btnLoan;

    @FXML
    void handleGoToViewProfile(ActionEvent event) {
        changeScene(event, "view/PasswordScene.fxml");
    }

    @FXML
    void handleGoToViewBooks(ActionEvent event) {
        changeScene(event, "view/BookScene.fxml");
    }

    @FXML
    void handleGoToViewUsers(ActionEvent event) {
        changeScene(event, "view/UserScene.fxml");
    }

    @FXML
    void handleGoToViewLoans(ActionEvent event) {
        changeScene(event, "view/LoanScene.fxml");
    }

}