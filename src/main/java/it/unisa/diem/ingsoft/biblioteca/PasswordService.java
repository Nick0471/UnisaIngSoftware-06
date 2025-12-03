package it.unisa.diem.ingsoft.biblioteca;

public interface PasswordService {
    void change(String password);
    boolean check(String password);
}
