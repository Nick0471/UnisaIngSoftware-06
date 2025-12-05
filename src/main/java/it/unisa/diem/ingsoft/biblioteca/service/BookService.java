/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

import java.util.List;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBooksByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;

/**
 * @brief Interfaccia per la gestione dei libri
 */
public interface BookService {
    /**
     * @brief Recupera tutti i libri presenti nel database.
     *  Esegue una query SQL per ottenere l'elenco completo di tutti i libri.
     * @return Una lista di libri contenente tutti i libri del databse.
     */
    List<Book> getAll() throws DuplicateBookByIsbnException;

    /**
     * @brief Recupera un libro tramite il suo codice isbn.
     * Il metodo itera su tutti i libri ottenuti e ne cerca uno
     * corispondente all'isbn fornito.
     * @param isbn Il codice isbn (Stringa) del libro da cercare.
     * @return Un Optional<Book> che contiene il libro se trovato, altrimenti Optional.empty().
     */
    Optional<Book> getByIsbn(String isbn);

    /**
     * @brief Recupera tutti i libri scritti da un determinato autore.
     * Itera su tutti i libri presenti nel database e filtra per l'autore specificato.
     * @param author Il nome dell'autore (Stringa) da cercare.
     * @return Una lista di libri contenente i libri dell'autore specificato.
     */
    List<Book> getAllByAuthor(String author);

    /**
     * @brief Recupera tutti i libri appartenenti a un determinato genere.
     * Itera su tutti i libri e filtra in base al genere specificato.
     * @param genre Il genere (Stringa) dei libri da cercare.
     * @return Una lista di libri contenente i libri del genere specificato.
     */
    List<Book> getAllByGenre(String genre);

    /**
     * @brief Recupera tutti i libri pubblicati in un anno specifico.
     * Itera su tutti i libri e filtra in base all'anno di pubblicazione specificato.
     * @param releaseYear L'anno di pubblicazione (intero) da cercare.
     * @return Una lista di libri contenente i libri pubblicati nell'anno specificato.
     */
    List<Book> getAllByReleaseYear(int releaseYear);

    /**
     * @brief Recupera tutti i libri con un determinato titolo.
     * Itera su tuti i libri e filtra in base al titolo specificato.
     * @param title Il titolo (Stringa) dei libri da cercare.
     * @return Una lista di libri contenente i libri con il titolo specificato.
     */
    List<Book> getAllByTitle(String title);

    /**
     * @brief Rimuove un libro dal database basandosi sul suo codice isbn.
     * Esegue un'istruzione SQL DELETE sul database per rimuovere
     * in modo permanente il record corrispondente all'isbn.
     * @param isbn Il codice isbn (Stringa) del libro da rimuovere.
     */
    boolean removeByIsbn(String isbn) throws UnknownBookByIsbnException;

    /**
     * @brief Aggiunge un libro al catalogo
     * @param book Il libro da aggiungere
     */
    void add(Book book) throws DuplicateBookByIsbnException;

    /**
     * @brief Aggiunge una lista di libri al catalogo
     * @param books La lista di libri da aggiungere
     */
    void addAll(List<Book> books) throws DuplicateBookByIsbnException, DuplicateBooksByIsbnException;

    /**
     * @brief Aggiorna le informazioni di un libro già registrato
     * @param book L'oggetto Book contenente l' ISBN del libro da modificare e
     *  le nuove informazioni da salvare
     * @invariant L'ISBN del libro è un invariante. Se è necessario modificarlo
     *  bisogna eliminare e reinserire il libro
     */
    void updateByIsbn(Book book) throws UnknownBookByIsbnException;

    /**
     * @brief Controlla se un utente con un Isbn è già stato registrato
     * @param isbn l'Isbn dell'utente da controllare
     * @return true se il libro esiste, false altrimenti
     */
    boolean existsByIsbn(String isbn);

    /**
     * @brief Recupera tutti gli ISBN che esistono già nel database tra quelli forniti.
     * @param isbns Una List<String> contenente gli ISBN da verificare.
     * @return Una List<String> contenente solo gli ISBN che sono stati trovati
     * esistenti nel database. La lista sarà vuota se non ci sono duplicati.
     */
    List<String> existingIsbns(List<String> isbns);
}
