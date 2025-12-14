/**
 * @brief Package delle eccezionie
 * @package it.unisa.diem.ingsoft.biblioteca.exception
 */
package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando si tenta di inserire un numero negativo
 * di copie.
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
