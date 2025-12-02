package it.unisa.diem.ingsoft.biblioteca;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanService {
    List<Loan> getAll();
    Optional<Loan> getByUserIDAndBookISBN(String userId, String bookISBN);
    List<Loan> getByUserID(String userId);
    List<Loan> getByBookISBN(String bookISBN);
	void register(String userId, String bookISBN, LocalDate start, LocalDate deadline);
    void complete(String userId, String bookISBN, LocalDate end);
};
