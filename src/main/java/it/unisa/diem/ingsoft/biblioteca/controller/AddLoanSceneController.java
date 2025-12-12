/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIsbnException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import it.unisa.diem.ingsoft.biblioteca.exception.LoanException;

import java.time.LocalDate;
import java.util.List;


/**
 * @brief Controller per l'inserimento di nuovi libri all'interno del catalogo.
 *
 * Permette di specificare i vari attributi di un prestito da registrare.
 * Estende {@link GuiController} per ereditare funzionalità comuni
 */
public class AddLoanSceneController extends GuiController {

    @FXML private TextField userMatricolaField;
    @FXML private Button btnResetUser;
    @FXML private TextField isbnField;
    @FXML private Button btnResetBook;
    @FXML private DatePicker loanDatePicker;
    @FXML private DatePicker returnDatePicker;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

    private final ContextMenu userSuggestions = new ContextMenu();
    private final ContextMenu bookSuggestions = new ContextMenu();

    private LoanService loanService;
    private UserService userService;
    private BookService bookService;

    /**
     * @brief Costruttore vuoto del controller.
     * Viene invocato dal FXMLLoader per caricare la nuova scena
     *
     */
    public AddLoanSceneController() {}

    /**
     * @brief Setter per i servizi di gestione dei prestiti, degli utenti e dei libri
     * @param serviceRepository Contenitore dei servizi da cui recuperare i Services
     *
     */
    @Override
    public void setServices(ServiceRepository serviceRepository) {

        this.loanService = serviceRepository.getLoanService();
        this.userService = serviceRepository.getUserService();
        this.bookService = serviceRepository.getBookService();

        this.setupUserAutocomplete();
        this.setupBookAutocomplete();
    }

