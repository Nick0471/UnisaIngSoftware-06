/**
 * @brief Package delle eccezionie
 * @package it.unisa.diem.ingsoft.biblioteca.exception
 */
package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando si tenta di rimuovere un libro di cui almeno una copia e' mancante in quanto
 *  parte di un prestito attivo.
 */
public class MissingBookCopiesException extends BookException {
    /**
     * @brief Costruttore con messaggio di errore.
     */
	public MissingBookCopiesException() {
		super("Almeno una copia di questo libro e' parte di un prestito attivo!");
	}
}
