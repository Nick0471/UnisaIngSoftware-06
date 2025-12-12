package it.unisa.diem.ingsoft.biblioteca.exception;

public class InvalidIdException extends Exception {
    public InvalidIdException() {
        super("La matricola inserita per la registrazione non è valida!");
    }
}
