/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.exception.UnsetPasswordException;
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
public class LogInSceneController extends GuiController {


    private PasswordService passwordService;

    public LogInSceneController(PasswordService passwordService){
        this.passwordService=passwordService;
    }

    @FXML
    private PasswordField insertedPassword;

    @FXML
    private Button btnLogin;


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

        try {
            if (this.passwordService.check(pass))
                changeScene(event, "homepageScene.fxml");
            else
                popUp("La password inserita non è corretta.");

        }catch (UnsetPasswordException e){
            popUp(e.getMessage());
        }


    }


}
