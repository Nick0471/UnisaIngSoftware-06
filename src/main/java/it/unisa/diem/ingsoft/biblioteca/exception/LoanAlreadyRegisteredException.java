/**
 * @brief Package delle eccezionie
 * @package it.unisa.diem.ingsoft.biblioteca.exception
 */
package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando si tenta di registrare un nuovo prestito, ma ne esiste già uno attivo
 * per la combinazione specificata di utente e libro.
 */
public class LoanAlreadyRegisteredException extends LoanException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica la presenza di un prestito già registrato per l'utente e
     * il libro specificati.
     */
	public LoanAlreadyRegisteredException() {
		super("Esiste già un prestito per l'utente ed il libro specificati");
	}
}
