/**
 * @brief Package delle eccezionie
 * @package it.unisa.diem.ingsoft.biblioteca.exception
 */
package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando si tenta di registrare un nuovo utente utilizzando
 * una matricola che è già associata a un account esistente nel database.
 */
public class DuplicateUserByIdException extends UserException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica la presenza di un utente già registrato con la matricola
     * fornita.
     */
	public DuplicateUserByIdException() {
		super("Esiste già un utente registrato con questa matricola");
	}
}
