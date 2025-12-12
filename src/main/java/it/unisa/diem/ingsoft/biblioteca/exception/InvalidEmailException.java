package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Ecceziona lanciata quando si tenta una modifica relativa ad un utente usando
 *  un'email non valida (non istituzionale)
 */
public class InvalidEmailException extends Exception {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica che l'email è non valida.
     */
    public InvalidEmailException() {
        super("La mail inserita per la registrazione non è valida!");
    }
}
