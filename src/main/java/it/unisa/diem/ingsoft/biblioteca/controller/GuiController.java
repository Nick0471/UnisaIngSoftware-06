/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import java.util.function.Consumer;

import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @brief Classe astratta base per fornire metodi ai controller dell'interfaccia grafica.
 *
 * Fornisce la possibilità di poter cambiare scena e
 * di visualizzare messaggi pop-up di errore
 *
 */

public abstract class GuiController {
    private ServiceRepository serviceRepository;

    public void setServices(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /**
     * @brief Cambia la visualizzazione della scena corrente caricando un nuovo file FXML.
     *
     * @param event L'evento che ha scatenato il cambio scena (es. click su un pulsante).
     * @param scene Il percorso della nuova scena da caricare salvata tramite file .fxml.
     */
    protected void changeScene(ActionEvent event, String scene) {
        try{
            FXMLLoader loader = Scenes.setupLoader(scene, this.serviceRepository);
            Parent root = Scenes.getRoot(loader);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.show();
        } catch (NullPointerException e) {
            this.popUp("ERRORE: Il file '" + scene + "' non è stato trovato nel percorso specificato.");
        }
    }

    /**
     * @brief Apre una nuova finestra modale e permette di configurare il suo controller.
     *
     * @param scene Il percorso del file FXML da caricare.
     * @param title Il titolo della nuova finestra.
     * @param controllerSetup Passa i dati al controller.
     * @param <T> Il tipo del controller che si sta caricando.
     */
    protected <T> void modalScene(String scene, String title, Consumer<T> controllerSetup) {
        FXMLLoader loader = Scenes.setupLoader(scene, this.serviceRepository);
        Parent root = Scenes.getRoot(loader);
        T controller = (T) loader.getController();

        if (controllerSetup != null) {
            controllerSetup.accept(controller);
        }

        Scene newScene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(newScene);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * @brief Chiude la scena corrente.
     *
     * @param event L'evento che ha scatenato il cambio scena (es. click su un pulsante).
     */
    protected void closeScene(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * @brief Mostra una finestra di pop-up con un messaggio di errore.
     *
     * @param message Il messaggio di testo da visualizzare nel pop-up.
     */
    protected void popUp(String message) {
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
