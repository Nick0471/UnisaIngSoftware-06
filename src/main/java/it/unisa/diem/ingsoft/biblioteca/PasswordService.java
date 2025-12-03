package it.unisa.diem.ingsoft.biblioteca;

/**
 * @brief Interfaccia per la gestione della password
 */
public interface PasswordService {
    /**
     * @brief Permette di cambiare la password di accesso
     * @param password La nuova password
     */
    void change(String password);

    /**
     * @brief Controlla se la password inserita è corretta
     * @param password La password da controllare
     * @return true se la password è corretta, false altrimenti
     */
    boolean check(String password);
}
