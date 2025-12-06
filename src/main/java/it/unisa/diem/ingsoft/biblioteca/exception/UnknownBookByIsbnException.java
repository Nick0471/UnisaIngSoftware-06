package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando un'operazione (es. rimozione o aggiornamento) viene richiesta
 * per un libro il cui codice ISBN non è presente nel database.
 * Questa eccezione indica che il record atteso non è stato trovato.
 */
public class UnknownBookByIsbnException extends BookException {

    /**
     * @brief Costruttore con messaggio di errore.
     */
    public UnknownBookByIsbnException() {
        super("Non esiste alcun libro con l'isbn inserito");
    }
}
