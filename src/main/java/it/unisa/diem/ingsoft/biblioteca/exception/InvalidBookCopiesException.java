/**
 * @brief Package delle eccezionie
 * @package it.unisa.diem.ingsoft.biblioteca.exception
 */
package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Ecceziona lanciata quando si tenta una modifica relativa ad un libro impostando
 *  le sue copie rimanenti > copie totali.
 */
public class InvalidBookCopiesException extends BookException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica che il numero di copie non e' valido.
     */
	public InvalidBookCopiesException() {
		super("Il numero di copie rimanenti del libro e' superiore a quello totale!");
	}
}
