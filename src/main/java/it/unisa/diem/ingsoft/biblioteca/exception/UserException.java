/**
 * @brief Package delle eccezionie
 * @package it.unisa.diem.ingsoft.biblioteca.exception
 */
package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione base per tutti gli errori specifici relativi alla gestione degli
 * utenti.
 */
public class UserException extends Exception {

    /**
     * @brief Costruttore con messaggio.
     *Inizializza l'eccezione UserException con un messaggio specifico.
     *
     * @param message Il messaggio che descrive l'errore specifico dell'utente.
     */
    public UserException(String message) {
        super(message);
    }
}
