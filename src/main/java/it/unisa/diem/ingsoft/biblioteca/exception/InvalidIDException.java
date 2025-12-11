package it.unisa.diem.ingsoft.biblioteca.exception;

public class InvalidIDException extends RuntimeException {
    public InvalidIDException() {
        super("La matricola inserita per la registrazione non è valida!");
    }
}
