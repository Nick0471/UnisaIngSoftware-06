package it.unisa.diem.ingsoft.biblioteca;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HomepageController{
    @FXML
    private Button btnProfile;

    @FXML
    private Button btnBook;

    @FXML
    private Button btnUser;

    @FXML
    private Button btnLoan;

    @FXML
    void handleGoToViewProfile(ActionEvent event) {
        changeScene(event, "profilo_utente.fxml");
    }

    @FXML
    void handleGoToViewBooks(ActionEvent event) {
        changeScene(event, "catalogo_libri.fxml");
    }

    @FXML
    void handleGoToViewUsers(ActionEvent event) {
        changeScene(event, "lista_utenti.fxml");
    }

    @FXML
    void handleGoToViewLoans(ActionEvent event) {
        changeScene(event, "prestiti.fxml");
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