package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(this.loanService.getByUserIDAndBookIsbn("USERID321", "ISBNTEST123")
                .isEmpty());

        assertTrue(this.loanService.getByUserId("USERID321")
                .isEmpty());

        assertTrue(this.loanService.getByBookIsbn("ISBNTEST123")
                .isEmpty());

        assertTrue(this.loanService.getAll()
                .isEmpty());

        assertFalse(this.loanService.has("USERID321", "ISBNTEST123"));

        assertEquals(this.loanService.countById("USERID321"), 0);

        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);
        assertDoesNotThrow(() -> {
            this.loanService.register("USERID321", "ISBNTEST123", start, deadline);
        });

        assertDoesNotThrow(() -> {
            this.loanService.register("USERID321", "TEST435", start, deadline);
        });

        assertDoesNotThrow(() -> {
            this.loanService.register("USER22", "ISBNTEST123", start, deadline);
        });
        
        assertThrows(LoanAlreadyRegisteredException.class, () -> {
            this.loanService.register("USERID321", "ISBNTEST123", start, deadline);
        });

        assertEquals(this.loanService.getByUserIDAndBookIsbn("USERID321", "ISBNTEST123"), 1);
        assertEquals(this.loanService.getByUserId("USERID321").size(), 2);
        assertEquals(this.loanService.getByBookIsbn("ISBNTEST123").size(), 1);
        assertEquals(this.loanService.getAll().size(), 3);
        assertTrue(this.loanService.has("USERID321", "ISBNTEST123"));
        assertEquals(this.loanService.countById("USERID321"), 2);

    }

    @Test
    public void check() {
        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete("NON_EXISTENT", "RANDOM_ISB_NON_EXISTENT", LocalDate.now());
        });

        LocalDate start = LocalDate.now();
        LocalDate deadline = start.plusDays(30);
        assertDoesNotThrow(() -> {
            this.loanService.register("NICOLA123", "ANIMAL_FARM", start, deadline);
        });

        assertDoesNotThrow(() -> {
            this.loanService.complete("NICOLA123", "ANIMAL_FARM", LocalDate.now());
        });

        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete("NICOLA123", "RANDOM_ISB_NON_EXISTENT", LocalDate.now());
        });

        assertThrows(UnknownLoanException.class, () -> {
            this.loanService.complete("NON_EXISTENT", "ANIMAL_FARM", LocalDate.now());
        });
    }
}
