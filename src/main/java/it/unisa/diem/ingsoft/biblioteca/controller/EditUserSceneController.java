/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByEmailException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidEmailException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


/**
 * @brief Controller per l'inserimento e modifica di nuovi utenti all'interno della lista.
 *
 * Permette di specificare i vari attributi del utente da aggiungere.
 * Estende {@link GuiController} per ereditare funzionalità comuni
 */
public class EditUserSceneController extends GuiController{
    @FXML private Label titleLabel;
    @FXML private TextField idField;
    @FXML private TextField surnameField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;


    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private UserService userService;

    private boolean isEditMode = false;


    /**
     * @brief Setter per lo userService.
     *
     * @param serviceRepository Il contenitore dei servizi da cui prelevare quello per la gestione dei libri
     */
    @Override
    public void setServices(ServiceRepository serviceRepository){
        super.setServices(serviceRepository);
        this.userService= serviceRepository.getUserService();

    }

    /**
     * @brief Inizializza il controller
     *
     * @note Fa in modo che i campi per l'inserimento  del nome e del cognome accettino solo lettere
     * @note Blocca l'inserimento della matricola a 10 caratteri.
     */

    @FXML
    public void initialize() {

        this.nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Verifica se il testo contiene qualcosa che NON è una lettera o uno spazio
            // [a-zA-Z ]* significa: accetta lettere minuscole, maiuscole e spazi
            if (!newValue.matches("[a-zA-Z ]*")) {
                // Rimuove tutto ciò che non è lettera o spazio
                this.nameField.setText(newValue.replaceAll("[^a-zA-Z ]", ""));
            }

        });


        this.surnameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z ]*")) {
                this.surnameField.setText(newValue.replaceAll("[^a-zA-Z ]", ""));
            }
        });


        this.idField.textProperty().addListener( (observable, oldValue, newValue) -> {
            if (newValue.length() > 10) {
                this.idField.setText(newValue.substring(0, 10));
            }

        });

    }



    /**
     * @brief Setter per modificare i dati della tabella.
     *
     * @param user l'utente passatogli dall'handleModifyUser di UserSceneController
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
            this.titleLabel.setText("Modifica Utente"); //Cambio il titolo della scena da "Aggiungi nuovo utente" a "Modifica Utente"
        }
    }

    /**
     * @brief Gestisce la conferma per l'aggiunta di un nuovo utente
     *
     * Recupera i dati dai campi di testo e verifica che i campi obbligatori non siano vuoti.
     * Crea un nuovo oggetto User e salva i dati sul database.
     * In caso di errore (campi vuoti o formato errato), mostra un popup di errore.
     *
     * @param event L'evento generato dal click sul pulsante "Conferma"/"Aggiorna".
     */
    @FXML
    private void handleConfirmAdd(ActionEvent event){

        String id = this.idField.getText().trim().toUpperCase();
        String email = this.emailField.getText();
        String name = this.nameField.getText();
        String surname = this.surnameField.getText();


        if (id.isEmpty() || surname.isEmpty() || name.isEmpty() || email.isEmpty()) {
            this.popUp(Alert.AlertType.ERROR,"Errore validazione", "Compila tutti i campi obbligatori.");
            return;
        }

        try {
            if (!this.userService.isIdValid(id)) {
                throw new InvalidIdException();
            }
        } catch (InvalidIdException e) {
            this.popUp(Alert.AlertType.ERROR, "ID non valido", e.getMessage());
            return;
        }


        try {
            if (!this.userService.isEmailValid(email)) {
                throw new InvalidEmailException();
            }
        } catch (InvalidEmailException e) {
            this.popUp(Alert.AlertType.ERROR, "Email non valida", e.getMessage());
            return;
        }


        User user = new User(id,email,name,surname);

        if (this.isEditMode) {
            try {
                this.userService.updateById(user);
            }catch(UnknownUserByIdException |  InvalidIdException  e){
                this.popUp(Alert.AlertType.ERROR, "Errore salvataggio", e.getMessage());
            }
        } else {
            try {
                this.userService.register(user);

            } catch (DuplicateUserByEmailException e) {
                this.popUp(Alert.AlertType.ERROR ,"Errore salvataggio", e.getMessage());
            } catch (DuplicateUserByIdException | InvalidEmailException | InvalidIdException e) {
                this.popUp(Alert.AlertType.ERROR ,"Errore salvataggio", e.getMessage());
            }
        }

        this.closeScene(event);
    }


    /**
     * @brief Gestisce l'annullamento dell'operazione.
     *
     * Chiude la finestra corrente senza effettuare alcuna modifica o inserimento.
     *
     * @param event L'evento generato dal click sul pulsante "Rimuovi".
     */
    @FXML
    private void handleCancel(ActionEvent event){
        this.closeScene(event);
    }


}
