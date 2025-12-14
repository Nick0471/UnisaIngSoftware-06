/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

import java.util.List;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBooksByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.MissingBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.exception.NegativeBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;

/**
 * @brief Interfaccia per la gestione dei libri
 */
public interface BookService {
    /**
     * @brief Recupera tutti i libri presenti nel catalogo.
     * @return Una lista di libri contenente tutti i libri del catalogo.
     */
    List<Book> getAll();

    /**
     * @brief Recupera un libro tramite il suo codice ISBN.
     * @param isbn Il codice isbn del libro da cercare.
     * @return Un Optional<Book> {@link Book} che contiene il libro se trovato,
     * altrimenti Optional.empty().
     */
    Optional<Book> getByIsbn(String isbn);

    /**
     * @brief Recupare una lista di libri il cui ISBN contiene la stringa specificata
     *  in qualsiasi posizione.
     * @param isbn L'ISBN da cercare.
     * @return Una lista di {@link Book} contenente i libri che rispettano questo
     * criterio.
     */
    List<Book> getAllByIsbnContaining(String isbn);

    /**
     * @brief Recupera una lista di libri il cui autore contiene la stringa specificata
     *  in qualsiasi posizione.
     * @param author Il nome dell'autore da cercare.
     * @return Una lista di {@link Book} contenente i libri che rispettano questo
     * criterio.
     */
    List<Book> getAllByAuthorContaining(String author);

    /**
     * @brief Recupera una lista di libri il cui genere contiene la stringa specificata
     *  in qualsiasi posizione.
     * @param genre Il genere dei libri da cercare.
     * @return Una lista di {@link Book} contenente i libri che rispettano questo
     * criterio.
     */
    List<Book> getAllByGenreContaining(String genre);

    /**
     * @brief Recupera una lista di libri il cui anno di pubblicazione coincide con quello specificato.
     * @param releaseYear L'anno di pubblicazione da cercare.
     * @return Una lista di {@link Book} contenente i libri che rispettano questo
     * criterio.
     */
    List<Book> getAllByReleaseYear(int releaseYear);

    /**
     * @brief Recupera una lista di libri il cui titolo contiene la stringa specificata
     *  in qualsiasi posizione.
     * @param title Il titolo dei libri da cercare.
     * @return Una lista di {@link Book} contenente i libri che rispettano questo
     * criterio.
     */
    List<Book> getAllByTitleContaining(String title);

    /**
     * @brief Rimuove un libro dal catalogo basandosi sul suo codice ISBN.
     * @param isbn Il codice ISBN del libro da rimuovere.
     * @return true se il libro è stato rimosso, false altrimenti.
     * @throws MissingBookCopiesException Se il libro specificato e' parte di un prestito attivo.
     */
    boolean removeByIsbn(String isbn) throws MissingBookCopiesException;

    /**
     * @brief Aggiunge un libro al catalogo.
     * @param book Il libro da aggiungere.
     * @throws DuplicateBookByIsbnException Se esiste già un libro con lo stesso ISBN.
     * @throws InvalidIsbnException Se l'ISBN del libro non è valido.
     * @throws NegativeBookCopiesException Se il numero di copie è negativo.
     */
    void add(Book book) throws DuplicateBookByIsbnException, InvalidIsbnException,
         NegativeBookCopiesException;

    /**
     * @brief Aggiunge una lista di libri al catalogo.
     * @param books La lista di libri da aggiungere.
     * @throws DuplicateBookByIsbnException Se esiste un libro duplicato (singolo).
     * @throws DuplicateBooksByIsbnException Se uno o più ISBN nella lista sono già presenti.
     * @throws InvalidIsbnException Se uno o più libri hanno un ISBN non valido.
     * @throws NegativeBookCopiesException Se uno o più libri hanno un numero di copie negativo.
     */
    void addAll(List<Book> books) throws DuplicateBookByIsbnException, DuplicateBooksByIsbnException,
         InvalidIsbnException, NegativeBookCopiesException;

    /**
     * @brief Aggiorna le informazioni di un libro già registrato.
     * @param book L'oggetto Book contenente l'ISBN del libro da modificare e
     *  le nuove informazioni da salvare.
     * @invariant L'ISBN del libro è un invariante. Se è necessario modificarlo
     *  bisogna eliminare e reinserire il libro.
     * @throws UnknownBookByIsbnException Se il libro da aggiornare non esiste.
     * @throws NegativeBookCopiesException Se i nuovi dati comportano un numero di copie negativo.
     * @throws InvalidBookCopiesException Se il numero di copie rimanenti > numero di copie totali.
     */
    void updateByIsbn(Book book) throws UnknownBookByIsbnException, NegativeBookCopiesException,
         InvalidBookCopiesException;

    /**
     * @brief Controlla se un libro con determinato ISBN è già stato registrato.
     * @param isbn l'ISBN del libro da controllare.
     * @return true se il libro esiste, false altrimenti.
     */
    boolean existsByIsbn(String isbn);

    /**
     * @brief Recupera tutti gli ISBN che esistono già nel catalogo tra quelli forniti.
     * @param isbns Una List<String> contenente gli ISBN da verificare.
     * @return Una List<String> contenente solo gli ISBN già esistenti nel catalogo.
     *  La lista sarà vuota se non ci sono duplicati.
     */
    List<String> existingIsbns(List<String> isbns);

    /**
     * @brief Conta le copie rimanenti di un libro.
     * @param isbn L'ISBN del libro da controllare.
     * @return Il numero di copie rimanenti.
     */
    int countRemainingCopies(String isbn);

    /**
     * @brief Controlla se un ISBN è valido.
     * @return true se l'ISBN è valido, false altrimenti.
     */
    boolean isIsbnValid(String isbn);

    /**
     * @brief Aggiorna il numero di copie rimanenti di un libro.
     * Somma il valore 'delta' alle copie attuali.
     * @param isbn L'ISBN del libro da aggiornare.
     * @param delta Il numero di copie da aggiungere (positivo) o rimuovere (negativo).
     * @throws UnknownBookByIsbnException Se il libro non esiste.
     * @throws NegativeBookCopiesException Se l'operazione porterebbe le copie < 0.
     * @throws InvalidBookCopiesException Se l'operazione porterebbe le copie > totale.
     */
    void updateRemainingCopies(String isbn, int delta) throws UnknownBookByIsbnException,
         NegativeBookCopiesException, InvalidBookCopiesException;
}
