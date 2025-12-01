package it.unisa.diem.ingsoft.biblioteca;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    Optional<Loan> getByUserIDAndBookISBN(String userId, String bookISBN);
    List<Loan> getByUserID(String userId);
    List<Loan> getByBookISBN(String bookISBN);
};
