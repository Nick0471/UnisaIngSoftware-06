/**
 * @brief Package dei model
 * @package it.unisa.diem.ingsoft.biblioteca.model
 */
package it.unisa.diem.ingsoft.biblioteca.model;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @brief Rappresenta un'entità Prestito nel sistema della biblioteca.
 * Questa classe contiene tutte le informazioni relative a un singolo prestito.
 */
public class Loan {
    private String bookIsbn;
    private String userId;
    private LocalDate loanStart;
    private LocalDate loanDeadline;
    private LocalDate loanEnd;

    /**
     * @brief Costruttore di default.
     * Necessario per alcune operazioni.
     */
    public Loan() {}

    /**
     * @brief Costruttore completo per inizializzare tutti gli attributi del prestito.
     *
     * @param bookIsbn Codice ISBN del libro associato al prestito.
     * @param userId Matricola dell'utente che ha effettuato il prestito.
     * @param loanStart Data di inizio del prestito.
     * @param loanDeadline Data di fine prestito.
     */
    public Loan(String bookIsbn, String userId, LocalDate loanStart, LocalDate loanDeadline) {
        this.bookIsbn = bookIsbn;
        this.userId = userId;
        this.loanStart = loanStart;
        this.loanDeadline = loanDeadline;
    }

    /**
     * @brief Restituisce il codice ISBN del libro associato al prestito.
     * @return Il codice ISBN (Stringa) del libro.
     */
    public String getBookIsbn() {
        return this.bookIsbn;
    }

    /**
     * @brief Restituisce la matricola dell'utente che ha effettuato il prestito.
     * @return La matricola dell'utente (Stringa).
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * @brief Restituisce la data di inizio del prestito.
     * @return La data di inizio (LocalDate).
     */
    public LocalDate getLoanStart() {
        return this.loanStart;
    }

    /**
     * @brief Restituisce la data di fine prestito.
     * @return La data di scadenza (LocalDate).
     */
    public LocalDate getLoanDeadline() {
        return this.loanDeadline;
    }

    /**
     * @brief Restituisce la data di effettiva restituzione del libro, se presente.
     * Utilizza Optional per gestire il caso in cui il libro non sia ancora stato restituito.
     *
     * @return Un Optional<LocalDate> contenente la data di restituzione se presente,
     * altrimenti un Optional vuoto.
     */
    public Optional<LocalDate> getLoanEnd() {
        return Optional.ofNullable(this.loanEnd);
    }

    /**
     * @brief Imposta la data di effettiva restituzione del libro.
     * @param loanEnd La data di restituzione (LocalDate).
     */
    public void setLoanEnd(LocalDate loanEnd) {
        this.loanEnd = loanEnd;
    }

    /**
     * @brief Imposta il codice ISBN del libro associato al prestito.
     * @param bookIsbn Il nuovo codice ISBN (Stringa).
     */
    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    /**
     * @brief Imposta la matricola dell'utente che ha effettuato il prestito.
     * @param userId La nuova matricola dell' utente (Stringa).
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @brief Imposta la data di inizio del prestito.
     * @param loanStart La nuova data di inizio (LocalDate).
     */
    public void setLoanStart(LocalDate loanStart) {
        this.loanStart = loanStart;
    }

    /**
     * @brief Imposta la data di fine prestito.
     * @param loanDeadline La nuova data di scadenza (LocalDate).
     */
    public void setLoanDeadline(LocalDate loanDeadline) {
        this.loanDeadline = loanDeadline;
    }
}
