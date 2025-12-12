package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByEmailException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidEmailException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIdException;
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
    public void register_ValidUser() {
        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF4", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });
    }

    @Test
    public void register_InvalidEmail() {
        assertThrows(InvalidEmailException.class, () -> {
            User user = new User("ABC123DEF4", "test@gmail.com", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });
    }

    @Test
    public void register_InvalidId() {
        assertThrows(InvalidIdException.class, () -> {
            User user = new User("ABC123", "test2@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });
    }

    @Test
    public void register_DuplicateId() {
        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF5", "test2@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertThrows(DuplicateUserByIdException.class, () -> {
            User duplicateId = new User("ABC123DEF5", "test3@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(duplicateId);
        });
    }

    @Test
    public void register_DuplicateEmail() {
        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF4", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertThrows(DuplicateUserByEmailException.class, () -> {
            User duplicateEmail = new User("DEF1231239", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(duplicateEmail);
        });
    }

    @Test
    public void getById_ExistingId() throws Exception {
        User user = new User("ABC123DEF5", "test@studenti.unisa.it", "SOFIA", "MANCINI");
        this.userService.register(user);

        assertTrue(this.userService.getByIdContaining("ABC123DEF5").isPresent());
        assertEquals("SOFIA", this.userService.getByIdContaining("ABC123DEF5").get().getName());
    }

    @Test
    public void getById_NonExistingId() {
        assertTrue(this.userService.getByIdContaining("ABC123PEFO").isEmpty());
    }

    @Test
    public void getAll_Populated() throws Exception {
        this.userService.register(new User("ABC123DEF5", "test@studenti.unisa.it", "SOFIA", "MANCINI"));
        assertFalse(this.userService.getAll().isEmpty());
    }

    @Test
    public void getAllByEmailContaining_ValidString() throws Exception {
        this.userService.register(new User("ABC123DEF5", "test@studenti.unisa.it", "SOFIA", "MANCINI"));
        assertFalse(this.userService.getAllByEmailContaining("studenti.unisa.it").isEmpty());
    }

    @Test
    public void getAllByFullNameContaining_ValidStrings() throws Exception {
        this.userService.register(new User("ABC123DEF5", "test@studenti.unisa.it", "SOFIA", "MANCINI"));
        
        assertFalse(this.userService.getAllByFullNameContaining("SOF", "NCI").isEmpty());
        assertFalse(this.userService.getAllByFullNameContaining("OF", "MAN").isEmpty());
        assertFalse(this.userService.getAllByFullNameContaining("", "INI").isEmpty());
    }

    @Test
    public void existsById_ExistingId() throws Exception {
        User user = new User("ABC123DEF5", "test@studenti.unisa.it", "VINCENZO DANIEL", "RAIMO");
        this.userService.register(user);
        
        assertTrue(this.userService.existsById("ABC123DEF5"));
    }

    @Test
    public void existsById_NonExistingId() {
        assertFalse(this.userService.existsById("INESISTENTE"));
    }

    @Test
    public void existsByEmail_ExistingEmail() throws Exception {
        User user = new User("ABC123DEF5", "test@studenti.unisa.it", "VINCENZO DANIEL", "RAIMO");
        this.userService.register(user);

        assertTrue(this.userService.existsByEmail("test@studenti.unisa.it"));
    }

    @Test
    public void existsByEmail_NonExistingEmail() {
        assertFalse(this.userService.existsByEmail("INESISTENTE2@studenti.unisa.it"));
    }

    @Test
    public void removeById_ExistingId() throws Exception {
        User user = new User("ABC123DEF5", "test@studenti.unisa.it", "NICOLO' MASSIMO", "LISENA");
        this.userService.register(user);

        assertTrue(this.userService.removeById("ABC123DEF5"));
    }

    @Test
    public void removeById_NonExistingId() {
        assertFalse(this.userService.removeById("INESISTENTE"));
    }

    @Test
    public void updateById_ExistingUser() throws Exception {
        User user = new User("ABC123DEF5", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
        this.userService.register(user);

        User updatedUser = new User("ABC123DEF5", "modified@studenti.unisa.it", "PICARELLA", "PICARELLA");
        assertDoesNotThrow(() -> this.userService.updateById(updatedUser));

        User retrieved = this.userService.getByIdContaining("ABC123DEF5").get();
        assertEquals("modified@studenti.unisa.it", retrieved.getEmail());
        assertEquals("PICARELLA", retrieved.getName());
        assertEquals("PICARELLA", retrieved.getSurname());
    }

    @Test
    public void updateById_NonExistingUser() {
        User user = new User("1234567890", "test@studenti.unisa.it", "EMPTY", "EMPTY");
        assertThrows(UnknownUserByIdException.class, () -> {
            this.userService.updateById(user);
        });
    }

    @Test
    public void isEmailValid_InvalidFormats() {
        assertFalse(this.userService.isEmailValid("INVALID@INVALID.IT"));
        assertFalse(this.userService.isEmailValid("INVALID"));
        assertFalse(this.userService.isEmailValid("@INVALID.IT"));
        assertFalse(this.userService.isEmailValid("nicola@studenti.unisa"));
        assertFalse(this.userService.isEmailValid("nicola@unisa.it"));
    }

    @Test
    public void isEmailValid_CorrectDomain() {
        assertTrue(this.userService.isEmailValid("nicola@studenti.unisa.it"));
    }

    @Test
    public void performance_Methods() {
        Duration duration = Duration.ofMillis(100);

        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF5", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
            this.userService.register(user);
        });

        assertTimeout(duration, () -> this.userService.getAll());
        assertTimeout(duration, () -> this.userService.getAllByIdContaining("ABC"));
        assertTimeout(duration, () -> this.userService.getAllByEmailContaining("test@studenti.unisa"));
        assertTimeout(duration, () -> this.userService.getAllByFullNameContaining("NIC", "PIC"));
        assertTimeout(duration, () -> this.userService.getByIdContaining("ABC123DEF5"));
        
        assertTimeout(duration, () -> {
            User user = new User("IDPAZZO123", "rara@studenti.unisa.it", "EMPTY", "EMPTY");
            this.userService.register(user);
        });

        assertTimeout(duration, () -> this.userService.removeById("INESISTENTE"));

        assertTimeout(duration, () -> {
            User user = new User("IDPAZZO123", "test2@studenti.unisa.it", "NICOLASS", "PICARELLA");
            this.userService.updateById(user);
        });

        assertTimeout(duration, () -> this.userService.existsById("IDPAZZO123"));
        assertTimeout(duration, () -> this.userService.existsByEmail("test@studenti.unisa.it"));
        assertTimeout(duration, () -> this.userService.isEmailValid("test@studenti.unisa.it"));
        assertTimeout(duration, () -> this.userService.isIdValid("1234567890"));
    }
}
