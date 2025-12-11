package it.unisa.diem.ingsoft.biblioteca;

import java.time.Duration;
import java.util.List;

import it.unisa.diem.ingsoft.biblioteca.exception.WrongIsbnException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBooksByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;

import static org.junit.jupiter.api.Assertions.*;

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
            Book book = new Book("1234567890000", "L'attacco dei giganti", "Hajime Isayama", 2009, 50,5,"Dark fantasy","Un bel manga");
            this.bookService.add(book);
        });

        assertThrows(DuplicateBookByIsbnException.class, () -> {
            Book duplicateBook = new Book("1234567890000", "1984", "George Orwell", 1948, 50,3,"Distopico","Un romanzo cupo");
            this.bookService.add(duplicateBook);
        });

        assertThrows(WrongIsbnException.class, () -> {
            Book duplicateBook = new Book("123456789", "1984", "George Orwell", 1948, 50,3,"Distopico","Un romanzo cupo");
            this.bookService.add(duplicateBook);
        });


    }

    @Test
    public void addAll() {
        assertDoesNotThrow(() -> {
            List<Book> books = List.of(
                    new Book("1234567890000", "One piece", "Oda", 1999, 50,3,"Avventura","Peak"),
                    new Book("1234567800000", "Chainsaw man", "Tatsuki Fujimoto", 2022, 50,4,"Fantasy","Topo di campagna o di città?")
            );
            this.bookService.addAll(books);
        });

        assertThrows(DuplicateBooksByIsbnException.class, () -> {
            List<Book> books = List.of(
                    new Book("1235567800000", "Jojo", "Hirohiko Araki", 1987, 50,9,"Fantasy","La parte 7 è la migliore"),
                    new Book("1234567800000", "Chainsaw man", "Tatsuki Fujimoto", 2022, 50,4,"Fantasy","Topo di campagna o di città?") //Duplicato

            );
            this.bookService.addAll(books);
        });
    }

    @Test
    public void get() {
        assertTrue(this.bookService.getByIsbn("NON-ESISTE").isEmpty());

        assertDoesNotThrow(() -> {
            this.bookService.add(new Book("1334567810000", "Blue lock S1", "Muneyuki Kaneshiro", 2018, 50,19,"Sportivo","Reo è il goat"));
            this.bookService.add(new Book("1334967820000", "Blue lock S2", "Muneyuki Kaneshiro", 2018, 50,19,"Sportivo","Chighiri è il goat"));
            this.bookService.add(new Book("1384567830000", "Blue lock S3", "Muneyuki Kaneshiro", 2018, 50,19,"Sportivo","Kurona è il goat"));
            this.bookService.add(new Book("2222222220000", "Fragole sulle pendici del Vesuvio", " Sabrin Cascoon", 2004, 21,20,"Biografia","Le fragole alle pendici del Vesuvio sono squisite"));
        });

        assertDoesNotThrow(() -> {
            this.bookService.getByIsbn("1334567810000").get();
        });

        Book retrieved = this.bookService.getByIsbn("1334567810000").get();
        assertEquals("Muneyuki Kaneshiro", retrieved.getAuthor());
        assertEquals("Blue lock S1", retrieved.getTitle());
        assertEquals(2018, retrieved.getReleaseYear());
        assertEquals(50, retrieved.getTotalCopies());
        assertEquals(19, retrieved.getRemainingCopies());
        assertEquals("Sportivo", retrieved.getGenre());
        assertEquals("Reo è il goat", retrieved.getDescription());

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
            Book book = new Book("6767676760000", "Dragon Ball", "Akira Toriyama", 1984, 50,40,"Fantasy","Kamehamea!!!");
            this.bookService.add(book);
        });

        assertFalse(this.bookService.existsByIsbn("INESISTENTE"));
        assertTrue(this.bookService.existsByIsbn("6767676760000"));
    }

    @Test
    public void remove() {
        assertFalse(this.bookService.removeByIsbn("INESISTENTE"));

        assertDoesNotThrow(() -> {
            Book book = new Book("6767676760000", "Dragon Ball", "Akira Toriyama", 1984, 50,40,"Fantasy","Kamehamea!!!");
            this.bookService.add(book);
        });
        assertTrue(this.bookService.removeByIsbn("6767676760000"));

    }

    @Test
    public void update() {
        assertThrows(UnknownBookByIsbnException.class, () -> {
            Book book = new Book("1234321670000", "Striano, Montoro e Atripalda", "Vincenzo D. Raimo", 2025, 4,4,"Politica","Origine di queste 3 città che hanno dato i natali a 3 scienziati.");
            this.bookService.updateByIsbn(book);
        });

        assertDoesNotThrow(() -> {
            Book book = new Book("1234321690000", "Striano, Montoro e Atripalda", "Vincenzo D. Raimo", 2025, 4,4,"Politica","Origine di queste 3 città che hanno dato i natali a 3 scienziati.");
            this.bookService.add(book);
        });

        assertDoesNotThrow(() -> {
            Book book = new Book("3519034830000", "Storia di un piatto vuoto", "Vincenzo D. Raimo", 2026, 1,0,"Avventura","R.I.P. Mattia L. Santoro");
            this.bookService.add(book);
        });

        assertDoesNotThrow(() -> {
            Book updatedBook = new Book("3519034830000", "An empty plate 's tale", "Vincenzo Dan. Raimo", 2026, 1,0,"Avventura","R.I.P. Mattia L. Santoro");

            this.bookService.updateByIsbn(updatedBook);
        });

        assertDoesNotThrow(() -> {
            Book book = this.bookService.getByIsbn("3519034830000").get();
            assertEquals("An empty plate 's tale", book.getTitle());
            assertEquals("Vincenzo Dan. Raimo", book.getAuthor());
            assertEquals(2026, book.getReleaseYear());
            assertEquals(0, book.getRemainingCopies());
            assertEquals("R.I.P. Mattia L. Santoro", book.getDescription());
        });
    }

    @Test
    public void countRemainingCopies() {
        assertDoesNotThrow(() -> {
            Book book = new Book("0409820250000", "Trip to Sofia", "Ancel P.", 2027, 14,3,"Fantascienza","Comprende racconto audio-visivo!");
            this.bookService.add(book);
        });

        assertEquals(3, this.bookService.countRemainingCopies("0409820250000"));
    }

    @Test
    public void speed(){
        Duration duration = Duration.ofMillis(100);

        assertDoesNotThrow(() -> {
            Book book = new Book("1234567890000", "L'attacco dei giganti", "Hajime Isayama", 2009, 50,5,"Dark fantasy","Un bel manga");
            this.bookService.add(book);
        });

        assertTimeout(duration, () -> {
            this.bookService.getAllByGenreContaining("Dark fantasy");
        });

        assertTimeout(duration, () -> {
            this.bookService.getByIsbn("1234567890000");
        });

        assertTimeout(duration, () -> {
            this.bookService.getAllByTitleContaining("L'attacco dei giganti");
        });

        assertTimeout(duration, () -> {
            this.bookService.getAllByAuthorContaining("Hajime Isayama");
        });

        assertTimeout(duration, () -> {
            Book book = new Book("1234567891234", "Prova 2", "Autore", 2009, 50,5,"Dark","Un bel libro");
            this.bookService.add(book);
        });

        assertTimeout(duration, () -> {
            this.bookService.removeByIsbn("INESISTENTE");
        });

        assertTimeout(duration, () -> {
            Book book = new Book("1234567891234", "Prova cambio velocità", "Autore", 2009, 50,5,"Dark","Un bel libro");
            this.bookService.updateByIsbn(book);
        });

        assertTimeout(duration, () -> {
            this.bookService.existsByIsbn("1234567891234");
        });

        assertTimeout(duration, () -> {
            List<Book> books = List.of(
                    new Book("1234567890000", "One piece", "Oda", 1999, 50,3,"Avventura","Peak"),
                    new Book("1234567800000", "Chainsaw man", "Tatsuki Fujimoto", 2022, 50,4,"Fantasy","Topo di campagna o di città?")
            );
            this.bookService.addAll(books);
        });


    }
}
