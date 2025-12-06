package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.model.User;

import it.unisa.diem.ingsoft.biblioteca.service.UserService;
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

/**
 * @brief Controller per la gestione della lista utenti.
 * Questa classe gestisce la vista principale per l'amministrazione degli utenti
 * della biblioteca. Permette di visualizzare l'elenco in una tabella,
 * filtrare i risultati (per ID, cognome, email), aggiungere, modificare
 * o rimuovere utenti interagendo con "UserService".
 */

public class UserController extends GuiController implements Initializable{

    @FXML private ComboBox<String> searchType;
    @FXML private TextField searchField;


    @FXML private TableView<User> userTable;


    @FXML private TableColumn<User, String> columnMatricola;
    @FXML private TableColumn<User, String> columnSurname;
    @FXML private TableColumn<User, String> columnName;
    @FXML private TableColumn<User, String> columnEmail;


    @FXML private Button btnHome;
    @FXML private Button btnAdd;
    @FXML private Button btnModify;
    @FXML private Button btnRemove;


    private UserService userService;

    private ObservableList<User> users;

    //controller
    public UserController(UserService userService){ this.userService=userService;}


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

        //Gli attributi di User sono i metodi di ricerca della Combobox
        this.searchType.getItems().addAll("id", "cognome", "nome", "email");
        this.searchType.setValue("id");

        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.filterUsers(newValue);
        });
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
     * @brief Filtra gli utenti in tabella in base al criterio selezionato.
     * Legge il valore della ComboBox (tipo di ricerca) e il testo inserito
     * nel campo di ricerca. Aggiorna la tabella con i risultati filtrati.
     * @param query La stringa da cercare. Se vuota o nulla, mostra tutti gli utenti.
     */
    //Leggendo il valore della ComboBox restituisce una tabella filtrata per l'attributo specificato
    @FXML
    private void filterUsers(String query) {
        if (query == null || query.isEmpty()) {
            this.updateTable();
            return;
        }
        String type = this.searchType.getValue();
        List<User> result = new ArrayList<>();

        switch (type) {
            case "id":
                this.userService.getById(query).ifPresent(result::add);
                break;
            case "cognome":
                result = this.userService.getAllByFullName(query); //Se sono uguali i cognomi ordina per nome
                break;
            case "email":
                result=this.userService.getAllByEmail(query);
                break;
            default:
                this.updateTable();
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
            super.popUp("Seleziona un utente da rimuovere.");
            return;
        }

        if(this.userService.removeById(selectedUser.getId()))
            this.updateTable();
        else
            super.popUp("Errore durante la rimozione del utente.");
    }

    /**
     * @brief Gestisce la modifica di un utente.
     * Recupera l'utente selezionato e invoca l'aggiornamento tramite il service.
     */
    @FXML
    private void handleModifyUser() {
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();

        if (selectedUser== null) {
            super.popUp("Seleziona un utente da modificare.");
            return;
        }

        this.userService.updateById(selectedUser);//L'id è l'unica cosa che non possiamo modificare
        this.updateTable();
    }

    /**
     * @brief Naviga alla schermata di aggiunta utente.
     *
     * @param event L'evento che ha scatenato l'azione.
     */
    @FXML
    private void handleAddUser(ActionEvent event) {
        super.changeScene(event, "view/AddUserScene.fxml");
    }


    /**
     * @brief Torna alla Dashboard principale (Home).
     *
     * @param event L'evento che ha scatenato l'azione.
     */
    @FXML
    private void handleBackToHome(ActionEvent event) {
        super.changeScene(event, "view/HomepageScene.fxml");
    }


}
