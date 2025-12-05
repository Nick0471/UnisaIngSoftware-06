/**
 * @brief Package dei mapper
 * @package it.unisa.diem.ingsoft.biblioteca.mapper
 */
package it.unisa.diem.ingsoft.biblioteca.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import it.unisa.diem.ingsoft.biblioteca.model.Book;

public class BookMapper implements RowMapper<Book> {
    /**
     * @brief Esegue la mappatura di una singola riga del ResultSet su un oggetto Book
     */
    @Override
    public Book map(ResultSet rs, StatementContext ctx) throws SQLException {
        Book book = new Book();

        String isbn = rs.getString("isbn");
        String title = rs.getString("title");
        String author = rs.getString("author");
        String genre = rs.getString("genre");
        String description = rs.getString("description");
        int releaseYear = rs.getInt("release_year");
        int totalCopies = rs.getInt("totalCopies");
        int remainingCopies = rs.getInt("remainingCopies");

        book.setIsbn(isbn);
        book.setTitle(title);
        book.setAuthor(author);
        book.setGenre(genre);
        book.setDescription(description);
        book.setReleaseYear(releaseYear);
        book.setTotalCopies(totalCopies);
        book.setRemainingCopies(remainingCopies);

        return book;
    }
}
