package it.unisa.diem.ingsoft.biblioteca.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class GuiController {

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

    protected void popUpErrore(String message) {
        // Creo un nuovo Stage
        Stage confirmationStage = new Stage();
        confirmationStage.setTitle("POP-UP");

        Label mess = new Label(message);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(mess);

        // Creo la nuova scena dove metto la stackPane
        Scene scene = new Scene(stackPane, 250, 100);
        confirmationStage.setScene(scene);
        confirmationStage.show();
    }
}
