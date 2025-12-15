/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;


import static it.unisa.diem.ingsoft.biblioteca.Views.FORGOTTEN_PASSWORD_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;

import it.unisa.diem.ingsoft.biblioteca.exception.UnsetPasswordException;
import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;


/**
 * @brief  Controller per la verifica di accesso al sistema".
 *
 * Permette di verifica che le credenziali inserite dall'utente prima di
 * accedere alla homepage.
 *
 * Estende {@link GuiController} per ereditare funzionalità comuni.
 */
public class LogInSceneController extends GuiController {

    private AuthService authService;


    /**
     * @brief Setter per i servizi di gestione della password.
     * @param serviceRepository Contenitore dei servizi da cui recuperare i Service
     *
     */
    @Override
    public void setServices(ServiceRepository serviceRepository){
        super.setServices(serviceRepository);
        this.authService= serviceRepository.getAuthService();
    }



    @FXML
    private PasswordField insertedPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnForgottenPassword;


    /**
     * @brief Gestisce il tentativo di accesso al sistema.
     * @param event L'evento ActionEvent generato dal click sul pulsante.
     */
    @FXML
    private void handleLogin(ActionEvent event){
        // Password inserita dall'utente da verificare
        String pass = this.insertedPassword.getText();

        try {
            if (this.authService.checkPassword(pass))
                this.changeScene(event, HOMEPAGE_PATH );
            else
                this.popUp(Alert.AlertType.ERROR, "Errore password", "La password inserita non è corretta.");

        }catch (UnsetPasswordException e){
            this.popUp(Alert.AlertType.ERROR, "Errore password", e.getMessage());
        }


    }


    @FXML
    private void handleforgot(ActionEvent event){this.changeScene(event, FORGOTTEN_PASSWORD_PATH);}



}
