package it.unisa.diem.ingsoft.biblioteca;

import java.util.List;
import java.util.Optional;

public class DatabaseBookService implements BookService {

    private final Database database;

    public DatabaseBookService(Database database) {
        this.database = database;
    }

    public Optional<Book> getByISBN(String ISBN){}

    public List<Book> getByAuthor(String author){}

    public List<Book> getByGenre(String genre){}

    public List<Book> getByReleaseYear(int releaseYear){}

    public List<Book> getByTitle(String title){}
}

