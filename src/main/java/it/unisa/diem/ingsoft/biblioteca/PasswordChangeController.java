
package it.unisa.diem.ingsoft.biblioteca;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;



public class PasswordChangeController extends BaseController{
    
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
