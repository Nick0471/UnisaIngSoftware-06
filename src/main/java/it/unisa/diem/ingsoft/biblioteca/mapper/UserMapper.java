/**
 * @brief Package dei mapper
 * @package it.unisa.diem.ingsoft.biblioteca.mapper
 */
package it.unisa.diem.ingsoft.biblioteca.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import it.unisa.diem.ingsoft.biblioteca.model.User;

/**
 * @brief Mapper che realizza la corrispondenza tra i dati salvati nelle colonne del
 * database della tabella 'users' e un oggetto del modello {@link User}.
 */
public class UserMapper implements RowMapper<User> {
    /**
     * @brief Esegue la mappatura di una singola riga del ResultSet su un oggetto User
     */
	@Override
	public User map(ResultSet rs, StatementContext ctx) throws SQLException {
        User user = new User();

        String id = rs.getString("id");
        String email = rs.getString("email");
        String name = rs.getString("name");
        String surname = rs.getString("surname");

        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        user.setSurname(surname);

        return user;
	}
}
