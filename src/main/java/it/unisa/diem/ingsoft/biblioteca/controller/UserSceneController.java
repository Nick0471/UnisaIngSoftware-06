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
 * @brief Controller per la gestione della lista utenti.
 * Questa classe gestisce la vista principale per l'amministrazione degli utenti
 * della biblioteca. Permette di visualizzare l'elenco in una tabella,
 * filtrare i risultati (per ID, cognome, email), aggiungere, modificare
 * o rimuovere utenti interagendo con "UserService".
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


    public UserSceneController(UserService userService){
        this.userService=userService;
    }



    /**
     * @brief Inizializza il controller e la vista.
     * Configura le colonne della tabella collegandole alle proprietà dell'oggetto User.
     * Popola la ComboBox per i filtri di ricerca e aggiunge un listener al campo
     * di ricerca per filtrare la tabella in tempo reale. Infine, carica i dati iniziali.
     * @param location La location utilizzata per risolvere i percorsi relativi all'oggetto radice, o null se sconosciuta.
     * @param resources Le risorse utilizzate per localizzare l'oggetto radice, o null se non localizzato.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        columnMatricola.setCellValueFactory(new PropertyValueFactory<>("id")); //sarebbe la matricola
        columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Nota: Gli items della ComboBox sono già definiti nell'FXML (Tutti, Matricola, Cognome, Email).
        // Non c'è bisogno di aggiungerli qui manualmente.

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
                searchFieldSecondary.clear(); // Pulisco il secondo campo per evitare filtri sporchi

                // Cambio il prompt in base alla selezione
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
     * @brief Aggiorna la tabella con l'elenco completo degli utenti.
     * Recupera tutti gli utenti dal service e aggiorna la TableView.
     */
    public void updateTable(){
        List<User> listUsers= userService.getAll();
        this.users = FXCollections.observableArrayList(listUsers);
        this.userTable.setItems(this.users);
    }


    /**
     * @brief Metodo helper per leggere i valori dai campi e chiamare il filtro.
     */
    private void executeFilter() {
        String type = searchType.getValue();
        String val1 = searchField.getText();
        String val2 = searchFieldSecondary.getText();

        filterUsers(type, val1, val2);
    }


    /**
     * @brief Filtra gli utenti in tabella in base al criterio selezionato.
     * @param type Il tipo di ricerca (Matricola, Cognome, Email, Tutti)
     * @param query1 Il valore del primo campo (Matricola, Cognome, o Email)
     * @param query2 Il valore del secondo campo (Nome, usato solo se type è Cognome)
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
                result = this.userService.getAllByFullNameContaining(query2, query1); //query1=cognome e query2=nome
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
     * @brief Gestisce la rimozione di un utente.
     * Recupera l'utente selezionato nella tabella. Se presente, ne richiede
     * la rimozione tramite il service e aggiorna la vista. In caso di errore
     * o mancata selezione, mostra un popup di errore.
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
     * @brief Gestisce la modifica di un utente.
     *
     */
    @FXML
    private void handleModifyUser(ActionEvent event) {
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();

        if (selectedUser== null) {
            super.popUp("Seleziona un utente da modificare.");
            return;
        }

        try {
            //Carica l'FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/ingsoft/biblioteca/view/AddUserScene.fxml"));
            Parent root = loader.load();

            //Recupera il controller
            AddUserSceneController controller = loader.getController();

            //Passa i dati necessari
            controller.setUserService(this.userService); // Passa il service
            controller.EditUser(selectedUser);           // Passa l'utente e attiva la modalità Modifica

            //Mostra la scena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            popUp("Errore nel caricamento della vista Modifica Utente.");
        }





    }

    /**
     * @brief Naviga alla schermata di aggiunta utente.
     *
     * @param event L'evento che ha scatenato l'azione.
     */
    @FXML
    private void handleAddUser(ActionEvent event) {
        try {
            // Carica l'FXML manualmente
            // Assicurati che il percorso sia corretto (es. potrebbe servire "/" all'inizio)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/ingsoft/biblioteca/view/AddUserScene.fxml"));
            Parent root = loader.load();

            // Recupera il controller della nuova scena
            AddUserSceneController controller = loader.getController();

            // Passa il UserService (FONDAMENTALE)
            controller.setUserService(this.userService);

            // Mostra la nuova scena
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            popUp("Errore nel caricamento della vista Aggiungi Utente.");
        }
    }




    /**
     * @brief Torna alla Dashboard principale (Home).
     *
     * @param event L'evento che ha scatenato l'azione.
     */
    @FXML
    private void handleBackToHome(ActionEvent event) {
        changeScene(event, "view/HomepageScene.fxml");
    }




    private void handleViewUserProfile(ActionEvent event) {
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            popUp("Seleziona un utente per visualizzarne il profilo.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/ingsoft/biblioteca/view/AccountUserScene.fxml"));
            Parent root = loader.load();

            AccountUserSceneController controller = loader.getController();

            // Passiamo TUTTI i servizi necessari e l'utente selezionato
            controller.setUserService(this.userService);
            controller.setProfileUser(selectedUser, this.loanService, this.bookService);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            popUp("Errore nel caricamento del profilo utente.");
        }
    }

}
