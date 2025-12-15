/**
 * @brief Package dei service
 * @package it.unisa.diem.ingsoft.biblioteca.service
 */
package it.unisa.diem.ingsoft.biblioteca.service;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.exception.UnsetAnswerException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnsetPasswordException;

/**
 * @brief Implementazione del AuthService usando un Database per la persistenza
 */
public class DatabaseAuthService implements AuthService {
    private final Database database;

    /**
     * @brief Costruisce un oggetto che implementa l'AuthService usando un database
     */
    public DatabaseAuthService(Database database) {
        this.database = database;
    }

    /**
     * @brief Inizializza o sovrascrive completamente la configurazione di sicurezza.
     * Cancella qualsiasi dato esistente e inserisce la nuova password e le risposte.
     * @param password la password da inserire.
     * @param answer1 la prima risposta da inserire
     * @param answer2 la seconda risposta da inserire
     * @param answer3 la terza risposta da inserire
     */
    @Override
    public void setup(String password, String answer1, String answer2, String answer3) {
        String passHash = BCrypt.hashpw(password, BCrypt.gensalt());
        String ans1Hash = BCrypt.hashpw(answer1, BCrypt.gensalt());
        String ans2Hash = BCrypt.hashpw(answer2, BCrypt.gensalt());
        String ans3Hash = BCrypt.hashpw(answer3, BCrypt.gensalt());

        this.database.getJdbi().useTransaction(handle -> {
            handle.execute("DELETE FROM auth");

            handle.createUpdate("INSERT INTO auth (password_hash, question_one, question_two, question_three) " +
                            "VALUES (:p, :q1, :q2, :q3)")
                    .bind("p", passHash)
                    .bind("q1", ans1Hash)
                    .bind("q2", ans2Hash)
                    .bind("q3", ans3Hash)
                    .execute();
        });
    }

/**
     * @brief Cambia la password di accesso al software.
     *  Esegue un updare SQL per aggiornare l'hash nel database.
     * @param password La nuova password.
     */
	@Override
	public void changePassword(String password) {
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
	public boolean checkPassword(String password) {
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
    * @brief Controlla se password e risposte sono state inserite.
    *  Conta le righe nella tabella auth.
    * @return true se è tutto inserito, false altrimenti.
    */
    @Override
    public boolean isPresent() {
        return this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT count(*) FROM auth")
                        .mapTo(Integer.class)
                        .one()) > 0;
    }

    /**
     * @brief Controlla se la risposta inserita è corretta.
     * Esegue una select SQL per ottenere l'hash della risposta specifica da confrontare.
     * @param answer La risposta da controllare.
     * @param number Il numero della risposta da controllare (1, 2 o 3).
     * @return true se la risposta è corretta, false altrimenti.
     * @throws UnsetAnswerException Se non c'è alcuna risposta alla domanda specificata.
     * @throws IllegalArgumentException Se il numero della domanda non è valido.
     */
    @Override
    public boolean checkAnswer(String answer, int number) {
        String column = this.getSecretAnswer(number);

        Optional<String> hashOpt = this.database.getJdbi()
                .withHandle(handle -> handle.createQuery("SELECT " + column + " FROM auth LIMIT 1")
                        .mapTo(String.class)
                        .findFirst());

        if (hashOpt.isEmpty()) {
            throw new UnsetAnswerException(number);
        }

        String hash = hashOpt.get();

        return BCrypt.checkpw(answer, hash);
    }

    @Override
    public void changeAnswer(String answer, int number) {
        String hash = BCrypt.hashpw(answer, BCrypt.gensalt());
        String column = this.getSecretAnswer(number);

        if (!this.isPresent()) {
            throw new UnsetAnswerException(number);
        }

        this.database.getJdbi()
                .useHandle(handle -> handle.createUpdate("UPDATE auth "
                                + "SET " + column + " = :answer_hash")
                        .bind("answer_hash", hash)
                        .execute());
    }

    private String getSecretAnswer(int questionNumber) {
        return switch (questionNumber) {
            case 1 -> "question_one";
            case 2 -> "question_two";
            case 3 -> "question_three";
            default -> throw new IllegalArgumentException("Numero domanda non valido: " + questionNumber);
        };
    }

    private void insertPassword(String hash) {
        this.database.getJdbi()
            .useHandle(handle -> handle.createUpdate("INSERT INTO auth(password_hash) "
                        + "VALUES (:password_hash)")
                    .bind("password_hash", hash)
                    .execute());
    }
}
