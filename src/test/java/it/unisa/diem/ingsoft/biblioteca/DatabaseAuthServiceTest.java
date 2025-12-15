package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.UnsetPasswordException;
import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseAuthService;

public class DatabaseAuthServiceTest {
    private AuthService authService;

    @BeforeAll
    public static void log() {
        System.out.println("--- AVVIO TEST AUTH ---");
        System.out.println("L'auth usa BCrypt per l'hash di password e risposte segrete");
        System.out.println("Il calcolo dell'hash prevede un tempo di esecuzione lungo (per design)!");
    }

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.authService = new DatabaseAuthService(database);
    }

    @Test
    public void setDefault() {
        assertFalse(this.authService.isPresent());

        this.authService.setDefault("Password123", "Risposta1", "Risposta2", "Risposta3");

        assertTrue(this.authService.isPresent());

        assertTrue(this.authService.checkPassword("Password123"));
        assertTrue(this.authService.checkAnswer("Risposta1", 1));
        assertTrue(this.authService.checkAnswer("Risposta2", 2));
        assertTrue(this.authService.checkAnswer("Risposta3", 3));
    }

    @Test
    public void isPresent_ReturnsTrueOnlyIfDataExists() {
        assertFalse(this.authService.isPresent());
        this.authService.setDefault("P", "1", "2", "3");
        assertTrue(this.authService.isPresent());
    }

    @Test
    public void changePassword_UpdatesPassword() {
        this.authService.setDefault("OldPass", "A1", "A2", "A3");

        String newPass = "NewPass123";
        assertDoesNotThrow(() -> this.authService.changePassword(newPass));

        assertTrue(this.authService.checkPassword(newPass));
    }

    @Test
    public void changeAnswer_UpdatesSpecificAnswer() {
        this.authService.setDefault("Pass", "OldAns1", "Ans2", "Ans3");

        String newAns = "NewAns1";
        assertDoesNotThrow(() -> this.authService.changeAnswer(newAns, 1));

        assertTrue(this.authService.checkAnswer(newAns, 1));
        assertTrue(this.authService.checkAnswer("Ans2", 2));
    }

    @Test
    public void checkPassword_CorrectAndIncorrect() {
        this.authService.setDefault("MyPass", "A1", "A2", "A3");

        assertTrue(this.authService.checkPassword("MyPass"));
        assertFalse(this.authService.checkPassword("WrongPass"));
    }

    @Test
    public void checkAnswer_CorrectAndIncorrect() {
        this.authService.setDefault("Pass", "Roma", "Blu", "Pizza");

        assertTrue(this.authService.checkAnswer("Roma", 1));
        assertFalse(this.authService.checkAnswer("Parigi", 1));

        assertTrue(this.authService.checkAnswer("Blu", 2));
        assertFalse(this.authService.checkAnswer("Rosso", 2));
    }

    @Test
    public void check_UnsetPassword() {
        assertThrows(UnsetPasswordException.class, () -> {
            this.authService.checkPassword("anyPassword");
        });
    }

    @AfterAll
    public static void teardown() {
        System.out.println("--- FINE TEST AUTH ---");
    }
}
