package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata in fase di esecuzione quando il sistema tenta di verificare
 * la password del bibliotecario, ma scopre che la password non è stata mai impostata
 * o è assente nel database.
 */
public class UnsetPasswordException extends RuntimeException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica l'assenza della password nel database.
     */
    public UnsetPasswordException() {
        super("Non è presente alcuna password nel database");
    }
}
