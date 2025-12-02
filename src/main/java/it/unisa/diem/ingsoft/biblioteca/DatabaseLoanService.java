package it.unisa.diem.ingsoft.biblioteca;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DatabaseLoanService implements LoanService {
    private final Database database;

    public DatabaseLoanService(Database database) {
        this.database = database;
    }

	@Override
	public Optional<Loan> getByUserIDAndBookISBN(String userId, String bookISBN) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans"
                        + " WHERE userId = :userId AND bookISBN = :bookISBN")
                    .bind("userId", userId)
                    .bind("bookISBN", bookISBN)
                    .mapTo(Loan.class)
                    .findFirst());
    }

    @Override
    public List<Loan> getByUserID(String userId) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans"
                        + " WHERE (userId = :userId)")
                    .bind("userId", userId)
                    .mapTo(Loan.class)
                    .list());
    }

    @Override
    public List<Loan> getByBookISBN(String bookISBN) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans"
                        + " WHERE bookISBN = :bookISBN")
                    .bind("bookISBN", bookISBN)
                    .mapTo(Loan.class)
                    .list());
    }

    @Override
    public void register(String userId, String bookISBN, LocalDate start, LocalDate deadline) {
        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("INSERT INTO loans(bookISBN, userId,"
                        + "loanStart, loanDeadline)"
                        + "VALUES (:bookISBN, :userId, :loanStart, :loanDeadline)")
                    .bind("bookISBN", bookISBN)
                    .bind("userId", userId)
                    .bind("loanStart", start)
                    .bind("loanDeadline", deadline)
                    .execute());
    }

	@Override
	public void complete(String userId, String bookISBN, LocalDate end) {
        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("UPDATE loans(loanEnd) SET loanEnd = :loanEnd"
                        + "WHERE userId = :userId AND bookISBN = :bookISBN")
                    .bind("userId", userId)
                    .bind("bookISBN", bookISBN)
                    .bind("loanEnd", end)
                    .execute());
	}

	@Override
	public List<Loan> getAll() {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM loans")
                    .mapTo(Loan.class)
                    .list());
	}
}
