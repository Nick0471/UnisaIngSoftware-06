/**
 * @brief Package dei controller
 * @package it.unisa.diem.ingsoft.biblioteca.controller
 */
package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

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

    /**
     * @brief Setter per il bookService.
     *
     * @param loanService Il servizio da utilizzare per la gestione dei libri settato dal chiamante
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
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

        if (matricola == null || matricola.trim().isEmpty()) {
            StringBuffer sb = new StringBuffer("Attenzione");
            sb.append("Matricola non trovata");
            super.popUp(sb.toString());
            return;
        }

        List<Loan> userLoans = loanService.getByUserId(matricola);

        if (userLoans.isEmpty()) {
           super.popUp("Nessun prestito attivo trovato per la matricola: " + matricola);

        return
    }

    /**
     * Gestisce la ricerca del libro tramite ISBN.
     *
     * Verifica l'esistenza del libro e la disponibilità di copie.
     */
    @FXML
    private void handleSearchBook(ActionEvent event) {

    }

    /**
     * Conferma l'inserimento del prestito.
     *
     * Raccoglie i dati, effettua le validazioni finali e salva il prestito.
     */
    @FXML
    private void handleConfirmLoan(ActionEvent event) {

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