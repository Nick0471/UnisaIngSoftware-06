/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.exception.*;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * @brief Questo Controller gestisce la scena di Aggiunta (e Modifica) utente.
 *
 * Questa classe gestisce l'inserimento dei dati anagrafici di un utente.
 * Supporta due modalità operative:
 * 1. **Creazione:** Permette di registrare un nuovo utente (tutti i campi sono modificabili).
 * 2. **Modifica:** Permette di aggiornare un utente esistente (il campo ID/Matricola viene bloccato).
 *
 * Estende {@link GuiController} per ereditare funzionalità comuni come la gestione dei popup e la chiusura della scena.
 */


public class AddUserSceneController extends GuiController{

    @FXML private TextField idField;
    @FXML private TextField surnameField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;


    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private UserService userService;

    private boolean isEditMode = false;


    @Override
    public void setServices(ServiceRepository serviceRepository){
        super.setServices(serviceRepository);
        this.userService= serviceRepository.getUserService();

    }


    /**
     * @brief Prepara la scena per la modifica di un utente esistente.
     *
     * Questo metodo deve essere chiamato da "userScene" prima di mostrare la scena, se si intende modificare un utente.
     * Effettua le seguenti operazioni:
     * - Imposta il flag `isEditMode` a `true`(che vuol dire che la modifica è abilitata)
     * - Popola i campi di testo con i dati dell'utente passato in input.
     * - Disabilita il campo `idField` per impedire la modifica della matricola.
     * - Modifica il testo del pulsante di conferma in "Aggiorna".
     *
     * @param user L'oggetto User contenente i dati attuali da modificare.
     */
    //Metodo che permette di MODICIARE un utente già esistente
    public void editUser(User user) {
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

    /**
     * @brief Gestisce l'azione di conferma (Aggiunta o Aggiornamento).
     *
     * Questo metodo viene invocato al click del pulsante `btnConfirm`.
     * La logica esecutiva è la seguente:
     *
     * 1. Valida che tutti i campi obbligatori siano riempiti.
     * 2. Crea un oggetto {@link User} temporaneo con i dati inseriti.
     * 3. In base a `isEditMode`:
     * - Se **Modifica**: Chiama `userService.updateById(user)`. Gestisce `UnknownUserByIdException`.
     * - Se **Nuovo**: Chiama `userService.register(user)`. Gestisce eccezioni di duplicazione (`DuplicateUserByEmailException`, `DuplicateUserByIdException`).
     * 4. Mostra un popup di successo o di errore.
     * 5. Chiude la scena in caso di successo.
     *
     * @param event L'evento generato dal click sul bottone.
     */
    @FXML
    private void handleConfirmAdd(ActionEvent event){

        String id = this.idField.getText();
        String email = this.emailField.getText();
        String name = this.nameField.getText();
        String surname = this.surnameField.getText();


        if (id.isEmpty() || surname.isEmpty() || name.isEmpty() || email.isEmpty()) {
            this.popUp(" Compila tutti i campi obbligatori.");
            return;
        }

        try {
            if (!this.userService.isEmailValid(email)) {
                throw new InvalidEmailException();
            }
        } catch (InvalidEmailException e) {
            this.popUp(e.getMessage());
            return;
        }

        try {
            if (!this.userService.isIdValid(id)) {
                throw new InvalidIDException();
            }
        } catch (InvalidIDException e) {
            this.popUp(e.getMessage());
            return;
        }





        User user = new User(id,email,name,surname);

        if (this.isEditMode) {
            try {
                this.userService.updateById(user);
                this.popUp("Utente aggiornato con successo!");
            }catch(UnknownUserByIdException e){
                this.popUp(e.getMessage());
            }
        } else {
            try {
                this.userService.register(user);
                this.popUp("Nuovo Utente registrato con successo!");
            } catch (DuplicateUserByEmailException e) {
                this.popUp(e.getMessage());
            } catch (DuplicateUserByIdException e) {
                this.popUp(e.getMessage());
            }
        }

        this.closeScene(event);
    }


    /**
     * @brief Gestisce l'annullamento dell'operazione.
     *
     * Chiude la finestra corrente senza effettuare alcuna modifica o inserimento.
     *
     * @param event L'evento generato dal click sul bottone Annulla.
     */
    @FXML
    private void handleCancel(ActionEvent event){
        this.closeScene(event);
    }


}
