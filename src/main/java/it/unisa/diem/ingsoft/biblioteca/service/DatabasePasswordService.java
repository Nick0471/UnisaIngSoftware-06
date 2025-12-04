package it.unisa.diem.ingsoft.biblioteca.service;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import it.unisa.diem.ingsoft.biblioteca.Database;

/**
 * @brief Implementazione del PasswordService usando un database
 */
public class DatabasePasswordService implements PasswordService {
    private final LogService logService;
    private final Database database;

    /**
     * @brief Costruisce un oggetto che implementa il PasswordService usando un database
     */
    public DatabasePasswordService(Database database, LogService logService) {
        this.database = database;
        this.logService = logService;
    }

	@Override
	public void change(String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("UPDATE auth"
                        + "SET password_hash = :password_hash")
                    .bind("password_hash", hash)
                    .execute());
	}

	@Override
	public boolean check(String password) {
        Optional<String> hashOpt = this.getPasswordHash();

        if (hashOpt.isEmpty()) {
            this.logService.logWarning("Il bibliotecario non ha ancora mai inserito una password di accesso!");
            return true;
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
