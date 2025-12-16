package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import static it.unisa.diem.ingsoft.biblioteca.Views.*;


public class ForgottenPasswordSceneController extends GuiController {

    private  AuthService passwordService;

    /**
     * @brief Setter per i servizi di gestione della password.
     * @param serviceRepository Contenitore dei servizi da cui recuperare il Service
     *
     */
    @Override
    public void setServices(ServiceRepository serviceRepository) {
        super.setServices(serviceRepository);
        this.passwordService = serviceRepository.getPasswordService();
    }

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnVerify;

    @FXML
    private TextField answer1Field;


    @FXML
    private TextField answer2Field;

    @FXML
    private TextField answer3Field;


    public void handleVerify(ActionEvent event){

        boolean a1 = this.passwordService.checkAnswer(answer1Field.getText(), 1);
        boolean a2 =  this.passwordService.checkAnswer(answer2Field.getText(), 2);
        boolean a3 = this.passwordService.checkAnswer(answer3Field.getText(), 3);

        if( a1 && a2 && a3 )
           changeScene(event,NEW_PASSWORD_PATH);
       else {
           this.popUp(Alert.AlertType.WARNING, "Valutazione password", "I campi non sono corretti");
           return;
       }
    }


    public void handleCancel(ActionEvent event){
        changeScene(event,LOGIN_PATH);
    }
}
