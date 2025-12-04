package it.unisa.diem.ingsoft.biblioteca;

import it.unisa.diem.ingsoft.biblioteca.Service.UserService;
import org.junit.jupiter.api.BeforeEach;

public class DatabaseUserServiceTest {
    private UserService userService;

    @BeforeEach
    public void setup() {
        Database database = new Database("jdbc:sqlite:test_db.db");
        this.userService = new DatabaseUserService(database);
    }
}
