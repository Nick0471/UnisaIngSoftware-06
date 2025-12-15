package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private User validUser;

    private final String validId = "ABC123DEF5";
    private final String validEmail = "test@studenti.unisa.it";
    private final String validName = "NICOLA";
    private final String validSurname = "PICARELLA";
    private final String invalidEmail = "test@gmail.com";
    private final String invalidId = "ABC123";

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.userService = new DatabaseUserService(database);
        
        this.validUser = new User(this.validId, this.validEmail, this.validName, this.validSurname);
    }

    @Test
    public void register_ValidUser() {
        assertDoesNotThrow(() -> {
            this.userService.register(this.validUser);
        });
    }

    @Test
    public void register_InvalidEmail() {
        assertThrows(InvalidEmailException.class, () -> {
            User user = new User(this.validId, this.invalidEmail, this.validName, this.validSurname);
            this.userService.register(user);
        });
    }

    @Test
    public void register_InvalidId() {
        assertThrows(InvalidIdException.class, () -> {
            User user = new User(this.invalidId, "test2@studenti.unisa.it", this.validName, this.validSurname);
            this.userService.register(user);
        });
    }

    @Test
    public void register_DuplicateId() {
        assertDoesNotThrow(() -> this.userService.register(this.validUser));

        assertThrows(DuplicateUserByIdException.class, () -> {
            User duplicateId = new User(this.validId, "test3@studenti.unisa.it", this.validName, this.validSurname);
            this.userService.register(duplicateId);
        });
    }

    @Test
    public void register_DuplicateEmail() {
        assertDoesNotThrow(() -> this.userService.register(this.validUser));

        assertThrows(DuplicateUserByEmailException.class, () -> {
            User duplicateEmail = new User("DEF1231239", this.validEmail, this.validName, this.validSurname);
            this.userService.register(duplicateEmail);
        });
    }

    @Test
    public void getById_ExistingId() {
        assertDoesNotThrow(() -> this.userService.register(this.validUser));

        assertTrue(this.userService.getById(this.validId).isPresent());
        assertEquals(this.validName, this.userService.getById(this.validId).get().getName());
    }

    @Test
    public void getById_NonExistingId() {
        assertTrue(this.userService.getById("ABC123PEFO").isEmpty());
    }

    @Test
    public void getAll_Populated() {
        assertDoesNotThrow(() -> this.userService.register(this.validUser));
        assertFalse(this.userService.getAll().isEmpty());
    }

    @Test
    public void getAllByEmailContaining_ValidString() {
        assertDoesNotThrow(() -> this.userService.register(this.validUser));
        assertFalse(this.userService.getAllByEmailContaining("studenti.unisa.it").isEmpty());
    }

    @Test
    public void getAllByFullNameContaining_ValidStrings() {
        assertDoesNotThrow(() -> this.userService.register(this.validUser));

        assertFalse(this.userService.getAllByFullNameContaining("NICO", "ELLA").isEmpty());
        assertFalse(this.userService.getAllByFullNameContaining("LA", "PIC").isEmpty());
        assertFalse(this.userService.getAllByFullNameContaining("", "AREL").isEmpty());
    }

    @Test
    public void existsById_ExistingId() {
        assertDoesNotThrow(() -> this.userService.register(this.validUser));
        assertTrue(this.userService.existsById(this.validId));
    }

    @Test
    public void existsById_NonExistingId() {
        assertFalse(this.userService.existsById("INESISTENTE"));
    }

    @Test
    public void existsByEmail_ExistingEmail() {
        assertDoesNotThrow(() -> this.userService.register(this.validUser));
        assertTrue(this.userService.existsByEmail(this.validEmail));
    }

    @Test
    public void existsByEmail_NonExistingEmail() {
        assertFalse(this.userService.existsByEmail("INESISTENTE2@studenti.unisa.it"));
    }

    @Test
    public void removeById_ExistingId() {
        assertDoesNotThrow(() -> this.userService.register(this.validUser));
        assertTrue(this.userService.removeById(this.validId));
    }

    @Test
    public void removeById_NonExistingId() {
        assertFalse(this.userService.removeById("INESISTENTE"));
    }

    @Test
    public void updateById_ExistingUser() {
        assertDoesNotThrow(() -> this.userService.register(this.validUser));

        User updatedUser = new User(this.validId, "modified@studenti.unisa.it", "MODIFIED_NAME", "MODIFIED_SURNAME");
        assertDoesNotThrow(() -> this.userService.updateById(updatedUser));

        User retrieved = this.userService.getById(this.validId).get();
        assertEquals("modified@studenti.unisa.it", retrieved.getEmail());
        assertEquals("MODIFIED_NAME", retrieved.getName());
        assertEquals("MODIFIED_SURNAME", retrieved.getSurname());
    }

    @Test
    public void updateById_NonExistingUser() {
        User user = new User("1234567890", this.validEmail, "EMPTY", "EMPTY");
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
}
