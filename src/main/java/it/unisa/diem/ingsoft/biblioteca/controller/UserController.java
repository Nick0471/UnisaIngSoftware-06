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

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import java.util.ResourceBundle;



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


    public void updateTable(){
        List<User> listUsers= userService.getAll();
        this.users = FXCollections.observableArrayList(listUsers);
        this.userTable.setItems(this.users);
    }


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
                result = this.userService.getBySurname(query);
                break;
            case "nome":
                result = this.userService.getByName(query);
                break;
            case "email":
                result=this.userService.getByEmail(query);
                break;
            default:
                this.updateTable();
        }

        this.users = FXCollections.observableArrayList(result);
        this.userTable.setItems(this.users);
    }

    @FXML
    private void handleDeleteUser(){
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();
        if (selectedUser== null) {
            super.popUpError("Seleziona un utente da rimuovere.");
            return;
        }

        if(this.userService.removeById(selectedUser.getId()))
            this.updateTable();
        else
            super.popUpError("Errore durante la rimozione del utente.");
    }


    @FXML
    private void handleModifyUser() {
        User selectedUser = this.userTable.getSelectionModel().getSelectedItem();

        if (selectedUser== null) {
            super.popUpError("Seleziona un utente da modificare.");
            return;
        }

        this.userService.updateById(selectedUser);//L'id è l'unica cosa che non possiamo modificare
        this.updateTable();
    }


    @FXML
    private void handleAddUser(ActionEvent event) {
        super.changeScene(event, "view/AddUserScene.fxml");
    }


    @FXML
    private void handleBackToHome(ActionEvent event) {
        super.changeScene(event, "view/HomepageScene.fxml");
    }


}
