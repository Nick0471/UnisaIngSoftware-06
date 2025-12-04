package it.unisa.diem.ingsoft.biblioteca;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class GUIController {

    protected void changeScene(ActionEvent event, String scene) {
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
