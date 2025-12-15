package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIdException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.LoanAlreadyRegisteredException;
import it.unisa.diem.ingsoft.biblioteca.exception.NegativeBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownLoanException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownUserByIdException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;

public class DatabaseLoanServiceTest {
    private LoanService loanService;
    private BookService bookService;
    private UserService userService;

    private final String validIsbn = "1234567890000";
    private final String secondIsbn = "1234567890001";
    private final String invalidIsbn = "INVALID";
    private final String nonExistentIsbn = "1111111111111";
    
    private final String validUserId = "USERID3214";
    private final String secondUserId = "USERID9999";
    private final String invalidUserId = "SHORT";
    private final String nonExistentUserId = "GHOST12345";

    private final LocalDate start = LocalDate.now();
    private final LocalDate deadline = this.start.plusDays(30);

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.bookService = new DatabaseBookService(database);
        this.userService = new DatabaseUserService(database);
        this.loanService = new DatabaseLoanService(this.userService, this.bookService, database);
    }

    @Test
    public void register_ValidLoan() {
        this.createAndAddBook(this.validIsbn, 5);
        this.createAndAddUser(this.validUserId);

        assertEquals(5, this.bookService.countRemainingCopies(this.validIsbn));

        assertDoesNotThrow(() -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
        });

        assertEquals(4, this.bookService.countRemainingCopies(this.validIsbn));
    }

    @Test
    public void register_LoanAlreadyRegistered() {
        this.createAndAddBook(this.validIsbn, 5);
        this.createAndAddUser(this.validUserId);

        assertDoesNotThrow(() -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
        });

        assertThrows(LoanAlreadyRegisteredException.class, () -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
        });
    }

    @Test
    public void register_InvalidUserId() {
        assertThrows(InvalidIdException.class, () -> {
            this.loanService.register(this.invalidUserId, this.validIsbn, this.start, this.deadline);
        });
    }

    @Test
    public void register_InvalidBookIsbn() {
        assertThrows(InvalidIsbnException.class, () -> {
            this.loanService.register(this.validUserId, this.invalidIsbn, this.start, this.deadline);
        });
    }

    @Test
    public void register_NoCopiesRemaining() {
        assertDoesNotThrow(() -> {
            Book book = new Book(this.validIsbn, "Raro", "Autore", 2000, 1, 0, "Test", "Desc");
            this.bookService.add(book);
        });

        this.createAndAddUser(this.validUserId);

        assertThrows(NegativeBookCopiesException.class, () -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
        });
    }

    @Test
    public void register_UnknownBookByIsbn() {
        this.createAndAddUser(this.validUserId);

        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.loanService.register(this.validUserId, this.nonExistentIsbn, this.start, this.deadline);
        });
    }

    @Test
    public void register_UnknownUserById() {
        this.createAndAddBook(this.validIsbn, 5);

        assertThrows(UnknownUserByIdException.class, () -> {
            this.loanService.register(this.nonExistentUserId, this.validIsbn, this.start, this.deadline);
        });
    }

    @Test
    public void complete_ActiveLoan() {
        this.createAndAddUser(this.validUserId);
        this.createAndAddBook(this.validIsbn, 5);

        assertDoesNotThrow(() -> this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline));

        assertTrue(this.loanService.isActive(this.validUserId, this.validIsbn));
        assertEquals(4, this.bookService.countRemainingCopies(this.validIsbn));

        assertDoesNotThrow(() -> {
            this.loanService.complete(this.validUserId, this.validIsbn, LocalDate.now());
        });

        assertFalse(this.loanService.isActive(this.validUserId, this.validIsbn));
        assertEquals(5, this.bookService.countRemainingCopies(this.validIsbn));
    }

    @Test
    public void complete_NonExistentLoan() {
        this.createAndAddBook(this.validIsbn, 5);
        this.createAndAddUser(this.validUserId);

        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete(this.validUserId, this.validIsbn, LocalDate.now());
        });
    }

    @Test
    public void getByUserIdAndBookIsbn_ExistingLoan() {
        this.createAndAddBook(this.validIsbn, 5);
        this.createAndAddUser(this.validUserId);

        assertDoesNotThrow(() -> this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline));

        assertTrue(this.loanService.getByUserIdAndBookIsbn(this.validUserId, this.validIsbn).isPresent());
    }

    @Test
    public void getByUserIdAndBookIsbn_NonExistingLoan() {
        assertTrue(this.loanService.getByUserIdAndBookIsbn(this.validIsbn, this.validUserId).isEmpty());
    }

    @Test
    public void getByUserId_ExistingLoans() {
        this.createAndAddBook(this.validIsbn, 5);
        this.createAndAddBook(this.secondIsbn, 5);
        this.createAndAddUser(this.validUserId);

        assertDoesNotThrow(() -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
            this.loanService.register(this.validUserId, this.secondIsbn, this.start, this.deadline);
        });

        assertEquals(2, this.loanService.getByUserIdContaining(this.validUserId).size());
    }

    @Test
    public void getByBookIsbn_ExistingLoans() {
        this.createAndAddBook(this.validIsbn, 5);
        this.createAndAddUser(this.validUserId);
        this.createAndAddUser(this.secondUserId);

        assertDoesNotThrow(() -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
            this.loanService.register(this.secondUserId, this.validIsbn, this.start, this.deadline);
        });

        assertEquals(2, this.loanService.getByBookIsbnContaining(this.validIsbn).size());
    }

    @Test
    public void getAll_Populated() {
        this.createAndAddBook(this.validIsbn, 5);
        this.createAndAddBook(this.secondIsbn, 5);
        this.createAndAddUser(this.validUserId);
        this.createAndAddUser(this.secondUserId);

        assertDoesNotThrow(() -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
            this.loanService.register(this.secondUserId, this.secondIsbn, this.start, this.deadline);
        });

        assertEquals(2, this.loanService.getAll().size());
    }

    @Test
    public void getActive_AfterComplete() {
        this.createAndAddBook(this.validIsbn, 5);
        this.createAndAddBook(this.secondIsbn, 5);
        this.createAndAddUser(this.validUserId);
        this.createAndAddUser(this.secondUserId);

        assertDoesNotThrow(() -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
            this.loanService.register(this.secondUserId, this.secondIsbn, this.start, this.deadline);
            
            this.loanService.complete(this.secondUserId, this.secondIsbn, LocalDate.now());
        });

        assertEquals(1, this.loanService.getActive().size());
    }

    @Test
    public void getActiveByUserId_AfterComplete() {
        this.createAndAddBook(this.validIsbn, 5);
        this.createAndAddBook(this.secondIsbn, 5);
        this.createAndAddUser(this.validUserId);

        assertDoesNotThrow(() -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
            this.loanService.register(this.validUserId, this.secondIsbn, this.start, this.deadline);
            
            this.loanService.complete(this.validUserId, this.validIsbn, LocalDate.now());
        });

        assertEquals(1, this.loanService.getActiveByUserId(this.validUserId).size());
    }

    @Test
    public void isActive_ActiveLoan() {
        this.createAndAddUser(this.validUserId);
        this.createAndAddBook(this.validIsbn, 5);

        assertDoesNotThrow(() -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
        });

        assertTrue(this.loanService.isActive(this.validUserId, this.validIsbn));
    }

    @Test
    public void isActive_ClosedLoan() {
        this.createAndAddUser(this.validUserId);
        this.createAndAddBook(this.validIsbn, 5);

        assertDoesNotThrow(() -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
            this.loanService.complete(this.validUserId, this.validIsbn, LocalDate.now());
        });

        assertFalse(this.loanService.isActive(this.validUserId, this.validIsbn));
    }

    @Test
    public void countById_ValidUser() {
        this.createAndAddUser(this.validUserId);
        this.createAndAddBook(this.validIsbn, 5);
        this.createAndAddBook(this.secondIsbn, 5);

        assertDoesNotThrow(() -> {
            this.loanService.register(this.validUserId, this.validIsbn, this.start, this.deadline);
            this.loanService.register(this.validUserId, this.secondIsbn, this.start, this.deadline);
        });

        assertDoesNotThrow(() -> {
            assertEquals(2, this.loanService.countById(this.validUserId));
        });
    }

    @Test
    public void countById_InvalidUser() {
        assertThrows(InvalidIdException.class, () -> {
            this.loanService.countById(this.invalidUserId);
        });
    }

    private void createAndAddBook(String isbn, int copies) {
        assertDoesNotThrow(() -> {
            Book book = new Book(isbn, "Titolo Test", "Autore Test", 2020, copies, copies, "Genre", "Desc");
            this.bookService.add(book);
        });
    }

    private void createAndAddUser(String id) {
        assertDoesNotThrow(() -> {
            User user = new User(id, id + "@studenti.unisa.it", "Name", "Surname");
            this.userService.register(user);
        });
    }
}
