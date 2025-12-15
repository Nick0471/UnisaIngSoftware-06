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

    private static final String VALID_ISBN = "1234567890000";
    private static final String SECOND_ISBN = "1234567890001";
    private static final String INVALID_ISBN = "INVALID";
    private static final String NON_EXISTENT_ISBN = "1111111111111";
    
    private static final String VALID_USER_ID = "USERID3214";
    private static final String SECOND_USER_ID = "USERID9999";
    private static final String INVALID_USER_ID = "SHORT";
    private static final String NON_EXISTENT_USER_ID = "GHOST12345";

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
        this.createAndAddBook(VALID_ISBN, 5);
        this.createAndAddUser(VALID_USER_ID);

        assertEquals(5, this.bookService.countRemainingCopies(VALID_ISBN));

        assertDoesNotThrow(() -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
        });

        assertEquals(4, this.bookService.countRemainingCopies(VALID_ISBN));
    }

    @Test
    public void register_LoanAlreadyRegistered() {
        this.createAndAddBook(VALID_ISBN, 5);
        this.createAndAddUser(VALID_USER_ID);

        assertDoesNotThrow(() -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
        });

        assertThrows(LoanAlreadyRegisteredException.class, () -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
        });
    }

    @Test
    public void register_InvalidUserId() {
        assertThrows(InvalidIdException.class, () -> {
            this.loanService.register(INVALID_USER_ID, VALID_ISBN, this.start, this.deadline);
        });
    }

    @Test
    public void register_InvalidBookIsbn() {
        assertThrows(InvalidIsbnException.class, () -> {
            this.loanService.register(VALID_USER_ID, INVALID_ISBN, this.start, this.deadline);
        });
    }

    @Test
    public void register_NoCopiesRemaining() {
        assertDoesNotThrow(() -> {
            Book book = new Book(VALID_ISBN, "Raro", "Autore", 2000, 1, 0, "Test", "Desc");
            this.bookService.add(book);
        });

        this.createAndAddUser(VALID_USER_ID);

        assertThrows(NegativeBookCopiesException.class, () -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
        });
    }

    @Test
    public void register_UnknownBookByIsbn() {
        this.createAndAddUser(VALID_USER_ID);

        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.loanService.register(VALID_USER_ID, NON_EXISTENT_ISBN, this.start, this.deadline);
        });
    }

    @Test
    public void register_UnknownUserById() {
        this.createAndAddBook(VALID_ISBN, 5);

        assertThrows(UnknownUserByIdException.class, () -> {
            this.loanService.register(NON_EXISTENT_USER_ID, VALID_ISBN, this.start, this.deadline);
        });
    }

    @Test
    public void complete_ActiveLoan() {
        this.createAndAddUser(VALID_USER_ID);
        this.createAndAddBook(VALID_ISBN, 5);

        assertDoesNotThrow(() -> this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline));

        assertTrue(this.loanService.isActive(VALID_USER_ID, VALID_ISBN));
        assertEquals(4, this.bookService.countRemainingCopies(VALID_ISBN));

        assertDoesNotThrow(() -> {
            this.loanService.complete(VALID_USER_ID, VALID_ISBN, LocalDate.now());
        });

        assertFalse(this.loanService.isActive(VALID_USER_ID, VALID_ISBN));
        assertEquals(5, this.bookService.countRemainingCopies(VALID_ISBN));
    }

    @Test
    public void complete_NonExistentLoan() {
        this.createAndAddBook(VALID_ISBN, 5);
        this.createAndAddUser(VALID_USER_ID);

        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete(VALID_USER_ID, VALID_ISBN, LocalDate.now());
        });
    }

    @Test
    public void getByUserIdAndBookIsbn_ExistingLoan() {
        this.createAndAddBook(VALID_ISBN, 5);
        this.createAndAddUser(VALID_USER_ID);

        assertDoesNotThrow(() -> this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline));

        assertTrue(this.loanService.getByUserIdAndBookIsbn(VALID_ISBN, VALID_USER_ID).isPresent());
    }

    @Test
    public void getByUserIdAndBookIsbn_NonExistingLoan() {
        assertTrue(this.loanService.getByUserIdAndBookIsbn(VALID_ISBN, VALID_USER_ID).isEmpty());
    }

    @Test
    public void getByUserId_ExistingLoans() {
        this.createAndAddBook(VALID_ISBN, 5);
        this.createAndAddBook(SECOND_ISBN, 5);
        this.createAndAddUser(VALID_USER_ID);

        assertDoesNotThrow(() -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
            this.loanService.register(VALID_USER_ID, SECOND_ISBN, this.start, this.deadline);
        });

        assertEquals(2, this.loanService.getByUserIdContaining(VALID_USER_ID).size());
    }

    @Test
    public void getByBookIsbn_ExistingLoans() {
        this.createAndAddBook(VALID_ISBN, 5);
        this.createAndAddUser(VALID_USER_ID);
        this.createAndAddUser(SECOND_USER_ID);

        assertDoesNotThrow(() -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
            this.loanService.register(SECOND_USER_ID, VALID_ISBN, this.start, this.deadline);
        });

        assertEquals(2, this.loanService.getByBookIsbnContaining(VALID_ISBN).size());
    }

    @Test
    public void getAll_Populated() {
        this.createAndAddBook(VALID_ISBN, 5);
        this.createAndAddBook(SECOND_ISBN, 5);
        this.createAndAddUser(VALID_USER_ID);
        this.createAndAddUser(SECOND_USER_ID);

        assertDoesNotThrow(() -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
            this.loanService.register(SECOND_USER_ID, SECOND_ISBN, this.start, this.deadline);
        });

        assertEquals(2, this.loanService.getAll().size());
    }

    @Test
    public void getActive_AfterComplete() {
        this.createAndAddBook(VALID_ISBN, 5);
        this.createAndAddBook(SECOND_ISBN, 5);
        this.createAndAddUser(VALID_USER_ID);
        this.createAndAddUser(SECOND_USER_ID);

        assertDoesNotThrow(() -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
            this.loanService.register(SECOND_USER_ID, SECOND_ISBN, this.start, this.deadline);
            
            this.loanService.complete(SECOND_USER_ID, SECOND_ISBN, LocalDate.now());
        });

        assertEquals(1, this.loanService.getActive().size());
    }

    @Test
    public void getActiveByUserId_AfterComplete() {
        this.createAndAddBook(VALID_ISBN, 5);
        this.createAndAddBook(SECOND_ISBN, 5);
        this.createAndAddUser(VALID_USER_ID);

        assertDoesNotThrow(() -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
            this.loanService.register(VALID_USER_ID, SECOND_ISBN, this.start, this.deadline);
            
            this.loanService.complete(VALID_USER_ID, VALID_ISBN, LocalDate.now());
        });

        assertEquals(1, this.loanService.getActiveByUserId(VALID_USER_ID).size());
    }

    @Test
    public void isActive_ActiveLoan() {
        this.createAndAddUser(VALID_USER_ID);
        this.createAndAddBook(VALID_ISBN, 5);

        assertDoesNotThrow(() -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
        });

        assertTrue(this.loanService.isActive(VALID_USER_ID, VALID_ISBN));
    }

    @Test
    public void isActive_ClosedLoan() {
        this.createAndAddUser(VALID_USER_ID);
        this.createAndAddBook(VALID_ISBN, 5);

        assertDoesNotThrow(() -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
            this.loanService.complete(VALID_USER_ID, VALID_ISBN, LocalDate.now());
        });

        assertFalse(this.loanService.isActive(VALID_USER_ID, VALID_ISBN));
    }

    @Test
    public void countById_ValidUser() {
        this.createAndAddUser(VALID_USER_ID);
        this.createAndAddBook(VALID_ISBN, 5);
        this.createAndAddBook(SECOND_ISBN, 5);

        assertDoesNotThrow(() -> {
            this.loanService.register(VALID_USER_ID, VALID_ISBN, this.start, this.deadline);
            this.loanService.register(VALID_USER_ID, SECOND_ISBN, this.start, this.deadline);
        });

        assertDoesNotThrow(() -> {
            assertEquals(2, this.loanService.countById(VALID_USER_ID));
        });
    }

    @Test
    public void countById_InvalidUser() {
        assertThrows(InvalidIdException.class, () -> {
            this.loanService.countById(INVALID_USER_ID);
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
