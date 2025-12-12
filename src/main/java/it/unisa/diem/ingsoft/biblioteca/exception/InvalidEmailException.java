package it.unisa.diem.ingsoft.biblioteca.exception;

public class InvalidEmailException extends Exception {
    public InvalidEmailException() {
        super("La mail inserita per la registrazione non è valida!");
    }
}
