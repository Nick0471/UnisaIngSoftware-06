/**
 * @brief Package dei mapper
 * @package it.unisa.diem.ingsoft.biblioteca.mapper
 */
package it.unisa.diem.ingsoft.biblioteca.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import it.unisa.diem.ingsoft.biblioteca.model.Book;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import it.unisa.diem.ingsoft.biblioteca.model.Loan;

/**
 * @brief Mapper che realizza la corrispondenza tra i dati salvati nelle colonne del
 * database della tabella 'loans' e un oggetto del modello {@link Loan}.
 */
public class LoanMapper implements RowMapper<Loan> {
    /**
     * @brief Esegue la mappatura di una singola riga del ResultSet su un oggetto Loan
     */
	@Override
	public Loan map(ResultSet rs, StatementContext ctx) throws SQLException {
        Loan loan = new Loan();

        String bookIsbn = rs.getString("book_isbn");
        String userId = rs.getString("user_id");
        LocalDate loanStart = rs.getObject("loan_start", LocalDate.class);
        LocalDate loanEnd = rs.getObject("loan_end", LocalDate.class);
        LocalDate loanDeadline = rs.getObject("loan_deadline", LocalDate.class);

        loan.setBookIsbn(bookIsbn);
        loan.setUserId(userId);
        loan.setLoanStart(loanStart);
        loan.setLoanEnd(loanEnd);
        loan.setLoanDeadline(loanDeadline);

        return loan;
	}

}
