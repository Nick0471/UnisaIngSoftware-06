package it.unisa.diem.ingsoft.biblioteca;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseBookService implements BookService {

    private final Database database;

    public DatabaseBookService(Database database) {
        this.database = database;
    }

    public List<Book> getAll() {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT * FROM books")
                        .mapTo(Book.class)
                        .list());
    }

    public Optional<Book> getByISBN(String ISBN){
        return this.getAll()
                .stream()
                .filter(book -> book.getISBN().equals(ISBN))
                .findFirst();
    }

    public List<Book> getByAuthor(String author){
        List<Book> listByAuthors = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getAuthor().equals(author)){
                listByAuthors.add(book);
            }
        }

        return listByAuthors;
    }

    public List<Book> getByGenre(String genre){
        List<Book> listByGenre = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getGenre().equals(genre)){
                listByGenre.add(book);
            }
        }

        return listByGenre;
    }

    public List<Book> getByReleaseYear(int releaseYear){
        List<Book> listByReleaseYear = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getReleaseYear() == releaseYear){
                listByReleaseYear.add(book);
            }
        }

        return listByReleaseYear;
    }

    public List<Book> getByTitle(String title){
        List<Book> listByTitle = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getTitle().equals(title)){
                listByTitle.add(book);
            }
        }

        return listByTitle;
    }

    public void removeByISBN(String ISBN){
        this.getAll().removeIf(book -> book.getISBN().equals(ISBN));
    }
}

