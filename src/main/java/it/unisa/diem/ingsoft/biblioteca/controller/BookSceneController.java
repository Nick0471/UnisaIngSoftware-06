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

    public BookSceneController(Database db){
        this.bookService = new DatabaseBookService(db);
    }

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

    private void updateTable() {
        List<Book> list = this.bookService.getAll();
        this.books = FXCollections.observableArrayList(list);
        this.bookCatalog.setItems(this.books);
    }

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

    @FXML
    private void handleBackToHome(ActionEvent event) {
        super.changeScene(event, "view/HomepageScene.fxml");
    }

    @FXML
    private void handleAddBook(ActionEvent event) {
        super.changeScene(event, "view/AddBookScene.fxml");
    }
}
