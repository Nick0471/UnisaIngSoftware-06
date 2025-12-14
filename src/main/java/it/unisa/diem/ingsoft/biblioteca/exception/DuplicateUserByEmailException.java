/**
 * @brief Package delle eccezionie
 * @package it.unisa.diem.ingsoft.biblioteca.exception
 */
package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando si tenta di registrare un nuovo utente utilizzando
 * un indirizzo email che è già associato a un account esistente nel database.
 */
public class DuplicateUserByEmailException extends UserException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica la presenza di un utente già registrato con l'email fornita.
     */
	public DuplicateUserByEmailException() {
		super("Esiste già un utente registrato con questa email");
	}
}
