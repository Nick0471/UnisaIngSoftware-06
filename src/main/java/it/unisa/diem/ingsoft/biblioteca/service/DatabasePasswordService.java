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

    /**
     * @brief Cambia la password di accesso al software.
     *  Esegue un updare SQL per aggiornare l'hash nel database.
     * @param password La nuova password.
     */
	@Override
	public void change(String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        if (!this.isPresent()) {
            this.insertPassword(hash);
            return;
        }

        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("UPDATE auth "
                        + "SET password_hash = :password_hash")
                    .bind("password_hash", hash)
                    .execute());
	}

    /**
     * @brief Controlla se la password inserita è corretta.
     *  Esegue una select SQL per ottenere l'hash da confrontare.
     * @param password La password da controllare.
     * @return true se la password è corretta, false altrimenti.
     * @throws UnsetPasswordException Se non c'è alcuna password salvata nel database.
     */
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

    /**
     * @brief Controlla se esiste una password nel database
     *  Esegue una select SQL per controllare se la password esiste
     * @return true se la password esiste, false altrimenti
     */
	@Override
	public boolean isPresent() {
        return this.getPasswordHash()
            .isPresent();
	}

    private void insertPassword(String hash) {
        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("INSERT INTO auth(password_hash) "
                        + "VALUES (:password_hash)")
                    .bind("password_hash", hash)
                    .execute());
    }
}
