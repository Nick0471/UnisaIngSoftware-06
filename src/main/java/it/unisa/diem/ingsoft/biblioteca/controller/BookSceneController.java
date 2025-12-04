package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @brief Controller per la gestione della view del catalogo dei libri.
 *
 * Gestisce la visualizzazione dei libri e implementa un filtro di ricerca,
 * inoltre rende possibile aggiungere, modificare o rimuovere libri dalla libreria.
 *
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
     * @brief Costruttore del controller.
     * Inizializza il servizio per la gestione dei libri collegandosi al database.
     *
     * @param db Database da utilizzare.
     */
    public BookSceneController(Database db){
        this.bookService = new DatabaseBookService(db);
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

        this.searchType.getItems().addAll("Titolo", "Autore", "Genere", "Anno", "ISBN");
        this.searchType.setValue("Titolo");

        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.filterBooks(newValue);
        });

        this.updateTable();
    }

    /**
     * @brief Aggiorna la TableView recuperando tutti i libri del catalogo.
     */
    private void updateTable() {
        List<Book> list = this.bookService.getAll();
        this.books = FXCollections.observableArrayList(list);
        this.bookCatalog.setItems(this.books);
    }

    /**
     * @brief Filtra i libri nella tabella in base alla query e al tipo di ricerca selezionato.
     *
     * @param query La stringa di ricerca inserita dall'utente.
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
            case "Titolo":
                result = this.bookService.getByTitle(query);
                break;
            case "Autore":
                result = this.bookService.getByAuthor(query);
                break;
            case "Genere":
                result = this.bookService.getByGenre(query);
                break;
            case "ISBN":
                this.bookService.getByIsbn(query).ifPresent(result::add);
                break;
            case "Anno":
                try {
                    int year = Integer.parseInt(query);
                    result = this.bookService.getByReleaseYear(year);
                } catch (NumberFormatException e) {
                    super.popUpError("L'anno deve essere un numero intero.");
                    return;
                }
                break;
            default:
                this.updateTable();
        }

        this.books = FXCollections.observableArrayList(result);
        this.bookCatalog.setItems(this.books);
    }

    /**
     * @brief Rimuove il libro selezionato dal catalogo.
     *
     * Verifica che un libro sia selezionato, chiama il metodo di rimozione
     * e aggiorna il catalogo.
     */
    @FXML
    private void handleDeleteBook() {
        Book selectedBook = this.bookCatalog.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            super.popUpError("Seleziona un libro da rimuovere.");
            return;
        }

        boolean success = this.bookService.removeByIsbn(selectedBook.getIsbn());

        if (success) {
            this.updateTable();
        } else {
            super.popUpError("Errore durante la rimozione del libro.");
        }
    }

    /**
     * @brief Modifica il libro selezionato.
     *
     * Verifica che un libro sia selezionato, chiama il metodo di modifica
     * e aggiorna il catalogo.
     *
     * @note Non è possibile modificare il codice ISBN del libro selezionato
     */
    @FXML
    private void handleModifyBook() {
        Book selectedBook = this.bookCatalog.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            super.popUpError("Seleziona un libro da modificare.");
            return;
        }

        this.bookService.updateByIsbn(selectedBook);
        this.updateTable();
    }

    /**
     * @brief Torna alla scena Homepage.
     * @param event L'evento generato dal click del pulsante.
     */
    @FXML
    private void handleBackToHome(ActionEvent event) {
        super.changeScene(event, "view/HomepageScene.fxml");
    }

    /**
     * @brief Mostra la scena per l'aggiunta di un nuovo libro nel catalogo.
     * @param event L'evento generato dal click del pulsante.
     */
    @FXML
    private void handleAddBook(ActionEvent event) {
        super.changeScene(event, "view/AddBookScene.fxml");
    }
}
