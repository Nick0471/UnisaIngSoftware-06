/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

/**
 * @brief Interfaccia per la gestione della password
 */
public interface PasswordService {
    /**
     * @brief Cambia la password di accesso al software.
     * @param password La nuova password.
     */
    void change(String password);

    /**
     * @brief Controlla se la password inserita è corretta.
     * @param password La password da controllare.
     * @return true se la password è corretta, false altrimenti.
     */
    boolean check(String password);

    /**
     * @brief Controlla se esiste una password per il software
     * @return true se la password esiste, false altrimenti
     */
    boolean isPresent();
}
