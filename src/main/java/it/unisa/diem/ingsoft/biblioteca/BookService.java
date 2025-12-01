package it.unisa.diem.ingsoft.biblioteca;

import java.util.List;
import java.util.Optional;

public interface BookService {
    Optional<Book> getByISBN(String ISBN);
    List<Book> getByAuthor(String author);
    List<Book> getByGenre(String genre);
    List<Book> getByReleaseYear(int releaseYear);
    List<Book> getByTitle(String title);
}
