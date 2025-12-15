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
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.exception.InvalidIsbnException;
import it.unisa.diem.ingsoft.biblioteca.exception.MissingBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.exception.NegativeBookCopiesException;
import it.unisa.diem.ingsoft.biblioteca.exception.UnknownBookByIsbnException;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;

public class DatabaseBookServiceTest {
    private final String validIsbn = "1234567890000";
    private final String secondValidIsbn = "1234567800000";
    private final String searchIsn = "1334567810000";
    private final String removeIsbn = "6767676760000";
    private final String updateIsbn = "3519034830000";
    private final String copiesIsbn = "0409820250000";
    private final String invalidIsbn = "123456789";
    private final String nonExistentIsbn = "NON-ESISTE";

    private BookService bookService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.bookService = new DatabaseBookService(database);
    }

    private void addBook(String isbn, int copies, int remainingCopies) {
        assertDoesNotThrow(() -> {
            Book book = new Book(isbn, "Titolo Test", "Autore Test", 2020, copies, remainingCopies, "Genre", "Desc");
            this.bookService.add(book);
        });
    }

    @Test
    public void add_ValidBook() {
        this.addBook(this.validIsbn, 50, 5);
    }

    @Test
    public void add_DuplicateIsbn() {
        this.addBook(this.validIsbn, 50, 5);

        assertThrows(DuplicateBookByIsbnException.class, () -> {
            Book duplicateBook = new Book(this.validIsbn, "1984", "George Orwell", 1948, 50, 3, "Distopico", "Un romanzo cupo");
            this.bookService.add(duplicateBook);
        });
    }

    @Test
    public void add_InvalidIsbn() {
        assertThrows(InvalidIsbnException.class, () -> {
            Book invalidBook = new Book(this.invalidIsbn, "1984", "George Orwell", 1948, 50, 3, "Distopico", "Un romanzo cupo");
            this.bookService.add(invalidBook);
        });
    }

    @Test
    public void add_NegativeTotalCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            Book negativeCopiesBook = new Book(this.validIsbn, "1984", "George Orwell", 1948, -4, 3, "Distopico", "Un romanzo cupo");
            this.bookService.add(negativeCopiesBook);
        });
    }

    @Test
    public void add_NegativeRemainingCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            Book negativeRemainingBook = new Book(this.validIsbn, "1984", "George Orwell", 1948, 50, -3, "Distopico", "Un romanzo cupo");
            this.bookService.add(negativeRemainingBook);
        });
    }

    @Test
    public void addAll_ValidList() {
        assertDoesNotThrow(() -> {
            List<Book> books = List.of(
                    new Book(this.validIsbn, "One piece", "Oda", 1999, 50, 3, "Adv", "Peak"),
                    new Book(this.secondValidIsbn, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Topo di campagna o di città?")
            );

            this.bookService.addAll(books);
        });
    }

    @Test
    public void addAll_NegativeCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            List<Book> books = List.of(
                new Book(this.secondValidIsbn, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Desc"),
                new Book(this.validIsbn, "One piece", "Oda", 1999, 50, -10, "Adv", "Peak")
            );
            this.bookService.addAll(books);
        });

        assertThrows(NegativeBookCopiesException.class, () -> {
            List<Book> books = List.of(
                new Book(this.secondValidIsbn, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Desc"),
                new Book(this.validIsbn, "One piece", "Oda", 1999, -12, 1, "Adv", "Peak")
            );
            this.bookService.addAll(books);
        });
    }

    @Test
    public void addAll_InvalidIsbn() {
        assertThrows(InvalidIsbnException.class, () -> {
            List<Book> books = List.of(
                new Book(this.validIsbn, "One piece", "Oda", 1999, 10, 1, "Adv", "Peak"),
                new Book(this.invalidIsbn, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Desc")
            );
            this.bookService.addAll(books);
        });
    }

    @Test
    public void addAll_DuplicateInList() {
        this.addBook(this.secondValidIsbn, 50, 4);

        assertThrows(DuplicateBooksByIsbnException.class, () -> {
            List<Book> books = List.of(
                    new Book("1235567800000", "Jojo", "Hirohiko Araki", 1987, 50, 9, "Fantasy", "Parte 7"),
                    new Book(this.secondValidIsbn, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Duplicato")
                    );
            this.bookService.addAll(books);
        });
    }

    @Test
    public void getByIsbn_ExistingIsbn() {
        this.addBook(this.searchIsn, 50, 19);

        assertTrue(this.bookService.getByIsbn(this.searchIsn).isPresent());

        Book retrieved = this.bookService.getByIsbn(this.searchIsn).get();
        
        assertEquals("Autore Test", retrieved.getAuthor());
        assertEquals("Titolo Test", retrieved.getTitle());
        assertEquals(2020, retrieved.getReleaseYear());
        assertEquals(50, retrieved.getTotalCopies());
        assertEquals(19, retrieved.getRemainingCopies());
        assertEquals("Genre", retrieved.getGenre());
        assertEquals("Desc", retrieved.getDescription());
    }

    @Test
    public void getByIsbn_NonExistingIsbn() {
        assertTrue(this.bookService.getByIsbn(this.nonExistentIsbn).isEmpty());
    }

    @Test
    public void getAll_Populated() {
        this.addBook(this.validIsbn, 10, 5);
        assertFalse(this.bookService.getAll().isEmpty());
    }

    @Test
    public void getAllByAuthor_MatchingString() {
        this.addBook(this.searchIsn, 50, 19);
        this.addBook("1334967820000", 50, 19);
        this.addBook("1384567830000", 50, 19);

        assertEquals(3, this.bookService.getAllByAuthorContaining("Autore").size());
    }

    @Test
    public void getAllByReleaseYear_MatchingYear() {
        this.addBook(this.searchIsn, 50, 19);

        assertFalse(this.bookService.getAllByReleaseYear(2020).isEmpty());
        assertTrue(this.bookService.getAllByReleaseYear(2000).isEmpty());
    }

    @Test
    public void getAllByGenre_MatchingString() {
        this.addBook(this.validIsbn, 50, 5);

        assertFalse(this.bookService.getAllByGenreContaining("Genre").isEmpty());
    }

    @Test
    public void getAllByIsbn_MatchingString() {
        this.addBook(this.validIsbn, 50, 5);
        assertFalse(this.bookService.getAllByIsbnContaining("789").isEmpty());
    }

    @Test
    public void getAllByTitle_MatchingString() {
        this.addBook(this.validIsbn, 50, 5);

        assertFalse(this.bookService.getAllByTitleContaining("Titolo").isEmpty());
    }

    @Test
    public void existsByIsbn_ExistingIsbn() {
        this.addBook(this.removeIsbn, 50, 40);
        assertTrue(this.bookService.existsByIsbn(this.removeIsbn));
    }

    @Test
    public void existsByIsbn_NonExistingIsbn() {
        assertFalse(this.bookService.existsByIsbn(this.nonExistentIsbn));
    }

    @Test
    public void removeByIsbn_ExistingIsbn() {
        this.addBook(this.removeIsbn, 50, 50);

        assertDoesNotThrow(() -> {
            assertTrue(this.bookService.removeByIsbn(this.removeIsbn));
        });
    }

    @Test
    public void removeByIsbn_MissingCopies() {
        this.addBook(this.removeIsbn, 50, 49);

        assertThrows(MissingBookCopiesException.class, () -> {
            this.bookService.removeByIsbn(this.removeIsbn);
        });
    }

    @Test
    public void removeByIsbn_NonExistingIsbn() {
        assertDoesNotThrow(() -> {
            assertFalse(this.bookService.removeByIsbn(this.nonExistentIsbn));
        });
    }

    @Test
    public void updateByIsbn_ExistingBook() {
        this.addBook(this.updateIsbn, 1, 0);

        Book updatedBook = new Book(this.updateIsbn, "An empty plate 's tale", "Vincenzo Dan. Raimo", 2026, 1, 0, "Adv", "R.I.P. Mattia L. Santoro");
        assertDoesNotThrow(() -> this.bookService.updateByIsbn(updatedBook));

        Book retrieved = this.bookService.getByIsbn(this.updateIsbn).get();
        assertEquals("An empty plate 's tale", retrieved.getTitle());
        assertEquals("Vincenzo Dan. Raimo", retrieved.getAuthor());
    }

    @Test
    public void updateByIsbn_NonExistingBook() {
        Book book = new Book(this.validIsbn, "Titolo", "Autore", 2025, 4, 4, "Politica", "Desc");

        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.bookService.updateByIsbn(book);
        });
    }

    @Test
    public void updateByIsbn_NegativeCopies() {
        this.addBook(this.validIsbn, 4, 4);

        Book invalidUpdate = new Book(this.validIsbn, "Titolo", "Autore", 2025, -4, 3, "Politica", "Desc");
        assertThrows(NegativeBookCopiesException.class, () -> {
            this.bookService.updateByIsbn(invalidUpdate);
        });
    }

    @Test
    public void countRemainingCopies_ExistingBook() {
        this.addBook(this.copiesIsbn, 14, 3);
        assertEquals(3, this.bookService.countRemainingCopies(this.copiesIsbn));
    }

    @Test
    public void updateRemainingCopies_Success() {
        String isbn = this.copiesIsbn;
        this.addBook(isbn, 14, 3);

        assertDoesNotThrow(() -> {
            this.bookService.updateRemainingCopies(isbn, 2);
            assertEquals(this.bookService.countRemainingCopies(isbn), 5);

            this.bookService.updateRemainingCopies(isbn, -2);
            assertEquals(this.bookService.countRemainingCopies(isbn), 3);
        });

        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.bookService.updateRemainingCopies(this.nonExistentIsbn, 1);
        });
    }

    @Test
    public void updateRemainingCopies_NegativeCopies() {
        String isbn = this.copiesIsbn;
        this.addBook(isbn, 14, 3);

        assertThrows(NegativeBookCopiesException.class, () -> {
            this.bookService.updateRemainingCopies(isbn, -10);
        });

        assertThrows(InvalidBookCopiesException.class, () -> {
            this.bookService.updateRemainingCopies(isbn, 100);
        });

        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.bookService.updateRemainingCopies(this.nonExistentIsbn, 1);
        });
    }
}
