package it.unisa.diem.ingsoft.biblioteca.exception;

/**
 * @brief Eccezione lanciata quando il sistema tenta di verificare
 * una risposta alle domande di default ma la risposta non è presente.
 */
public class UnsetAnswerException extends RuntimeException {

    /**
     * @brief Costruttore di default, inizializza l'eccezione con un messaggio di errore.
     */
    public UnsetAnswerException(int number) {
        super("Non è presente la risposta alla domanda n. " + number);
    }
}
