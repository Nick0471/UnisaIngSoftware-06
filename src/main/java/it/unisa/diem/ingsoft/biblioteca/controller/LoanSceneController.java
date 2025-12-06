/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.exception.LoanException;
import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @brief Controller per la gestione della view dei prestit.
 *
 * Gestisce la visualizzazione dei prestiti e implementa un filtro di ricerca,
 * inoltre rende possibile aggiungere o rimuovere prestiti attivi .
 *
 */
public class LoanSceneController extends GuiController implements Initializable {
    @FXML
    private ComboBox<String> searchType;
    @FXML
    private TextField searchField;

    @FXML
    private TableView<Loan> loanTable;
    @FXML
    private TableColumn<Loan, String> columnUserId;
    @FXML
    private TableColumn<Loan, String> columnIsbn;
    @FXML
    private TableColumn<Loan, LocalDate> columnStartDate;
    @FXML
    private TableColumn<Loan, LocalDate> columnDeadline;

    @FXML
    private Button btnHome;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnReturn;

    private LoanService loanService;
    private UserService userService;
    private BookService bookService;
    private ObservableList<Loan> loans;

    /**
     * @brief Costruttore vuoto del controller.
     * Viene invocato dal FXMLLoader per caricare la nuova scena
     *
     */
    public LoanSceneController() {
    }

    /**
     * @brief Setter per i servizi di gestione dei prestiti, degli utenti e dei libri
     * @param loanService Il servizio da utilizzare per la gestione dei prestiti settato dal chiamante
     * @param userService Il servizio da utilizzare per la gestione degli utenti settato dal chiamante
     * @param bookService Il servizio da utilizzare per la gestione dei libri settato dal chiamante
     *
     */
    public void setLoanServices(LoanService loanService, UserService userService, BookService bookService) {
        this.loanService = loanService;
        this.userService = userService;
        this.bookService = bookService;

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

        this.columnUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        this.columnIsbn.setCellValueFactory(new PropertyValueFactory<>("bookIsbn"));
        this.columnStartDate.setCellValueFactory(new PropertyValueFactory<>("loanStart"));
        this.columnDeadline.setCellValueFactory(new PropertyValueFactory<>("loanDeadline"));

        this.searchType.getItems().addAll("Matricola", "ISBN");
        this.searchType.setValue("Matricola");

        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.filterLoans(newValue);
        });
    }

    /**
     * Aggiorna la TableView recuperando tutti i prestiti attivi.
     */
    private void updateTable() {
        List<Loan> list = this.loanService.getAll();
        this.loans = FXCollections.observableArrayList(list);
        this.loanTable.setItems(this.loans);
    }

    /**
     * Filtra i prestiti nella tabella in base alla query e al tipo di ricerca selezionato.
     *
     * query La stringa di ricerca inserita dall'utente.
     */
    @FXML
    private void filterLoans(String query) {
        if (query == null || query.isEmpty()) {
            this.updateTable();
            return;
        }

        String type = this.searchType.getValue();
        List<Loan> result = new ArrayList<>();

        switch (type) {
            case "Matricola":
                result = this.loanService.getByUserId(query);
                break;
            case "ISBN":
                result = this.loanService.getByBookIsbn(query);
                break;
            default:
                this.updateTable();
        }

        this.loans = FXCollections.observableArrayList(result);
        this.loanTable.setItems(this.loans);
    }

    /**
     * Torna alla scena Homepage.
     * event L'evento generato dal click del pulsante.
     */
    @FXML
    private void handleBackToHome(ActionEvent event) {
        super.changeScene(event, "view/HomepageScene.fxml");
    }

    /**
     * Mostra la scena per l'aggiunta di un nuovo prestito.
     * event L'evento generato dal click del pulsante.
     */
    @FXML
    private void handleAddLoan(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddLoanScene.fxml"));
            Parent root = loader.load();

            AddLoanSceneController addController = loader.getController();
            addController.setAddLoanServices(this.loanService, this.userService, this.bookService);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi Prestito");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            this.updateTable();

        } catch (IOException e) {
            super.popUp("Errore nel caricamento della finestra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Salva la restituzione di un prestito.
     *
     * Verifica che un prestito sia selezionato
     * chiama il metodo di rimozione e aggiorna la lista dei prestiti.
     */
    @FXML
    private void handleReturnLoan(ActionEvent event) {
        Loan selectedLoan = this.loanTable.getSelectionModel().getSelectedItem();

        if (selectedLoan == null) {
            super.popUp("Seleziona un prestito da restituire.");
            return;
        }

        try{
            this.loanService.complete(selectedLoan.getUserId(), selectedLoan.getBookIsbn(), selectedLoan.getLoanDeadline());
            this.updateTable();
        }catch(LoanException e){
            super.popUp(e.getMessage());
        }
    }

}
