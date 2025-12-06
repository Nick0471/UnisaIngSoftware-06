package it.unisa.diem.ingsoft.biblioteca.controller;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.model.User;

import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;


public class AccountUserController extends GuiController implements Initializable {

    @FXML
    private Button btnClose;
    @FXML
    private Label labelMatricola;
    @FXML
    private Label labelNome;
    @FXML
    private Label labelCognome;
    @FXML
    private Label labelEmail;

    @FXML
    private TableView<Loan> loansTable;

    @FXML
    private TableColumn<Loan, String> columnIsbn;
    @FXML
    private TableColumn<Loan, String> columnTitle;
    @FXML
    private TableColumn<Loan, LocalDate> columnStartDate;
    @FXML
    private TableColumn<Loan, LocalDate> columnDeadline;

    private LoanService loanService;
    private BookService bookService;
    private User user;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurazione delle colonne basata sulla classe Loan
        // La stringa nel PropertyValueFactory DEVE corrispondere al nome del campo nella classe Loan

        // "bookIsbn" corrisponde a getBookIsbn()
        columnIsbn.setCellValueFactory(new PropertyValueFactory<>("bookIsbn"));

        // "loanStart" corrisponde a getLoanStart()
        columnStartDate.setCellValueFactory(new PropertyValueFactory<>("loanStart"));

        // "loanDeadline" corrisponde a getLoanDeadline()
        columnDeadline.setCellValueFactory(new PropertyValueFactory<>("loanDeadline"));

        //Configurazione della colonna TITOLO (dato derivato tramite BookService)
        columnTitle.setCellValueFactory(cellData -> {

            Loan loan = cellData.getValue();
            String isbn = loan.getBookIsbn();

            // Recupera il titolo usando il servizio libri
            return bookService.getByIsbn(isbn)
                    .map(book -> new SimpleStringProperty(book.getTitle()))
                    .orElse(new SimpleStringProperty("Titolo non trovato"));
        });
    }


    /**
     *
     */
    public void datiUtente(User user, LoanService loanService, BookService bookService) {
        this.user = user;
        this.loanService = loanService;
        this.bookService = bookService;

        // Imposta le label con i dati dell'utente passato
        if (user != null) {
            labelMatricola.setText(user.getId());
            labelNome.setText(user.getName());
            labelCognome.setText(user.getSurname());
            labelEmail.setText(user.getEmail());
        }

        // Una volta che abbiamo l'utente e i servizi, aggiorniamo la tabella
        updateTable();
    }


    /**
     *
     */
    private void updateTable() {

        // LOGICA DI FILTRO:
        // Chiamo il metodo dell'interfaccia che recupera SOLO i prestiti di questo utente
        List<Loan> userLoans = loanService.getByUserId(user.getId());

        // Inserisco i dati filtrati nella tabella
        loansTable.getItems().setAll(userLoans);
    }

    private void handleClose(ActionEvent event) {
        changeScene(event, "view/UserScene.fxml");
    }


}


