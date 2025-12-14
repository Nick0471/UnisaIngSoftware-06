/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;


import static it.unisa.diem.ingsoft.biblioteca.Views.ACCOUNT_USER_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.EDIT_USER_PATH;
import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @brief Controller per la gestione della view della lista utenti.
 *
 * Gestisce la visualizzazione degli utenti e implementa un filtro di ricerca,
 * inoltre rende possibile aggiungere, modificare o rimuovere utenti dalla lista.
 *
 *Estende {@link GuiController} e implementa {@link Initializable}.
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


    /**
     * @brief Setter per lo userService.
     * @param serviceRepository Il contenitore dei servizi da cui prelevare quello per la gestione degli utenti
     */
    @Override
    public void setServices(ServiceRepository serviceRepository){
        super.setServices(serviceRepository);

        this.userService= serviceRepository.getUserService();
        this.loanService = serviceRepository.getLoanService();
        this.bookService = serviceRepository.getBookService();

        this.updateTable();
    }


    /**
     * @brief Inizializza il controller.
     *
     * Configura le colonne della tabella e configura i Listener per la visibilitò
     *
     * @param location La location utilizzata per risolvere i percorsi relativi all'oggetto root, o null se non nota.
     * @param resources Le risorse utilizzate per localizzare l'oggetto root, o null se non localizzato.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){

        this.columnMatricola.setCellValueFactory(new PropertyValueFactory<>("id")); //matricola
        this.columnSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        this.columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));


        // Listener per cambiare la visibilità del secondo campo di ricerca
        this.searchType.valueProperty().addListener((obs, oldVal, newVal) -> {

            if ("Cognome ".equals(newVal)) {
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

            }
            // Aggiorno la tabella dopo aver scelto il filtro di ricerca
            this.filterUsers();
        });



        // Listener sui campi di testo: ogni volta che si scrive, filtra
        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> this.filterUsers());
        this.searchFieldSecondary.textProperty().addListener((observable, oldValue, newValue) -> this.filterUsers());

        this.searchType.setValue("Matricola ");
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
     * @brief Filtra la lista degli utenti in base ai criteri specificati.
     *
     */
    private void filterUsers() {

        String type = this.searchType.getValue();
        String val1 = this.searchField.getText();
        String val2 = this.searchFieldSecondary.getText();

        if (this.userService == null) return;

        if (val1 == null || val1.isEmpty()) {
            this.updateTable();
            return;
        }
        List<User> result = new ArrayList<>();

        switch (type) {
            case "Matricola ":
                result = this.userService.getAllByIdContaining(val1);
                break;
            case "Cognome ":
                result = this.userService.getAllByFullNameContaining(val2, val1);
                break;
            case "Email ":
                result = this.userService.getAllByEmailContaining(val1);
                break;
            default:
                result = this.userService.getAll();
                break;
        }

        //prima di caricare gli elementi in tabella è necessario inseririli in una observableArrayList
        this.users = FXCollections.observableArrayList(result);

        //popola la tabella
        this.userTable.setItems(this.users);
    }



    /**
     * @brief Gestisce l'eliminazione dell'utente selezionato.
     *
     * Verifica che un utente sia selezionato e che non abbia prestiti attivi,
     * in quel caso lo elimina.
     */
    @FXML
    private void handleDeleteUser(){
        //seleziono la riga dell'utente scelto
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();

        if (selectedUser== null) {
            this.popUp(Alert.AlertType.WARNING, "Errore selezione","Seleziona un utente da rimuovere");
            return;
        }

        //ottengo la lista dei SOLI prestiti attivi di un singolo utente
        List<Loan> loanList = this.loanService.getActiveByUserId(selectedUser.getId());

        if(loanList.isEmpty()) {
            this.userService.removeById(selectedUser.getId());
            this.updateTable();
        }else
            this.popUp(Alert.AlertType.WARNING,"Errore selezione", "Non puoi rimuovere un utente che ha ancora prestiti attivi");
    }


    /**
     * @brief Apre la scena per la modifica dell'utente selezionato.
     *
     * Verifica che un utente sia selezionato, chiama il metodo di modifica
     * e aggiorna la lista.
     */
    @FXML
    private void handleModifyUser() {
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();

        if (selectedUser== null) {
            super.popUp(Alert.AlertType.WARNING,"Errore selezione", "Seleziona un utente da modificare.");
            return;
        }

        this.modalScene(EDIT_USER_PATH , "Modifica Utente", (EditUserSceneController controller) -> {controller.editUser(selectedUser); });

        this.updateTable();

    }

    /**
     * @brief Apre la scena per l'inserimento di un utente.
     *
     */
    @FXML
    private void handleAddUser() {

        this.modalScene(EDIT_USER_PATH , "Aggiungi Utente", null);

        this.updateTable();
    }



    /**
     * @brief Torna alla schermata principale (Homepage).
     * @param event L'evento che ha scatenato l'azione.
     */
    @FXML
    private void handleBackToHome(ActionEvent event) {this.changeScene(event, HOMEPAGE_PATH);}



    /**
     * @brief Apre la scena per la visualizzazione dell'utente selezionato.
     *
     */
   @FXML
    private void handleViewUserProfile() {
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            this.popUp(Alert.AlertType.WARNING,"Errore selezione", "Seleziona un utente per visualizzarne il profilo.");
            return;
        }

        this.modalScene(ACCOUNT_USER_PATH, "Profilo Utente", (AccountUserSceneController controller) -> {
            controller.setUserProfile(selectedUser, this.loanService);
        });

        this.updateTable();
    }
}
