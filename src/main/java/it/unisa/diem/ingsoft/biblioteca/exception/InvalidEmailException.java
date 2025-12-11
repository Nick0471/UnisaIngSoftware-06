package it.unisa.diem.ingsoft.biblioteca.exception;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException() {
        super("La mail inserita per la registrazione non è valida!");
    }
}
