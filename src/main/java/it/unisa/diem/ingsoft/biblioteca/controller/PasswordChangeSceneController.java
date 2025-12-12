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

import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;


/**
 * @brief Questo Controller gestisce la scena "Modifica Passwrod".
 */
public class PasswordChangeSceneController extends GuiController {


    private PasswordService passwordService;

    @Override
    public void setServices(ServiceRepository serviceRepository){
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
            this.changeScene(event, HOMEPAGE_PATH );
        } else {
            this.popUp("La nuova password deve essere da 6 a 10 caratteri.");

        }

    }
}
