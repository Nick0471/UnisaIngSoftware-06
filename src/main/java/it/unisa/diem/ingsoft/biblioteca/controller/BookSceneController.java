/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import static it.unisa.diem.ingsoft.biblioteca.Views.EDIT_BOOK_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import it.unisa.diem.ingsoft.biblioteca.exception.BookException;
import it.unisa.diem.ingsoft.biblioteca.exception.MissingBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @brief Controller per la gestione della view del catalogo dei libri.
 *
 * Gestisce la visualizzazione dei libri e implementa un filtro di ricerca,
 * inoltre rende possibile aggiungere, modificare o rimuovere libri dalla libreria.
 *
 *Estende {@link GuiController} e implementa {@link Initializable}.
 */
public class BookSceneController extends GuiController implements Initializable {

    @FXML private ComboBox<String> searchType;
    @FXML private TextField searchField;

    @FXML private TableView<Book> bookCatalog;
    @FXML private TableColumn<Book, String> columnTitle;
    @FXML private TableColumn<Book, String> columnAuthor;
    @FXML private TableColumn<Book, String> columnGenre;
    @FXML private TableColumn<Book, Integer> columnYear;
    @FXML private TableColumn<Book, String> columnISBN;
    @FXML private TableColumn<Book, Integer> columnTotalCopies;
    @FXML private TableColumn<Book, Integer> columnCopies;
    @FXML private TableColumn<Book, String> columnDescription;

    @FXML private Button btnHome;
    @FXML private Button btnAdd;
    @FXML private Button btnModify;
    @FXML private Button btnRemove;

    private BookService bookService;
    private ObservableList<Book> books;

    /**
     * @brief Costruttore vuoto del controller.
     * Viene invocato dal FXMLLoader per caricare la nuova scena
     *
     */
    public BookSceneController() {}

    /**
     * @brief Setter per il bookService.
     *
     * @param serviceRepository Il contenitore dei servizi da cui prelevare quello per la gestione dei libri
     */
    @Override
    public void setServices(ServiceRepository serviceRepository) {
        super.setServices(serviceRepository);
        this.bookService = serviceRepository.getBookService();

        this.updateTable();
    }

    /**
     * @brief Inizializza il controller.
     * Configura le colonne della tabella, imposta i tipi di ricerca disponibili
     * e carica i dati iniziali.
     *
     * @param location La location utilizzata per risolvere i percorsi relativi all'oggetto root, o null se non nota.
     * @param resources Le risorse utilizzate per localizzare l'oggetto root, o null se non localizzato.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.columnTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        this.columnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        this.columnGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        this.columnYear.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        this.columnISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        this.columnTotalCopies.setCellValueFactory(new PropertyValueFactory<>("totalCopies"));
        this.columnCopies.setCellValueFactory(new PropertyValueFactory<>("remainingCopies"));
        this.columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            String selectedType = this.searchType.getValue();
            String input;
            if(newValue == null){
                input = "";
            }else{
                input = newValue;
            }

            if ("ISBN ".equals(selectedType)) {

                if (!input.matches("\\d*")) {
                    input = input.replaceAll("[^\\d]", "");
                }

                if (input.length() > 13) {
                    input = input.substring(0, 13);
                }

                if (!input.equals(newValue)) {
                    this.searchField.setText(input);
                    return;
                }
            }

            if ("Anno ".equals(selectedType)) {

                if (!input.matches("\\d*")) {
                    input = input.replaceAll("[^\\d]", "");
                }

                if (input.length() > 4) {
                    input = input.substring(0, 4);
                }

                if (!input.equals(newValue)) {
                    this.searchField.setText(input);
                    return;
                }
            }

            this.filterBooks(this.searchField.getText());
        });

        this.searchType.valueProperty().addListener((obs, oldVal, newVal) -> {
            this.searchField.setText("");
            this.updateTable();
        });

        this.searchType.setValue("Titolo ");


    }

    /**
     * Aggiorna la TableView recuperando tutti i libri del catalogo.
     */
    private void updateTable() {
        if (this.bookService == null) {
            return;
        }

        List<Book> list = this.bookService.getAll();
        this.books = FXCollections.observableArrayList(list);
        this.bookCatalog.setItems(this.books);
    }

