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

        String bookISBN = rs.getString("bookISBN");
        String userId = rs.getString("userId");
        LocalDate loanStart = rs.getObject("loanStart", LocalDate.class);
        LocalDate loanEnd = rs.getObject("loanStart", LocalDate.class);
        LocalDate loanDeadline = rs.getObject("loanStart", LocalDate.class);

        loan.setBookISBN(bookISBN);
        loan.setUserId(userId);
        loan.setLoanStart(loanStart);
        loan.setLoanEnd(loanEnd);
        loan.setLoanDeadline(loanDeadline);

        return loan;
	}

}
