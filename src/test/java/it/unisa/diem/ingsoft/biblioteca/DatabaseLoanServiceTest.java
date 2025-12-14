package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import it.unisa.diem.ingsoft.biblioteca.exception.*;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;

public class DatabaseLoanServiceTest {
    private LoanService loanService;
    private BookService bookService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        UserService userService = new DatabaseUserService(database);
        this.bookService = new DatabaseBookService(database);
        this.loanService = new DatabaseLoanService(userService, bookService, database);
    }

    @Test
    public void register_ValidLoan() {
        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);

        assertDoesNotThrow(() -> {
            Book book = new Book("1234567890000", "Titolo Test Prestiti", "Autore Test Prestiti", 2000, 5, 5, "Test", "Descrizione simpy");
            this.bookService.add(book);
        });

        assertEquals(5, bookService.countRemainingCopies("1234567890000"));

        assertDoesNotThrow(() -> {
            this.loanService.register("USERID3214", "1234567890000", start, deadline);
        });

        assertEquals(4, bookService.countRemainingCopies("1234567890000"));
    }

    @Test
    public void register_LoanAlreadyRegistered() {
        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);

        assertDoesNotThrow(() -> {
            Book book = new Book("1234567890000", "Titolo Test Prestiti", "Autore Test Prestiti", 2000, 5, 5, "Test", "Descrizione simpy");
            this.bookService.add(book);
        });

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
    public void register_NoCopiesRemaining() {
        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);

        Book Book = new Book("9999999999999", "Raro", "Autore", 2000, 1, 0, "Test", "Descrizione simpy");
        assertDoesNotThrow(() -> this.bookService.add(Book));

        assertThrows(NegativeBookCopiesException.class, () -> {
            this.loanService.register("1234567890", "9999999999999", start, deadline);
        });
    }

    @Test
    public void register_UnknownBookByIsbn() {
        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);

        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.loanService.register("USERID3214", "1111111111111", start, deadline);
        });
    }


    @Test
    public void complete_ActiveLoan() {
        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);
        String userId = "NICOLA1234";
        String isbn = "1234567890000";

        assertDoesNotThrow(() -> {
            Book book = new Book(isbn, "Titolo Test Prestiti", "Autore Test Prestiti", 2000, 5, 5, "Test", "Descrizione simpy");
            this.bookService.add(book);
        });

        assertDoesNotThrow(() -> {
            this.loanService.register(userId, isbn, start, deadline);
        });

        assertTrue(this.loanService.isActive(userId, isbn));
        assertEquals(4, bookService.countRemainingCopies(isbn));

        assertDoesNotThrow(() -> {
            this.loanService.complete(userId, isbn, LocalDate.now());
        });

        assertFalse(this.loanService.isActive(userId, isbn));
        assertEquals(5, bookService.countRemainingCopies(isbn));
    }

    @Test
    public void complete_NonExistentLoan() {

        assertDoesNotThrow(() -> {
            Book book = new Book("1234567890000", "Titolo Test Prestiti", "Autore Test Prestiti", 2000, 5, 5, "Test", "Descrizione simpy");
            this.bookService.add(book);
        });

        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete("NON_EXISTENT", "1234567890000", LocalDate.now());
        });
    }

    @Test
    public void complete_AlreadyCompletedLoan() {
        LocalDate now = LocalDate.now();
        assertDoesNotThrow(() -> {
            this.loanService.register("NICOLA1234", "1234567890000", now, now.plusDays(30));
            this.loanService.complete("NICOLA1234", "1234567890000", now);
        });

        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete("NICOLA1234", "1234567890000", now);
        });
    }

    @Test
    public void getByUserIdAndBookIsbn_ExistingLoan() {
        LocalDate now = LocalDate.now();
        String userId = "USERID3214";
        String isbn = "1234567890000";

        assertDoesNotThrow(() -> {
            this.loanService.register(userId, isbn, now, now.plusDays(30));
        });

        assertTrue(this.loanService.getByUserIdAndBookIsbn(userId, isbn).isPresent());
    }

    @Test
    public void getByUserIdAndBookIsbn_NonExistingLoan() {
        assertTrue(this.loanService.getByUserIdAndBookIsbn("USERID3214", "1234567890000").isEmpty());
    }

    @Test
    public void getByUserId_ExistingLoans() {
        LocalDate now = LocalDate.now();
        String userId = "USERID3214";

        assertDoesNotThrow(() -> {
            this.loanService.register(userId, "1234567890000", now, now.plusDays(30));
            this.loanService.register(userId, "1234567890001", now, now.plusDays(30));
        });

        assertEquals(2, this.loanService.getByUserIdContaining(userId).size());
    }

    @Test
    public void getByBookIsbn_ExistingLoans() {
        LocalDate now = LocalDate.now();
        String isbn = "1234567890000";

        assertDoesNotThrow(() -> {
            this.loanService.register("USER111111", isbn, now, now.plusDays(30));
            this.loanService.register("USER222222", isbn, now, now.plusDays(30));
        });

        assertEquals(2, this.loanService.getByBookIsbnContaining(isbn).size());
    }

    @Test
    public void getAll_Populated() {
        LocalDate now = LocalDate.now();

        assertDoesNotThrow(() -> {
            this.loanService.register("USER111111", "1234567890000", now, now.plusDays(30));
            this.loanService.register("USER222222", "1234567890001", now, now.plusDays(30));
        });

        assertEquals(2, this.loanService.getAll().size());
    }

    @Test
    public void getActive_AfterComplete() {
        LocalDate now = LocalDate.now();

        assertDoesNotThrow(() -> {
            this.loanService.register("USER111111", "1234567890000", now, now.plusDays(30));

            this.loanService.register("USER222222", "1234567890001", now, now.plusDays(30));
            this.loanService.complete("USER222222", "1234567890001", now);
        });

        assertEquals(1, this.loanService.getActive().size());
    }

    @Test
    public void getActiveByUserId_AfterComplete() {
        LocalDate now = LocalDate.now();
        String user = "USER_MIXED";

        assertDoesNotThrow(() -> {
            this.loanService.register(user, "1234567890000", now, now.plusDays(30));

            this.loanService.register(user, "1234567890001", now, now.plusDays(30));
            this.loanService.complete(user, "1234567890000", now);
        });

        assertEquals(1, this.loanService.getActiveByUserId(user).size());
    }

    @Test
    public void isActive_ActiveLoan() {
        LocalDate now = LocalDate.now();

        assertDoesNotThrow(() -> {
            this.loanService.register("USERID3214", "1234567890000", now, now.plusDays(30));
        });

        assertTrue(this.loanService.isActive("USERID3214", "1234567890000"));
    }

    @Test
    public void isActive_ClosedLoan() {
        LocalDate now = LocalDate.now();

        assertDoesNotThrow(() -> {
            this.loanService.register("USERID3214", "1234567890000", now, now.plusDays(30));
            this.loanService.complete("USERID3214", "1234567890000", now);
        });

        assertFalse(this.loanService.isActive("USERID3214", "1234567890000"));
    }

    @Test
    public void countById_ValidUser() {
        LocalDate now = LocalDate.now();
        String user = "USERID3214";

        assertDoesNotThrow(() -> {
            this.loanService.register(user, "1234567890000", now, now.plusDays(30));
            this.loanService.register(user, "1234567890001", now, now.plusDays(30));
        });

        assertDoesNotThrow(() -> {
            assertEquals(2, this.loanService.countById(user));
        });
        
    }

    @Test
    public void countById_InvalidUser() {
        assertThrows(InvalidIdException.class, () -> {
            this.loanService.countById("SHORT");
        });
    }
}
