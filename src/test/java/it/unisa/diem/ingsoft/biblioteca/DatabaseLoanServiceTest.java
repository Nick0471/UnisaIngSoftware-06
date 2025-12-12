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

import it.unisa.diem.ingsoft.biblioteca.exception.LoanAlreadyRegisteredException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownLoanException;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;

public class DatabaseLoanServiceTest {
    private LoanService loanService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.loanService = new DatabaseLoanService(database);
    }

    @Test
    public void get() {
        assertTrue(this.loanService.getByUserIDAndBookIsbn("USERID3214", "ISBNTEST12345")
                .isEmpty());

        assertTrue(this.loanService.getByUserId("USERID3214")
                .isEmpty());

        assertTrue(this.loanService.getByBookIsbn("ISBNTEST12345")
                .isEmpty());

        assertTrue(this.loanService.getAll()
                .isEmpty());

        assertFalse(this.loanService.isActive("USERID3214", "ISBNTEST12345"));

        assertEquals(this.loanService.countById("USERID3214"), 0);

        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);
        assertDoesNotThrow(() -> {
            this.loanService.register("USERID3214", "ISBNTEST12345", start, deadline);
        });

        assertDoesNotThrow(() -> {
            this.loanService.register("USERID3214", "TEST435", start, deadline);
        });

        assertDoesNotThrow(() -> {
            this.loanService.register("USER222222", "ISBNTEST12345", start, deadline);
        });
        
        assertThrows(LoanAlreadyRegisteredException.class, () -> {
            this.loanService.register("USERID3214", "ISBNTEST12345", start, deadline);
        });

        assertTrue(this.loanService.getByUserIDAndBookIsbn("USERID3214", "ISBNTEST12345").isPresent());
        assertEquals(this.loanService.getByUserId("USERID3214").size(), 2);
        assertEquals(this.loanService.getByBookIsbn("ISBNTEST12345").size(), 2);
        assertEquals(this.loanService.getAll().size(), 3);
        assertEquals(this.loanService.getAllActive().size(), 3);
        assertEquals(this.loanService.getAllActiveByUserID("USERID3214").size(), 2);
        assertTrue(this.loanService.isActive("USERID3214", "ISBNTEST12345"));
        assertEquals(this.loanService.countById("USERID3214"), 2);
    }

    @Test
    public void check() {
        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete("NON_EXISTENT", "RANDOM_ISB_NON_EXISTENT", LocalDate.now());
        });

        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);
        assertDoesNotThrow(() -> {
            this.loanService.register("NICOLA1234", "ANIMAL_FARM", start, deadline);
        });

        assertEquals(this.loanService.getAllActive().size(), 1);

        assertDoesNotThrow(() -> {
            this.loanService.complete("NICOLA1234", "ANIMAL_FARM", LocalDate.now());
        });

        assertFalse(this.loanService.isActive("NICOLA1234", "ANIMAL_FARM"));

        assertEquals(this.loanService.getAllActive().size(), 0);
        assertEquals(this.loanService.getAllActiveByUserID("NICOLA1234").size(), 0);

        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete("NICOLA1234", "RANDOM_ISB_NON_EXISTENT", LocalDate.now());
        });

        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete("NON_EXISTENT", "ANIMAL_FARM", LocalDate.now());
        });
    }

    @Test
    public void speed() {
        LocalDate now = LocalDate.now();
        Duration duration = Duration.ofMillis(100);

        assertDoesNotThrow(() -> {
            this.loanService.register("ABC1314156", "9788808123456", now, now.plusDays(30));
        });

        assertTimeout(duration, () -> {
            this.loanService.getAll();
        });

        assertTimeout(duration, () -> {
            this.loanService.getAllActiveByUserID("ABC1314156");
        });

        assertTimeout(duration, () -> {
            this.loanService.getByUserIDAndBookIsbn("ABC1314156", "978-88-08-12345-6");
        });

        assertTimeout(duration, () -> {
            this.loanService.getByUserId("ABC1314156");
        });

        assertTimeout(duration, () -> {
            this.loanService.getByBookIsbn("9788808123456");
        });

        assertTimeout(duration, () -> {
            this.loanService.isActive("ABC1314156", "978-88-08-12345-6");
        });

        assertTimeout(duration, () -> {
            this.loanService.countById("ABC1314156");
        });

        assertTimeout(duration, () -> {
            this.loanService.register("TEMPUSER11", "TEMP_ISBN", now, now.plusDays(15));
        });

        assertTimeout(duration, () -> {
            this.loanService.complete("TEMPUSER11", "TEMP_ISBN", now);
        });

        assertTimeout(duration, () -> {
            this.loanService.isActive("NON_EXISTENT", "NON_EXISTENT");
        });
    }
}
