package it.unisa.diem.ingsoft.biblioteca;

import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;

import java.util.List;
import java.util.Optional;


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
                                + " WHERE (releaseYear = :releaseYear)")
                        .bind("releaseYear", releaseYear)
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
    public boolean removeByIsbn(String isbn){
        return this.database.getJdbi()
                .withHandle(handle -> handle.createUpdate("DELETE FROM books WHERE isbn = :isbn")
                        .bind("isbn", isbn)
                        .execute()) > 0;
    }

    @Override
    public void add(Book book){
        this.database.getJdbi()
                .withHandle(handle -> handle.createUpdate(
                                "INSERT INTO books (ISBN, title, author, genre, releaseYear) " +
                                        "VALUES (:isbn, :title, :author, :genre, :releaseYear)"
                        )
                        .bind("isbn", book.getIsbn())
                        .bind("title", book.getTitle())
                        .bind("author", book.getAuthor())
                        .bind("genre", book.getGenre())
                        .bind("releaseYear", book.getReleaseYear())
                        .execute());
    }


    @Override
    public void addAll(List<Book> books) {
        this.database.getJdbi()
                .useHandle(handle -> {
                    String sql = "INSERT INTO books (ISBN, title, author, genre, releaseYear) " +
                            "VALUES (:isbn, :title, :author, :genre, :releaseYear)";

                    var batch = handle.prepareBatch(sql);

                    for (Book book : books) {
                        batch.bind("isbn", book.getIsbn())
                                .bind("title", book.getTitle())
                                .bind("author", book.getAuthor())
                                .bind("genre", book.getGenre())
                                .bind("releaseYear", book.getReleaseYear())
                                .add();
                    }

                    batch.execute();
                });
    }

    @Override
    public void updateByIsbn(Book book){
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
                                + "releaseYear = :releaseYear, "
                                + "totalCopies = :totalCopies, "
                                + "remainingCopies = :remainingCopies, "
                                + "description = :description "
                                + "WHERE ISBN = :isbn")
                        .bind("isbn", isbn)
                        .bind("title", title)
                        .bind("author", author)
                        .bind("genre", genre)
                        .bind("releaseYear", releaseYear)
                        .bind("totalCopies", totalCopies)
                        .bind("remainingCopies", remainingCopies)
                        .bind("description", description)
                        .execute());
    }
}
