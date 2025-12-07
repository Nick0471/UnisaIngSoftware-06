/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;


import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByEmailException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownUserByIdException;

import it.unisa.diem.ingsoft.biblioteca.model.User;

import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;


public class AddUserSceneController extends GuiController{

    @FXML private TextField idField;
    @FXML private TextField surnameField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;


    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private UserService userService;

    private boolean isEditMode = false;


    public void setUserService(UserService userService) {
        this.userService = userService;
    }



    //Metodo che per mette di MOFICIARE un utente già esistente
    public void EditUser(User user) {
        if (user != null) {
            this.isEditMode = true;
            this.idField.setText(user.getId());
            this.emailField.setText(user.getEmail());
            this.nameField.setText(user.getName());
            this.surnameField.setText(user.getSurname());

            this.idField.setDisable(true); //Quando si MODIFICA un utente non è possibile cambiare la matricola
            this.btnConfirm.setText("Aggiorna"); //Cambia il testo del pulsante da "Conferma" ad  "Aggiorna"
        }
    }



    @FXML
    private void handleConfirmAdd(ActionEvent event){

        String id = this.idField.getText();
        String email = this.emailField.getText();
        String name = this.nameField.getText();
        String surname = this.surnameField.getText();


        if (id.isEmpty() || surname.isEmpty() || name.isEmpty() || email.isEmpty()) {
            popUp(" Compila tutti i campi obbligatori.");
            return;
        }

        User user = new User(id,email,name,surname);

        if (isEditMode) {
            try {
                userService.updateById(user);
                popUp("Utente aggiornato con successo!");
            }catch(UnknownUserByIdException e){
                popUp(e.getMessage());
            }
        } else {
            try {
                userService.register(user);
                popUp("Nuovo Utente registrato con successo!");
            } catch (DuplicateUserByEmailException e) {
                popUp(e.getMessage());
            } catch (DuplicateUserByIdException e) {
                popUp(e.getMessage());
            }
        }

        closeScene(event);
    }



    /**
     * @brief Chiude la finestra di aggiunta senza salvare le modifiche.
     * @param event L'evento che ha scatenato il cambio scena (es. click su un pulsante).
     */
    @FXML
    private void handleCancel(ActionEvent event){
        closeScene(event);
    }


}
