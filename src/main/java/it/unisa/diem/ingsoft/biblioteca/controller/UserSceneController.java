package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.model.User;

import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import java.util.ResourceBundle;

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


    private UserService userService= this.getService().getUserService;
    private LoanService loanService= this.getService().getLoanService;
    private BookService bookService= this.getService().getBookService;

    private ObservableList<User> users;


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
        columnMatricola.setCellValueFactory(new PropertyValueFactory<>("id")); //matricola
        columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));


        // Listener per cambiare la visibilità del secondo campo di ricerca
        searchType.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("Cognome".equals(newVal)) {
                // Se filtro per Cognome, mostro il campo per il Nome
                searchFieldSecondary.setVisible(true);
                searchFieldSecondary.setManaged(true);
                searchField.setPromptText("Inserisci Cognome...");
                searchFieldSecondary.setPromptText("Inserisci Nome...");
            } else {
                // Altrimenti nascondo il secondo campo
                searchFieldSecondary.setVisible(false);
                searchFieldSecondary.setManaged(false);
                searchFieldSecondary.clear(); // Pulisco il secondo campo


                if("Matricola".equals(newVal)) searchField.setPromptText("Inserisci Matricola...");
                else if("Email".equals(newVal)) searchField.setPromptText("Inserisci Email...");
                else searchField.setPromptText("Cerca utente...");
            }
            // Aggiorno la tabella quando cambio filtro
            executeFilter();
        });

        // Listener sui campi di testo: ogni volta che si scrive, filtra
        searchField.textProperty().addListener((observable, oldValue, newValue) -> executeFilter());
        searchFieldSecondary.textProperty().addListener((observable, oldValue, newValue) -> executeFilter());

        // Caricamento iniziale
        this.updateTable();

    }

    /**
     * @brief Aggiorna la tabella recuperando tutti gli utenti registrati.
     */
    public void updateTable(){
        List<User> listUsers= userService.getAll();
        this.users = FXCollections.observableArrayList(listUsers);
        this.userTable.setItems(this.users);
    }

    /**
     * @brief Metodo helper che recupera i valori dai campi di ricerca e invoca il filtraggio.
     */
    private void executeFilter() {
        String type = searchType.getValue();
        String val1 = searchField.getText();
        String val2 = searchFieldSecondary.getText();

        filterUsers(type, val1, val2);
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
            case "Matricola":
                this.userService.getById(query1).ifPresent(result::add);
                break;

            case "Cognome":
                result = this.userService.getAllByFullNameContaining(query1, query2); //query1=cognome e query2=nome
                break;

            case "Email":
                result = this.userService.getAllByEmailContaining(query1);
                break;

            case "Tutti":
            default:
                result = this.userService.getAll();
                break;
        }

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
            popUp("Seleziona un utente da rimuovere.");
            return;
        }

        List<Loan> loanList = loanService.getByUserId(selectedUser.getId());

        if(loanList.isEmpty()) {
            this.userService.removeById(selectedUser.getId());
            this.updateTable();
        }else
            popUp("Non puoi rimuovere un utente che ha ancora prestiti attivi");
    }

    /**
     * @brief Apre la scena "AddUserScene".
     *
     * Carica `AddUserScene.fxml` e passa il controllo al {@link AddUserSceneController}.
     * Chiama il metodo `EditUser()` sul controller di destinazione per pre-compilare i campi
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
            controller.EditUser(selectedUser);
        });

        this.updateTable();

    }

    /**
     * @brief Apre la scena "AddUserScene".
     *
     * Carica `AddUserScene.fxml` e passa il `UserService` al controller.
     * A differenza di `handleModifyUser`, qui non viene chiamato `EditUser`,
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
        changeScene(event, "view/HomepageScene.fxml");
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
            popUp("Seleziona un utente per visualizzarne il profilo.");
            return;
        }

        this.modalScene("it/unisa/diem/ingsoft/biblioteca/view/AccountUserScene.fxml", "Profilo Utente", (AccountUserSceneController controller) -> {
            controller.setProfileUser(selectedUser, loanService, bookService);
        });

        this.updateTable();


    }

}
