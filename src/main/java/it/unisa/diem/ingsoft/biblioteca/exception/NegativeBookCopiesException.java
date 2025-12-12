package it.unisa.diem.ingsoft.biblioteca.exception;

public class NegativeBookCopiesException extends BookException {
    public NegativeBookCopiesException() {
        super("Numero di copie del libro negativo!");
    }
}
