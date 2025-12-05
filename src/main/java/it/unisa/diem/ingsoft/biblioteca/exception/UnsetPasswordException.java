package it.unisa.diem.ingsoft.biblioteca.exception;

public class UnsetPasswordException extends RuntimeException {
    public UnsetPasswordException() {
        super("Non è presente alcuna password nel database");
    }
}
