package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;


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

    private BookService bookService;

    /**
     * @brief Setter per il bookService.
     *
     * @param bookService Il servizio da utilizzare per la gestione dei libri settato dal chiamante
     */
    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * @brief Inizializza il controller
     *
     * @note Fa in modo che i campi per l'inserimento dell'anno e del numero di copie accettino solo numeri interi
     */
    @FXML
    public void initialize() {

    }

    /**
     * @brief Gestisce la ricerca dell'utente tramite matricola.
     *
     * @note Verifica l'esistenza dell'utente e la possibilità di concedergli il prestito.
     */
    @FXML
    void handleSearchUser(ActionEvent event) {

    }

    /**
     * @brief Gestisce la ricerca del libro tramite ISBN.
     *
     * @note Verifica l'esistenza del libro e la disponibilità di copie.
     */
    @FXML
    void handleSearchBook(ActionEvent event) {

    }

    /**
     * @brief Conferma l'inserimento del prestito.
     *
     * @note Raccoglie i dati, effettua le validazioni finali e salva il prestito.
     */
    @FXML
    void handleConfirmLoan(ActionEvent event) {

    }

    /**
     * @brief Chiude la finestra di aggiunta senza salvare le modifiche.
     *
     * @param event L'evento che ha scatenato il cambio scena (es. click su un pulsante).
     */
    @FXML
    void handleCancel(ActionEvent event) {
        super.closeScene(event);
    }
}