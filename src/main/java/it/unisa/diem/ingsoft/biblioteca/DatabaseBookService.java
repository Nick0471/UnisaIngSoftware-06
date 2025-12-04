package it.unisa.diem.ingsoft.biblioteca;

import java.util.ArrayList;
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
        List<Book> listByAuthors = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getAuthor().equals(author)){
                listByAuthors.add(book);
            }
        }

        return listByAuthors;
    }

    @Override
    public List<Book> getByGenre(String genre){
        List<Book> listByGenre = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getGenre().equals(genre)){
                listByGenre.add(book);
            }
        }

        return listByGenre;
    }


    @Override
    public List<Book> getByReleaseYear(int releaseYear){
        List<Book> listByReleaseYear = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getReleaseYear() == releaseYear){
                listByReleaseYear.add(book);
            }
        }

        return listByReleaseYear;
    }

    @Override
    public List<Book> getByTitle(String title){
        List<Book> listByTitle = new ArrayList<>();
        for(Book book : this.getAll()){
            if(book.getTitle().equals(title)){
                listByTitle.add(book);
            }
        }

        return listByTitle;
    }

    @Override
    public boolean removeByIsbn(String isbn){
        return this.database.getJdbi()
                .withHandle(handle -> handle.createUpdate("DELETE FROM books WHERE isbn = :isbn")
                        .bind("isbn", isbn)
                        .execute()) > 0;
    }
}
