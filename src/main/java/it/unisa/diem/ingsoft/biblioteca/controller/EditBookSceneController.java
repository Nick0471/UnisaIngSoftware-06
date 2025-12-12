/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.exception.BookException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * @brief Controller per l'inserimento di nuovi libri all'interno del catalogo.
 *
 * Permette di specificare i vari attributi di un libro da aggiungere.
 * Estende {@link GuiController} per ereditare funzionalità comuni
 */
public class EditBookSceneController extends GuiController{
    @FXML private Label titleLabel;
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
    private boolean isEditMode = false;

    /**
     * @brief Costruttore vuoto del controller.
     * Viene invocato dal FXMLLoader per caricare la nuova scena
     *
     */
    public EditBookSceneController() {}

    /**
     * @brief Setter per il bookService.
     *
     * @param serviceRepository Il contenitore dei servizi da cui prelevare quello per la gestione dei libri
     */
    @Override
    public void setServices(ServiceRepository serviceRepository) {
        super.setServices(serviceRepository);
        this.bookService = serviceRepository.getBookService();
    }

    /**
     * @brief Setter per modificare i dati della tabella.
     *
     * @param book Il libro passatogli dall'handleModifyBook di BookSceneController
     */
    public void setBookToEdit(Book book) {
        if (book != null) {
            this.isEditMode = true;
            this.titleField.setText(book.getTitle());
            this.authorField.setText(book.getAuthor());
            this.genreField.setText(book.getGenre());
            this.yearField.setText(String.valueOf(book.getReleaseYear()));
            this.isbnField.setText(book.getIsbn());
            this.copiesField.setText(String.valueOf(book.getTotalCopies()));
            this.descriptionArea.setText(book.getDescription());

            this.isbnField.setDisable(true);
            this.btnConfirm.setText("Aggiorna");
            this.titleLabel.setText("Modifica Libro");
        }
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

        this.isbnField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                this.isbnField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        this.isbnField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 13) {
                this.isbnField.setText(newValue.substring(0, 13));
            }
        });
    }

    /**
     * Gestisce la conferma per l'aggiunta di un nuovo libro.

     * Recupera i dati dai campi di testo, verifica che i campi obbligatori non siano vuoti
     * e converte i valori numerici. Crea un nuovo oggetto Book e salva i dati sul database.
     * In caso di errore (campi vuoti o formato errato), mostra un popup di errore.
     *
     * event L'evento generato dal click sul pulsante "Conferma".
     */
    @FXML
    private void handleConfirmAdd(ActionEvent event) {
        String title = this.titleField.getText();
        String author = this.authorField.getText();
        String genre = this.genreField.getText();
        String yearText = this.yearField.getText();
        String isbn = this.isbnField.getText();
        String copiesText = this.copiesField.getText();
        String description = this.descriptionArea.getText();

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || isbn.isEmpty() || yearText.isEmpty() || copiesText.isEmpty()) {
            super.popUp(Alert.AlertType.ERROR,  "Errore di validazione","Compila tutti i campi obbligatori.");
            return;
        }

        try {
            int year = Integer.parseInt(yearText);
            int copies = Integer.parseInt(copiesText);

            Book book = new Book(isbn, title, author, year, copies, copies, genre, description);

            try {
                if (isEditMode) {
                    this.bookService.updateByIsbn(book);
                    super.popUp(Alert.AlertType.INFORMATION, "Successo", "Libro modificato.");
                } else {
                    this.bookService.add(book);
                    super.popUp(Alert.AlertType.INFORMATION, "Successo", "Libro aggiunto.");
                }
                super.closeScene(event);
            } catch (BookException e) {
                super.popUp(Alert.AlertType.ERROR ,"Errore salvataggio", e.getMessage());
            }

        } catch (NumberFormatException e) {
            super.popUp(Alert.AlertType.ERROR, "Errore validazione","Anno e numero di copie devono essere valori interi.");
        }
    }

    /**
     * Chiude la finestra di aggiunta senza salvare le modifiche.
     *
     * event L'evento che ha scatenato il cambio scena (es. click su un pulsante).
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        super.closeScene(event);
    }
}
