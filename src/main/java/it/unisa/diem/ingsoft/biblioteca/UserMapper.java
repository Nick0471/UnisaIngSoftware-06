package it.unisa.diem.ingsoft.biblioteca;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

/**
 * @brief Mapper che permette di realizzare la corrispondenza tra i dati salvati
 *  nelle colonne del database ed un oggetto di classe User.
 *  Necessario per utilizzare il metodo `mapTo(User.class)` nelle Query
 */
public class UserMapper implements RowMapper<User> {
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
