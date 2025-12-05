package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.DatabasePasswordService;
import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;


/**
 * @brief Controller per la gestione della PasswrodScene.
 * * Questa classe gestisce l'aggiornamento della password di accesso del bibliotecario.
 * Estende {@link GuiController} per ereditare le funzionalità comuni di navigazione,
 * (changeScene) e gestione dei messaggi di errore (popUpErrore).
 */
public class PasswordChangeController extends GuiController {

    private final PasswordService passwordService;


    public PasswordChangeController(PasswordService passwordService){
        this.passwordService=passwordService;
    }


    /**
     * @brief Bottone per confermare l'aggiornamento della password,.
     */
    @FXML
    private Button btnUpdate;

    /**
     * @brief Campo di input per l'inserimento della password attuale.
     */
    @FXML
    private PasswordField currentPassword;

    /**
     * @brief Campo di input per l'inserimento della nuova password.
     */
    @FXML
    private PasswordField newPassword;

    /**
     * @brief Campo di input per la conferma della nuova password.
     */
    @FXML
    private PasswordField newPasswordConfirm;


    /**
     * @brief Gestisce l'evento di aggiornamento della password e il ritorno alla homepage.
     * * Il metodo esegue le seguenti operazioni:
     * 1. Recupera la password attuale inserita dall'utente.
     * 2. Istanzia il servizio di gestione password del database.
     * 3. Verifica la correttezza della password attuale, tramite il metodo "check(String)}".
     * 4. Se la password attuale è corretta, verifica che la nuova password abbia una lunghezza di almeno 6 caratteri.
     * 5. Se i requisiti sono soddisfatti, aggiorna la password nel database e cambia scena verso la homepage.
     * * @param event L'evento ActionEvent generato dal click sul bottone 'Aggiorna'.
     */
    @FXML
    public void handleGoToViewHomepage(ActionEvent event) {
        // Password inserita dall'utente
        String pass = currentPassword.getText();


        if (!this.passwordService.check(pass)) {
            popUpError("Errore di Autenticazione: La password inserita non è corretta.");
            return; //
        }

        // Cambio password solo se rispetta il requisito di lunghezza (in questo caso == 6)
        if(newPassword.getText().length() >= 6){
            this.passwordService.change(newPassword.getText());
            changeScene(event, "homepage.fxml");
        } else {
            popUpError("La nuova password deve essere di 6 caratteri.");

        }

    }
}