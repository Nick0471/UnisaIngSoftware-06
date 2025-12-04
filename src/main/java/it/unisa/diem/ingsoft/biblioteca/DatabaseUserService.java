package it.unisa.diem.ingsoft.biblioteca;

import java.util.List;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByEmailException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;

public class DatabaseUserService implements UserService {
    private final Database database;

    public DatabaseUserService(Database database) {
        this.database = database;
    }

	@Override
	public void register(User user) throws DuplicateUserByEmailException,
           DuplicateUserByIdException {
        String email = user.getEmail();
        if (this.existsByEmail(email))
            throw new DuplicateUserByEmailException();

        String id = user.getId();
        if (this.existsById(id))
            throw new DuplicateUserByIdException();

        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("INSERT INTO users(id, email, name, surname)"
                        + "VALUES (:id, :email, :name, :surname)")
                    .bind("id", user.getId())
                    .bind("email", user.getEmail())
                    .bind("name", user.getName())
                    .bind("surname", user.getSurname())
                    .execute());
	}

	@Override
	public List<User> getAll() {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM users")
                    .mapTo(User.class)
                    .list());
	}

	@Override
	public Optional<User> getById(String id) throws UnknownUserByIdException {
        if (!this.existsById(id))
            throw new UnknownUserByIdException();

        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM users"
                        + "WHERE id = :id")
                    .bind("id", id)
                    .mapTo(User.class)
                    .findFirst());
	}

	@Override
	public boolean removeById(String id) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createUpdate("DELETE FROM users WHERE id = :id")
                    .bind("id", id)
                    .execute()) > 0;
	}

	@Override
	public void updateById(User user) throws UnknownUserByIdException {
        String id = user.getId();
        if (!this.existsById(id))
            throw new UnknownUserByIdException();

        String email = user.getEmail();
        String name = user.getName();
        String surname = user.getSurname();

        this.database.getJdbi()
            .withHandle(handle -> handle.createUpdate("UPDATE users(email, name, surname)"
                        + "SET email = :email, name = :name, surname = :surname"
                        + "WHERE id = :id")
                    .bind("id", id)
                    .bind("email", email)
                    .bind("name", name)
                    .bind("surname", surname)
                    .execute());
	}

	@Override
	public boolean existsById(String id) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT COUNT(id) FROM users"
                        + "WHERE id = :id")
                    .bind("id", id)
                    .mapTo(Integer.class)
                    .one()) > 0;
	}

	@Override
	public boolean existsByEmail(String email) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT COUNT(email) FROM users"
                        + "WHERE email = :email")
                    .bind("email", email)
                    .mapTo(Integer.class)
                    .one()) > 0;
	}
}
