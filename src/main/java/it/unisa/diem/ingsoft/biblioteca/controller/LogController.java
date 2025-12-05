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
    PasswordField passwordField;

    @FXML
    Button btnLogin;

    @FXML
    private void handleLogin(ActionEvent event)){

        if(usernameField.getText().equalsIgnoreCase(NOME CHE STA NEL DATABASE))


    }

}
