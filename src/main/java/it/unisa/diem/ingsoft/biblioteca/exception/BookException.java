package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione base per tutti gli errori specifici della gestione dei libri.
 */
public class BookException extends Exception {

    /**
     * @brief Costruttore con messaggio.
     * @param message Il messaggio di dettaglio dell'errore.
     */
    public BookException(String message) {
        super(message);
    }
}
