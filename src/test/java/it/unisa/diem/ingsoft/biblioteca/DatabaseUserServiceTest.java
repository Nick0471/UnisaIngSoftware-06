package it.unisa.diem.ingsoft.biblioteca;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DatabaseUserServiceTest {
    private UserService userService;

    @BeforeEach
    public void setup() {
        Database database = new Database("jdbc:sqlite:test_db.db");
        this.userService = new DatabaseUserService(database);
    }

    @Test
    public void userRegistration() {
        int maxUsers = 10_000;

        List<User> users = new ArrayList<>();
        for (int i = 0; i < maxUsers; i++) {
            User user = new User("ID" + i, "EMAIL" + i, "NAME" + i, "SURNAME" + i);
            users.add(user);
        }

        long start = System.currentTimeMillis();
        this.userService.registerAll(users);
        long end = System.currentTimeMillis();

        long elapsed = end - start;
        System.out.println("Elapsed (ms): " + elapsed);
    }
}
