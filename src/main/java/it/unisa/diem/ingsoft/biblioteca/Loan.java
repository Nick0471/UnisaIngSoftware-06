package it.unisa.diem.ingsoft.biblioteca;

import java.time.LocalDate;
import java.util.Optional;

public class Loan {
    private String bookIsbn;
    private String userId;
    private LocalDate loanStart;
    private LocalDate loanDeadline;
    private LocalDate loanEnd;

    public Loan() {}

    public Loan(String bookIsbn, String userId, LocalDate loanStart, LocalDate loanDeadline) {
        this.bookIsbn = bookIsbn;
        this.userId = userId;
        this.loanStart = loanStart;
        this.loanDeadline = loanDeadline;
    }

    public String getBookIsbn() {
        return this.bookIsbn;
    }

    public String getUserId() {
        return this.userId;
    }

    public LocalDate getLoanStart() {
        return this.loanStart;
    }

    public LocalDate getLoanDeadline() {
        return this.loanDeadline;
    }

    public Optional<LocalDate> getLoanEnd() {
        return Optional.ofNullable(this.loanEnd);
    }

    public void setLoanEnd(LocalDate loanEnd) {
        this.loanEnd = loanEnd;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLoanStart(LocalDate loanStart) {
        this.loanStart = loanStart;
    }

    public void setLoanDeadline(LocalDate loanDeadline) {
        this.loanDeadline = loanDeadline;
    }
}
