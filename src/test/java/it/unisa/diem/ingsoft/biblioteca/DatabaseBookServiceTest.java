package it.unisa.diem.ingsoft.biblioteca;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.DuplicateBooksByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.NegativeBookCopiesException;
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
    public void add_ValidBook() {
        assertDoesNotThrow(() -> {
            Book book = new Book("1234567890000", "L'attacco dei giganti", "Hajime Isayama", 2009, 50, 5, "Dark fantasy", "Un bel manga");
            this.bookService.add(book);
        });
    }

    @Test
    public void add_DuplicateIsbn() {
        assertDoesNotThrow(() -> {
            Book book = new Book("1234567890000", "L'attacco dei giganti", "Hajime Isayama", 2009, 50, 5, "Dark fantasy", "Un bel manga");
            this.bookService.add(book);
        });

        assertThrows(DuplicateBookByIsbnException.class, () -> {
            Book duplicateBook = new Book("1234567890000", "1984", "George Orwell", 1948, 50, 3, "Distopico", "Un romanzo cupo");
            this.bookService.add(duplicateBook);
        });
    }

    @Test
    public void add_InvalidIsbn() {
        assertThrows(InvalidIsbnException.class, () -> {
            Book invalidBook = new Book("123456789", "1984", "George Orwell", 1948, 50, 3, "Distopico", "Un romanzo cupo");
            this.bookService.add(invalidBook);
        });
    }

    @Test
    public void add_NegativeTotalCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            Book negativeCopiesBook = new Book("2134657890000", "1984", "George Orwell", 1948, -4, 3, "Distopico", "Un romanzo cupo");
            this.bookService.add(negativeCopiesBook);
        });
    }

    @Test
    public void add_NegativeRemainingCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            Book negativeRemainingBook = new Book("2134657890000", "1984", "George Orwell", 1948, 50, -3, "Distopico", "Un romanzo cupo");
            this.bookService.add(negativeRemainingBook);
        });
    }

    @Test
    public void addAll_ValidList() {
        assertDoesNotThrow(() -> {
            List<Book> books = List.of(
                    new Book("1234567890000", "One piece", "Oda", 1999, 50, 3, "Avventura", "Peak"),
                    new Book("1234567800000", "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Topo di campagna o di città?")
            );
            this.bookService.addAll(books);
        });
    }

    @Test
    public void addAll_DuplicateInList() {
        assertDoesNotThrow(() -> {
            this.bookService.add(new Book("1234567800000", "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Desc"));
        });

        assertThrows(DuplicateBooksByIsbnException.class, () -> {
            List<Book> books = List.of(
                    new Book("1235567800000", "Jojo", "Hirohiko Araki", 1987, 50, 9, "Fantasy", "Parte 7"),
                    new Book("1234567800000", "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Duplicato")
            );
            this.bookService.addAll(books);
        });
    }

    @Test
    public void getByIsbn_ExistingIsbn() throws Exception {
        this.bookService.add(new Book("1334567810000", "Blue lock S1", "Muneyuki Kaneshiro", 2018, 50, 19, "Sportivo", "Reo è il goat"));

        assertTrue(this.bookService.getByIsbn("1334567810000").isPresent());
        
        Book retrieved = this.bookService.getByIsbn("1334567810000").get();
        assertEquals("Muneyuki Kaneshiro", retrieved.getAuthor());
        assertEquals("Blue lock S1", retrieved.getTitle());
        assertEquals(2018, retrieved.getReleaseYear());
        assertEquals(50, retrieved.getTotalCopies());
        assertEquals(19, retrieved.getRemainingCopies());
        assertEquals("Sportivo", retrieved.getGenre());
        assertEquals("Reo è il goat", retrieved.getDescription());
    }

    @Test
    public void getByIsbn_NonExistingIsbn() {
        assertTrue(this.bookService.getByIsbn("NON-ESISTE").isEmpty());
    }

    @Test
    public void getAll_Populated() throws Exception {
        this.bookService.add(new Book("1234567890000", "Libro Test", "Autore", 2020, 10, 5, "Genere", "Desc"));
        assertFalse(this.bookService.getAll().isEmpty());
    }

    @Test
    public void getAllByAuthor_MatchingString() throws Exception {
        this.bookService.add(new Book("1334567810000", "Blue lock S1", "Muneyuki Kaneshiro", 2018, 50, 19, "Sportivo", "Desc"));
        this.bookService.add(new Book("1334967820000", "Blue lock S2", "Muneyuki Kaneshiro", 2019, 50, 19, "Sportivo", "Desc"));
        this.bookService.add(new Book("1384567830000", "Blue lock S3", "Muneyuki Kaneshiro", 2020, 50, 19, "Sportivo", "Desc"));

        assertEquals(3, this.bookService.getAllByAuthorContaining("Kane").size());
    }

    @Test
    public void getAllByReleaseYear_MatchingYear() throws Exception {
        this.bookService.add(new Book("1334567810000", "Book 2018", "Author", 2018, 50, 19, "Genre", "Desc"));
        
        assertFalse(this.bookService.getAllByReleaseYear(2018).isEmpty());
        assertTrue(this.bookService.getAllByReleaseYear(2000).isEmpty());
    }
    
    @Test
    public void getAllByGenre_MatchingString() throws Exception {
        this.bookService.add(new Book("1234567890000", "L'attacco dei giganti", "Isayama", 2009, 50, 5, "Dark fantasy", "Desc"));
        
        assertFalse(this.bookService.getAllByGenreContaining("Dark fantasy").isEmpty());
    }
    
    @Test
    public void getAllByTitle_MatchingString() throws Exception {
         this.bookService.add(new Book("1234567890000", "L'attacco dei giganti", "Isayama", 2009, 50, 5, "Dark fantasy", "Desc"));
         
         assertFalse(this.bookService.getAllByTitleContaining("L'attacco dei giganti").isEmpty());
    }

    @Test
    public void existsByIsbn_ExistingIsbn() throws Exception {
        Book book = new Book("6767676760000", "Dragon Ball", "Akira Toriyama", 1984, 50, 40, "Fantasy", "Kamehamea!!!");
        this.bookService.add(book);

        assertTrue(this.bookService.existsByIsbn("6767676760000"));
    }

    @Test
    public void existsByIsbn_NonExistingIsbn() {
        assertFalse(this.bookService.existsByIsbn("INESISTENTE"));
    }

    @Test
    public void removeByIsbn_ExistingIsbn() throws Exception {
        Book book = new Book("6767676760000", "Dragon Ball", "Akira Toriyama", 1984, 50, 40, "Fantasy", "Kamehamea!!!");
        this.bookService.add(book);

        assertTrue(this.bookService.removeByIsbn("6767676760000"));
    }

    @Test
    public void removeByIsbn_NonExistingIsbn() {
        assertFalse(this.bookService.removeByIsbn("INESISTENTE"));
    }

    @Test
    public void updateByIsbn_ExistingBook() throws Exception {
        Book book = new Book("3519034830000", "Storia di un piatto vuoto", "Vincenzo D. Raimo", 2026, 1, 0, "Avventura", "R.I.P. Mattia L. Santoro");
        this.bookService.add(book);

        Book updatedBook = new Book("3519034830000", "An empty plate 's tale", "Vincenzo Dan. Raimo", 2026, 1, 0, "Avventura", "R.I.P. Mattia L. Santoro");
        assertDoesNotThrow(() -> this.bookService.updateByIsbn(updatedBook));

        Book retrieved = this.bookService.getByIsbn("3519034830000").get();
        assertEquals("An empty plate 's tale", retrieved.getTitle());
        assertEquals("Vincenzo Dan. Raimo", retrieved.getAuthor());
    }

    @Test
    public void updateByIsbn_NonExistingBook() {
        Book book = new Book("1234321670000", "Titolo", "Autore", 2025, 4, 4, "Politica", "Desc");
        
        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.bookService.updateByIsbn(book);
        });
    }

    @Test
    public void updateByIsbn_NegativeCopies() throws Exception {
        Book book = new Book("1234321690000", "Titolo", "Autore", 2025, 4, 4, "Politica", "Desc");
        this.bookService.add(book);

        Book invalidUpdate = new Book("1234321690000", "Titolo", "Autore", 2025, -4, 3, "Politica", "Desc");
        assertThrows(NegativeBookCopiesException.class, () -> {
            this.bookService.updateByIsbn(invalidUpdate);
        });
    }

    @Test
    public void countRemainingCopies_ExistingBook() throws Exception {
        Book book = new Book("0409820250000", "Trip to Sofia", "Ancel P.", 2027, 14, 3, "Fantascienza", "Desc");
        this.bookService.add(book);

        assertEquals(3, this.bookService.countRemainingCopies("0409820250000"));
    }

    @Test
    public void performance_Methods() {
        Duration duration = Duration.ofMillis(100);

        assertDoesNotThrow(() -> {
            Book book = new Book("1234567890000", "L'attacco dei giganti", "Hajime Isayama", 2009, 50, 5, "Dark fantasy", "Un bel manga");
            this.bookService.add(book);
        });

        assertTimeout(duration, () -> this.bookService.getAllByGenreContaining("Dark fantasy"));
        assertTimeout(duration, () -> this.bookService.getByIsbn("1234567890000"));
        assertTimeout(duration, () -> this.bookService.getAllByTitleContaining("L'attacco dei giganti"));
        assertTimeout(duration, () -> this.bookService.getAllByAuthorContaining("Hajime Isayama"));

        assertTimeout(duration, () -> {
            Book book = new Book("1234567891234", "Prova 2", "Autore", 2009, 50, 5, "Dark", "Un bel libro");
            this.bookService.add(book);
        });

        assertTimeout(duration, () -> this.bookService.removeByIsbn("INESISTENTE"));

        assertTimeout(duration, () -> {
            Book book = new Book("1234567891234", "Prova cambio velocità", "Autore", 2009, 50, 5, "Dark", "Un bel libro");
            this.bookService.updateByIsbn(book);
        });

        assertTimeout(duration, () -> this.bookService.existsByIsbn("1234567891234"));

        assertTimeout(duration, () -> {
            List<Book> books = List.of(
                    new Book("1234567890100", "One piece", "Oda", 1999, 50, 3, "Avventura", "Peak"),
                    new Book("1234567800200", "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Topo")
            );
            this.bookService.addAll(books);
        });
    }
}
