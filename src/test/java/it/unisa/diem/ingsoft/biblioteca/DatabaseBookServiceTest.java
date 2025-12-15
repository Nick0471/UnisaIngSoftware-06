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

    private final String VALID_ISBN = "1234567890000";
    private final String SECOND_VALID_ISBN = "1234567800000";
    private final String INVALID_ISBN = "123456789";
    private final String NON_EXISTENT_ISBN = "0000000000000";

    private final Book VALID_BOOK = new Book(
        this.VALID_ISBN, "AOT", "Hajime Isayama", 2009, 50, 5, "Dark fantasy", "Un bel manga"
    );

    private final Book SECOND_VALID_BOOK = new Book(
        this.SECOND_VALID_ISBN, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 50, "Urban Fantasy", "Pochita"
    );

    private final Book DUPLICATE_ISBN_BOOK = new Book(
        this.VALID_ISBN, "Duplicato", "Autore X", 2025, 10, 10, "Genere", "Desc"
    );

    private final Book INVALID_ISBN_BOOK = new Book(
        this.INVALID_ISBN, "Invalid", "Author", 2000, 10, 10, "Genere", "Desc"
    );

    private final Book NEGATIVE_TOTAL_COPIES_BOOK = new Book(
        this.VALID_ISBN, "AOT", "Hajime Isayama", 2009, -5, 5, "Dark fantasy", "Desc"
    );

    private final Book NEGATIVE_REMAINING_COPIES_BOOK = new Book(
        this.VALID_ISBN, "AOT", "Hajime Isayama", 2009, 50, -5, "Dark fantasy", "Desc"
    );

    private final Book UPDATED_BOOK = new Book(
        this.VALID_ISBN, "AOT: The Final Season", "MAPPA", 2023, 50, 5, "Dark fantasy", "Updated Desc"
    );

    private BookService bookService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.bookService = new DatabaseBookService(database);
    }

    @Test
    public void add_ValidBook() {
        this.addBooks(this.VALID_BOOK);
    }

    @Test
    public void add_DuplicateIsbn() {
        this.addBooks(this.VALID_BOOK);

        assertThrows(DuplicateBookByIsbnException.class, () -> {
            this.bookService.add(this.DUPLICATE_ISBN_BOOK);
        });
    }

    @Test
    public void add_InvalidIsbn() {
        assertThrows(InvalidIsbnException.class, () -> {
            this.bookService.add(this.INVALID_ISBN_BOOK);
        });
    }

    @Test
    public void add_NegativeTotalCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            this.bookService.add(this.NEGATIVE_TOTAL_COPIES_BOOK);
        });
    }

    @Test
    public void add_NegativeRemainingCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            this.bookService.add(this.NEGATIVE_REMAINING_COPIES_BOOK);
        });
    }

    @Test
    public void addAll_ValidList() {
        assertDoesNotThrow(() -> {
            List<Book> books = List.of(this.VALID_BOOK, this.SECOND_VALID_BOOK);
            this.bookService.addAll(books);
        });
    }

    @Test
    public void addAll_NegativeCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            List<Book> books = List.of(this.SECOND_VALID_BOOK, this.NEGATIVE_REMAINING_COPIES_BOOK);
            this.bookService.addAll(books);
        });

        assertThrows(NegativeBookCopiesException.class, () -> {
            List<Book> books = List.of(this.SECOND_VALID_BOOK, this.NEGATIVE_TOTAL_COPIES_BOOK);
            this.bookService.addAll(books);
        });
    }

    @Test
    public void addAll_InvalidIsbn() {
        assertThrows(InvalidIsbnException.class, () -> {
            List<Book> books = List.of(this.VALID_BOOK, this.INVALID_ISBN_BOOK);
            this.bookService.addAll(books);
        });
    }

    @Test
    public void addAll_DuplicateInList() {
        this.addBooks(this.VALID_BOOK);

        assertThrows(DuplicateBooksByIsbnException.class, () -> {
            List<Book> books = List.of(this.SECOND_VALID_BOOK, this.DUPLICATE_ISBN_BOOK);
            this.bookService.addAll(books);
        });
    }

    @Test
    public void getByIsbn_ExistingIsbn() {
        this.addBooks(this.VALID_BOOK);

        assertTrue(this.bookService.getByIsbn(this.VALID_ISBN).isPresent());

        Book retrieved = this.bookService.getByIsbn(this.VALID_ISBN).get();
        assertEquals(this.VALID_BOOK.getAuthor(), retrieved.getAuthor());
        assertEquals(this.VALID_BOOK.getTitle(), retrieved.getTitle());
        assertEquals(this.VALID_BOOK.getReleaseYear(), retrieved.getReleaseYear());
        assertEquals(this.VALID_BOOK.getTotalCopies(), retrieved.getTotalCopies());
        assertEquals(this.VALID_BOOK.getRemainingCopies(), retrieved.getRemainingCopies());
        assertEquals(this.VALID_BOOK.getGenre(), retrieved.getGenre());
        assertEquals(this.VALID_BOOK.getDescription(), retrieved.getDescription());
    }

    @Test
    public void getByIsbn_NonExistingIsbn() {
        assertTrue(this.bookService.getByIsbn(this.NON_EXISTENT_ISBN).isEmpty());
    }

    @Test
    public void getAll_Populated() {
        this.addBooks(this.VALID_BOOK);
        assertFalse(this.bookService.getAll().isEmpty());
    }

    @Test
    public void getAllByAuthor_MatchingString() {
        this.addBooks(this.VALID_BOOK, this.SECOND_VALID_BOOK);

        assertEquals(1, this.bookService.getAllByAuthorContaining("Isayama").size());
        assertEquals(2, this.bookService.getAllByAuthorContaining("a").size());
    }

    @Test
    public void getAllByReleaseYear_MatchingYear() {
        this.addBooks(this.VALID_BOOK, this.SECOND_VALID_BOOK);

        assertFalse(this.bookService.getAllByReleaseYear(2009).isEmpty());
        assertTrue(this.bookService.getAllByReleaseYear(1900).isEmpty());
    }

    @Test
    public void getAllByGenre_MatchingString() {
        this.addBooks(this.VALID_BOOK, this.SECOND_VALID_BOOK);

        assertFalse(this.bookService.getAllByGenreContaining("Dark").isEmpty());
    }

    @Test
    public void getAllByIsbn_MatchingString() {
        this.addBooks(this.VALID_BOOK);

        assertFalse(this.bookService.getAllByIsbnContaining("789").isEmpty());
    }

    @Test
    public void getAllByTitle_MatchingString() {
        this.addBooks(this.VALID_BOOK);

        assertFalse(this.bookService.getAllByTitleContaining("AOT").isEmpty());
    }

    @Test
    public void existsByIsbn_ExistingIsbn() {
        this.addBooks(this.VALID_BOOK);
        assertTrue(this.bookService.existsByIsbn(this.VALID_ISBN));
    }

    @Test
    public void existsByIsbn_NonExistingIsbn() {
        assertFalse(this.bookService.existsByIsbn(this.NON_EXISTENT_ISBN));
    }

    @Test
    public void removeByIsbn_ExistingIsbn() {
        this.addBooks(this.SECOND_VALID_BOOK);

        assertDoesNotThrow(() -> {
            assertTrue(this.bookService.removeByIsbn(this.SECOND_VALID_ISBN));
        });
    }

    @Test
    public void removeByIsbn_MissingCopies() {
        this.addBooks(this.VALID_BOOK);

        assertThrows(MissingBookCopiesException.class, () -> {
            this.bookService.removeByIsbn(this.VALID_ISBN);
        });
    }

    @Test
    public void removeByIsbn_NonExistingIsbn() {
        assertDoesNotThrow(() -> {
            assertFalse(this.bookService.removeByIsbn(this.NON_EXISTENT_ISBN));
        });
    }

    @Test
    public void updateByIsbn_ExistingBook() {
        this.addBooks(this.VALID_BOOK);

        assertDoesNotThrow(() -> this.bookService.updateByIsbn(this.UPDATED_BOOK));

        Book retrieved = this.bookService.getByIsbn(this.VALID_ISBN).get();
        assertEquals(this.UPDATED_BOOK.getTitle(), retrieved.getTitle());
        assertEquals(this.UPDATED_BOOK.getAuthor(), retrieved.getAuthor());
    }

    @Test
    public void updateByIsbn_NonExistingBook() {
        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.bookService.updateByIsbn(this.VALID_BOOK);
        });
    }

    @Test
    public void updateByIsbn_NegativeCopies() {
        this.addBooks(this.VALID_BOOK);

        assertThrows(NegativeBookCopiesException.class, () -> {
            this.bookService.updateByIsbn(this.NEGATIVE_TOTAL_COPIES_BOOK);
        });
    }

    @Test
    public void countRemainingCopies_ExistingBook() {
        this.addBooks(this.VALID_BOOK);
        assertEquals(5, this.bookService.countRemainingCopies(this.VALID_ISBN));
    }

    @Test
    public void updateRemainingCopies_Success() {
        this.addBooks(this.VALID_BOOK);

        assertDoesNotThrow(() -> {
            this.bookService.updateRemainingCopies(this.VALID_ISBN, 2);
            assertEquals(7, this.bookService.countRemainingCopies(this.VALID_ISBN));

            this.bookService.updateRemainingCopies(this.VALID_ISBN, -2);
            assertEquals(5, this.bookService.countRemainingCopies(this.VALID_ISBN));
        });
    }

    @Test
    public void updateRemainingCopies_NegativeCopies() {
        this.addBooks(this.VALID_BOOK);

        assertThrows(NegativeBookCopiesException.class, () -> {
            this.bookService.updateRemainingCopies(this.VALID_ISBN, -10);
        });

        assertThrows(InvalidBookCopiesException.class, () -> {
            this.bookService.updateRemainingCopies(this.VALID_ISBN, 100);
        });

        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.bookService.updateRemainingCopies(this.NON_EXISTENT_ISBN, 1);
        });
    }

    // I punti servono per far passare uno o piu' libri
    private void addBooks(Book... books) {
        assertDoesNotThrow(() -> {
            for (Book book : books) {
                this.bookService.add(book);
            }
        });
    }
}
