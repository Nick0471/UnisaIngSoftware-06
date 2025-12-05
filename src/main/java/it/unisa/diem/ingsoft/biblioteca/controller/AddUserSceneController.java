package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddUserSceneController extends GuiController{

    @FXML private TextField idField;
    @FXML private TextField surnameField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;


    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private UserService userService;


    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    /**
     * @brief Inizializza il controller
     *
     * @note Fa in modo che il campo per l'inserimento della matricola accetti solo numeri interi
     */
    @FXML
    public void initialize() {
        this.idField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{4}")) {
                this.idField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }


    /**
     * @brief Gestisce la conferma per l'aggiunta di un nuovo utente.
     * Recupera i dati dai campi di testo, verifica che i campi obbligatori non siano vuoti
     * e converte i valori numerici.
     * Crea un nuovo oggetto User e salva i dati sul database.
     * In caso di errore (campi vuoti o formato errato), mostra un popup di errore.
     * @param event L'evento generato dal click sul pulsante "Conferma".
     */
    @FXML
    private void handleConfirmAdd(ActionEvent event){}

    /**
     * @brief Chiude la finestra di aggiunta senza salvare le modifiche.
     *
     * @param event L'evento che ha scatenato il cambio scena (es. click su un pulsante).
     */
    @FXML
    private void handleCancel(ActionEvent event){}

}
