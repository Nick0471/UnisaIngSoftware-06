package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.UnsetPasswordException;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseAuthService;
import it.unisa.diem.ingsoft.biblioteca.service.AuthService;

public class DatabasePasswordServiceTest {
    private AuthService passwordService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.passwordService = new DatabaseAuthService(database);
    }

    @Test
    public void change_NewPassword() {
        String testPassword = "TEST-!%123";

        assertDoesNotThrow(() -> {
            this.passwordService.changePassword(testPassword);
        });
    }

    @Test
    public void check_CorrectPassword() {
        String testPassword = "q!!@ABC123";
        this.passwordService.changePassword(testPassword);

        assertTrue(this.passwordService.checkPassword(testPassword));
    }

    @Test
    public void check_IncorrectPassword() {
        String testPassword = "correctPassword";
        this.passwordService.changePassword(testPassword);

        assertFalse(this.passwordService.checkPassword("WRONG_PASSWORD"));
    }

    @Test
    public void check_UnsetPassword() {
        assertThrows(UnsetPasswordException.class, () -> {
            this.passwordService.checkPassword("anyPassword");
        });
    }

    @Test
    public void isPresent_WhenPasswordSet() {
        this.passwordService.changePassword("SETPASSWORD");
        assertTrue(this.passwordService.isPresent());
    }

    @Test
    public void isPresent_WhenPasswordNotSet() {
        assertFalse(this.passwordService.isPresent());
    }
}
