package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class BookSceneController extends GuiController {
    @FXML private ComboBox<String> searchType;
    @FXML private TextField searchField;

    @FXML private TableView<Book> BookCatalog;

    @FXML private TableColumn<Book, String> ColumnTitle;
    @FXML private TableColumn<Book, String> ColumnAuthor;
    @FXML private TableColumn<Book, String> ColumnGenre;
    @FXML private TableColumn<Book, Integer> ColumnYear;
    @FXML private TableColumn<Book, String> ColumnISBN;
    @FXML private TableColumn<Book, Integer> ColumnTotalCopies;
    @FXML private TableColumn<Book, Integer> ColumnCopies;
    @FXML private TableColumn<Book, String> ColumnDescription;

    @FXML private Button btnHome;
    @FXML private Button btnAdd;
    @FXML private Button btnModify;
    @FXML private Button btnRemove;
}
