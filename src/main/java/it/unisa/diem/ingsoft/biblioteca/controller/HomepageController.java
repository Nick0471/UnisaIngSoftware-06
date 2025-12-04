package it.unisa.diem.ingsoft.biblioteca.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * @brief Controller per la view della homepage.
 *
 * Gestisce la navigazione verso le diverse sezioni dell'applicazione:
 * Modifica Password, Gestione Libri, Gestione Utenti e Prestiti.
 *
 */
public class HomepageController extends GuiController {
    @FXML private Button btnProfile;
    @FXML private Button btnBook;
    @FXML private Button btnUser;
    @FXML private Button btnLoan;

    /**
     * @brief Mostra la scena per la modifica della password del bibliotecario.
     * @param event L'evento generato dal click del pulsante.
     */
    @FXML
    void handleGoToViewProfile(ActionEvent event) {
        changeScene(event, "view/PasswordScene.fxml");
    }

    /**
     * @brief Mostra la scena per la visualizzazione del catalogo dei libri.
     * @param event L'evento generato dal click del pulsante.
     */
    @FXML
    void handleGoToViewBooks(ActionEvent event) {
        changeScene(event, "view/BookScene.fxml");
    }

    /**
     * @brief Mostra la scena per la visualizzazione della lista utenti.
     * @param event L'evento generato dal click del pulsante.
     */
    @FXML
    void handleGoToViewUsers(ActionEvent event) {
        changeScene(event, "view/UserScene.fxml");
    }

    /**
     * @brief Mostra la scena per la visualizzazione dei prestiti attivi.
     * @param event L'evento generato dal click del pulsante.
     */
    @FXML
    void handleGoToViewLoans(ActionEvent event) {
        changeScene(event, "view/LoanScene.fxml");
    }

}