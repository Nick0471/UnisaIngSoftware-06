package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.NoSuchElementException;

import it.unisa.diem.ingsoft.biblioteca.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;

public class DatabaseUserServiceTest {
    private UserService userService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.userService = new DatabaseUserService(database);
    }

    @Test
    public void register() {
        assertThrows(InvalidEmailException.class, () -> {
            User user = new User("ABC123DEF4", "test@gmail.com", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF4", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertThrows(InvalidIDException.class, () -> {
            User user = new User("ABC123", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF5", "test2@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertThrows(DuplicateUserByIdException.class, () -> {
User duplicateId = new User("ABC123DEF5", "test3@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(duplicateId);
        });

        assertThrows(DuplicateUserByEmailException.class, () -> {
            User duplicateEmail = new User("DEF1231239", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(duplicateEmail);
        });
    }

    @Test
    public void get() {
        assertThrows(NoSuchElementException.class, () -> {
            this.userService.getById("ABC123PEFO")
                .get();
        });

        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF5", "test@studenti.unisa.it", "SOFIA", "MANCINI");
            this.userService.register(user);
        });

        assertDoesNotThrow(() -> {
            this.userService.getById("ABC123DEF5")
                .get();
        });

        assertFalse(this.userService.getAll().isEmpty());
        assertFalse(this.userService.getAllByEmailContaining("studenti.unisa.it").isEmpty());
        assertFalse(this.userService.getAllByFullNameContaining("SOF", "NCI").isEmpty());
        assertFalse(this.userService.getAllByFullNameContaining("OF", "MAN").isEmpty());
        assertFalse(this.userService.getAllByFullNameContaining("", "INI").isEmpty());
    }

    @Test
    public void exists() {
        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF5", "test@studenti.unisa.it", "VINCENZO DANIEL", "RAIMO");
            this.userService.register(user);
        });

        assertFalse(() -> this.userService.existsById("INESISTENTE"));
        assertFalse(() -> this.userService.existsByEmail("INESISTENTE2"));
        assertTrue(() -> this.userService.existsById("ABC123DEF5"));
        assertTrue(() -> this.userService.existsByEmail("test@studenti.unisa.it"));
    }

    @Test
    public void remove() {
        assertFalse(() -> this.userService.removeById("INESISTENTE"));

        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF5", "test@studenti.unisa.it", "NICOLO' MASSIMO", "LISENA");
            this.userService.register(user);
        });

        assertTrue(() -> this.userService.removeById("ABC123DEF5"));
    }

    @Test
    public void update() {
        assertThrows(UnknownUserByIdException.class, () -> {
            User user = new User("INESISTENTE", "INESISTENTE", "EMPTY", "EMPTY");
            this.userService.updateById(user);
        });

        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF5", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF5", "modified@studenti.unisa.it", "PICARELLA", "PICARELLA");
            this.userService.updateById(user);
        });

        assertDoesNotThrow(() -> {
            User user = this.userService.getById("ABC123DEF5").get();
            assertEquals(user.getEmail(), "modified@studenti.unisa.it");
            assertEquals(user.getName(), "PICARELLA");
            assertEquals(user.getSurname(), "PICARELLA");
        });
    }

    @Test
    public void validation() {
        assertFalse(this.userService.isEmailValid("INVALID@INVALID.IT"));
        assertFalse(this.userService.isEmailValid("INVALID"));
        assertFalse(this.userService.isEmailValid("@INVALID.IT"));
        assertFalse(this.userService.isEmailValid("nicola@studenti.unisa"));
        assertFalse(this.userService.isEmailValid("nicola@unisa.it"));
        assertTrue(this.userService.isEmailValid("nicola@studenti.unisa.it"));
    }

    @Test
    public void speed() {
        Duration duration = Duration.ofMillis(100);

        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF5", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertTimeout(duration, () -> {
            this.userService.getAll();
        });

        assertTimeout(duration, () -> {
            this.userService.getAllByIdContaining("ABC");
        });


        assertTimeout(duration, () -> {
            this.userService.getAllByEmailContaining("test@studenti.unisa");
        });

        assertTimeout(duration, () -> {
            this.userService.getAllByFullNameContaining("NIC", "PIC");
        });

        assertTimeout(duration, () -> {
            this.userService.getById("ABC123DEF5");
        });

        assertTimeout(duration, () -> {
            User user = new User("IDPAZZO123", "rara@studenti.unisa.it", "EMPTY", "EMPTY");
            this.userService.register(user);
        });

        assertTimeout(duration, () -> {
            this.userService.removeById("INESISTENTE");
        });

        assertTimeout(duration, () -> {
            User user = new User("ABC123DEF0", "test2@studenti.unisa.it", "NICOLASS", "PICARELLA");
            this.userService.updateById(user);
        });

        assertTimeout(duration, () -> {
            this.userService.existsById("ABC123DEF0");
        });

        assertTimeout(duration, () -> {
            this.userService.existsByEmail("test@studenti.unisa.it");
        });

        assertTimeout(duration, () -> {
            this.userService.isEmailValid("test@studenti.unisa.it");
        });

        assertTimeout(duration, () -> {
            this.userService.isIdValid("1234567890");
        });
    }
}