    /**
     * @brief Inizializza il controller
     *
     * @note Imposta la data odierna come inizio prestito e +30 giorni come fine prestito di default.
     */
    @FXML
    public void initialize() {
        this.loanDatePicker.setValue(LocalDate.now());
        this.returnDatePicker.setValue(LocalDate.now().plusDays(30));

        this.isbnField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                this.isbnField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        this.isbnField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 13) {
                this.isbnField.setText(newValue.substring(0, 13));
            }
        });

        this.userMatricolaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 10) {
                this.userMatricolaField.setText(newValue.substring(0, 10));
            }
        });

        this.btnResetUser.setDisable(true);
        this.btnResetBook.setDisable(true);
    }

    /**
     * Gestisce la logica di autocompletamento per gli UTENTI.
     */
    private void setupUserAutocomplete() {
        this.userMatricolaField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (this.userMatricolaField.isDisabled()) return;

            if (newValue == null || newValue.isEmpty()) {
                this.userSuggestions.hide();
                return;
            }

            List<User> results = this.userService.getAllByIdContaining(newValue);

            this.userSuggestions.getItems().clear();

            if (!results.isEmpty()) {
                //Seleziona i primi 7 risultati
                for (User user : results.stream().limit(7).toList()) {

                    //Associa la matricola al nome e al cognome
                    String label = user.getId() + " - " + user.getName() + " " + user.getSurname();
                    MenuItem item = new MenuItem(label);

                    item.setOnAction(e -> {
                        this.userMatricolaField.setText(user.getId());
                        this.userMatricolaField.setDisable(true);
                        this.btnResetUser.setDisable(false);
                        this.userSuggestions.hide();
                    });

                    this.userSuggestions.getItems().add(item);
                }

                //Mostra la tendina sotto il campo di testo
                if (!this.userSuggestions.isShowing()) {
                    this.userSuggestions.show(this.userMatricolaField, Side.BOTTOM, 0, 0);
                }
            } else {
                this.userSuggestions.hide();
            }
        });

        //Nasconde la tendina se interagisco con altro
        this.userMatricolaField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) this.userSuggestions.hide();
        });
    }

    /**
     * Gestisce la logica di autocompletamento per i LIBRI.
     */
    private void setupBookAutocomplete() {
        this.isbnField.textProperty().addListener((observable, oldValue, newValue) -> {

            if (this.isbnField.isDisabled()) return;

            if (newValue == null || newValue.isEmpty()) {
                this.bookSuggestions.hide();
                return;
            }

            List<Book> results = this.bookService.getAllByIsbnContaining(newValue);

            this.bookSuggestions.getItems().clear();

            if (!results.isEmpty()) {
                //Seleziona i primi 7 risultati
                for (Book book : results.stream().limit(7).toList()) {

                    //Associa l'ISBN al titolo
                    String label = book.getIsbn() + " - " + book.getTitle();
                    MenuItem item = new MenuItem(label);

                    item.setOnAction(e -> {
                        this.isbnField.setText(book.getIsbn());
                        this.isbnField.setDisable(true);
                        this.btnResetBook.setDisable(false);
                        this.bookSuggestions.hide();
                    });

                    this.bookSuggestions.getItems().add(item);
                }

                //Mostra la tendina sotto il campo di testo
                if (!this.bookSuggestions.isShowing()) {
                    this.bookSuggestions.show(this.isbnField, Side.BOTTOM, 0, 0);
                }
            } else {
                this.bookSuggestions.hide();
            }
        });

        this.isbnField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) this.bookSuggestions.hide();
        });
    }

    /**
     * Gestisce la ricerca dell'utente tramite matricola.
     *
     * Verifica l'esistenza dell'utente e la possibilità di concedergli il prestito.
     */
    @FXML
    private void handleSearchUser(ActionEvent event) {
        this.userMatricolaField.setDisable(false);
        this.userMatricolaField.clear();
        this.btnResetUser.setDisable(true);
        this.userMatricolaField.requestFocus();
    }

    /**
     * Gestisce la ricerca del libro tramite ISBN.
     *
     * Verifica l'esistenza del libro e la disponibilità di copie.
     */
    @FXML
    private void handleSearchBook(ActionEvent event) {
        this.isbnField.setDisable(false);
        this.isbnField.clear();
        this.btnResetBook.setDisable(true);
        this.isbnField.requestFocus();
    }

    /**
     * Conferma l'inserimento del prestito.
     *
     * Raccoglie i dati, effettua le validazioni finali e salva il prestito.
     */
    @FXML
    private void handleConfirmLoan(ActionEvent event) {
        String matricola = this.userMatricolaField.getText();
        String isbn = this.isbnField.getText();
        LocalDate start = this.loanDatePicker.getValue();
        LocalDate end = this.returnDatePicker.getValue();

        if (!this.userMatricolaField.isDisabled()) {
            super.popUp(Alert.AlertType.WARNING, "Utente non selezionato", "Seleziona un utente dal menu a tendina.");
            return;
        }

        if (!this.isbnField.isDisabled()) {
            super.popUp(Alert.AlertType.WARNING, "Libro non selezionato","Seleziona un libro dal menu a tendina.");
            return;
        }

        if (matricola.isEmpty() || isbn.isEmpty() || start == null || end == null) {
            super.popUp(Alert.AlertType.ERROR, "Errore validazione", "Tutti i campi sono obbligatori.");
            return;
        }

        if (end.isBefore(start)) {
            super.popUp(Alert.AlertType.ERROR, "Errore validazione","La data di fine prestito non può essere precedente all'inizio.");
            return;
        }

        try {
            if (this.loanService.countById(matricola) == 3) {
                super.popUp(Alert.AlertType.ERROR, "L'utente ha già tre prestiti attivi", "Restituire almeno un libro per poter richiedere un nuovo prestito");
                return;
            }
        }catch(InvalidIdException e){super.popUp(Alert.AlertType.ERROR, "ID non valido", e.getMessage());}

        if (this.bookService.countRemainingCopies(isbn) == 0) {
            super.popUp(Alert.AlertType.ERROR, "Copie non disponibili", "Attualmente non ci sono copie disponibili per questo libro.");
            return;
        }

        try {
            this.loanService.register(matricola, isbn, start, end);
            super.closeScene(event);
            //super.popUp(Alert.AlertType.INFORMATION, "Successo", "Prestito registrato.");
        } catch (LoanException | InvalidIdException | InvalidIsbnException e) {
            super.popUp(Alert.AlertType.ERROR, "Errore durante la registrazione", e.getMessage());
        }
    }

    /**
     * Chiude la finestra di aggiunta senza salvare le modifiche.
     *
     * event L'evento che ha scatenato il cambio scena (es. click su un pulsante).
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        super.closeScene(event);
    }
}