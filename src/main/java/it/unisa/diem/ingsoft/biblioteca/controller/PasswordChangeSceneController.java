/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;


/**
 * @brief Questo Controller gestisce la scena "Modifica Passwrod".
 */
public class PasswordChangeSceneController extends GuiController {


    private PasswordService passwordService;

    @Override
    public void setServices(ServiceRepository serviceRepository) {
        super.setServices(serviceRepository);
        this.passwordService = serviceRepository.getPasswordService();
    }


    @FXML
    private Button btnUpdate;

    @FXML
    private PasswordField currentPassword;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField newPasswordConfirm;


    @FXML
    private void handleGoToViewHomepage(ActionEvent event) {
        String oldPass = this.currentPassword.getText();
        String newPass = this.newPassword.getText();
        String confirmPass = this.newPasswordConfirm.getText();


        if (!this.passwordService.check(oldPass)) {
            this.popUp(Alert.AlertType.ERROR, "Errore password", "La vecchia password inserita non è corretta.");
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



        this.passwordService.change(newPass);

        this.popUp(Alert.AlertType.CONFIRMATION, "Successo", "Password aggiornata correttamente.");

        this.changeScene(event, HOMEPAGE_PATH);
    }

}