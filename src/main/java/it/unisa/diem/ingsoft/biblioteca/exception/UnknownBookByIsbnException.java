package it.unisa.diem.ingsoft.biblioteca.exception;

public class UnknownBookByIsbnException extends BookException {
    public UnknownBookByIsbnException() {
        super("Non esiste alcun libro con l'isbn inserito");
    }
}
