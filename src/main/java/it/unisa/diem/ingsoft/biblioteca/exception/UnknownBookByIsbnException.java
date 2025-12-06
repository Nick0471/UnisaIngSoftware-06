package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando un'operazione di update viene richiesta
 * per un libro il cui codice ISBN non è presente nel database.
 */
public class UnknownBookByIsbnException extends BookException {

    /**
     * @brief Costruttore con messaggio di errore.
     */
    public UnknownBookByIsbnException() {
        super("Non esiste alcun libro con l'isbn inserito");
    }
}
