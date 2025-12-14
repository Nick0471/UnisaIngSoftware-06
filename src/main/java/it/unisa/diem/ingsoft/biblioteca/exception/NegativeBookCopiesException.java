/**
 * @brief Package delle eccezionie
 * @package it.unisa.diem.ingsoft.biblioteca.exception
 */
package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Ecceziona lanciata quando si tenta una modifica relativa ad un libro
 *  con un numero di copie negativo.
 */
public class NegativeBookCopiesException extends BookException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica che il numero di copie è negativo.
     */
    public NegativeBookCopiesException() {
        super("Numero di copie del libro negativo!");
    }
}
