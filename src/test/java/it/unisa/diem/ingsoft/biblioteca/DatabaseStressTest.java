package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseStressTest {

    // Cartella temporanea creata da JUnit
    // Viene eliminata quando il test finisce
    @TempDir
    private static Path tempDir;

    private static Database database;
    private static UserService userService;
    private static BookService bookService;
    private static LoanService loanService;

    // DEVE ESSERE STATICO!
    @BeforeAll
    public static void setup() {
        Path dbPath = tempDir.resolve("stress.db");
        database = Database.at(dbPath);

        userService = new DatabaseUserService(database);
        bookService = new DatabaseBookService(database);
        loanService = new DatabaseLoanService(userService, bookService, database);

        System.out.println("--- INIZIO STRESS TEST ---");
    }

    @Test
    @Order(1)
    public void insert_Users() {
        int total = 10000;
        long start = System.currentTimeMillis();

        assertDoesNotThrow(() -> {
            // SCRIVIAMO SU DISCO SOLO QUANDO ABBIAMO INSERITO TUTTO!
            database.getJdbi().useTransaction(handle -> {
                for (int i = 0; i < total; i++) {
                    String email = String.format("email.test%d@studenti.unisa.it", i);
                    String id = String.format("MATRI%05d", i);

                    User user = new User(id, email, "TESTNAME", "TESTSURNAME");
                    userService.register(user);
                }
            });
        });

        long end = System.currentTimeMillis();
        String out = String.format("INSERIMENTO DI %d UTENTI - IMPIEGATO: %d (ms)",
                total,
                end - start);

        System.out.println(out);
    }

    @Test
    @Order(2)
    public void insert_Books() {
        int total = 10000;
        long start = System.currentTimeMillis();

        assertDoesNotThrow(() -> {
            // SCRIVIAMO SU DISCO SOLO QUANDO ABBIAMO INSERITO TUTTO!
            database.getJdbi().useTransaction(handle -> {
                for (int i = 0; i < 10000; i++) {
                    String isbn = String.format("00000000%05d", i);

                    Book book = new Book(isbn, "TITOLO", "AUTORE", 1990, 3, 3, "DRAMMATICO", "DESC");
                    bookService.add(book);
                }
            });
        });

        long end = System.currentTimeMillis();
        String out = String.format("INSERIMENTO DI %d LIBRI - IMPIEGATO: %d (ms)",
                total,
                end - start);

        System.out.println(out);
    }

    @Test
    @Order(3)
    public void search_BooksAndUsers() {
        Optional<User> user = userService.getById("MATRI00500");
        assertTrue(user.isPresent());

        Optional<Book> book = bookService.getByIsbn("0000000009999");
        assertTrue(book.isPresent());
    }

    @Test
    @Order(4)
    public void register_SimultaneousLoans() {
        int total = 5000;
        long start = System.currentTimeMillis();
        LocalDate now = LocalDate.now();

        assertDoesNotThrow(() -> {
            // SCRIVIAMO SU DISCO SOLO QUANDO ABBIAMO INSERITO TUTTO!
            database.getJdbi().useTransaction(handle -> {
                for (int i = 0; i < total; i++) {
                    String userId = String.format("MATRI%05d", i);
                    String isbn = String.format("00000000%05d", i);

                    loanService.register(userId, isbn, now, now.plusDays(30));
                }
            });
        });

        long end = System.currentTimeMillis();
        String out = String.format("INSERIMENTO DI %d PRESTITI - IMPIEGATO: %d (ms)",
                total,
                end - start);

        System.out.println(out);
    }

    @Test
    @Order(5)
    public void search_UserById() {
        List<User> users = userService.getAllByIdContaining("MATRI0000");
        assertTrue(users.size() >= 10);
    }

    @Test
    @Order(6)
    public void performance_GetAllBooks() {
        long start = System.currentTimeMillis();

        List<Book> allBooks = bookService.getAll();

        long end = System.currentTimeMillis();

        assertTrue(allBooks.size() >= 10000);
        System.out.println(String.format("RECUPERO COMPLETO DI 10000 LIBRI: IMPIEGATO %d ms", 
                    end - start));
    }

    @Test
    @Order(7)
    public void performance_SearchByTitleContaining() {
        long start = System.currentTimeMillis();

        List<Book> results = bookService.getAllByTitleContaining("TITOLO");

        long end = System.currentTimeMillis();

        assertTrue(results.size() >= 10000);
        System.out.println(String.format("RICERCA SU 10000 LIBRI: IMPIEGATO %d ms", end - start));
    }

    @Test
    @Order(8)
    public void performance_ReturnLoans() {
        int count = 1000;
        long start = System.currentTimeMillis();
        LocalDate now = LocalDate.now();

        assertDoesNotThrow(() -> {
            database.getJdbi().useTransaction(handle -> {
                for (int i = 0; i < count; i++) {
                    String userId = String.format("MATRI%05d", i);
                    String isbn = String.format("00000000%05d", i);

                    loanService.complete(userId, isbn, now);
                }
            });
        });

        long end = System.currentTimeMillis();

        System.out.println(String.format("RESTITUZIONE DI %d PRESTITI: IMPIEGATO %d ms",
                    count, end - start));
    }

    @AfterAll
    public static void teardown() {
        database.close();

        System.out.println("--- FINE STRESS TEST ---");
    }
}
