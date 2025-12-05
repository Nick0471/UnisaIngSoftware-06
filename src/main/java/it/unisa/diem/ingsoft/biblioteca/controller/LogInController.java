package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;


/**
 * @brief Controller per la gestione dell'autenticazione utente.
 * * Questa classe gestisce l'interazione tra la vista di Login (FXML)
 * e la logica di business per la verifica delle credenziali.
 * Estende la classe base GuiController per le funzionalità comuni.
 */
public class LogInController extends GuiController {

    /**
     * Service responsabile della verifica della sicurezza e delle password.
     */
    private PasswordService passwordService;

    public LogInController(PasswordService passwordService){
        this.passwordService=passwordService;
    }


    /**
     * Campo di input per l'inserimento della password
     */
    @FXML
    PasswordField insertedPassword;


    /**
     * @brief Bottone per confermare l'ccesso.
     */
    @FXML
    Button btnLogin;


    /**
     * @brief Gestisce l'evento di login quando viene premuto il pulsante "Accedi".
     * * Recupera la password inserita dall'utente, la valida tramite
     * PasswordService e, in caso di successo, effettua il cambio
     * scena verso la Homepage. In caso contrario, mostra un popup di errore.
     * * @param event L'evento generato dal click sul pulsante (ActionEvent).
     */
    @FXML
    private void handleLogin(ActionEvent event){
        // Password inserita dall'utente da verificare
        String pass = insertedPassword.getText();

        if (this.passwordService.check(pass))
            changeScene(event,"homepageScene.fxml");
        else
            popUpError("La password inserita non è corretta.");

    }


}
