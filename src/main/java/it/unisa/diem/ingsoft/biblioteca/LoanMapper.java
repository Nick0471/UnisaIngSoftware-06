package it.unisa.diem.ingsoft.biblioteca;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class LoanMapper implements RowMapper<Loan> {

	@Override
	public Loan map(ResultSet rs, StatementContext ctx) throws SQLException {
        Loan loan = new Loan();

        String bookISBN = rs.getString("book_isbn");
        String userId = rs.getString("user_id");
        LocalDate loanStart = rs.getObject("loan_start", LocalDate.class);
        LocalDate loanEnd = rs.getObject("loan_end", LocalDate.class);
        LocalDate loanDeadline = rs.getObject("loan_deadline", LocalDate.class);

        loan.setBookISBN(bookISBN);
        loan.setUserId(userId);
        loan.setLoanStart(loanStart);
        loan.setLoanEnd(loanEnd);
        loan.setLoanDeadline(loanDeadline);

        return loan;
	}

}
