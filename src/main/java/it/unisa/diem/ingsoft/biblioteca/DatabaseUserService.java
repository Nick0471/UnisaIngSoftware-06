package it.unisa.diem.ingsoft.biblioteca;

import it.unisa.diem.ingsoft.biblioteca.Service.UserService;

import java.util.List;
import java.util.Optional;

public class DatabaseUserService implements UserService {
    private final Database database;

    public DatabaseUserService(Database database) {
        this.database = database;
    }

	@Override
	public void register(User user) {
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
	public Optional<User> getById(String id) {
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
	public void updateById(String id, User user) {
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
}
