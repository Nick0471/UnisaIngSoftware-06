package it.unisa.diem.ingsoft.biblioteca.controller;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @brief Questo Controller gestisce la scena "Account Utente".
 *
 * Questa classe gestisce l'interfaccia grafica che mostra i dettagli del profilo
 * di un utente selezionato dalla tabella e la lista dei suoi prestiti (attivi e passati).
 * Estende {@link GuiController} e implementa {@link Initializable}.
 */
public class AccountUserSceneController extends GuiController implements Initializable {

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
    public void setServices(ServiceRepository serviceRepository){
        super.setServices(serviceRepository);
        this.loanService= serviceRepository.getLoanService();
        this.bookService= serviceRepository.getBookService();
    }


    /**
     * @brief Inizializza il controller e configura le colonne della tabella.
     *
     * Questo metodo viene chiamato automaticamente dopo che il file FXML è stato caricato.
     * Configura le `CellValueFactory` per le colonne della tabella dei prestiti.
     * In particolare, definisce una logica personalizzata per la colonna del titolo,
     * che viene recuperato dinamicamente tramite il `BookService` usando l'ISBN del prestito.
     *
     * @param location  La location utilizzata per risolvere i percorsi relativi all'oggetto radice, o null se sconosciuta.
     * @param resources Le risorse utilizzate per localizzare l'oggetto radice, o null se non localizzate.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurazione delle colonne basata sulla classe Loan
        // La stringa nel PropertyValueFactory DEVE corrispondere al nome del campo nella classe Loan

        // "bookIsbn" corrisponde a getBookIsbn()
        this.columnIsbn.setCellValueFactory(new PropertyValueFactory<>("bookIsbn"));

        // "loanStart" corrisponde a getLoanStart()
        this.columnStartDate.setCellValueFactory(new PropertyValueFactory<>("loanStart"));

        // "loanDeadline" corrisponde a getLoanDeadline()
        this.columnDeadline.setCellValueFactory(new PropertyValueFactory<>("loanDeadline"));

        //Configurazione della colonna TITOLO (dato derivato tramite BookService)
        this.columnTitle.setCellValueFactory(cellData -> {

            Loan loan = cellData.getValue();
            String isbn = loan.getBookIsbn();

            // Recupera il titolo usando il servizio libri
            return this.bookService.getByIsbn(isbn)
                    .map(book -> new SimpleStringProperty(book.getTitle()))
                    .orElse(new SimpleStringProperty("Titolo non trovato"));
        });
    }

    /**
     * @brief Imposta i dati dell'utente e inizializza i servizi necessari.
     *
     * Questo metodo deve essere chiamato dal controller precedente per passare
     * le informazioni dell'utente di cui visualizzare il profilo.
     * Aggiorna le label dell'interfaccia con i dati anagrafici e popola la tabella dei prestiti.
     *
     * @param user        L'oggetto User contenente i dati dell'utente da visualizzare.
     * @param loanService Il servizio per gestire le operazioni sui prestiti.
     * @param bookService Il servizio per gestire le operazioni sui libri (usato per recuperare i titoli).
     */
    public void setUserProfile(User user, LoanService loanService, BookService bookService) {
        this.user = user;
        this.loanService = loanService;
        this.bookService = bookService;

        // Imposta le label con i dati dell'utente passato
        if (user != null) {
            this.labelMatricola.setText(user.getId());
            this.labelNome.setText(user.getName());
            this.labelCognome.setText(user.getSurname());
            this.labelEmail.setText(user.getEmail());
        }

        // Una volta che abbiamo l'utente e i servizi, aggiorniamo la tabella
        this.updateTable();
    }


    /**
     * @brief Aggiorna la tabella dei prestiti filtrando per l'utente corrente.
     *
     * Recupera la lista dei prestiti associati all'ID dell'utente corrente tramite
     * il `LoanService` e aggiorna gli elementi della `loansTable`.
     */
    private void updateTable() {

        List<Loan> userLoans = this.loanService.getAllActiveByUserID(this.user.getId());


        // Converto la lista in ObservableList
        ObservableList<Loan> observableLoans = FXCollections.observableArrayList(userLoans);

        // Imposto la lista nella tabella
        this.loansTable.setItems(observableLoans);

        // Codice per visualizzare in rosso i prestiti "scaduti"
        this.loansTable.setRowFactory(tv -> new javafx.scene.control.TableRow<Loan>() {
            @Override
            protected void updateItem(Loan loan, boolean empty) {
                super.updateItem(loan, empty);

                if (loan == null || empty) {
                    this.setStyle("");
                } else {

                    if (loan.getLoanDeadline().isBefore(LocalDate.now())) {
                        this.setStyle("-fx-background-color: #fc3737; -fx-text-fill: #990000;");
                    } else {
                        this.setStyle("");
                    }
                }
            }
        });


    }


    /**
     * @brief Gestisce l'azione di chiusura della finestra.
     *
     * @param event L'evento generato dal click sul bottone.
     */
    @FXML
    private void handleClose(ActionEvent event) {
        this.closeScene(event);
    }


}


