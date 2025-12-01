package it.unisa.diem.ingsoft.biblioteca;

import org.jdbi.v3.core.Jdbi;

public class Database {
    private final Jdbi jdbi;

    public Database(String connectionUrl) {
        this.jdbi = Jdbi.create(connectionUrl, "", "");
        this.jdbi.registerRowMapper(new UserMapper());

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
    }
}
