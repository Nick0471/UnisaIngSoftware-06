package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.LoanAlreadyRegisteredException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownLoanException;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;

public class DatabaseLoanServiceTest {
    private LoanService loanService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        UserService userService = new DatabaseUserService(database);
        BookService bookService = new DatabaseBookService(database);
        this.loanService = new DatabaseLoanService(userService, bookService, database);
    }

    @Test
    public void register_ValidLoan() {
        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);

        assertDoesNotThrow(() -> {
            this.loanService.register("USERID3214", "1234567890000", start, deadline);
        });
    }

    @Test
    public void register_LoanAlreadyRegistered() {
        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);

        assertDoesNotThrow(() -> {
            this.loanService.register("USERID3214", "1234567890000", start, deadline);
        });

        assertThrows(LoanAlreadyRegisteredException.class, () -> {
            this.loanService.register("USERID3214", "1234567890000", start, deadline);
        });
    }

    @Test
    public void register_InvalidUserId() {
        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);

        assertThrows(InvalidIdException.class, () -> {
            this.loanService.register("SHORT", "1234567890000", start, deadline);
        });
    }

    @Test
    public void register_InvalidBookIsbn() {
        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);

        assertThrows(InvalidIsbnException.class, () -> {
            this.loanService.register("USERID3214", "INVALID", start, deadline);
        });
    }

    @Test
    public void complete_ActiveLoan() throws Exception {
        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);
        String userId = "NICOLA1234";
        String isbn = "1234567890000";

        this.loanService.register(userId, isbn, start, deadline);
        assertTrue(this.loanService.isActive(userId, isbn));

        assertDoesNotThrow(() -> {
            this.loanService.complete(userId, isbn, LocalDate.now());
        });

        assertFalse(this.loanService.isActive(userId, isbn));
    }

    @Test
    public void complete_NonExistentLoan() {
        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete("NON_EXISTENT", "1234567890000", LocalDate.now());
        });
    }

    @Test
    public void complete_AlreadyCompletedLoan() throws Exception {
        this.loanService.register("NICOLA1234", "1234567890000", LocalDate.now(), LocalDate.now().plusDays(30));
        this.loanService.complete("NICOLA1234", "1234567890000", LocalDate.now());

        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete("NICOLA1234", "1234567890000", LocalDate.now());
        });
    }

    @Test
    public void getByUserIdAndBookIsbn_ExistingLoan() throws Exception {
        String userId = "USERID3214";
        String isbn = "1234567890000";
        this.loanService.register(userId, isbn, LocalDate.now(), LocalDate.now().plusDays(30));

        assertTrue(this.loanService.getByUserIdAndBookIsbn(userId, isbn).isPresent());
    }

    @Test
    public void getByUserIdAndBookIsbn_NonExistingLoan() {
        assertTrue(this.loanService.getByUserIdAndBookIsbn("USERID3214", "1234567890000").isEmpty());
    }

    @Test
    public void getByUserId_ExistingLoans() throws Exception {
        String userId = "USERID3214";
        this.loanService.register(userId, "1234567890000", LocalDate.now(), LocalDate.now().plusDays(30));
        this.loanService.register(userId, "1234567890001", LocalDate.now(), LocalDate.now().plusDays(30));

        assertEquals(2, this.loanService.getByUserId(userId).size());
    }

    @Test
    public void getByBookIsbn_ExistingLoans() throws Exception {
        String isbn = "1234567890000";
        this.loanService.register("USER111111", isbn, LocalDate.now(), LocalDate.now().plusDays(30));
        this.loanService.register("USER222222", isbn, LocalDate.now(), LocalDate.now().plusDays(30));

        assertEquals(2, this.loanService.getByBookIsbn(isbn).size());
    }

    @Test
    public void getAll_Populated() throws Exception {
        this.loanService.register("USER111111", "1234567890000", LocalDate.now(), LocalDate.now().plusDays(30));
        this.loanService.register("USER222222", "1234567890001", LocalDate.now(), LocalDate.now().plusDays(30));

        assertEquals(2, this.loanService.getAll().size());
    }

    @Test
    public void getActive_AfterComplete() throws Exception {
        this.loanService.register("USER111111", "1234567890000", LocalDate.now(), LocalDate.now().plusDays(30));
        
        this.loanService.register("USER222222", "1234567890001", LocalDate.now(), LocalDate.now().plusDays(30));
        this.loanService.complete("USER222222", "1234567890001", LocalDate.now());

        assertEquals(1, this.loanService.getActive().size());
    }

    @Test
    public void getActiveByUserId_AfterComplete() throws Exception {
        String user = "USER_MIXED";
        this.loanService.register(user, "1234567890000", LocalDate.now(), LocalDate.now().plusDays(30));
        
        this.loanService.register(user, "1234567890001", LocalDate.now(), LocalDate.now().plusDays(30));
        this.loanService.complete(user, "1234567890000", LocalDate.now());

        assertEquals(1, this.loanService.getActiveByUserId(user).size());
    }

    @Test
    public void isActive_ActiveLoan() throws Exception {
        this.loanService.register("USERID3214", "1234567890000", LocalDate.now(), LocalDate.now().plusDays(30));
        assertTrue(this.loanService.isActive("USERID3214", "1234567890000"));
    }

    @Test
    public void isActive_ClosedLoan() throws Exception {
        this.loanService.register("USERID3214", "1234567890000", LocalDate.now(), LocalDate.now().plusDays(30));
        this.loanService.complete("USERID3214", "1234567890000", LocalDate.now());
        
        assertFalse(this.loanService.isActive("USERID3214", "1234567890000"));
    }

    @Test
    public void countById_ValidUser() throws Exception {
        String user = "USERID3214";
        this.loanService.register(user, "1234567890000", LocalDate.now(), LocalDate.now().plusDays(30));
        this.loanService.register(user, "1234567890001", LocalDate.now(), LocalDate.now().plusDays(30));
        
        assertEquals(2, this.loanService.countById(user));
    }

    @Test
    public void countById_InvalidUser() {
        assertThrows(InvalidIdException.class, () -> {
            this.loanService.countById("SHORT");
        });
    }

    @Test
    public void performance_Methods() {
        LocalDate now = LocalDate.now();
        Duration duration = Duration.ofMillis(100);

        assertDoesNotThrow(() -> {
            this.loanService.register("ABC1314156", "9788808123456", now, now.plusDays(30));
        });

        assertTimeout(duration, () -> this.loanService.getAll());
        assertTimeout(duration, () -> this.loanService.getActiveByUserId("ABC1314156"));
        assertTimeout(duration, () -> this.loanService.getByUserIdAndBookIsbn("ABC1314156", "9788808123456"));
        assertTimeout(duration, () -> this.loanService.getByUserId("ABC1314156"));
        assertTimeout(duration, () -> this.loanService.getByBookIsbn("9788808123456"));
        assertTimeout(duration, () -> this.loanService.isActive("ABC1314156", "9788808123456"));
        assertTimeout(duration, () -> this.loanService.countById("ABC1314156"));

        assertTimeout(duration, () -> {
            this.loanService.register("TEMPUSER11", "1234567890000", now, now.plusDays(15));
        });

        assertTimeout(duration, () -> {
            this.loanService.complete("TEMPUSER11", "1234567890000", now);
        });
    }
}
