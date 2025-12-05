package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByEmailException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownUserByIdException;
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
        assertDoesNotThrow(() -> {
            User user = new User("ABC123", "test@gmail.com", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertThrows(DuplicateUserByIdException.class, () -> {
            User duplicateId = new User("ABC123", "test2@gmail.com", "NICOLA", "PICARELLA");
            this.userService.register(duplicateId);
        });

        assertThrows(DuplicateUserByEmailException.class, () -> {
            User duplicateEmail = new User("DEF123", "test@gmail.com", "NICOLA", "PICARELLA");
            this.userService.register(duplicateEmail);
        });
    }

    @Test
    public void get() {
        assertThrows(NoSuchElementException.class, () -> {
            this.userService.getById("ABC456")
                .get();
        });

        assertDoesNotThrow(() -> {
            User user = new User("ABC456", "test@virgilio.it", "SOFIA", "MANCINI");
            this.userService.register(user);
        });

        assertDoesNotThrow(() -> {
            this.userService.getById("ABC456")
                .get();
        });

        assertFalse(this.userService.getAll().isEmpty());
        assertFalse(this.userService.getAllByEmailContaining("test@virgilio").isEmpty());
        assertFalse(this.userService.getAllByFullNameContaining("SOF", "NCI").isEmpty());
        assertFalse(this.userService.getAllByFullNameContaining("OF", "MAN").isEmpty());
        assertFalse(this.userService.getAllByFullNameContaining("", "INI").isEmpty());
    }

    @Test
    public void exists() {
        assertDoesNotThrow(() -> {
            User user = new User("ABC789", "test@altervista.it", "VINCENZO DANIEL", "RAIMO");
            this.userService.register(user);
        });

        assertFalse(() -> this.userService.existsById("INESISTENTE"));
        assertFalse(() -> this.userService.existsByEmail("INESISTENTE2"));
        assertTrue(() -> this.userService.existsById("ABC789"));
        assertTrue(() -> this.userService.existsByEmail("test@altervista.it"));
    }

    @Test
    public void remove() {
        assertFalse(() -> this.userService.removeById("INESISTENTE"));

        assertDoesNotThrow(() -> {
            User user = new User("ABC101112", "test@alice.it", "NICOLO' MASSIMO", "LISENA");
            this.userService.register(user);
        });

        assertTrue(() -> this.userService.removeById("ABC101112"));
    }

    @Test
    public void update() {
        assertThrows(UnknownUserByIdException.class, () -> {
            User user = new User("INESISTENTE", "INESISTENTE", "EMPTY", "EMPTY");
            this.userService.updateById(user);
        });

        assertDoesNotThrow(() -> {
            User user = new User("ABC131415", "test@altervista.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertDoesNotThrow(() -> {
            User user = new User("ABC131415", "modified@altervista.it", "PICARELLA", "PICARELLA");
            this.userService.updateById(user);
        });

        assertDoesNotThrow(() -> {
            User user = this.userService.getById("ABC131415").get();
            assertEquals(user.getEmail(), "modified@altervista.it");
            assertEquals(user.getName(), "PICARELLA");
            assertEquals(user.getSurname(), "PICARELLA");
        });
    }
}
