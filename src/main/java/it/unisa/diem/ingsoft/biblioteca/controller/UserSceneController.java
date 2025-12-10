package it.unisa.diem.ingsoft.biblioteca.controller;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
* @brief Questo Controller gestisce la scena "Gestione utenti"
* Questa classe gestisce la visualizzazione tabellare di tutti gli utenti registrati nel sistema.
* Fornisce funzionalità per:
* - Filtrare gli utenti per Matricola, Email o Cognome (con supporto per Nome secondario).
* - Aggiungere un nuovo utente.
* - Modificare un utente esistente.
* - Rimuovere un utente
* - Visualizzare il profilo dettagliato di un utente.
*/

public class UserSceneController extends GuiController implements Initializable{

    @FXML private ComboBox<String> searchType;

    @FXML private TextField searchField;
    @FXML private TextField searchFieldSecondary;


    @FXML private TableView<User> userTable;


    @FXML private TableColumn<User, String> columnMatricola;
    @FXML private TableColumn<User, String> columnSurname;
    @FXML private TableColumn<User, String> columnName;
    @FXML private TableColumn<User, String> columnEmail;


    @FXML private Button btnHome;
    @FXML private Button btnAdd;
    @FXML private Button btnModify;
    @FXML private Button btnRemove;
    @FXML private Button btnUserProfile;


    private UserService userService;
    private LoanService loanService;
    private BookService bookService;

    private ObservableList<User> users;



    @Override
    public void setServices(ServiceRepository serviceRepository){
        super.setServices(serviceRepository);
        this.userService= serviceRepository.getUserService();
    }


    /**
     * @brief Inizializza il controller, le colonne della tabella e i listener per la ricerca.
     *
     * Configura:
     * 1. Il binding tra le colonne della TableView e le proprietà della classe {@link User}.
     * 2. Un listener sul `searchType` (ComboBox) per gestire la visibilità del `searchFieldSecondary:
     * se il filtro è "Cognome", appare il secondo campo per filtrare anche per Nome.
     * 3. Listener sui campi di testo per eseguire il filtro in tempo reale (mentre si digita).
     *
     * @param location  URL location per i percorsi relativi.
     * @param resources ResourceBundle per la localizzazione.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        this.columnMatricola.setCellValueFactory(new PropertyValueFactory<>("id")); //matricola
        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));


        // Listener per cambiare la visibilità del secondo campo di ricerca
        this.searchType.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Cognome".equals(newVal)) {
                // Se filtro per Cognome, mostro il campo per il Nome
                this.searchFieldSecondary.setVisible(true);
                this.searchFieldSecondary.setManaged(true);
                this.searchField.setPromptText("Inserisci Cognome...");
                this.searchFieldSecondary.setPromptText("Inserisci Nome...");
            } else {
                // Altrimenti nascondo il secondo campo
                this.searchFieldSecondary.setVisible(false);
                this.searchFieldSecondary.setManaged(false);
                this.searchFieldSecondary.clear(); // Pulisco il secondo campo


                if("Matricola".equals(newVal)) this.searchField.setPromptText("Inserisci Matricola...");
                else if("Email".equals(newVal)) this.searchField.setPromptText("Inserisci Email...");
                else this.searchField.setPromptText("Cerca utente...");
            }
            // Aggiorno la tabella quando cambio filtro
            this.executeFilter();
        });

        // Listener sui campi di testo: ogni volta che si scrive, filtra
        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> this.executeFilter());
        this.searchFieldSecondary.textProperty().addListener((observable, oldValue, newValue) -> this.executeFilter());

        // Caricamento iniziale
        this.updateTable();

    }

    /**
     * @brief Aggiorna la tabella recuperando tutti gli utenti registrati.
     */
    public void updateTable(){
        List<User> listUsers= this.userService.getAll();
        this.users = FXCollections.observableArrayList(listUsers);
        this.userTable.setItems(this.users);
    }

    /**
     * @brief Metodo helper che recupera i valori dai campi di ricerca e invoca il filtraggio.
     */
    private void executeFilter() {
        String type = this.searchType.getValue();
        String val1 = this.searchField.getText();
        String val2 = this.searchFieldSecondary.getText();

        this.filterUsers(type, val1, val2);
    }


