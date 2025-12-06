/**
 * @brief Package dei model
 * @package it.unisa.diem.ingsoft.biblioteca.model
 */
package it.unisa.diem.ingsoft.biblioteca.model;

/**
 * @brief Rappresenta un'entità Libro nel sistema della biblioteca.
 * Questa classe contiene tutte le informazioni relative a un singolo libro.
 */
public class Book {
    private String isbn;
    private String title;
    private String author;
    private int releaseYear;
    private int totalCopies;
    private int remainingCopies;
    private String genre;
    private String description;

    /**
     * @brief Costruttore completo per inizializzare tutti gli attributi del libro.
     *
     * @param ISBN Codice ISBN del libro.
     * @param title Titolo del libro.
     * @param author Autore/i del libro.
     * @param releaseYear Anno di pubblicazione.
     * @param totalCopies Numero totale di copie.
     * @param remainingCopies Numero di copie disponibili.
     * @param genre Genere del libro.
     * @param description Descrizione del libro.
     */
    public Book(String ISBN, String title, String author, int releaseYear, int totalCopies,
            int remainingCopies, String genre, String description) {
        this.isbn = ISBN;
        this.title = title;
        this.author = author;
        this.releaseYear = releaseYear;
        this.totalCopies = totalCopies;
        this.remainingCopies = remainingCopies;
        this.genre = genre;
        this.description = description;
    }

    /**
     * @brief Costruttore di default.
     * Necessario per alcune operazioni.
     */
    public Book() {

    }

    /**
     * @brief Restituisce il codice ISBN del libro.
     * @return Il codice ISBN (Stringa).
     */
    public String getIsbn() {
        return this.isbn;
    }

    /**
     * @brief Imposta il codice ISBN del libro.
     * @param ISBN Il nuovo codice ISBN.
     */
    public void setIsbn(String ISBN) {
        this.isbn = ISBN;
    }

    /**
     * @brief Restituisce il titolo del libro.
     * @return Il titolo (Stringa).
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * @brief Imposta il titolo del libro.
     * @param title Il nuovo titolo.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @brief Restituisce l'autore del libro.
     * @return L'autore (Stringa).
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * @brief Imposta l'autore del libro.
     * @param author Il nuovo autore.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @brief Restituisce l'anno di pubblicazione.
     * @return L'anno di pubblicazione (intero).
     */
    public int getReleaseYear() {
        return this.releaseYear;
    }

    /**
     * @brief Imposta l'anno di pubblicazione.
     * @param releaseYear Il nuovo anno di pubblicazione.
     */
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * @brief Restituisce il numero totale di copie.
     * @return Il numero totale di copie (intero).
     */
    public int getTotalCopies() {
        return this.totalCopies;
    }

    /**
     * @brief Imposta il numero totale di copie.
     * @param totalCopies Il nuovo numero totale di copie.
     */
    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    /**
     * @brief Restituisce il numero di copie disponibili per il prestito.
     * @return Il numero di copie disponibili (intero).
     */
    public int getRemainingCopies() {
        return this.remainingCopies;
    }

    /**
     * @brief Imposta il numero di copie disponibili.
     * @param remainingCopies Il nuovo numero di copie disponibili.
     */
    public void setRemainingCopies(int remainingCopies) {
        this.remainingCopies = remainingCopies;
    }

    /**
     * @brief Restituisce il genere del libro.
     * @return Il genere (Stringa).
     */
    public String getGenre() {
        return this.genre;
    }

    /**
     * @brief Imposta il genere del libro.
     * @param genre Il nuovo genere.
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * @brief Restituisce la descrizione del libro.
     * @return La descrizione (Stringa).
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @brief Imposta la descrizione del libro.
     * @param description La nuova descrizione o sinossi.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
