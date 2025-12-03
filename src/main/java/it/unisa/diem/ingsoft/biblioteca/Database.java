package it.unisa.diem.ingsoft.biblioteca;

import org.jdbi.v3.core.Jdbi;

public class Database {
    private final Jdbi jdbi;

    public Database(String connectionUrl) {
        this.jdbi = Jdbi.create(connectionUrl, "", "");
        this.jdbi.registerRowMapper(new UserMapper());
        this.jdbi.registerRowMapper(new LoanMapper());

        this.setupTables();
    }

    public Jdbi getJdbi() {
        return this.jdbi;
    }

    private void setupTables() {
        this.jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id TEXT NOT NULL UNIQUE PRIMARY KEY,"
                    + "email TEXT NOT NULL UNIQUE,"
                    + "name TEXT NOT NULL,"
                    + "surname TEXT NOT NULL"
                    + ");");
        });

        this.jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE IF NOT EXISTS loans ("
                    + "id INTEGER PRIMARY KEY,"
                    + "book_isbn TEXT NOT NULL,"
                    + "user_id TEXT NOT NULL,"
                    + "loan_start DATE NOT NULL,"
                    + "loan_deadline DATE NOT NULL,"
                    + "loan_end DATE"
                    + ");");
        });

        this.jdbi.useHandle(handle -> {
            handle.execute("CREATE TABLE IF NOT EXISTS books ("
                    + "isbn TEXT NOT NULL PRIMARY KEY,"
                    + "title TEXT NOT NULL,"
                    + "author TEXT NOT NULL,"
                    + "release_year INTEGER NOT NULL,"
                    + "total_copies INTEGER NOT NULL,"
                    + "remaining_copies INTEGER NOT NULL,"
                    + "genre TEXT NOT NULL,"
                    + "description TEXT NOT NULL"
                    + ");");
        });
    }
}
