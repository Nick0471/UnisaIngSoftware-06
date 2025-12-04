package it.unisa.diem.ingsoft.biblioteca.exception;

public class BookNotFoundException extends BookException {
    public BookNotFoundException(String isbn) {

        super("Libro con ISBN " + isbn + "non trovato nel database.");
    }
}
