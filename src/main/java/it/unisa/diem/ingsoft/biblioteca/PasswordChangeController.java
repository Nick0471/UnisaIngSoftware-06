
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

    private void popUpErrore(String message) {

        //Creo un nuovo Stage
        Stage confirmationStage = new Stage();
        confirmationStage.setTitle("POP-UP");

        Label mess = new Label(message);

        StackPane stackPane = new StackPane();

        stackPane.getChildren().add(mess);

        //Creo la nuova scesa dove metto la stackPane
        Scene scene = new Scene(stackPane, 250, 100);
        confirmationStage.setScene(scene);
        confirmationStage.show();
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


}
