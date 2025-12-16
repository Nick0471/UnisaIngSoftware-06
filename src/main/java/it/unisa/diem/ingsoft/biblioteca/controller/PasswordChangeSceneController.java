/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.UPDATE_QUESTIONS_PATH;


/**
 * @brief  Controller per il cambio password per accedere al sistema".
 *
 * Permette di verificare la vecchia password inserita e di sostituirla
 * con una nuova.
 *
 * Estende {@link GuiController} per ereditare funzionalità comuni.
 */
public class PasswordChangeSceneController extends GuiController {


    private AuthService passwordService;

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
    private Button btnUpdate;

    @FXML
    private Button btnReturn;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField newPasswordConfirm;




    /**
     * @brief Gestisce il tentativo di cambio password.
     * @param event L'evento ActionEvent generato dal click sul pulsante "Aggiorna".
     */
    @FXML
    private void handleGoToViewHomepage(ActionEvent event) {


        String newPass = this.newPassword.getText();
        String confirmPass = this.newPasswordConfirm.getText();


        if(newPass.isEmpty()){
            this.popUp(Alert.AlertType.WARNING, "Valutazione password","Compila tutti i campi obligatori");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            this.popUp(Alert.AlertType.WARNING, "Valutazione password", "Le due password non coincidono.");
            return;
        }


        if (newPass.length() < 6 || newPass.length() > 10) {
            this.popUp(Alert.AlertType.WARNING, "Valutazione password", "La nuova password deve essere da 6 a 10 caratteri.");
            return;
        }

        //cambio effettivamente la password
        this.passwordService.changePassword(newPass);

        this.changeScene(event, HOMEPAGE_PATH);
    }



    @FXML
    private void handleGoToUpdateQuestions(ActionEvent event) {
        super.changeScene(event, UPDATE_QUESTIONS_PATH);
    }

}