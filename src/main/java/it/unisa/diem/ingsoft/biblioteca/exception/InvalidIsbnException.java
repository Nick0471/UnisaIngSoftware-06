package it.unisa.diem.ingsoft.biblioteca.exception;

public class InvalidIsbnException extends BookException {
    public InvalidIsbnException() {
        super("E' stato inserito un isbn sbagliato!");
    }
}
