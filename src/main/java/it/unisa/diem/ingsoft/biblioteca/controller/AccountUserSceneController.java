/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */

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
 * @brief Controller per la visualizzazione dell'Account Utente".
 *
 * Permette di visualizzare i dati e i prestiti attivi di un utente
 *
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


    /**
     * @brief Setter per i servizi di gestione del utente.
     * @param serviceRepository Contenitore dei servizi da cui recuperare i Services
     *
     */
    @Override
    public void setServices(ServiceRepository serviceRepository){
        super.setServices(serviceRepository);
        this.loanService= serviceRepository.getLoanService();
        this.bookService= serviceRepository.getBookService();
    }


    /**
     * @brief Inizializza il controller e configura le colonne della tabella.
     *
     * @param location  La location utilizzata per risolvere i percorsi relativi all'oggetto radice, o null se sconosciuta.
     * @param resources Le risorse utilizzate per localizzare l'oggetto radice, o null se non localizzate.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.columnIsbn.setCellValueFactory(new PropertyValueFactory<>("bookIsbn"));

        this.columnStartDate.setCellValueFactory(new PropertyValueFactory<>("loanStart"));


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
     * @brief Configura il profilo dell'utente corrente e popola la tabella dei prestiti.
     *
     * @param user L'oggetto {@link User} rappresentante l'utente di cui visualizzare il profilo.
     * @param loanService Il servizio di gestione dei prestiti, utilizzato per recuperare i prestiti dell'utente
     *
     */

    public void setUserProfile(User user, LoanService loanService) {
        this.user = user;
        this.loanService = loanService;


        if (user != null) {
            this.labelMatricola.setText(user.getId());
            this.labelNome.setText(user.getName());
            this.labelCognome.setText(user.getSurname());
            this.labelEmail.setText(user.getEmail());
        }

        //ottengo la lista dei prestiti si un singolo utente
        List<Loan> userLoans = this.loanService.getActiveByUserId(this.user.getId());

        // Converto la lista in ObservableList
        ObservableList<Loan> observableLoans = FXCollections.observableArrayList(userLoans);


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
    private void handleClose(ActionEvent event) {this.closeScene(event);}


}


