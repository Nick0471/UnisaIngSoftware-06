/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.exception.UnsetPasswordException;

/**
 * @brief Implementazione del PasswordService usando un Database per la persistenza
 */
public class DatabasePasswordService implements PasswordService {
    private final Database database;

    /**
     * @brief Costruisce un oggetto che implementa il PasswordService usando un database
     */
    public DatabasePasswordService(Database database) {
        this.database = database;
    }

	@Override
	public void change(String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("UPDATE auth "
                        + "SET password_hash = :password_hash")
                    .bind("password_hash", hash)
                    .execute());
	}

	@Override
	public boolean check(String password) {
        Optional<String> hashOpt = this.getPasswordHash();

        if (hashOpt.isEmpty()) {
            throw new UnsetPasswordException();
        }

        String hash = hashOpt.get();
        return BCrypt.checkpw(password, hash);
	}

    private Optional<String> getPasswordHash() {
        return this.database.getJdbi()
            .withHandle(handle -> handle.createQuery("SELECT password_hash FROM auth LIMIT 1")
                    .mapTo(String.class)
                    .findFirst());
    }
}
