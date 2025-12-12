package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Ecceziona lanciata quando si tenta una modifica relativa ad un libro
 *  con un isbn non valido.
 */
public class InvalidIsbnException extends BookException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica che l'id non è valido.
     */
    public InvalidIsbnException() {
        super("E' stato inserito un isbn sbagliato!");
    }
}
