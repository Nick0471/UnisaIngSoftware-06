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

public class DatabaseAuthServiceTest {
    private AuthService passwordService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.passwordService = new DatabaseAuthService(database);
    }

    @Test
    public void setDefault() {
        assertFalse(this.passwordService.isPresent());

        this.passwordService.setDefault("Password123", "Risposta1", "Risposta2", "Risposta3");

        assertTrue(this.passwordService.isPresent());

        assertTrue(this.passwordService.checkPassword("Password123"));
        assertTrue(this.passwordService.checkAnswer("Risposta1", 1));
        assertTrue(this.passwordService.checkAnswer("Risposta2", 2));
        assertTrue(this.passwordService.checkAnswer("Risposta3", 3));
    }

    @Test
    public void isPresent_ReturnsTrueOnlyIfDataExists() {
        assertFalse(this.passwordService.isPresent());
        this.passwordService.setDefault("P", "1", "2", "3");
        assertTrue(this.passwordService.isPresent());
    }

    @Test
    public void changePassword_UpdatesPassword() {
        this.passwordService.setDefault("OldPass", "A1", "A2", "A3");

        String newPass = "NewPass123";
        assertDoesNotThrow(() -> this.passwordService.changePassword(newPass));

        assertTrue(this.passwordService.checkPassword(newPass));
    }

    @Test
    public void changeAnswer_UpdatesSpecificAnswer() {
        this.passwordService.setDefault("Pass", "OldAns1", "Ans2", "Ans3");

        String newAns = "NewAns1";
        assertDoesNotThrow(() -> this.passwordService.changeAnswer(newAns, 1));

        assertTrue(this.passwordService.checkAnswer(newAns, 1));
        assertTrue(this.passwordService.checkAnswer("Ans2", 2));
    }

    @Test
    public void checkPassword_CorrectAndIncorrect() {
        this.passwordService.setDefault("MyPass", "A1", "A2", "A3");

        assertTrue(this.passwordService.checkPassword("MyPass"));
        assertFalse(this.passwordService.checkPassword("WrongPass"));
    }

    @Test
    public void checkAnswer_CorrectAndIncorrect() {
        this.passwordService.setDefault("Pass", "Roma", "Blu", "Pizza");

        assertTrue(this.passwordService.checkAnswer("Roma", 1));
        assertFalse(this.passwordService.checkAnswer("Parigi", 1));

        assertTrue(this.passwordService.checkAnswer("Blu", 2));
        assertFalse(this.passwordService.checkAnswer("Rosso", 2));
    }

    @Test
    public void check_UnsetPassword() {
        assertThrows(UnsetPasswordException.class, () -> {
            this.passwordService.checkPassword("anyPassword");
        });
    }

}
