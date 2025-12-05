package it.unisa.diem.ingsoft.biblioteca.service;

import java.util.List;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBooksByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;

public class DatabaseBookService implements BookService {
    private final Database database;

    public DatabaseBookService(Database database) {
        this.database = database;
    }

    @Override
    public List<Book> getAll() {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books")
                        .mapTo(Book.class)
                        .list());
    }

    @Override
    public Optional<Book> getByIsbn(String isbn){
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books"
                                + "WHERE ISBN = :ISBN")
                        .bind("ISBN", isbn)
                        .mapTo(Book.class)
                        .findFirst());
    }

    @Override
    public List<Book> getByAuthor(String author){
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books"
                                + " WHERE (author = :author)")
                        .bind("author", author)
                        .mapTo(Book.class)
                        .list());
    }

    @Override
    public List<Book> getByGenre(String genre){
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books"
                                + " WHERE (genre = :genre)")
                        .bind("genre", genre)
                        .mapTo(Book.class)
                        .list());
    }

    @Override
    public List<Book> getByReleaseYear(int releaseYear){
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books"
                                + " WHERE (release_year = :release_year)")
                        .bind("release_year", releaseYear)
                        .mapTo(Book.class)
                        .list());
    }

    @Override
    public List<Book> getByTitle(String title){
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books"
                                + " WHERE (title = :title)")
                        .bind("title", title)
                        .mapTo(Book.class)
                        .list());
    }

    @Override
    public boolean removeByIsbn(String isbn) throws UnknownBookByIsbnException {
        if(!this.existsByIsbn(isbn))
            throw new UnknownBookByIsbnException();

        return this.database.getJdbi()
                            .withHandle(handle -> handle.createUpdate("DELETE FROM books WHERE isbn = :isbn")
                            .bind("isbn", isbn)
                            .execute()) > 0;



    }

    @Override
    public void add(Book book) throws DuplicateBookByIsbnException {
        if (this.existsByIsbn(book.getIsbn())) {
            throw new DuplicateBookByIsbnException();
        }

        this.database.getJdbi()
                .withHandle(handle -> handle.createUpdate(
                                "INSERT INTO books (ISBN, title, author, genre, releaseYear) " +
                                        "VALUES (:isbn, :title, :author, :genre, :releaseYear)"
                        )
                        .bind("isbn", book.getIsbn())
                        .bind("title", book.getTitle())
                        .bind("author", book.getAuthor())
                        .bind("genre", book.getGenre())
                        .bind("release_year", book.getReleaseYear())
                        .execute());
    }

    @Override
    public void addAll(List<Book> books) throws DuplicateBooksByIsbnException {
        List<String> newIsbns = books.stream()
                .map(Book::getIsbn)
                .toList();

        List<String> existingIsbns = this.existingIsbns(newIsbns);

        if (!existingIsbns.isEmpty()) {
            throw new DuplicateBooksByIsbnException(newIsbns);
        }

        this.database.getJdbi()
                .useHandle(handle -> {
                    String sql = "INSERT INTO books (ISBN, title, author, genre, releaseYear) " +
                            "VALUES (:isbn, :title, :author, :genre, :release_year)";

                    var batch = handle.prepareBatch(sql);

                    for (Book book : books) {
                        batch.bind("isbn", book.getIsbn())
                                .bind("title", book.getTitle())
                                .bind("author", book.getAuthor())
                                .bind("genre", book.getGenre())
                                .bind("release_year", book.getReleaseYear())
                                .add();
                    }

                    batch.execute();
                });
    }

    @Override
    public void updateByIsbn(Book book) throws UnknownBookByIsbnException{
        if(!this.existsByIsbn(book.getIsbn()))
            throw new UnknownBookByIsbnException();

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
                                + "totalCopies = :totalCopies, "
                                + "remainingCopies = :remainingCopies, "
                                + "description = :description "
                                + "WHERE isbn = :isbn")
                        .bind("isbn", isbn)
                        .bind("title", title)
                        .bind("author", author)
                        .bind("genre", genre)
                        .bind("release_year", releaseYear)
                        .bind("totalCopies", totalCopies)
                        .bind("remainingCopies", remainingCopies)
                        .bind("description", description)
                        .execute());
    }

    public boolean existsByIsbn(String isbn) {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT COUNT(isbn) FROM books"
                                + "WHERE isbn = :isbn")
                        .bind("isbn", isbn)
                        .mapTo(Integer.class)
                        .one()) > 0;
    }

    public List<String> existingIsbns(List<String> isbns){
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT isbn FROM books WHERE isbn IN (<isbns>)")
                        .bindList("isbns", isbns)
                        .mapTo(String.class)
                        .list());
    }
}