    /**
     * Filtra i libri nella tabella in base alla query e al tipo di ricerca selezionato.
     *
     * query La stringa di ricerca inserita dall'utente.
     */
    @FXML
    private void filterBooks(String query) {
        if (query == null || query.isEmpty()) {
            this.updateTable();
            return;
        }

        String type = this.searchType.getValue();
        List<Book> result = new ArrayList<>();

        switch (type) {
            case "Titolo ":
                result = this.bookService.getAllByTitleContaining(query);
                break;
            case "Autore ":
                result = this.bookService.getAllByAuthorContaining(query);
                break;
            case "Genere ":
                result = this.bookService.getAllByGenreContaining(query);
                break;
            case "ISBN ":
                result = this.bookService.getAllByIsbnContaining(query);
                break;
            case "Anno ":
                try {
                    int year = Integer.parseInt(query);
                    result = this.bookService.getAllByReleaseYear(year);
                } catch (NumberFormatException e) {
                    super.popUp(Alert.AlertType.WARNING , "Errore validazione","L'anno deve essere un numero intero.");
                    return;
                }
                break;
            default:
                this.updateTable();
                return;
        }

        this.books = FXCollections.observableArrayList(result);
        this.bookCatalog.setItems(this.books);
    }

    /**
     * Rimuove il libro selezionato dal catalogo.
     *
     * Verifica che un libro sia selezionato, chiama il metodo di rimozione
     * e aggiorna il catalogo.
     */
    @FXML
    private void handleDeleteBook() {
        Book selectedBook = this.bookCatalog.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            super.popUp(Alert.AlertType.WARNING,"Errore selezione","Seleziona un libro da rimuovere.");
            return;
        }

        Optional<ButtonType> result = super.popUpConfirmation("Eliminazione Libro", "Sei sicuro di volere rimuovere il libro selezionato?");

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = false;
            try {
                success = this.bookService.removeByIsbn(selectedBook.getIsbn());
                if (success) {
                    this.updateTable();
                } else {
                    super.popUp(Alert.AlertType.ERROR, "Errore validazione", "Libro specificato inesistente.");
                }
            } catch (BookException e) {
                super.popUp(Alert.AlertType.ERROR, "Errore selezione", e.getMessage());
            }

            this.updateTable();
        }
    }

    /**
     * Modifica il libro selezionato.
     *
     * Verifica che un libro sia selezionato, chiama il metodo di modifica
     * e aggiorna il catalogo.
     *
     * Non è possibile modificare il codice ISBN del libro selezionato
     */
    @FXML
    private void handleModifyBook() {
        Book selectedBook = this.bookCatalog.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            super.popUp(Alert.AlertType.WARNING, "Errore selezione", "Seleziona un libro da modificare.");
            return;
        }

        super.modalScene(EDIT_BOOK_PATH, "Modifica Libro", (EditBookSceneController controller) -> {
            controller.setBookToEdit(selectedBook);
        });

        this.updateTable();
    }

    /**
     * Torna alla scena Homepage.
     * event L'evento generato dal click del pulsante.
     */
    @FXML
    private void handleBackToHome(ActionEvent event) {
        super.changeScene(event, HOMEPAGE_PATH);
    }

    /**
     * Mostra la scena per l'aggiunta di un nuovo libro nel catalogo.
     * event L'evento generato dal click del pulsante.
     */
    @FXML
    private void handleAddBook(ActionEvent event) {
        super.modalScene(EDIT_BOOK_PATH, "Aggiungi Libro", null);

        this.updateTable();
    }
}
