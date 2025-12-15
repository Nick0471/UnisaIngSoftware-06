/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

/**
 * @brief Interfaccia per la gestione della password e delle domande
 */
public interface AuthService {
    /**
     * @brief Cambia la password di accesso al software.
     * @param password La nuova password.
     */
    void changePassword(String password);

    /**
     * @brief Controlla se la password inserita è corretta.
     * @param password La password da controllare.
     * @return true se la password è corretta, false altrimenti.
     */
    boolean checkPassword(String password);

    /**
     * @brief Controlla se esiste una password per il software
     * @return true se la password esiste, false altrimenti
     */
    boolean isPresent();

    /**
     * @brief Controlla se la risposta inserita è corretta.
     * @param answer La risposta da controllare.
     * @param number Il numero della risposta da controllare.
     * @return true se la risposta è corretta, false altrimenti.
     */
    boolean checkAnswer(String answer, int number);

    /**
     * @brief Cambia una delle risposte alle domande di accesso al software.
     * @param answer La nuova risposta.
     * @param number Il numero della domanda da modificare.
     */
    void changeAnswer(String answer, int number);
}
