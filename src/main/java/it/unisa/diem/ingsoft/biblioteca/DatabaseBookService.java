package it.unisa.diem.ingsoft.biblioteca;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @brief Implementazione del servizio di gestione dei libri che interagisce con il
 * database.
 * * La classe fornisce metodi per ottenere libri presenti nel database
 * secondo un criterio di ricerca specificato.
 * Viene inoltre fornito un metodo per la rimozione dei libri memorizzati nel database.
 */
public class DatabaseBookService implements BookService {

    /**
     * @brief Riferimento al database utilizzato per l'accesso ai dati.
     */
    private final Database database;

    /**
     * @brief Costrutore della classe DatabaseBookService.
     * * @param database L'istanza del database da utilizzare per le operazioni sui libri.
     */
    public DatabaseBookService(Database database) {
        this.database = database;
    }

    /**
     * @brief Recupera tutti i libri presenti nel database.
     * * Esegue una query SQL per ottenere l'elenco completo di tutti i libri.
     * * @return Una lista di libri contenente tutti i libri del databse.
     */
    public List<Book> getAll() {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books")
                        .mapTo(Book.class)
                        .list());
    }

    /**
     * @brief Recupera un libro tramite il suo codice ISBN.
     * * Il metodo itera su tutti i libri ottenuti e ne cerca uno
     * corispondente all'ISBN fornito.
     * * @param ISBN Il codice ISBN (Stringa) del libro da cercare.
     * @return Un Optional<Book> che contiene il libro se trovato, altrimenti Optional.empty().
     */
    public Optional<Book> getByISBN(String ISBN){
        return this.getAll()
                .stream()
                .filter(book -> book.getISBN().equals(ISBN))
                .findFirst();
    }

    /**
     * @brief Recupera tutti i libri scritti da un determinato autore.
     * * Itera su tutti i libri presenti nel database e filtra per l'autore specificato.
     * * @param author Il nome dell'autore (Stringa) da cercare.
     * @return Una lista di libri contenente i libri dell'autore specificato.
     */
    public List<Book> getByAuthor(String author){
        List<Book> listByAuthors = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getAuthor().equals(author)){
                listByAuthors.add(book);
            }
        }

        return listByAuthors;
    }

    /**
     * @brief Recupera tutti i libri appartenenti a un determinato genere.
     * * Itera su tutti i libri e filtra in base al genere specificato.
     * * @param genre Il genere (Stringa) dei libri da cercare.
     * @return Una lista di libri contenente i libri del genere specificato.
     */
    public List<Book> getByGenre(String genre){
        List<Book> listByGenre = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getGenre().equals(genre)){
                listByGenre.add(book);
            }
        }

        return listByGenre;
    }

    /**
     * @brief Recupera tutti i libri pubblicati in un anno specifico.
     * * Itera su tutti i libri e filtra in base all'anno di pubblicazione specificato.
     * * @param releaseYear L'anno di pubblicazione (intero) da cercare.
     * @return Una lista di libri contenente i libri pubblicati nell'anno specificato.
     */
    public List<Book> getByReleaseYear(int releaseYear){
        List<Book> listByReleaseYear = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getReleaseYear() == releaseYear){
                listByReleaseYear.add(book);
            }
        }

        return listByReleaseYear;
    }

    /**
     * @brief Recupera tutti i libri con un determinato titolo.
     * * Itera su tuti i libri e filtra in base al titolo specificato.
     * * @param title Il titolo (Stringa) dei libri da cercare.
     * @return Una lista di libri contenente i libri con il titolo specificato.
     */
    public List<Book> getByTitle(String title){
        List<Book> listByTitle = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getTitle().equals(title)){
                listByTitle.add(book);
            }
        }

        return listByTitle;
    }

    /**
     * @brief Rimuove un libro dal database basandosi sul suo codice ISBN.
     * * Esegue un'istruzione SQL DELETE sul database per rimuovere
     * in modo permanente il record corrispondente all'ISBN.
     * * @param ISBN Il codice ISBN (Stringa) del libro da rimuovere.
     */
    public boolean removeByISBN(String ISBN){
        return this.database.getJdbi()
                .withHandle(handle -> handle.createUpdate("DELETE FROM books WHERE ISBN = :isbn")
                        .bind("isbn", ISBN)
                        .execute()) > 0;
    }
}
