package it.unisa.diem.ingsoft.biblioteca;

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
	public void registerAll(List<User> users) {
        this.database.getJdbi()
            .useHandle(handle -> {
                var batch = handle.prepareBatch("INSERT INTO users(id, email, name, surname)"
                        + "VALUES (:id, :email, :name, :surname)");

                for (User user : users) {
                    batch.bind("id", user.getId())
                        .bind("email", user.getEmail())
                        .bind("name", user.getName())
                        .bind("surname", user.getSurname())
                        .add();
                }

                batch.execute();
            });
	}

	@Override
	public List<User> getAll() {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM users")
                    .mapTo(User.class)
                    .list());
	}

	@Override
	public Optional<User> getByEmail(String email) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT * FROM users"
                        + "WHERE email = :email")
                    .bind("email", email)
                    .mapTo(User.class)
                    .findFirst());
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
	public boolean removeByEmail(String email) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createUpdate("DELETE FROM users WHERE email = :email")
                    .bind("email", email)
                    .execute()) > 0;
	}

	@Override
	public boolean removeById(String id) {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createUpdate("DELETE FROM users WHERE id = :id")
                    .bind("id", id)
                    .execute()) > 0;
	}
}
