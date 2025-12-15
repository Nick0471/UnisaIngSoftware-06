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
    private static final String VALID_ISBN = "1234567890000";
    private static final String SECOND_VALID_ISBN = "1234567800000";
    private static final String SEARCH_TEST_ISBN = "1334567810000";
    private static final String REMOVE_TEST_ISBN = "6767676760000";
    private static final String UPDATE_TEST_ISBN = "3519034830000";
    private static final String COPIES_TEST_ISBN = "0409820250000";
    private static final String INVALID_ISBN = "123456789";
    private static final String NON_EXISTENT_ISBN = "NON-ESISTE";

    private BookService bookService;

    @BeforeEach
    public void setup() {
        Database database = Database.inMemory();
        this.bookService = new DatabaseBookService(database);
    }

    @Test
    public void add_ValidBook() {
        assertDoesNotThrow(() -> {
            Book book = new Book(VALID_ISBN, "AOT", "Hajime Isayama", 2009, 50, 5, "Dark fantasy", "Un bel manga");
            this.bookService.add(book);
        });
    }

    @Test
    public void add_DuplicateIsbn() {
        assertDoesNotThrow(() -> {
            Book book = new Book(VALID_ISBN, "AOT", "Hajime Isayama", 2009, 50, 5, "Dark fantasy", "Un bel manga");
            this.bookService.add(book);
        });

        assertThrows(DuplicateBookByIsbnException.class, () -> {
            // Tentativo di aggiungere un libro diverso ma con lo stesso ISBN
            Book duplicateBook = new Book(VALID_ISBN, "1984", "George Orwell", 1948, 50, 3, "Distopico", "Un romanzo cupo");
            this.bookService.add(duplicateBook);
        });
    }

    @Test
    public void add_InvalidIsbn() {
        assertThrows(InvalidIsbnException.class, () -> {
            Book invalidBook = new Book(INVALID_ISBN, "1984", "George Orwell", 1948, 50, 3, "Distopico", "Un romanzo cupo");
            this.bookService.add(invalidBook);
        });
    }

    @Test
    public void add_NegativeTotalCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            // Uso VALID_ISBN perché l'errore atteso è sulle copie, non sull'ISBN
            Book negativeCopiesBook = new Book(VALID_ISBN, "1984", "George Orwell", 1948, -4, 3, "Distopico", "Un romanzo cupo");
            this.bookService.add(negativeCopiesBook);
        });
    }

    @Test
    public void add_NegativeRemainingCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            Book negativeRemainingBook = new Book(VALID_ISBN, "1984", "George Orwell", 1948, 50, -3, "Distopico", "Un romanzo cupo");
            this.bookService.add(negativeRemainingBook);
        });
    }

    @Test
    public void addAll_ValidList() {
        assertDoesNotThrow(() -> {
            List<Book> books = List.of(
                    new Book(VALID_ISBN, "One piece", "Oda", 1999, 50, 3, "Adv", "Peak"),
                    new Book(SECOND_VALID_ISBN, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Topo di campagna o di città?")
            );

            this.bookService.addAll(books);
        });
    }

    @Test
    public void addAll_NegativeCopies() {
        assertThrows(NegativeBookCopiesException.class, () -> {
            List<Book> books = List.of(
                new Book(SECOND_VALID_ISBN, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Desc"),
                new Book(VALID_ISBN, "One piece", "Oda", 1999, 50, -10, "Adv", "Peak")
            );
            this.bookService.addAll(books);
        });

        assertThrows(NegativeBookCopiesException.class, () -> {
            List<Book> books = List.of(
                new Book(SECOND_VALID_ISBN, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Desc"),
                new Book(VALID_ISBN, "One piece", "Oda", 1999, -12, 1, "Adv", "Peak")
            );
            this.bookService.addAll(books);
        });
    }

    @Test
    public void addAll_InvalidIsbn() {
        assertThrows(InvalidIsbnException.class, () -> {
            List<Book> books = List.of(
                new Book(VALID_ISBN, "One piece", "Oda", 1999, 10, 1, "Adv", "Peak"),
                new Book(INVALID_ISBN, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Desc")
            );
            this.bookService.addAll(books);
        });
    }

    @Test
    public void addAll_DuplicateInList() {
        assertDoesNotThrow(() -> {
            this.bookService.add(new Book(SECOND_VALID_ISBN, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Desc"));
        });

        assertThrows(DuplicateBooksByIsbnException.class, () -> {
            List<Book> books = List.of(
                    new Book("1235567800000", "Jojo", "Hirohiko Araki", 1987, 50, 9, "Fantasy", "Parte 7"),
                    new Book(SECOND_VALID_ISBN, "Chainsaw man", "Tatsuki Fujimoto", 2022, 50, 4, "Fantasy", "Duplicato")
                    );
            this.bookService.addAll(books);
        });
    }

    @Test
    public void getByIsbn_ExistingIsbn() {
        assertDoesNotThrow(() -> {
            this.bookService.add(new Book(SEARCH_TEST_ISBN, "Blue lock S1", "Muneyuki Kaneshiro", 2018, 50, 19, "Sportivo", "Reo è il goat"));
        });

        assertTrue(this.bookService.getByIsbn(SEARCH_TEST_ISBN).isPresent());

        Book retrieved = this.bookService.getByIsbn(SEARCH_TEST_ISBN).get();
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
        assertTrue(this.bookService.getByIsbn(NON_EXISTENT_ISBN).isEmpty());
    }

    @Test
    public void getAll_Populated() {
        assertDoesNotThrow(() -> {
            this.bookService.add(new Book(VALID_ISBN, "Libro Test", "Autore", 2020, 10, 5, "Genere", "Desc"));
        });
        assertFalse(this.bookService.getAll().isEmpty());
    }

    @Test
    public void getAllByAuthor_MatchingString() {
        assertDoesNotThrow(() -> {
            this.bookService.add(new Book(SEARCH_TEST_ISBN, "Blue lock S1", "Muneyuki Kaneshiro", 2018, 50, 19, "Sportivo", "Desc"));
            this.bookService.add(new Book("1334967820000", "Blue lock S2", "Muneyuki Kaneshiro", 2019, 50, 19, "Sportivo", "Desc"));
            this.bookService.add(new Book("1384567830000", "Blue lock S3", "Muneyuki Kaneshiro", 2020, 50, 19, "Sportivo", "Desc"));
        });

        assertEquals(3, this.bookService.getAllByAuthorContaining("Kane").size());
    }

    @Test
    public void getAllByReleaseYear_MatchingYear() {
        assertDoesNotThrow(() -> {
            this.bookService.add(new Book(SEARCH_TEST_ISBN, "Book 2018", "Author", 2018, 50, 19, "Genre", "Desc"));
        });

        assertFalse(this.bookService.getAllByReleaseYear(2018).isEmpty());
        assertTrue(this.bookService.getAllByReleaseYear(2000).isEmpty());
    }

    @Test
    public void getAllByGenre_MatchingString() {
        assertDoesNotThrow(() -> {
            this.bookService.add(new Book(VALID_ISBN, "AOT", "Isayama", 2009, 50, 5, "Dark fantasy", "Desc"));
        });

        assertFalse(this.bookService.getAllByGenreContaining("Dark fantasy").isEmpty());
    }

    @Test
    public void getAllByIsbn_MatchingString() {
        assertDoesNotThrow(() -> {
            this.bookService.add(new Book(VALID_ISBN, "AOT", "Isayama", 2009, 50, 5, "Dark fantasy", "Desc"));
        });

        assertFalse(this.bookService.getAllByIsbnContaining("789").isEmpty());
    }

    @Test
    public void getAllByTitle_MatchingString() {
        assertDoesNotThrow(() -> {
            this.bookService.add(new Book(VALID_ISBN, "AOT", "Isayama", 2009, 50, 5, "Dark fantasy", "Desc"));
        });

        assertFalse(this.bookService.getAllByTitleContaining("AOT").isEmpty());
    }

    @Test
    public void existsByIsbn_ExistingIsbn() {
        Book book = new Book(REMOVE_TEST_ISBN, "Dragon Ball", "Toriyama", 1984, 50, 40, "Fantasy", "Kamehamea!!!");
        assertDoesNotThrow(() -> {
            this.bookService.add(book);
        });

        assertTrue(this.bookService.existsByIsbn(REMOVE_TEST_ISBN));
    }

    @Test
    public void existsByIsbn_NonExistingIsbn() {
        assertFalse(this.bookService.existsByIsbn(NON_EXISTENT_ISBN));
    }

    @Test
    public void removeByIsbn_ExistingIsbn() {
        Book book = new Book(REMOVE_TEST_ISBN, "Dragon Ball", "Toriyama", 1984, 50, 50, "Fantasy", "Kamehamea!!!");
        assertDoesNotThrow(() -> {
            this.bookService.add(book);
        });

        assertDoesNotThrow(() -> {
            assertTrue(this.bookService.removeByIsbn(REMOVE_TEST_ISBN));
        });
    }

    @Test
    public void removeByIsbn_MissingCopies() {
        Book book = new Book(REMOVE_TEST_ISBN, "Dragon Ball", "Toriyama", 1984, 50, 49, "Fantasy", "Kamehamea!!!");
        assertDoesNotThrow(() -> {
            this.bookService.add(book);
        });

        assertThrows(MissingBookCopiesException.class, () -> {
            this.bookService.removeByIsbn(REMOVE_TEST_ISBN);
        });
    }

    @Test
    public void removeByIsbn_NonExistingIsbn() {
        assertDoesNotThrow(() -> {
            assertFalse(this.bookService.removeByIsbn(NON_EXISTENT_ISBN));
        });
    }

    @Test
    public void updateByIsbn_ExistingBook() {
        Book book = new Book(UPDATE_TEST_ISBN, "Storia di un piatto vuoto", "Vincenzo D. Raimo", 2026, 1, 0, "Adv", "R.I.P. Mattia L. Santoro");
        assertDoesNotThrow(() -> {
            this.bookService.add(book);
        });

        Book updatedBook = new Book(UPDATE_TEST_ISBN, "An empty plate 's tale", "Vincenzo Dan. Raimo", 2026, 1, 0, "Adv", "R.I.P. Mattia L. Santoro");
        assertDoesNotThrow(() -> this.bookService.updateByIsbn(updatedBook));

        Book retrieved = this.bookService.getByIsbn(UPDATE_TEST_ISBN).get();
        assertEquals("An empty plate 's tale", retrieved.getTitle());
        assertEquals("Vincenzo Dan. Raimo", retrieved.getAuthor());
    }

    @Test
    public void updateByIsbn_NonExistingBook() {
        // Uso un ISBN valido ma non presente nel DB per questo test
        Book book = new Book(VALID_ISBN, "Titolo", "Autore", 2025, 4, 4, "Politica", "Desc");

        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.bookService.updateByIsbn(book);
        });
    }

    @Test
    public void updateByIsbn_NegativeCopies() {
        // Uso VALID_ISBN per semplicità
        Book book = new Book(VALID_ISBN, "Titolo", "Autore", 2025, 4, 4, "Politica", "Desc");
        assertDoesNotThrow(() -> {
            this.bookService.add(book);
        });

        Book invalidUpdate = new Book(VALID_ISBN, "Titolo", "Autore", 2025, -4, 3, "Politica", "Desc");
        assertThrows(NegativeBookCopiesException.class, () -> {
            this.bookService.updateByIsbn(invalidUpdate);
        });
    }

    @Test
    public void countRemainingCopies_ExistingBook() {
        Book book = new Book(COPIES_TEST_ISBN, "Trip to Sofia", "Ancel P.", 2027, 14, 3, "Fantascienza", "Desc");
        assertDoesNotThrow(() -> {
            this.bookService.add(book);
        });

        assertEquals(3, this.bookService.countRemainingCopies(COPIES_TEST_ISBN));
    }

    @Test
    public void updateRemainingCopies_Success() {
        String isbn = COPIES_TEST_ISBN;
        Book book = new Book(isbn, "Trip to Sofia", "Ancel P.", 2027, 14, 3, "Fantascienza", "Desc");

        assertDoesNotThrow(() -> {
            this.bookService.add(book);

            this.bookService.updateRemainingCopies(isbn, 2);
            assertEquals(this.bookService.countRemainingCopies(isbn), 5);

            this.bookService.updateRemainingCopies(isbn, -2);
            assertEquals(this.bookService.countRemainingCopies(isbn), 3);
        });

        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.bookService.updateRemainingCopies(NON_EXISTENT_ISBN, 1);
        });
    }

    @Test
    public void updateRemainingCopies_NegativeCopies() {
        String isbn = COPIES_TEST_ISBN;
        Book book = new Book(isbn, "Trip to Sofia", "Ancel P.", 2027, 14, 3, "Fantascienza", "Desc");

        assertDoesNotThrow(() -> {
            this.bookService.add(book);
        });

        assertThrows(NegativeBookCopiesException.class, () -> {
            this.bookService.updateRemainingCopies(isbn, -10);
        });

        assertThrows(InvalidBookCopiesException.class, () -> {
            this.bookService.updateRemainingCopies(isbn, 100);
        });

        assertThrows(UnknownBookByIsbnException.class, () -> {
            this.bookService.updateRemainingCopies(NON_EXISTENT_ISBN, 1);
        });
    }
}
