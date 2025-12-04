package it.unisa.diem.ingsoft.biblioteca.exception;

import java.sql.SQLException;

public class DatabaseUnreachableException extends Exception {
    public DatabaseUnreachableException(SQLException e) {
        super("Connessione al database fallita", e);
    }
} 
