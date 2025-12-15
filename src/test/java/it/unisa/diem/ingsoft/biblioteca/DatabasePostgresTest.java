package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByEmailException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;

public class DatabasePostgresTest {
    private static Database database;
    private static UserService userService;

    @BeforeAll
    public static void setup() {
        System.out.println("--- INIZIO TEST POSTGRES ---");
        System.out.println("Assicurati di essere connesso ad internet!");
        System.out.println("Connessione...");

        String postgres = "jdbc:postgresql://nicolatorch.duckdns.org:5432/postgres?user=postgres&password=abc123";

        assertDoesNotThrow(() -> {
            database = Database.connect(postgres)
                .get();
        });

        userService = new DatabaseUserService(database);

        System.out.println("Connesso!");
    }

    @Test
    public void register_Throws() {
        assertThrows(DuplicateUserByEmailException.class, () -> {
            userService.register(new User("MATRICOLA2", "nicola@studenti.unisa.it", "NICOLA", "PICARELLA"));
        });

        assertThrows(DuplicateUserByIdException.class, () -> {
            userService.register(new User("MATRICOLA1", "nicola2@studenti.unisa.it", "NICOLA", "PICARELLA"));
        });
    }

    @Test
    public void get_UserEmail() {
        assertEquals("nicola@studenti.unisa.it", userService.getById("MATRICOLA1")
                .get()
                .getEmail());
    }

    @AfterAll
    public static void teardown () {
        System.out.println("--- FINE TEST POSTGRES ---");
    }
}
