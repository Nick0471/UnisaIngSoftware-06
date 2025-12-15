package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata in fase di esecuzione quando il sistema tenta di verificare
 * una risposta alle domande di default, ma scopre che la risposta non è stata mai impostata
 * o è assente nel database.
 */
public class UnsetAnswerException extends RuntimeException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore
     * standard che notifica l'assenza della risposta nel database.
     */
    public UnsetAnswerException(int number) {
        super("Non è presente alcuna risposta nel database con numero: " + number);
    }
}
