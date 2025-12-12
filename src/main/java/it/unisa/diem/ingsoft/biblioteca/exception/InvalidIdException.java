package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Ecceziona lanciata quando si tenta una modifica relativa ad un utente usando
 *  una matricola non valida.
 */
public class InvalidIdException extends Exception {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica che la matricola non è valida.
     */
    public InvalidIdException() {
        super("La matricola inserita per la registrazione non è valida!");
    }
}
