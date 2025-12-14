/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.exception.UnsetPasswordException;
import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;


/**
 * @brief  Controller per la verifica di accesso al sistema".
 *
 * Permette di verifica che le credenziali inserite dall'utente prima di
 * accedere alla homepage.
 *
 * Estende {@link GuiController} per ereditare funzionalità comuni.
 */
public class LogInSceneController extends GuiController {

    private PasswordService passwordService;


    /**
     * @brief Setter per i servizi di gestione della password.
     * @param serviceRepository Contenitore dei servizi da cui recuperare i Service
     *
     */
    @Override
    public void setServices(ServiceRepository serviceRepository){
        super.setServices(serviceRepository);
        this.passwordService= serviceRepository.getPasswordService();
    }



    @FXML
    private PasswordField insertedPassword;

    @FXML
    private Button btnLogin;


    /**
     * @brief Gestisce il tentativo di accesso al sistema.
     * @param event L'evento ActionEvent generato dal click sul pulsante.
     */
    @FXML
    private void handleLogin(ActionEvent event){
        // Password inserita dall'utente da verificare
        String pass = this.insertedPassword.getText();

        try {
            if (this.passwordService.check(pass))
                this.changeScene(event, HOMEPAGE_PATH );
            else
                this.popUp(Alert.AlertType.ERROR, "Errore password", "La password inserita non è corretta.");

        }catch (UnsetPasswordException e){
            this.popUp(Alert.AlertType.ERROR, "Errore password", e.getMessage());
        }


    }


}
