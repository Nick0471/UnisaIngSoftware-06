package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando si tenta di aggiungere un libro il cui codice ISBN è già presente nel catalogo.
 */
public class DuplicateBookByIsbnException extends BookException {

    /**
     * @brief Costruttore con messaggio di errore.
     */
    public DuplicateBookByIsbnException() {
        super("Esiste già un libro con questo isbn nel catalogo");
    }
}
