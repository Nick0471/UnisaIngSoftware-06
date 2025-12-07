package it.unisa.diem.ingsoft.biblioteca.exception;

import java.util.List;

/**
 * @brief Eccezione lanciata quando si tenta di eseguire un'operazione di inserimento
 * che include uno o più libri i cui codici ISBN sono già presenti nel catalogo.
 */
public class DuplicateBooksByIsbnException extends BookException {
    private final List<String> isbns;

    /**
     * @brief Costruttore che accetta la lista degli ISBN duplicati.
     * Inizializza l'eccezione con un messaggio standard che avvisa della presenza di
     * duplicati e memorizza la lista degli ISBN in questione.
     *
     * @param isbns La List<String> contenente tutti gli ISBN che hanno fallito
     * l'inserimento perché già esistenti.
     */
    public DuplicateBooksByIsbnException(List<String> isbns) {
        super("Esistono già uno o più libri con questi isbn nel catalogo");
        this.isbns = isbns;
    }

    /**
     * @brief Restituisce la lista dei codici ISBN duplicati.
     * @return Una List<String> immutabile contenente gli ISBN che sono già presenti nel database.
     */
    public List<String> getIsbns() {
        return this.isbns;
    }
}
