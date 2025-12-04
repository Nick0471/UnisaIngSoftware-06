
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

import it.unisa.diem.ingsoft.biblioteca.PasswordService;

public class PasswordChangeController {
    
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
        changeScene(event, "homepage.fxml");
    }
    
    
    private void changeScene(ActionEvent event, String scene) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(scene));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene new_scene = new Scene(root);
            stage.setScene(new_scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("ERRORE: Impossibile caricare il file '" + scene + "'");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("ERRORE: Il file '" + scene + "' non è stato trovato nel percorso specificato.");
        }
    }

    private PasswordService passwordService;
    public PasswordChangeController() {
        // Qui devi inizializzare la classe CONCRETA che implementa il servizio.
        // Esempio: se hai una classe "PasswordServiceImpl", scriverai:
        this.passwordService = new DatabasePasswordService();
    }

    public void comparison(){
        if(check(currentPassword.getText());
    }



    
    
    
}
