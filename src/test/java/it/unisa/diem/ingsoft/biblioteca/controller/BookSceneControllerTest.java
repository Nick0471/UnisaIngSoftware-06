package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import static it.unisa.diem.ingsoft.biblioteca.Views.BOOK_PATH;

public class BookSceneControllerTest extends ApplicationTest {
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

    private void resetSearchField(){
        this.doubleClickOn("#searchField");
        this.push(KeyCode.CONTROL, KeyCode.A);
        this.push(KeyCode.DELETE);
        this.sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 8);
    }

    @Test
    public void test1_Initialization() {
        System.out.println("--- TEST 1: CARICAMENTO CATALOGO ---");
        this.sleep(3000);

        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 8);
    }

    @Test
    public void test2_Sorting() {
        System.out.println("--- TEST 2: ORDINAMENTO CATALOGO ---");
        this.sleep(500);

        System.out.println("Ordino per autore");
        this.clickOn("Autore");
        this.sleep(1500);

        System.out.println("Ordino per anno");
        this.clickOn("Anno");
        this.sleep(1500);

        System.out.println("Ordino per titolo");
        this.clickOn("Titolo");
        this.sleep(1500);

        System.out.println("Ordino per ISBN");
        this.clickOn("ISBN");
        this.sleep(1500);
    }

    @Test
    public void test3_SearchFunctionality() {
        System.out.println("--- TEST 3: FILTRI DI RICERCA ---");
        this.sleep(500);

        System.out.println("Cerco senza criterio di selezione");
        this.clickOn("#searchField").write("J.R.R. Tolkien");
        this.sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().isEmpty());

        this.resetSearchField();

        this.clickOn("#searchField").write("The Hobbit");
        this.sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 1);

        this.resetSearchField();

        System.out.println("Cerco autore: J.R.R. Tolkien");
        this.clickOn("#searchType").clickOn("Autore ");
        this.clickOn("#searchField").write("J.R.R. Tolkien");
        this.sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 5);

        this.resetSearchField();

        System.out.println("Cerco autore: J.K. Rowling");
        this.clickOn("#searchField").write("J.K. Rowling");
        this.sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().isEmpty());

        this.resetSearchField();

        System.out.println("Cerco titolo: The Silmarillion");
        this.clickOn("#searchType").clickOn("Titolo ");
        this.clickOn("#searchField").write("The Silmarillion");
        this.sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 1);

        this.resetSearchField();

        System.out.println("Cerco titolo inesistente: Harry Potter");
        this.clickOn("#searchField").write("Harry Potter");
        this.sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().isEmpty());

        this.resetSearchField();

        System.out.println("Cerco ISBN: 9780451524935");
        this.clickOn("#searchType").clickOn("ISBN ");
        this.clickOn("#searchField").write("9780451524935");
        this.sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 1);

        this.resetSearchField();

        System.out.println("Cerco Genere: Fantasy");
        this.clickOn("#searchType").clickOn("Genere ");
        this.clickOn("#searchField").write("Fantasy");
        this.sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 6);

        this.resetSearchField();

        System.out.println("Cerco Anno: 1954");
        this.clickOn("#searchType").clickOn("Anno ");
        this.clickOn("#searchField").write("1954");
        this.sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 2);

        this.resetSearchField();
    }

    @Test
    public void test4_RemoveBook(){
        System.out.println("--- TEST 4: RIMOZIONE LIBRO ---");

        int initialSize = this.lookup("#bookCatalog").queryTableView().getItems().size();

        System.out.println("Seleziono il libro: 1984");
        this.clickOn("9780451524935");
        this.sleep(1500);

        System.out.println("Clicco su Rimuovi");
        this.clickOn("#btnRemove");
        //this.clickOn("OK");per chiudere il popup

        this.sleep(2000);

        FxAssert.verifyThat("#bookCatalog", (TableView<Loan> t) -> t.getItems().size() == initialSize - 1);
    }

    @Test
    public void test5_RemoveBookError(){
        System.out.println("--- TEST 5: RIMOZIONE LIBRO FALLITA ---");

        int initialSize = this.lookup("#bookCatalog").queryTableView().getItems().size();

        System.out.println("Non seleziono un libro");
        System.out.println("Clicco su Rimuovi");
        this.clickOn("#btnRemove");
        this.clickOn("OK");

        this.sleep(1000);

        FxAssert.verifyThat("#bookCatalog", (TableView<Loan> t) -> t.getItems().size() == initialSize);
    }

    @Test
    public void test6_ModifyBook(){
        System.out.println("--- TEST 6: MODIFICA LIBRO ---");

        System.out.println("Seleziono il libro: 1984");
        this.clickOn("9780451524935");
        this.sleep(1000);

        System.out.println("Clicco su Modifica");
        this.clickOn("#btnModify");
        this.sleep(1500);

        FxAssert.verifyThat("Modifica Libro", NodeMatchers.isVisible());

        System.out.println("Chiudo modale...");
        this.clickOn("#btnCancel");
        this.sleep(1000);
    }

    @Test
    public void test7_AddBook(){
        System.out.println("--- TEST 6: AGGIUNTA LIBRO ---");

        System.out.println("Clicco su Aggiungi");
        this.clickOn("#btnAdd");
        this.sleep(1000);

        FxAssert.verifyThat("Aggiungi Nuovo Libro", NodeMatchers.isVisible());

        System.out.println("Chiudo modale...");
        this.clickOn("#btnCancel");
        this.sleep(1000);
    }

    @Test
    public void test8_NavigationHome() {
        System.out.println("--- TEST 8: NAVIGAZIONE HOME ---");

        this.sleep(1000);
        System.out.println("Clicco Home...");
        this.clickOn("#btnHome");
        this.sleep(2000);

        FxAssert.verifyThat("Biblioteca Universitaria", NodeMatchers.isVisible());
        System.out.println("Homepage raggiunta.");
    }

}


