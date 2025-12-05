package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.service.LogService;
import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LogController extends GuiController {

    private LogService logService;
    private PasswordService passwordService;

    public LogController(LogService logService, PasswordService passwordService){
        this.logService=logService;
        this.passwordService=passwordService;
    }

    @FXML
    TextField usernameField;

    @FXML
    PasswordField insertedPassword;

    @FXML
    Button btnLogin;

    @FXML
    private void handleLogin(ActionEvent event)){
        // Password inserita dall'utente da verificare
        String pass = insertedPassword.getText();

        if (!this.passwordService.check(pass)) {
            popUpError("La password inserita non è corretta.");
        }



    }

}
