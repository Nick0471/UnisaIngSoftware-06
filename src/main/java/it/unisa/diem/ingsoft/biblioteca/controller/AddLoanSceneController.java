/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import it.unisa.diem.ingsoft.biblioteca.exception.LoanException;

import java.time.LocalDate;


/**
 * @brief Controller per l'inserimento di nuovi libri all'interno del catalogo.
 *
 * Permette di specificare i vari attributi di un prestito da registrare.
 */
public class AddLoanSceneController extends GuiController {

    @FXML private TextField userMatricolaField;
    @FXML private Button btnSearchUser;
    @FXML private TextField isbnField;
    @FXML private Button btnSearchBook;
    @FXML private DatePicker loanDatePicker;
    @FXML private DatePicker returnDatePicker;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;

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
    }

    /**
     * @brief Inizializza il controller
     *
     * @note Imposta la data odierna come inizio prestito e +30 giorni come fine prestito di default.
     */
    @FXML
    public void initialize() {
        loanDatePicker.setValue(LocalDate.now());
        returnDatePicker.setValue(LocalDate.now().plusDays(30));
    }

    /**
     * Gestisce la ricerca dell'utente tramite matricola.
     *
     * Verifica l'esistenza dell'utente e la possibilità di concedergli il prestito.
     */
    @FXML
    private void handleSearchUser(ActionEvent event) {
        String matricola = userMatricolaField.getText();
        if (matricola.isEmpty()) {
            super.popUp("Inserisci una matricola.");
            return;
        }

        if (userService.existsById(matricola)) {
            userMatricolaField.setDisable(true);
        } else {
            super.popUp("Utente non trovato o ID errato.");
        }
    }

    /**
     * Gestisce la ricerca del libro tramite ISBN.
     *
     * Verifica l'esistenza del libro e la disponibilità di copie.
     */
    @FXML
    private void handleSearchBook(ActionEvent event) {
        String isbn = isbnField.getText();
        if (isbn.isEmpty()) {
            super.popUp("Inserisci un ISBN.");
            return;
        }

        if (bookService.existsByIsbn(isbn)) {
            isbnField.setDisable(true);
        } else {
            super.popUp("Libro non trovato o ISBN errato.");
        }
    }

    /**
     * Conferma l'inserimento del prestito.
     *
     * Raccoglie i dati, effettua le validazioni finali e salva il prestito.
     */
    @FXML
    private void handleConfirmLoan(ActionEvent event) {
        String matricola = userMatricolaField.getText();
        String isbn = isbnField.getText();
        LocalDate start = loanDatePicker.getValue();
        LocalDate end = returnDatePicker.getValue();

        if (matricola.isEmpty() || isbn.isEmpty() || start == null || end == null) {
            super.popUp("Tutti i campi sono obbligatori.");
            return;
        }

        if (end.isBefore(start)) {
            super.popUp("La data di fine prestito non può essere precedente all'inizio.");
            return;
        }

        if (!userService.existsById(matricola)) {
            super.popUp("Matricola utente non valida.");
            return;
        }

        if (loanService.countById(matricola) == 3) {
            super.popUp("L'utente ha già tre prestiti attivi.\nRestituire almeno un libro per poter richiedere un nuovo prestito");
            return;
        }

        if (!bookService.existsByIsbn(isbn)) {
            super.popUp("ISBN libro non valido.");
            return;
        }

        if (bookService.countRemainingCopies(isbn) == 0) {
            super.popUp("Attualmente non ci sono copie disponibili per questo libro.");
            return;
        }

        try {
            loanService.register(matricola, isbn, start, end);
            super.closeScene(event);
        } catch (LoanException e) {
            super.popUp(e.getMessage());
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