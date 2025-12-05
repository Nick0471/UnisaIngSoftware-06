package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * @brief Controller per l'inserimento di nuovi libri all'interno del catalogo.
 *
 * Permette di specificare i vari attributi di un libro da aggiungere.
 */
public class AddBookSceneController extends GuiController{
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField genreField;
    @FXML private TextField yearField;
    @FXML private TextField isbnField;
    @FXML private TextField copiesField;
    @FXML private TextArea descriptionArea;

    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private BookService bookService;

    /**
     * @brief Setter per il bookService.
     *
     * @param bookService Il servizio da utilizzare per la gestione dei libri settato dal chiamante
     */
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * @brief Inizializza il controller
     *
     * @note Fa in modo che i campi per l'inserimento dell'anno e del numero di copie accettino solo numeri interi
     */
    @FXML
    public void initialize() {
        this.yearField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{4}")) {
                this.yearField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });


        this.copiesField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                this.copiesField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * @brief Gestisce la conferma per l'aggiunta di un nuovo libro.

     * Recupera i dati dai campi di testo, verifica che i campi obbligatori non siano vuoti
     * e converte i valori numerici. Crea un nuovo oggetto Book e salva i dati sul database.
     * In caso di errore (campi vuoti o formato errato), mostra un popup di errore.
     *
     * @param event L'evento generato dal click sul pulsante "Conferma".
     */
    @FXML
    private void handleConfirmAdd(ActionEvent event) {
        String title = this.titleField.getText();
        String author = this.authorField.getText();
        String genre = this.genreField.getText();
        String yearText = this.yearField.getText();
        String isbn = this.isbnField.getText();
        String copiesText = this.copiesField.getText();
        String description =this.descriptionArea.getText();

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || isbn.isEmpty() || yearText.isEmpty() || copiesText.isEmpty()) {
            StringBuffer sb = new StringBuffer("Errore di Validazione,");
            sb.append("Compila tutti i campi obbligatori (Titolo, Autore, ISBN, Anno, Copie).");
            super.popUpError(sb.toString());
            return;
        }

        try {
            int year = Integer.parseInt(yearText);
            int copies = Integer.parseInt(copiesText);

            Book book = new Book(title, author, genre, isbn, year, copies, copies, description);
            this.bookService.add(book);

            StringBuffer sb = new StringBuffer("Successo");
            sb.append("Libro aggiunto correttamente al catalogo");
            super.popUpError(sb.toString());

            super.closeScene(event);

        } catch (NumberFormatException e) {
            super.popUpError("Anno e numero di copie devono essere dei valori inter");
        }
    }

    /**
     * @brief Chiude la finestra di aggiunta senza salvare le modifiche.
     *
     * @param event L'evento che ha scatenato il cambio scena (es. click su un pulsante).
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        super.closeScene(event);
    }
}
