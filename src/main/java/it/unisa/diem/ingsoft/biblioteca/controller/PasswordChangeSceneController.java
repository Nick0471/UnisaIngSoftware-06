/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;


/**
 * @brief Questo Controller gestisce la scena "Modifica Passwrod".
 *
 * Estende {@link GuiController} per ereditare le funzionalità comuni di navigazione,
 * (changeScene) e gestione dei messaggi di errore (popUpErrore).
 */
public class PasswordChangeSceneController extends GuiController {


    private PasswordService passwordService;

    @Override
    public void setService(ServiceRepository serviceRepository){
        super.setServices(serviceRepository);
        this.passwordService= serviceRepository.getPasswordService();
    }


    @FXML
    private Button btnUpdate;

    @FXML
    private PasswordField currentPassword;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField newPasswordConfirm;


    /**
     * @brief Gestisce l'evento di aggiornamento della password e il ritorno alla homepage.
     * * Il metodo esegue le seguenti operazioni:
     * 1. Recupera la password attuale inserita dall'utente.
     * 2. Verifica la correttezza della password attuale, tramite il metodo "check(String)}".
     * 3. Se la password attuale è corretta, verifica che la nuova password abbia una lunghezza di almeno 6 caratteri.
     * 4. Se i requisiti sono soddisfatti, aggiorna la password  e cambia scena verso la homepage.
     *
     * @param event L'evento ActionEvent generato dal click sul bottone 'Aggiorna'.
     */
    @FXML
    private void handleGoToViewHomepage(ActionEvent event) {
        // Password inserita dall'utente
        String pass = this.currentPassword.getText();


        if (!this.passwordService.check(pass)) {
            this.popUp("La password inserita non è corretta.");
            return;
        }

        // Cambio password solo se rispetta il requisito di lunghezza (in questo caso == 6)
        if(this.newPassword.getText().length() >= 6 && this.newPassword.getText().length() <= 10){
            this.passwordService.change(this.newPassword.getText());
            this.changeScene(event, "homepageScene.fxml");
        } else {
            this.popUp("La nuova password deve essere da 6 a 10 caratteri.");

        }

    }
}
