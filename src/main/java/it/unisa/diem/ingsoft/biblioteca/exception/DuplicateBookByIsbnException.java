package it.unisa.diem.ingsoft.biblioteca.exception;

public class DuplicateBookByIsbnException extends BookException {
    public DuplicateBookByIsbnException() {
        super("Esiste già un libro con questo isbn nel catalogo");
    }
}
