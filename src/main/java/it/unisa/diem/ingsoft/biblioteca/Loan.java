package it.unisa.diem.ingsoft.biblioteca;

import java.time.LocalDate;
import java.util.Optional;

public class Loan {
    private String bookISBN;
    private String userId;
    private LocalDate loanStart;
    private LocalDate loanDeadline;
    private LocalDate loanEnd;

    public Loan() {}

    public Loan(String bookISBN, String userId, LocalDate loanStart, LocalDate loanDeadline) {
        this.bookISBN = bookISBN;
        this.userId = userId;
        this.loanStart = loanStart;
        this.loanDeadline = loanDeadline;
    }

    public String getBookISBN() {
        return this.bookISBN;
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

    public void setBookISBN(String bookISBN) {
        this.bookISBN = bookISBN;
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
