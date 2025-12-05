package it.unisa.diem.ingsoft.biblioteca.exception;

import it.unisa.diem.ingsoft.biblioteca.model.Book;

import java.util.List;

public class DuplicateBooksByIsbnException extends BookException {
    private final List<String> isbns;

    public DuplicateBooksByIsbnException(List<String> isbns) {
        super("Esistono già uno o più libri con questi isbn nel catalogo");
        this.isbns = isbns;
    }

    public List<String> getIsbns() {
        return this.isbns;
    }
}
