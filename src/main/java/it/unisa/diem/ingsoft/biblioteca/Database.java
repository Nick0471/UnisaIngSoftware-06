/**
 * @brief Database utilizzato per salvare i dati
 * @package it.unisa.diem.ingsoft.biblioteca
 */
package it.unisa.diem.ingsoft.biblioteca;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.jdbi.v3.core.Jdbi;

import it.unisa.diem.ingsoft.biblioteca.exception.DatabaseUnreachableException;
import it.unisa.diem.ingsoft.biblioteca.mapper.BookMapper;
import it.unisa.diem.ingsoft.biblioteca.mapper.LoanMapper;
import it.unisa.diem.ingsoft.biblioteca.mapper.UserMapper;

/**
 * @brief Classe di incapsulamento del database con JDBI
 */
public class Database {
    private final Jdbi jdbi;

    /**
     * @brief Costruttore che inizializza JDBI per un database
     * @param connection La connessione verso il database
     */
    public Database(Connection connection) {
        this.jdbi = Jdbi.create(connection);
        this.jdbi.registerRowMapper(new UserMapper());
        this.jdbi.registerRowMapper(new LoanMapper());
        this.jdbi.registerRowMapper(new BookMapper());

        this.setupTables();
    }

    /**
     * @brief Crea un oggetto di classe Database che incapsula JDBI per la connessione ad un
     *  database
     * @param connectionUrl L'URL che identifica la locazione del database
     * @return Un oggetto di tipo Database con la connessione specificata
     * @throws DatabaseUnreachableException La connessione è fallita
     */
    public static Database connect(String connectionUrl) throws DatabaseUnreachableException {
        // Non usiamo il try-with-resources: JDBI gestisce la connessione
        try {
            Connection connection = DriverManager.getConnection(connectionUrl);
            return new Database(connection);
        } catch(SQLException e) {
            throw new DatabaseUnreachableException(e);
        }
    }

    /**
     * @brief Crea un oggetto di classe Database che incapsula JDBI per la connessione ad un
     *  database IN-MEMORIA.
     *  Generalmente utilizzato per i test
     * @return Un oggetto di tipo Database con la connessione ad un database in memoria
     */
    public static Database inMemory() {
        // Non usiamo il try-with-resources: JDBI gestisce la connessione
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
            return new Database(connection);

        // Questa eccezione non sarà lanciata se l'url di connessione è corretto: il database è in memoria
        } catch(SQLException e) {
            throw new RuntimeException("Eccezione SQL con Database inMemory", e);
        }
    }


    /**
     * @brief Getter per l'istanza di JDBI connessa al database
     */
    public Jdbi getJdbi() {
        return this.jdbi;
    }

    private void setupTables() {
        String usersSql = "CREATE TABLE IF NOT EXISTS users ("
            + "id TEXT NOT NULL UNIQUE PRIMARY KEY,"
            + "email TEXT NOT NULL UNIQUE,"
            + "name TEXT NOT NULL,"
            + "surname TEXT NOT NULL"
            + ");";

        String loansSql = "CREATE TABLE IF NOT EXISTS loans ("
            + "id INTEGER PRIMARY KEY,"
            + "book_isbn TEXT NOT NULL,"
            + "user_id TEXT NOT NULL,"
            + "loan_start DATE NOT NULL,"
            + "loan_deadline DATE NOT NULL,"
            + "loan_end DATE"
            + ");";

        String booksSql = "CREATE TABLE IF NOT EXISTS books ("
            + "isbn TEXT NOT NULL PRIMARY KEY,"
            + "title TEXT NOT NULL,"
            + "author TEXT NOT NULL,"
            + "release_year INTEGER NOT NULL,"
            + "total_copies INTEGER NOT NULL,"
            + "remaining_copies INTEGER NOT NULL,"
            + "genre TEXT NOT NULL,"
            + "description TEXT NOT NULL"
            + ");";

        String passwordSql = "CREATE TABLE IF NOT EXISTS auth ("
            + "password_hash TEXT NOT NULL PRIMARY KEY"
            + ");";

        this.jdbi.useHandle(handle -> {
            handle.execute(usersSql);
            handle.execute(loansSql);
            handle.execute(booksSql);
            handle.execute(passwordSql);
        });
    }
}
