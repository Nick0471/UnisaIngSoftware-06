package it.unisa.diem.ingsoft.biblioteca.controller;


import static it.unisa.diem.ingsoft.biblioteca.Views.LOGIN_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.NEW_PASSWORD_PATH;

import it.unisa.diem.ingsoft.biblioteca.exception.UnsetAnswerException;
import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class ForgottenPasswordSceneController extends GuiController {

    private  AuthService authService;

    /**
     * @brief Setter per i servizi di gestione della password.
     * @param serviceRepository Contenitore dei servizi da cui recuperare il Service
     *
     */
    @Override
    public void setServices(ServiceRepository serviceRepository) {
        super.setServices(serviceRepository);
        this.authService = serviceRepository.getAuthService();
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


    @FXML
    public void handleVerify(ActionEvent event){

        try {
            boolean a1 = this.authService.checkAnswer(this.answer1Field.getText().trim(), 1);
            boolean a2 = this.authService.checkAnswer(this.answer2Field.getText().trim(), 2);
            boolean a3 = this.authService.checkAnswer(this.answer3Field.getText().trim(), 3);



            if (a1 && a2 && a3)
                this.changeScene(event, NEW_PASSWORD_PATH);
            else {
                this.popUp(Alert.AlertType.WARNING, "Valutazione password", "I campi non sono corretti");
                return;
            }
        }catch (UnsetAnswerException e){
            this.popUp(Alert.AlertType.ERROR, "Errore Dati", e.getMessage());
        }

    }

    @FXML
    public void handleCancel(ActionEvent event){this.changeScene(event,LOGIN_PATH);
    }
}
