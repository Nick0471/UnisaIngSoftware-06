package it.unisa.diem.ingsoft.biblioteca;

import java.util.List;
import java.util.Optional;

public interface BookService {
    /**
     * @brief Recupera tutti i libri presenti nel database.
     *  Esegue una query SQL per ottenere l'elenco completo di tutti i libri.
     * @return Una lista di libri contenente tutti i libri del databse.
     */
    List<Book> getAll();

    /**
     * @brief Recupera un libro tramite il suo codice ISBN.
     * Il metodo itera su tutti i libri ottenuti e ne cerca uno
     * corispondente all'ISBN fornito.
     * @param ISBN Il codice ISBN (Stringa) del libro da cercare.
     * @return Un Optional<Book> che contiene il libro se trovato, altrimenti Optional.empty().
     */
    Optional<Book> getByISBN(String ISBN);

    /**
     * @brief Recupera tutti i libri scritti da un determinato autore.
     * Itera su tutti i libri presenti nel database e filtra per l'autore specificato.
     * @param author Il nome dell'autore (Stringa) da cercare.
     * @return Una lista di libri contenente i libri dell'autore specificato.
     */
    List<Book> getByAuthor(String author);

    /**
     * @brief Recupera tutti i libri appartenenti a un determinato genere.
     * Itera su tutti i libri e filtra in base al genere specificato.
     * @param genre Il genere (Stringa) dei libri da cercare.
     * @return Una lista di libri contenente i libri del genere specificato.
     */
    List<Book> getByGenre(String genre);

    /**
     * @brief Recupera tutti i libri pubblicati in un anno specifico.
     * Itera su tutti i libri e filtra in base all'anno di pubblicazione specificato.
     * @param releaseYear L'anno di pubblicazione (intero) da cercare.
     * @return Una lista di libri contenente i libri pubblicati nell'anno specificato.
     */
    List<Book> getByReleaseYear(int releaseYear);

    /**
     * @brief Recupera tutti i libri con un determinato titolo.
     * Itera su tuti i libri e filtra in base al titolo specificato.
     * @param title Il titolo (Stringa) dei libri da cercare.
     * @return Una lista di libri contenente i libri con il titolo specificato.
     */
    List<Book> getByTitle(String title);

    /**
     * @brief Rimuove un libro dal database basandosi sul suo codice ISBN.
     * Esegue un'istruzione SQL DELETE sul database per rimuovere
     * in modo permanente il record corrispondente all'ISBN.
     * @param ISBN Il codice ISBN (Stringa) del libro da rimuovere.
     */
    boolean removeByISBN(String ISBN);
    void add(Book book);
    void addAll(List<Book> books);
}
