package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTimeout;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;

public class DatabasePerformanceTest {

    @TempDir
    private static Path tempPath;

    private static Database database;
    private static UserService userService;
    private static BookService bookService;
    private static LoanService loanService;

    @BeforeAll
    public static void setup() {
        Path dbPath = tempPath.resolve("speed.db"); 
        database = Database.at(dbPath);

        userService = new DatabaseUserService(database);
        bookService = new DatabaseBookService(database);
        loanService = new DatabaseLoanService(userService, bookService, database);
    }

    @Test
    public void speed_BookService() {
        Duration duration = Duration.ofMillis(50);

        assertDoesNotThrow(() -> {
            Book book = new Book("1134567890001", "L'attacco dei giganti", "Hajime Isayama", 2009, 50, 5, "Dark fantasy", "Un bel manga");
            bookService.add(book);
        });

        assertTimeout(duration, () -> bookService.getAllByGenreContaining("Dark fantasy"));
        assertTimeout(duration, () -> bookService.getByIsbn("1134567890001"));
        assertTimeout(duration, () -> bookService.getAllByTitleContaining("L'attacco dei giganti"));
        assertTimeout(duration, () -> bookService.getAllByAuthorContaining("Hajime Isayama"));

        assertTimeout(duration, () -> {
            Book book = new Book("1234567891234", "Prova 2", "Autore", 2009, 50, 5, "Dark", "Un bel libro");
            bookService.add(book);
        });

        assertTimeout(duration, () -> bookService.removeByIsbn("INESISTENTE"));

        assertTimeout(duration, () -> {
            Book book = new Book("1234567891234", "Prova cambio velocità", "Autore", 2009, 50, 5, "Dark", "Un bel libro");
            bookService.updateByIsbn(book);
        });

        assertTimeout(duration, () -> bookService.existsByIsbn("1234567891234"));

        assertTimeout(duration, () -> {
            List<Book> books = List.of(
                    new Book("1234567890100", "One piece", "Oda", 1999, 50, 3, "Avventura", "Peak"),
                    new Book("1234567800200", "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Topo")
            );
            bookService.addAll(books);
        });
    }

    @Test
    public void speed_LoanService() {
        LocalDate now = LocalDate.now();
        Duration duration = Duration.ofMillis(50);

        assertDoesNotThrow(() -> {
            User user = new User("ABC1314156", "testperf@studenti.unisa.it", "Test", "Perf");
            userService.register(user);
            Book book = new Book("9788808123456", "Test Book", "Author", 2020, 10, 10, "Genre", "Desc");
            bookService.add(book);
            
            User user2 = new User("TEMPUSER11", "temp@studenti.unisa.it", "Temp", "User");
            userService.register(user2);
            Book book2 = new Book("1234567890000", "Test Book 2", "Author", 2020, 10, 10, "Genre", "Desc");
            bookService.add(book2);
        });

        assertDoesNotThrow(() -> {
            loanService.register("ABC1314156", "9788808123456", now, now.plusDays(30));
        });

        assertTimeout(duration, () -> loanService.getAll());
        assertTimeout(duration, () -> loanService.getActiveByUserId("ABC1314156"));
        assertTimeout(duration, () -> loanService.getByUserIdAndBookIsbn("ABC1314156", "9788808123456"));
        assertTimeout(duration, () -> loanService.getByUserIdContaining("ABC1314156"));
        assertTimeout(duration, () -> loanService.getByBookIsbnContaining("9788808123456"));
        assertTimeout(duration, () -> loanService.isActive("ABC1314156", "9788808123456"));
        assertTimeout(duration, () -> loanService.countById("ABC1314156"));

        assertTimeout(duration, () -> {
            loanService.register("TEMPUSER11", "1234567890000", now, now.plusDays(15));
        });

        assertTimeout(duration, () -> {
            loanService.complete("TEMPUSER11", "1234567890000", now);
        });
    }

    @Test
    public void speed_UserService() {
        Duration duration = Duration.ofMillis(50);

        assertDoesNotThrow(() -> {
            User user = new User("ABC123DEF5", "test@studenti.unisa.it", "NICOLA", "PICARELLA");
            userService.register(user);
        });

        assertTimeout(duration, () -> userService.getAll());
        assertTimeout(duration, () -> userService.getAllByIdContaining("ABC"));
        assertTimeout(duration, () -> userService.getAllByEmailContaining("test@studenti.unisa"));
        assertTimeout(duration, () -> userService.getAllByFullNameContaining("NIC", "PIC"));
        assertTimeout(duration, () -> userService.getById("ABC123DEF5"));

        assertTimeout(duration, () -> {
            User user = new User("IDPAZZO123", "rara@studenti.unisa.it", "EMPTY", "EMPTY");
            userService.register(user);
        });

        assertTimeout(duration, () -> userService.removeById("INESISTENTE"));

        assertTimeout(duration, () -> {
            User user = new User("IDPAZZO123", "test2@studenti.unisa.it", "NICOLASS", "PICARELLA");
            userService.updateById(user);
        });

        assertTimeout(duration, () -> userService.existsById("IDPAZZO123"));
        assertTimeout(duration, () -> userService.existsByEmail("test@studenti.unisa.it"));
        assertTimeout(duration, () -> userService.isEmailValid("test@studenti.unisa.it"));
        assertTimeout(duration, () -> userService.isIdValid("1234567890"));
    }

    @AfterAll
    public static void teardown() {
        database.close();
    }
}
