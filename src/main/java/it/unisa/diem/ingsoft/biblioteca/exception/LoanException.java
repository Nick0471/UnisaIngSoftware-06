/**
 * @brief Package delle eccezionie
 * @package it.unisa.diem.ingsoft.biblioteca.exception
 */
package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione base per tutti gli errori specifici relativi alla gestione dei prestiti.
 */
public class LoanException extends Exception {

    /**
     * @brief Costruttore con messaggio, inizializza l'eccezione LoanException con un
     * messaggio detagliato.
     *
     * @param message Il messaggio che descrive l'errore specifico del prestito.
     */
    public LoanException(String message) {
        super(message);
    }
}
