package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBooksByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;

public class DatabaseBookServiceTest {
    private BookService bookService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.bookService = new DatabaseBookService(database);
    }

    @Test
    public void add() {
        assertDoesNotThrow(() -> {
            // Assumo un costruttore: ISBN, Titolo, Autore, Genere, Anno
            Book book = new Book("123456789", "L'attacco dei giganti", "Hajime Isayama", 2009, 50,5,"Dark fantasy","Un bel manga");
            this.bookService.add(book);
        });

        // Test duplicato ISBN
        assertThrows(DuplicateBookByIsbnException.class, () -> {
            Book duplicateBook = new Book("123456789", "1984", "George Orwell", 1948, 50,3,"Distopico","Un romanzo cupo");
            this.bookService.add(duplicateBook);
        });
    }

    @Test
    public void addAll() {
        assertDoesNotThrow(() -> {
            List<Book> books = List.of(
                    new Book("123456789", "One piece", "Oda", 1999, 50,3,"Avventura","Peak"),
                    new Book("123456780", "Chainsaw man", "Tatsuki Fujimoto", 2022, 50,4,"Fantasy","Topo di campagna o di città?")
            );
            this.bookService.addAll(books);
        });

        assertThrows(DuplicateBooksByIsbnException.class, () -> {
            List<Book> books = List.of(
                    new Book("123556780", "Jojo", "Hirohiko Araki", 1987, 50,9,"Fantasy","La parte 7 è la migliore"),
                    new Book("123456780", "Chainsaw man", "Tatsuki Fujimoto", 2022, 50,4,"Fantasy","Topo di campagna o di città?") //Duplicato

            );
            this.bookService.addAll(books);
        });
    }

    @Test
    public void get() {
        assertTrue(this.bookService.getByIsbn("NON-ESISTE").isEmpty());

        assertDoesNotThrow(() -> {
            this.bookService.add( new Book("133456780", "Blue lock", "Muneyuki Kaneshiro", 2018, 50,19,"Sportivo","Reo è il goat"));
            this.bookService.add(new Book("133496780", "Blue lock", "Muneyuki Kaneshiro", 2018, 50,19,"Sportivo","Chighiri è il goat"));
            this.bookService.add(new Book("138456780", "Blue lock", "Muneyuki Kaneshiro", 2018, 50,19,"Sportivo","Kurona è il goat"));
        });

        assertDoesNotThrow(() -> {
            this.bookService.getByIsbn("133496780").get();
        });

        Book retrieved = this.bookService.getByIsbn("133496780").get();
        assertEquals("Blue lock", retrieved.getAuthor());

        assertDoesNotThrow(() -> {
            assertFalse(this.bookService.getAll().isEmpty());
        });

        assertFalse(this.bookService.getAllByAuthorContaining("Kane").isEmpty()); // Dovrebbe trovarne 3
        assertEquals(3, this.bookService.getAllByAuthorContaining("Kane").size());

        assertFalse(this.bookService.getAllByReleaseYear(2018).isEmpty());
        assertTrue(this.bookService.getAllByReleaseYear(2000).isEmpty());
    }

    @Test
    public void exists() {
        assertDoesNotThrow(() -> {
            Book book = new Book("676767676", "Dragon Ball", "Akira Toriyama", 1984, 50,40,"Fantasy","Kamehamea!!!");
            this.bookService.add(book);
        });

        assertFalse(this.bookService.existsByIsbn("INESISTENTE"));
        assertTrue(this.bookService.existsByIsbn("676767676"));
    }

    @Test
    public void remove() {
        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.bookService.removeByIsbn("INESISTENTE");
        });

        assertDoesNotThrow(() -> {
            Book book = new Book("191919191", "La storia della Salernitana", "Vincenzo D. Raimo", 2025, 19,19,"Sportivo","Salerno");
            this.bookService.add(book);
        });

        assertDoesNotThrow(() -> {
            boolean result = this.bookService.removeByIsbn("191919191");
            assertTrue(result);
        });

        assertFalse(this.bookService.existsByIsbn("191919191"));
    }

    @Test
    public void update() {
        assertThrows(UnknownBookByIsbnException.class, () -> {
            Book book = new Book("123432167", "Striano, Montoro e Atripalda", "Vincenzo D. Raimo", 2025, 4,4,"Politica","Origine di queste 3 città che hanno dato i natali a 3 scienziati.");
            this.bookService.updateByIsbn(book);
        });

        assertDoesNotThrow(() -> {
            Book book = new Book("123432169", "Striano, Montoro e Atripalda", "Vincenzo D. Raimo", 2025, 4,4,"Politica","Origine di queste 3 città che hanno dato i natali a 3 scienziati.");
            this.bookService.add(book);
        });

        assertDoesNotThrow(() -> {
            Book updatedBook = new Book("351903483", "An empty plate 's tale", "Vincenzo Dan. Raimo", 2026, 1,0,"Avventura","R.I.P. Mattia L. Santoro");

            this.bookService.updateByIsbn(updatedBook);
        });

        assertDoesNotThrow(() -> {
            Book user = this.bookService.getByIsbn("351903483").get();
            assertEquals("An empty plate 's tale", user.getTitle());
            assertEquals("Vincenzo Dan. Raimo", user.getAuthor());
            assertEquals(2026, user.getReleaseYear());
            assertEquals(5, user.getRemainingCopies());
            assertEquals("2020-2025", user.getDescription());
        });
    }

    @Test
    public void countRemainingCopies() {
        assertDoesNotThrow(() -> {
            Book book = new Book("040982025", "Trip to Sofia", "Ancel P.", 2027, 14,3,"Fantascienza","Comprende racconto audio-visivo!");
            this.bookService.add(book);

            this.bookService.updateByIsbn(book);
        });

        assertEquals(3, this.bookService.countRemainingCopies("040982025"));
    }
}