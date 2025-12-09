package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.UnsetPasswordException;
import it.unisa.diem.ingsoft.biblioteca.service.DatabasePasswordService;
import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;

public class DatabasePasswordServiceTest {
    private PasswordService passwordService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.passwordService = new DatabasePasswordService(database);
    }

    @Test
    public void change() {
        String testPassword = "TEST-!%123";

        assertDoesNotThrow(() -> {
            this.passwordService.change(testPassword);
        });
    }

    @Test
    public void check() {
        String testPassword = "q!!@ABC123";

        assertThrows(UnsetPasswordException.class, () -> {
            this.passwordService.check(testPassword);
        });

        assertDoesNotThrow(() -> {
            this.passwordService.change(testPassword);
        });

        assertTrue(this.passwordService.check(testPassword));
        assertFalse(this.passwordService.check("INVALID_PASSWORD!@#"));
    }
}
