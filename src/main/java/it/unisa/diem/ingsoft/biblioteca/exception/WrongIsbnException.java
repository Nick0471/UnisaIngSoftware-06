package it.unisa.diem.ingsoft.biblioteca.exception;

public class WrongIsbnException extends BookException {
    public WrongIsbnException() {
        super("è stato inserito un isbn sbagliato!");
    }
}
