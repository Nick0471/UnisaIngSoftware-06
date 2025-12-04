package it.unisa.diem.ingsoft.biblioteca;

import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DatabaseLoanService implements LoanService {
    private final Database database;

    public DatabaseLoanService(Database database) {
        this.database = database;
    }

	@Override
	public Optional<Loan> getByUserIDAndBookIsbn(String userId, String bookIsbn) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans"
                        + " WHERE user_id = :user_id AND book_isbn = :book_isbn")
                    .bind("user_id", userId)
                    .bind("book_isbn", bookIsbn)
                    .mapTo(Loan.class)
                    .findFirst());
    }

    @Override
    public List<Loan> getByUserId(String userId) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans"
                        + " WHERE (user_id = :user_id)")
                    .bind("user_id", userId)
                    .mapTo(Loan.class)
                    .list());
    }

    @Override
    public List<Loan> getByBookIsbn(String bookIsbn) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans"
                        + " WHERE book_isbn = :book_isbn")
                    .bind("book_isbn", bookIsbn)
                    .mapTo(Loan.class)
                    .list());
    }

    @Override
    public void register(String userId, String bookIsbn, LocalDate start, LocalDate deadline) {
        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("INSERT INTO loans(book_isbn, user_id,"
                        + "loan_start, loan_deadline)"
                        + "VALUES (:book_isbn, :user_id, :loan_start, :loan_deadline)")
                    .bind("book_isbn", bookIsbn)
                    .bind("user_id", userId)
                    .bind("loan_start", start)
                    .bind("loan_deadline", deadline)
                    .execute());
    }

	@Override
	public void complete(String userId, String bookIsbn, LocalDate end) {
        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("UPDATE loans(loan_end) SET loan_end = :loan_end"
                        + "WHERE user_id = :user_id AND book_isbn = :book_isbn")
                    .bind("user_id", userId)
                    .bind("book_isbn", bookIsbn)
                    .bind("loan_end", end)
                    .execute());
	}

	@Override
	@Override
	public List<Loan> getAll() {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans")
                    .mapTo(Loan.class)
                    .list());
	}

	@Override
	public boolean has(String userId, String bookIsbn) {
        return this.getByUserIDAndBookIsbn(userId, bookIsbn)
            .isPresent();
	}
}
