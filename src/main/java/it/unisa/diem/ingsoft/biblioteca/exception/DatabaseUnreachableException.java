package it.unisa.diem.ingsoft.biblioteca.exception;

import java.sql.SQLException;

/**
 * @brief Eccezione lanciata quando il servizio non è in grado di stabilire
 *  una connessione al database.
 */
public class DatabaseUnreachableException extends Exception {

    /**
     * @brief Costruttore che accetta l'eccezione SQLException originale.
     * Inizializza l'eccezione con un messaggio di errore standard e imposta
     * l'eccezione SQL come causa sottostante.
     *
     * @param e L'eccezione SQLException generata durante la connessione
     */
    public DatabaseUnreachableException(SQLException e) {
        super("Connessione al database fallita", e);
    }
} 
