package it.unisa.diem.ingsoft.biblioteca;

public interface LogService {
    void log(String message);
    void logWarning(String message);
    void logError(String message);
}
