/**
 * @brief Package dei model
 * @package it.unisa.diem.ingsoft.biblioteca.model
 */
package it.unisa.diem.ingsoft.biblioteca.model;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private int releaseYear;
    private int totalCopies;
    private int remainingCopies;
    private String genre;
    private String description;

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

    public Book() {

    }

    public String getIsbn() {
        return this.isbn;
    }

    public void setIsbn(String ISBN) {
        this.isbn = ISBN;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getReleaseYear() {
        return this.releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public int getTotalCopies() {
        return this.totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getRemainingCopies() {
        return this.remainingCopies;
    }

    public void setRemainingCopies(int remainingCopies) {
        this.remainingCopies = remainingCopies;
    }

    public String getGenre() {
        return this.genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
