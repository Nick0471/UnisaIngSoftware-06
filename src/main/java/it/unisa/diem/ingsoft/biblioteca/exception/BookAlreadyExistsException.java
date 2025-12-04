package it.unisa.diem.ingsoft.biblioteca.exception;

public class BookAlreadyExistsException extends BookException {
    public BookAlreadyExistsException(String isbn) {

        super("Impossibile aggiungere il libro. ISBN: " + isbn + " è già presente nel database.");
    }
}
