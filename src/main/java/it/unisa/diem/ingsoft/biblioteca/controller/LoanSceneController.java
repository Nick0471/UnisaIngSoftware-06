/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import static it.unisa.diem.ingsoft.biblioteca.Views.ADD_LOAN_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.unisa.diem.ingsoft.biblioteca.exception.BookException;
import it.unisa.diem.ingsoft.biblioteca.exception.LoanException;
import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @brief Controller per la gestione della view dei prestiti.
 *
 * Gestisce la visualizzazione dei prestiti e implementa un filtro di ricerca,
 * inoltre rende possibile aggiungere o rimuovere prestiti attivi.
 *
 *Estende {@link GuiController} e implementa {@link Initializable}.
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
    private ObservableList<Loan> loans;

    /**
     * @brief Costruttore vuoto del controller.
     * Viene invocato dal FXMLLoader per caricare la nuova scena
     *
     */
    public LoanSceneController() {
    }

    /**
     * @brief Setter per loanService.
     * @param serviceRepository Il contenitore dei servizi da cui prelevare quello per la gestione dei prestiti
     *
     */
    @Override
    public void setServices(ServiceRepository serviceRepository) {
        super.setServices(serviceRepository);
        this.loanService = serviceRepository.getLoanService();

        this.updateTable();
    }

    /**
     * @brief Inizializza il controller.
     * Configura le colonne della tabella, evidenziando i prestiti scaduti,
     * imposta i tipi di ricerca disponibili e
     * carica i dati iniziali.
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

            if ("Matricola ".equals(selectedType)) {

                if (input.length() > 10) {
                    input = input.substring(0, 10);
                }

                if (!input.equals(newValue)) {
                    this.searchField.setText(input);
                    return;
                }
            }

            this.filterLoans(this.searchField.getText());
        });

        this.searchType.valueProperty().addListener((obs, oldVal, newVal) -> {
            this.searchField.setText("");
            this.updateTable();
        });

        this.searchType.setValue("Matricola ");

        // Codice per visualizzare in rosso i prestiti "scaduti"
        this.loanTable.setRowFactory(tv -> new javafx.scene.control.TableRow<Loan>() {
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
     * Aggiorna la TableView recuperando tutti i prestiti attivi.
     */
    private void updateTable() {
        if (this.loanService == null) {
            return;
        }

        List<Loan> list = this.loanService.getActive();
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
            case "Matricola " -> result = this.loanService.getActiveByUserIdContaining(query);
            case "ISBN " -> result = this.loanService.getActiveByBookIsbnContaining(query);at
            default -> {
                this.updateTable();
                return;
            }
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
        super.changeScene(event, HOMEPAGE_PATH);
    }

    /**
     * Mostra la scena per l'aggiunta di un nuovo prestito.
     * event L'evento generato dal click del pulsante.
     */
    @FXML
    private void handleAddLoan(ActionEvent event) {
        super.modalScene(ADD_LOAN_PATH, "Aggiungi Prestito", null);

        this.updateTable();
    }

    /**
     * Salva la restituzione di un prestito.
     *
     * Verifica che un prestito sia selezionato
     * chiama il metodo di restituzione e aggiorna la lista dei prestiti.
     */
    @FXML
    private void handleReturnLoan(ActionEvent event) {
        Loan selectedLoan = this.loanTable.getSelectionModel().getSelectedItem();

        if (selectedLoan == null) {
            super.popUp(Alert.AlertType.WARNING,"Prestito non selezionato","Seleziona un prestito da restituire.");
            return;
        }

        try{
            this.loanService.complete(selectedLoan.getUserId(), selectedLoan.getBookIsbn(), selectedLoan.getLoanDeadline());
            this.updateTable();
        }catch(LoanException  | BookException e){
            super.popUp(Alert.AlertType.ERROR, "Errore durante la rimozione", e.getMessage());
        }
    }

}
