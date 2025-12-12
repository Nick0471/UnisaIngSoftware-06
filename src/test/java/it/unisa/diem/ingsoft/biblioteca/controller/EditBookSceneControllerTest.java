package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;

import static it.unisa.diem.ingsoft.biblioteca.Views.BOOK_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EditBookSceneControllerTest extends ApplicationTest {
    private BookService bookService;

    @Override
    public void start(Stage stage){
        Database db = Database.inMemory();

        this.bookService = new DatabaseBookService(db);
        ServiceRepository serviceRepository = new ServiceRepository(null, null, this.bookService, null);

        try {
            this.setUp();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FXMLLoader loader = Scenes.setupLoader(BOOK_PATH, serviceRepository);
        Parent root =  loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Visualizzazione catalogo libri");
        stage.setScene(scene);
        stage.show();
    }

    public void setUp() throws Exception {

        if(!this.bookService.existsByIsbn("9780618391110")) {
            this.bookService.add(new Book("9780618391110", "The Silmarillion", "J.R.R. Tolkien", 1977, 5, 5, "Fantasy",
                    "Raccolta di miti e leggende della Terra di Mezzo, dalla creazione del mondo alla Terza Era."));
        }

        if(!this.bookService.existsByIsbn("9780547928227")) {
            this.bookService.add(new Book("9780547928227", "The Hobbit", "J.R.R. Tolkien", 1937, 10, 5, "Fantasy",
                    "Le avventure di Bilbo Baggins."));
        }

        if(!this.bookService.existsByIsbn("9780547928210")) {
            this.bookService.add(new Book("9780547928210", "The Fellowship of the Ring", "J.R.R. Tolkien", 1954, 8, 5, "Fantasy",
                    "Il primo volume della trilogia. Frodo Baggins eredita l'Unico Anello e inizia il pericoloso viaggio verso il Monte Fato."));
        }

        if(!this.bookService.existsByIsbn("9780547928203")) {
            this.bookService.add(new Book("9780547928203", "The Two Towers", "J.R.R. Tolkien", 1954, 8, 5, "Fantasy",
                    "Il secondo volume della trilogia. Mentre Frodo e Sam continuano il cammino, Aragorn, Legolas e Gimli inseguono gli Uruk-hai."));
        }

        if(!this.bookService.existsByIsbn("9780547928197")) {
            this.bookService.add(new Book("9780547928197", "The Return of the King", "J.R.R. Tolkien", 1955, 8, 5, "Fantasy",
                    "Il terzo volume della trilogia. Mentre l'esercito di Sauron muove guerra a Gondor, l'Anello si avvicina alla sua distruzione."));
        }

        if(!this.bookService.existsByIsbn("9780064471046")) {
            this.bookService.add(new Book("9780064471046", "The Lion, the Witch and the Wardrobe", "C.S. Lewis", 1950, 7, 5, "Fantasy",
                    "Quattro fratelli attraversano un armadio magico e scoprono la terra di Narnia, congelata in un inverno eterno dalla Strega Bianca."));
        }

        if(!this.bookService.existsByIsbn("9780451524935")) {
            this.bookService.add(new Book("9780451524935", "1984", "George Orwell", 1949, 15, 5, "Dystopian",
                    "Un romanzo agghiacciante su un regime totalitario che sorveglia ogni mossa dei cittadini. Il Grande Fratello ti sta guardando."));
        }

        if(!this.bookService.existsByIsbn("9780486282114")) {
            this.bookService.add(new Book("9780486282114", "Frankenstein", "Mary Shelley", 1818, 4, 4, "Gothic Horror",
                    "La storia del dottor Victor Frankenstein e della creatura mostruosa ma senziente che egli assembla e riporta in vita."));
        }
    }

    @Test
    public void test1_AddNewBook(){
        System.out.println("--- TEST 1: AGGIUNTA NUOVO LIBRO ---");

        int initialSize = this.lookup("#bookCatalog").queryTableView().getItems().size();

        this.clickOn("#btnAdd");
        this.sleep(500);

        this.clickOn("#titleField").write("Dune");
        this.clickOn("#authorField").write("Frank Herbert");
        this.clickOn("#genreField").write("Sci-Fi");
        this.clickOn("#yearField").write("1965");
        this.clickOn("#isbnField").write("9780441013593");
        this.clickOn("#copiesField").write("10");
        this.clickOn("#descriptionArea").write("Il pianeta delle spezie.");

        this.clickOn("#btnConfirm");
        this.sleep(500);

        this.clickOn("OK");
        this.sleep(500);

        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == initialSize + 1);
    }

    @Test
    public void test2_AbortOperation() {
        System.out.println("--- TEST 2: ANNULLA INSERIMENTO ---");

        int initialSize = this.lookup("#bookCatalog").queryTableView().getItems().size();

        this.clickOn("#btnAdd");
        this.sleep(500);

        this.clickOn("#isbnField").write("1234567890123");

        this.clickOn("#btnCancel");
        this.sleep(500);

        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == initialSize);
    }

    @Test
    public void test3_ModifyBook() {
        System.out.println("--- TEST 3: MODIFICA LIBRO ---");

        this.clickOn("1984");

        this.clickOn("#btnModify");
        this.sleep(500);

        FxAssert.verifyThat("#isbnField", (javafx.scene.control.TextField t) -> t.getText().equals("9780451524935"));
        FxAssert.verifyThat("#isbnField", (javafx.scene.control.TextField t) -> t.isDisabled());


        this.doubleClickOn("#copiesField").write("5");

        this.clickOn("#btnConfirm");
        this.sleep(500);
        this.clickOn("OK");


        Book updatedBook = this.bookService.getByIsbn("9780618391110").get();
        assertEquals(5, updatedBook.getTotalCopies());
    }


    @Test
    public void test4_EmptyFields() {
        System.out.println("--- TEST 4: VALIDAZIONE CAMPI VUOTI ---");

        int initialSize = this.lookup("#bookCatalog").queryTableView().getItems().size();

        this.clickOn("#btnAdd");
        this.sleep(500);

        this.clickOn("#btnConfirm");
        this.sleep(500);

        this.clickOn("OK");


        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == initialSize);
    }
}
