/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBooksByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.MissingBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.exception.NegativeBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;

/**
 * @brief Implementazione del BookService usando un Database per la persistenza
 */
public class DatabaseBookService implements BookService {
    private final Pattern isbnPattern = Pattern.compile("\\d+");
    private final Database database;

    /**
     * @brief Costruisce un oggetto che implementa il BookService usando un database
     */
    public DatabaseBookService(Database database) {
        this.database = database;
    }

    /**
     * @brief Recupera tutti i libri presenti nel database.
     *  Esegue una query SQL per ottenere l'elenco completo di tutti i libri.
     * @return Una lista di libri contenente tutti i libri del catalogo.
     */
    @Override
    public List<Book> getAll() {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books")
                        .mapTo(Book.class)
                        .list());
    }

    /**
     * @brief Recupera un libro tramite il suo codice ISBN.
     *  Esegue una query SQL per ottenere il libro dal database.
     * @param isbn Il codice isbn del libro da cercare.
     * @return Un Optional<Book> {@link Book} che contiene il libro se trovato,
     * altrimenti Optional.empty().
     */
    @Override
    public Optional<Book> getByIsbn(String isbn) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books "
                                + "WHERE isbn = :isbn")
                        .bind("isbn", isbn)
                        .mapTo(Book.class)
                        .findFirst());
    }

    /**
     * @brief Recupera una lista di libri il cui autore contiene la stringa specificata
     *  in qualsiasi posizione.
     *  Esegue una query SQL per ottenere la lista dei libri dal database.
     * @param author Il nome dell'autore da cercare.
     * @return Una lista di {@link Book} contenente i libri che rispettano questo
     * criterio.
     */
    @Override
    public List<Book> getAllByAuthorContaining(String author) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books "
                                + "WHERE author LIKE :author")
                        .bind("author", "%" + author + "%")
                        .mapTo(Book.class)
                        .list());
    }

    /**
     * @brief Recupera una lista di libri il cui genere contiene la stringa specificata
     *  in qualsiasi posizione.
     *  Esegue una query SQL per ottenere la lista dei libri dal database.
     * @param genre Il genere dei libri da cercare.
     * @return Una lista di {@link Book} contenente i libri che rispettano questo
     * criterio.
     */
    @Override
    public List<Book> getAllByGenreContaining(String genre) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books "
                                + "WHERE genre LIKE :genre")
                        .bind("genre", "%" + genre + "%")
                        .mapTo(Book.class)
                        .list());
    }

    /**
     * @brief Recupera una lista di libri il cui anno di pubblicazione coincide con quello specificato.
     *  Esegue una query SQL per ottenere la lista dei libri dal database.
     * @param releaseYear L'anno di pubblicazione da cercare.
     * @return Una lista di {@link Book} contenente i libri che rispettano questo
     * criterio.
     */
    @Override
    public List<Book> getAllByReleaseYear(int releaseYear) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books "
                                + "WHERE release_year LIKE :release_year")
                        .bind("release_year", "%" + releaseYear + "%")
                        .mapTo(Book.class)
                        .list());
    }

    /**
     * @brief Recupera una lista di libri il cui titolo contiene la stringa specificata
     *  in qualsiasi posizione.
     *  Esegue una query SQL per ottenere la lista dei libri dal database.
     * @param title Il titolo dei libri da cercare.
     * @return Una lista di {@link Book} contenente i libri che rispettano questo
     * criterio.
     */
    @Override
    public List<Book> getAllByTitleContaining(String title) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books "
                                + "WHERE title LIKE :title")
                        .bind("title", "%" + title + "%")
                        .mapTo(Book.class)
                        .list());
    }

    /**
     * @brief Rimuove un libro dal catalogo basandosi sul suo codice ISBN.
     *  Esegue una delete SQL per rimuovere il libro se presente nel database.
     * @param isbn Il codice ISBN del libro da rimuovere.
     * @return true se il libro è stato rimosso, false altrimenti.
     * @throws MissingBookCopiesException Se il libro specificato e' parte di un prestito attivo.
     */
    @Override
    public boolean removeByIsbn(String isbn) throws MissingBookCopiesException {
        if(!this.existsByIsbn(isbn)) {
            return false;
        }

        Book book = this.getByIsbn(isbn)
            .get();

        if (book.getRemainingCopies() != book.getTotalCopies()) {
            throw new MissingBookCopiesException();
        }

        return this.database.getJdbi()
                            .withHandle(handle -> handle.createUpdate("DELETE FROM books "
                                        + "WHERE isbn = :isbn")
                            .bind("isbn", isbn)
                            .execute()) > 0;
    }

    /**
     * @brief Aggiunge un libro al catalogo.
     *  Esegue un insert SQL per l'inserimento del libro nel database, controllando che
     *  l'isbn inserito sia corretto.
     * @param book Il libro da aggiungere.
     * @throws DuplicateBookByIsbnException Se esiste già un libro con lo stesso ISBN.
     * @throws InvalidIsbnException Se l'ISBN del libro non è valido.
     * @throws NegativeBookCopiesException Se il numero di copie totali o rimanenti
     * è negativo.
     */
    @Override
    public void add(Book book) throws DuplicateBookByIsbnException, InvalidIsbnException,
           NegativeBookCopiesException {
        String isbn = book.getIsbn();

        if (this.existsByIsbn(isbn)) {
            throw new DuplicateBookByIsbnException();
        }

        if (!this.isIsbnValid(isbn)) {
            throw new InvalidIsbnException();
        }

        if (book.getRemainingCopies() < 0 || book.getTotalCopies() < 0) {
            throw new NegativeBookCopiesException();
        }

        this.database.getJdbi()
                .withHandle(handle -> handle.createUpdate(
                                "INSERT INTO books (isbn, title, author, genre, release_year, total_copies, remaining_copies, description) " +
                                        "VALUES (:isbn, :title, :author, :genre, :release_year, :total_copies, :remaining_copies, :description)"
                        )
                        .bind("isbn", book.getIsbn())
                        .bind("title", book.getTitle())
                        .bind("author", book.getAuthor())
                        .bind("genre", book.getGenre())
                        .bind("release_year", book.getReleaseYear())
                        .bind("total_copies", book.getTotalCopies())
                        .bind("remaining_copies", book.getRemainingCopies())
                        .bind("description", book.getDescription())
                        .execute());
    }

    /**
     * @brief Aggiunge una lista di libri al catalogo.
     *  Esegue una insert SQL per l'inserimento dei libri nel database, controllando che
     *  l'isbn sia corretto per ogni libro.
     * @param books La lista di libri da aggiungere.
     * @throws DuplicateBooksByIsbnException Se uno o più ISBN nella lista sono già
     * presenti nel database.
     * @throws InvalidIsbnException Se almeno un libro nella lista ha un ISBN non
     * valido.
     * @throws NegativeBookCopiesException Se almeno un libro ha un numero di copie
     * negativo.
     */
    @Override
    public void addAll(List<Book> books) throws DuplicateBooksByIsbnException, InvalidIsbnException,
        NegativeBookCopiesException {

        for(Book book : books){
            String isbn = book.getIsbn();
            if (!this.isIsbnValid(isbn)) {
                throw new InvalidIsbnException();
            }

            if (book.getRemainingCopies() < 0 || book.getTotalCopies() < 0) {
                throw new NegativeBookCopiesException();
            }
        }

        List<String> newIsbns = books.stream()
                .map(Book::getIsbn)
                .toList();

        List<String> existingIsbns = this.existingIsbns(newIsbns);

        if (!existingIsbns.isEmpty()) {
            throw new DuplicateBooksByIsbnException(newIsbns);
        }

        this.database.getJdbi()
                .useHandle(handle -> {
                    String sql = "INSERT INTO books (isbn, title, author, genre, release_year, total_copies, remaining_copies, description) " +
                            "VALUES (:isbn, :title, :author, :genre, :release_year, :total_copies, :remaining_copies, :description)";

                    var batch = handle.prepareBatch(sql);

                    for (Book book : books) {
                        batch.bind("isbn", book.getIsbn())
                                .bind("title", book.getTitle())
                                .bind("author", book.getAuthor())
                                .bind("genre", book.getGenre())
                                .bind("release_year", book.getReleaseYear())
                                .bind("total_copies", book.getTotalCopies())
                                .bind("remaining_copies", book.getRemainingCopies())
                                .bind("description", book.getDescription())
                                .add();
                    }

                    batch.execute();
                });
    }

    /**
     * @brief Aggiorna le informazioni di un libro già registrato.
     *  Esegue un update SQL per la modifica delle informazioni nel database.
     * @param book L'oggetto Book contenente l'ISBN del libro da modificare e
     *  le nuove informazioni da salvare.
     * @invariant L'ISBN del libro è un invariante. Se è necessario modificarlo
     *  bisogna eliminare e reinserire il libro.
     * @throws UnknownBookByIsbnException Se il libro con l'ISBN specificato non esiste.
     * @throws NegativeBookCopiesException Se il nuovo numero di copie è negativo.
     * @throws InvalidBookCopiesException Se il numero di copie rimanenti > numero di copie totali.
     */
    @Override
    public void updateByIsbn(Book book) throws UnknownBookByIsbnException,
           NegativeBookCopiesException, InvalidBookCopiesException {
        if (!this.existsByIsbn(book.getIsbn())) {
            throw new UnknownBookByIsbnException();
        }

        if (book.getTotalCopies() < 0 || book.getRemainingCopies() < 0) {
            throw new NegativeBookCopiesException();
        }

        if (book.getRemainingCopies() > book.getTotalCopies()) {
            throw new InvalidBookCopiesException();
        }

        String isbn = book.getIsbn();
        String title = book.getTitle();
        String author = book.getAuthor();
        String genre = book.getGenre();
        int releaseYear = book.getReleaseYear();
        int totalCopies = book.getTotalCopies();
        int remainingCopies = book.getRemainingCopies();
        String description = book.getDescription();

        this.database.getJdbi()
                .withHandle(handle -> handle.createUpdate("UPDATE books SET "
                                + "title = :title, "
                                + "author = :author, "
                                + "genre = :genre, "
                                + "release_year = :release_year, "
                                + "total_copies = :total_copies, "
                                + "remaining_copies = :remaining_copies, "
                                + "description = :description "
                                + "WHERE isbn = :isbn")
                        .bind("isbn", isbn)
                        .bind("title", title)
                        .bind("author", author)
                        .bind("genre", genre)
                        .bind("release_year", releaseYear)
                        .bind("total_copies", totalCopies)
                        .bind("remaining_copies", remainingCopies)
                        .bind("description", description)
                        .execute());
    }

    /**
     * @brief Controlla se un libro con determinato ISBN è già stato registrato.
     *  Esegue una select count SQL per contare gli ISBN già presenti nel database.
     * @param isbn l'ISBN del libro da controllare.
     * @return true se il libro esiste, false altrimenti.
     */
    @Override
    public boolean existsByIsbn(String isbn) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT COUNT(isbn) FROM books "
                                + "WHERE isbn = :isbn")
                        .bind("isbn", isbn)
                        .mapTo(Integer.class)
                        .one()) > 0;
    }

    /**
     * @brief Recupera tutti gli ISBN che esistono già nel catalogo tra quelli forniti.
     *  Esegue una select SQL per recuperare la lista degli isbn già presenti.
     * @param isbns Una List<String> contenente gli ISBN da verificare.
     * @return Una List<String> contenente solo gli ISBN già esistenti nel catalogo.
     *  La lista sarà vuota se non ci sono duplicati.
     */
    @Override
    public List<String> existingIsbns(List<String> isbns) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT isbn FROM books WHERE isbn IN (<isbns>)")
                        .bindList("isbns", isbns)
                        .mapTo(String.class)
                        .list());
    }

    /**
     * @brief Conta le copie rimanenti di un libro.
     * Esegue una select SQL per prendere la colonna relativa alle copie rimanenti nel database.
     * @param isbn L'ISBN del libro da controllare.
     * @return Il numero di copie rimanenti.
     */
	@Override
	public int countRemainingCopies(String isbn) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT remaining_copies FROM books "
                        + "WHERE isbn = :isbn")
                    .bind("isbn", isbn)
                    .mapTo(Integer.class)
                    .one());
	}

    /**
     * @brief Controlla se un ISBN è valido.
     *  Controlla se l'ISBN è lungo 13 caratteri.
     * @return true se l'ISBN è valido, false altrimenti.
     */
	@Override
	public boolean isIsbnValid(String isbn) {
        return isbn.length() == 13 && this.isbnPattern.matcher(isbn).matches();
	}

    /**
     * @brief Aggiorna il numero di copie rimanenti di un libro.
     * Somma il valore 'delta' alle copie attuali.
     * @param isbn L'ISBN del libro da aggiornare.
     * @param delta Il numero di copie da aggiungere (positivo) o rimuovere (negativo).
     * @throws UnknownBookByIsbnException Se il libro non esiste.
     * @throws NegativeBookCopiesException Se l'operazione porterebbe le copie < 0.
     * @throws InvalidBookCopiesException Se il numero di copie rimanenti > totale.
     */
    @Override
    public void updateRemainingCopies(String isbn, int delta) throws UnknownBookByIsbnException,
           NegativeBookCopiesException, InvalidBookCopiesException {
        if (!this.existsByIsbn(isbn)) {
            throw new UnknownBookByIsbnException();
        }

        Book book = this.getByIsbn(isbn)
            .get();

        int currentCopies = book.getRemainingCopies();
        int newRemainingCopies = currentCopies + delta;

        book.setRemainingCopies(newRemainingCopies);
        this.updateByIsbn(book);
    }


    /**
     * @brief Recupare una lista di libri il cui ISBN contiene la stringa specificata
     *  in qualsiasi posizione.
     *  Esegue una select SQL per ottenere i libri dal database.
     * @param isbn L'ISBN da cercare.
     * @return Una lista di {@link Book} contenente i libri che rispettano questo
     * criterio.
     */
	@Override
	public List<Book> getAllByIsbnContaining(String isbn) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books "
                                + "WHERE isbn LIKE :isbn")
                        .bind("isbn", "%" + isbn + "%")
                        .mapTo(Book.class)
                        .list());
	}
}
