package it.unisa.diem.ingsoft.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * @brief Classe astratta base per fornire metodi ai controller dell'interfaccia grafica.
 *
 * Fornisce la possibilità di poter cambiare scena e
 * di visualizzare messaggi pop-up di errore
 *
 */
public abstract class GuiController {
    /**
     * @brief Cambia la visualizzazione della scena corrente caricando un nuovo file FXML.
     *
     * @param event L'evento che ha scatenato il cambio scena (es. click su un pulsante).
     * @param scene Il percorso della nuova scena da caricare salvata tramite file .fxml.
     */
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

    /**
     * @brief Chiude la scena corrente.
     *
     * @param event L'evento che ha scatenato il cambio scena (es. click su un pulsante).
     */
    private void closeScene(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * @brief Mostra una finestra di pop-up con un messaggio di errore.
     *
     * @param message Il messaggio di testo da visualizzare nel pop-up.
     */
    protected void popUpError(String message) {
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
