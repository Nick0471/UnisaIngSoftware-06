package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StressTest {
    private static UserService userService;
    private static BookService bookService;
    private static LoanService loanService;

    // DEVE ESSERE STATICO!
    @BeforeAll
    public static void setup() {
        Database db = Database.inMemory();
        userService = new DatabaseUserService(db);
        bookService = new DatabaseBookService(db);
        loanService = new DatabaseLoanService(userService, bookService, db);
    }

    @Test
    @Order(1)
    public void insert_Users() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 1000; i++) {
                String email = String.format("email.test%d@studenti.unisa.it", i);
                String id = String.format("MATRIC%04d", i);

                User user = new User(id, email, "TESTNAME", "TESTSURNAME");
                userService.register(user);
            }
        });
    }

    @Test
    @Order(2)
    public void insert_Books() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10000; i++) {
                String isbn = String.format("00000000%05d", i);

                Book book = new Book(isbn, "TITOLO", "AUTORE", 1990, 3, 3, "DRAMMATICO", "DESC");
                bookService.add(book);
            }
        });
    }

    @Test
    @Order(3)
    public void search_BooksAndUsers() {
        Optional<User> user = userService.getById("MATRIC0500");
        assertTrue(user.isPresent());

        Optional<Book> book = bookService.getByIsbn("0000000009999");
        assertTrue(book.isPresent());
    }

    @Test
    @Order(4)
    public void register_SimultaneousLoans() {
        assertDoesNotThrow(() -> {
            LocalDate now = LocalDate.now();

            for (int i = 0; i < 500; i++) {
                String userId = String.format("MATRIC%04d", i);
                String isbn = String.format("00000000%05d", i);

                loanService.register(userId, isbn, now, now.plusDays(30));
            }
        });

        assertTrue(userService.getById("MATRIC0499").isPresent());
        assertTrue(bookService.getByIsbn("0000000000499").isPresent());
        assertTrue(loanService.isActive("MATRIC0499", "0000000000499"));

        int copies = bookService.countRemainingCopies("0000000000499");
        assertEquals(2, copies);
    }

    @Test
    @Order(5)
    public void search_UserById() {
        List<User> users = userService.getAllByIdContaining("MATRIC000");
        assertTrue(users.size() >= 10);
    }
}
