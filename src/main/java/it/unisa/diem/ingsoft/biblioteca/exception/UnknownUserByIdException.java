package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando un'operazione viene richiesta per un utente la cui
 * matricola non è presente nel database.
 */
public class UnknownUserByIdException extends UserException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica l'assenza di un utente identificato dalla matricola inserita.
     */
	public UnknownUserByIdException() {
		super("Non esiste alcun utente con la matricola inserita");
	}
}
