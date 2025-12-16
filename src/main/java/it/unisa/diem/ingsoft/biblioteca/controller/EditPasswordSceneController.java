/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.UPDATE_ANSWERS_PATH;

import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

/**
 * @brief Controller per il cambio password per accedere al sistema.
 *
 * Permette di verificare la vecchia password inserita e di sostituirla
 * con una nuova.
 *
 * Estende {@link GuiController} per ereditare funzionalità comuni.
 */
public class EditPasswordSceneController extends GuiController {
    @FXML private Button btnUpdate;
    @FXML private Button btnReturn;
    @FXML private Button btnUpdateQuestions;
    @FXML private PasswordField currentPassword;
    @FXML private PasswordField newPassword;
    @FXML private PasswordField newPasswordConfirm;

    private AuthService passwordService;

    /**
     * @brief Setter per i servizi di gestione della password.
     * @param serviceRepository Contenitore dei servizi da cui recuperare il Service
     */
    @Override
    public void setServices(ServiceRepository serviceRepository) {
        super.setServices(serviceRepository);
        this.passwordService = serviceRepository.getAuthService();
    }

    /**
     * @brief Gestisce il tentativo di cambio password.
     * @param event L'evento ActionEvent generato dal click sul pulsante "Aggiorna".
     */
    @FXML
    private void handleConfirmUpdate(ActionEvent event) {

        String oldPass = this.currentPassword.getText();
        String newPass = this.newPassword.getText();
        String confirmPass = this.newPasswordConfirm.getText();

        if (!this.passwordService.checkPassword(oldPass)) {
            this.popUp(Alert.AlertType.ERROR, "Errore password", "La password vecchia inserita non è corretta.");
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

        this.passwordService.changePassword(newPass);

        this.changeScene(event, HOMEPAGE_PATH);
    }

    /**
     * @brief Gestisce l'annullamento dell'operazione
     * @param event
     */
    @FXML
    private void handleReturn(ActionEvent event) {
        this.changeScene(event, HOMEPAGE_PATH);
    }

    /**
     * @brief Cambia scena per poter modificare le risposte delle domande di sicurezza
     * @param event
     */
    @FXML
    private void handleGoToUpdateSecurityQuestions(ActionEvent event) {
        this.changeScene(event, UPDATE_ANSWERS_PATH);
    }
}