    /**
     * @brief Filtra la lista degli utenti in base ai criteri specificati.
     *
     * Gestisce i diversi casi di ricerca:
     * - Matricola:Ricerca esatta per ID.
     * - Cognome:Ricerca "contain" su Cognome (query1) e Nome (query2).
     * - Email:Ricerca "contain" su Email.
     * - Tutti:Ricarica la lista completa.
     *
     * @param type   Il tipo di filtro selezionato (es. "Matricola", "Cognome").
     * @param query1 Il valore del campo di ricerca principale.
     * @param query2 Il valore del campo di ricerca secondario (usato solo per il Nome).
     */
    private void filterUsers(String type, String query1, String query2) {

        // Se il campo principale è vuoto e non siamo in modalità "Tutti", resetta la tabella
        if ((query1 == null || query1.trim().isEmpty()) && !"Tutti".equals(type)) {
            this.updateTable();
            return;
        }

        List<User> result = new ArrayList<>();

        if (type == null) type = "Tutti";

        switch (type) {
            case "Matricola" -> this.userService.getById(query1).ifPresent(result::add);
            case "Cognome" -> result = this.userService.getAllByFullNameContaining(query1, query2); //query1=cognome e query2=nome
            case "Email" -> result = this.userService.getAllByEmailContaining(query1);
            case "Tutti" -> result = this.userService.getAll();
            default -> result = this.userService.getAll();
        }
        ;

        this.users = FXCollections.observableArrayList(result);
        this.userTable.setItems(this.users);
    }

    /**
     * @brief Gestisce l'eliminazione dell'utente selezionato.
     *
     * Verifica preventivamente se l'utente ha prestiti attivi tramite `loanService`.
     * - Se ha prestiti: Mostra un popup di errore e blocca l'eliminazione.
     * - Se non ha prestiti: Elimina l'utente tramite `userService` e aggiorna la tabella.
     */
    @FXML
    private void handleDeleteUser(){
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();

        if (selectedUser== null) {
            this.popUp("Seleziona un utente da rimuovere.");
            return;
        }

        List<Loan> loanList = this.loanService.getByUserId(selectedUser.getId());

        if(loanList.isEmpty()) {
            this.userService.removeById(selectedUser.getId());
            this.updateTable();
        }else
            this.popUp("Non puoi rimuovere un utente che ha ancora prestiti attivi");
    }

    /**
     * @brief Apre la scena "AddUserScene".
     *
     * Carica `AddUserScene.fxml` e passa il controllo al {@link AddUserSceneController}.
     * Chiama il metodo `editUser()` sul controller di destinazione per pre-compilare i campi
     * e impostare la modalità di modifica (blocco della matricola).
     *
     * @param event L'evento che ha scatenato l'azione.
     */
    @FXML
    private void handleModifyUser(ActionEvent event) {
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();

        if (selectedUser== null) {
            super.popUp("Seleziona un utente da modificare.");
            return;
        }

        this.modalScene("it/unisa/diem/ingsoft/biblioteca/view/AddUserScene.fxml", "Modifica Utente", (AddUserSceneController controller) -> {
            controller.editUser(selectedUser);
        });

        this.updateTable();

    }

    /**
     * @brief Apre la scena "AddUserScene".
     *
     * Carica `AddUserScene.fxml` e passa il `UserService` al controller.
     * A differenza di `handleModifyUser`, qui non viene chiamato `editUser`,
     * quindi i campi saranno vuoti e modificabili.
     *
     * @param event L'evento che ha scatenato l'azione.
     */
    @FXML
    private void handleAddUser(ActionEvent event) {
        this.modalScene("/it/unisa/diem/ingsoft/biblioteca/view/AddUserScene.fxml", "Aggiungi Utente", null);

        this.updateTable();
    }



    /**
     * @brief Torna alla schermata principale (Homepage).
     *
     * @param event L'evento che ha scatenato l'azione.
     */
    @FXML
    private void handleBackToHome(ActionEvent event) {
        this.changeScene(event, "view/HomepageScene.fxml");
    }


    /**
     * @brief Apre la scena "Account Utente"
     *
     * Carica `AccountUserScene.fxml` e inizializza il controller {@link AccountUserSceneController}
     * passando l'utente selezionato e tutti i servizi necessari (User, Loan, Book) per visualizzare
     * lo storico prestiti e i dettagli anagrafici.
     *
     * @param event L'evento che ha scatenato l'azione.
     */

    @FXML
    private void handleViewUserProfile(ActionEvent event) {
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            this.popUp("Seleziona un utente per visualizzarne il profilo.");
            return;
        }

        this.modalScene("it/unisa/diem/ingsoft/biblioteca/view/AccountUserScene.fxml", "Profilo Utente", (AccountUserSceneController controller) -> {
            controller.setUserProfile(selectedUser, this.loanService, this.bookService);
        });

        this.updateTable();


    }

}
