package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

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
    public void change_NewPassword() {
        String testPassword = "TEST-!%123";

        assertDoesNotThrow(() -> {
            this.passwordService.change(testPassword);
        });
    }

    @Test
    public void check_CorrectPassword() {
        String testPassword = "q!!@ABC123";
        this.passwordService.change(testPassword);

        assertTrue(this.passwordService.check(testPassword));
    }

    @Test
    public void check_IncorrectPassword() {
        String testPassword = "correctPassword";
        this.passwordService.change(testPassword);

        assertFalse(this.passwordService.check("WRONG_PASSWORD"));
    }

    @Test
    public void check_UnsetPassword() {
        assertThrows(UnsetPasswordException.class, () -> {
            this.passwordService.check("anyPassword");
        });
    }

    @Test
    public void isPresent_WhenPasswordSet() {
        this.passwordService.change("SETPASSWORD");
        assertTrue(this.passwordService.isPresent());
    }

    @Test
    public void isPresent_WhenPasswordNotSet() {
        assertFalse(this.passwordService.isPresent());
    }

    @Test
    public void speed_Methods() {
        Duration duration = Duration.ofMillis(100);

        assertDoesNotThrow(() -> {
            this.passwordService.change("PASSWORD123!@@!");
        });

        assertTimeout(duration, () -> {
            this.passwordService.isPresent();
        });

        assertTimeout(duration, () -> {
            this.passwordService.check("PASSWORD123!@@!");
        });
    }
}
