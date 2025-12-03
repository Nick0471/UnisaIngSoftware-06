package it.unisa.diem.ingsoft.biblioteca;

/**
 * @brief Interfaccia per il logging
 */
public interface LogService {
    /**
     * @brief Mostra un messaggio di informazione
     * @param message Il messaggio da mostrare
     */
    void log(String message);

    /**
     * @brief Mostra un messaggio di warning
     * @param message Il messaggio da mostrare
     */
    void logWarning(String message);

    /**
     * @brief Mostra un messaggio di errore
     * @param message Il messaggio da mostrare
     */
    void logError(String message);
}
