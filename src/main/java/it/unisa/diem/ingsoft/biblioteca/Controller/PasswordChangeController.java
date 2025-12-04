
package it.unisa.diem.ingsoft.biblioteca.Controller;

import it.unisa.diem.ingsoft.biblioteca.DatabasePasswordService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;


public class PasswordChangeController extends GuiController {
    
    @FXML
    private Button btnUpdate;
    
   
    @FXML 
    private PasswordField currentPassword;
    
    
    @FXML 
    private PasswordField newPassword;
    
    @FXML 
    private PasswordField newPasswordConfirm;
    
               
    @FXML
    void handleGoToViewHomepage(ActionEvent event) {
            //Password inserita dall'utente
            String pass = currentPassword.getText();

            //Riferimento al database per poter usare le funzioni
            DatabasePasswordService database = new DatabasePasswordService(databaseInstance, logServiceInstance);


            if (!database.check(pass)) {
                popUpErrore("Errore di Autenticazione: La password inserita non è corretta.");
                return; // Interrompe l'operazione
            }


            //Cambio password solo se rispetta il requisito di minimo 6 caratteri
            if(newPassword.getText().length() == 6){
                database.change(newPassword.getText());
                changeScene(event, "homepage.fxml");
            }
    }



}
