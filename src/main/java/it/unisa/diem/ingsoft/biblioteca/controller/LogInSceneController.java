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
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;


/**
 * @brief  Questo Controller gestisce la scena "Accesso".
 *
 * Verifica che le credenziali inserite dall'utente, prima di consentire
 * l'accesso alla dashboard principale, siano corrette.
 *
 * Estende {@link GuiController} per ereditare le funzionalità di navigazione e gestione popup.
 */
public class LogInSceneController extends GuiController {

    private PasswordService passwordService;

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
     *
     * Questo metodo viene invocato quando l'utente preme il pulsante "Accedi".
     * La logica esecutiva è la seguente:
     * 1. Recupera la stringa inserita nel campo `insertedPassword`.
     * 2. Invoca {@link PasswordService#check(String)} per verificare la corrispondenza con la password salvata.
     * 3. Successo:Effettua il cambio scena verso "homepageScene.fxml".
     * 4. Fallimento:Mostra un popUp di errore ("Password non corretta").
     * 5. Eccezione:Se viene sollevata {@link UnsetPasswordException} (es. nessuna password configurata nel sistema o errore di lettura),
     * viene mostrato un popup con il messaggio dell'eccezione.
     *
     * @param event L'evento ActionEvent generato dal click sul pulsante.
     */
    @FXML
    private void handleLogin(ActionEvent event){
        // Password inserita dall'utente da verificare
        String pass = this.insertedPassword.getText();

        try {
            if (this.passwordService.check(pass))
                this.changeScene(event, "/it/unisa/diem/ingsoft/biblioteca/view/HomepageScene.fxml");
            else
                this.popUp("La password inserita non è corretta.");

        }catch (UnsetPasswordException e){
            this.popUp(e.getMessage());
        }


    }


}
