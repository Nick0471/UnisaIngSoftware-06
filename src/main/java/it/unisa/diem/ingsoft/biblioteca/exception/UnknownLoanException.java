/**
 * @brief Package delle eccezionie
 * @package it.unisa.diem.ingsoft.biblioteca.exception
 */
package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando un'operazione di update
 * viene richiesta per un prestito che non esiste o non è attivo per la combinazione specificata
 * di utente e libro.
 */
public class UnknownLoanException extends LoanException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica l'assenza di un prestito per l'utente e il libro specificati.
     */
	public UnknownLoanException() {
		super("Non esiste alcun prestito tra utente e libro specificati");
	}
}
