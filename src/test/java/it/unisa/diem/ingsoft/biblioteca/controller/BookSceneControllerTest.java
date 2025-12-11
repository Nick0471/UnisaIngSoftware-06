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
            setUp();
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

        if(!bookService.existsByIsbn("9780618391110")) {
            bookService.add(new Book("9780618391110", "The Silmarillion", "J.R.R. Tolkien", 1977, 5, 5, "Fantasy",
                    "Raccolta di miti e leggende della Terra di Mezzo, dalla creazione del mondo alla Terza Era."));
        }

        if(!bookService.existsByIsbn("9780547928227")) {
            bookService.add(new Book("9780547928227", "The Hobbit", "J.R.R. Tolkien", 1937, 10, 5, "Fantasy",
                    "Le avventure di Bilbo Baggins."));
        }

        if(!bookService.existsByIsbn("9780547928210")) {
            bookService.add(new Book("9780547928210", "The Fellowship of the Ring", "J.R.R. Tolkien", 1954, 8, 5, "Fantasy",
                    "Il primo volume della trilogia. Frodo Baggins eredita l'Unico Anello e inizia il pericoloso viaggio verso il Monte Fato."));
        }

        if(!bookService.existsByIsbn("9780547928203")) {
            bookService.add(new Book("9780547928203", "The Two Towers", "J.R.R. Tolkien", 1954, 8, 5, "Fantasy",
                    "Il secondo volume della trilogia. Mentre Frodo e Sam continuano il cammino, Aragorn, Legolas e Gimli inseguono gli Uruk-hai."));
        }

        if(!bookService.existsByIsbn("9780547928197")) {
            bookService.add(new Book("9780547928197", "The Return of the King", "J.R.R. Tolkien", 1955, 8, 5, "Fantasy",
                    "Il terzo volume della trilogia. Mentre l'esercito di Sauron muove guerra a Gondor, l'Anello si avvicina alla sua distruzione."));
        }

        if(!bookService.existsByIsbn("9780064471046")) {
            bookService.add(new Book("9780064471046", "The Lion, the Witch and the Wardrobe", "C.S. Lewis", 1950, 7, 5, "Fantasy",
                    "Quattro fratelli attraversano un armadio magico e scoprono la terra di Narnia, congelata in un inverno eterno dalla Strega Bianca."));
        }

        if(!bookService.existsByIsbn("9780451524935")) {
            bookService.add(new Book("9780451524935", "1984", "George Orwell", 1949, 15, 5, "Dystopian",
                    "Un romanzo agghiacciante su un regime totalitario che sorveglia ogni mossa dei cittadini. Il Grande Fratello ti sta guardando."));
        }

        if(!bookService.existsByIsbn("9780486282114")) {
            bookService.add(new Book("9780486282114", "Frankenstein", "Mary Shelley", 1818, 4, 4, "Gothic Horror",
                    "La storia del dottor Victor Frankenstein e della creatura mostruosa ma senziente che egli assembla e riporta in vita."));
        }
    }

    private void resetSearchField(){
        doubleClickOn("#searchField");
        push(KeyCode.CONTROL, KeyCode.A);
        push(KeyCode.DELETE);
        sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 8);
    }

    @Test
    public void test1_Initialization() {
        System.out.println("--- TEST 1: CARICAMENTO CATALOGO ---");
        sleep(3000);

        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 8);
    }

    @Test
    public void test2_Sorting() {
        System.out.println("--- TEST 2: ORDINAMENTO CATALOGO ---");
        sleep(500);

        System.out.println("Ordino per autore");
        clickOn("Autore");
        sleep(1500);

        System.out.println("Ordino per anno");
        clickOn("Anno");
        sleep(1500);

        System.out.println("Ordino per titolo");
        clickOn("Titolo");
        sleep(1500);

        System.out.println("Ordino per ISBN");
        clickOn("ISBN");
        sleep(1500);
    }

    @Test
    public void test3_SearchFunctionality() {
        System.out.println("--- TEST 3: FILTRI DI RICERCA ---");

        System.out.println("Cerco titolo: The Silmarillion");
        clickOn("#searchType").clickOn("Titolo");
        clickOn("#searchField").write("The Silmarillion");
        sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 1);

        resetSearchField();

        System.out.println("Cerco titolo inesistente: Harry Potter");
        clickOn("#searchField").write("Harry Potter");
        sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().isEmpty());

        resetSearchField();

        System.out.println("Cerco autore: J.R.R. Tolkien");
        clickOn("#searchType").clickOn("Autore");
        clickOn("#searchField").write("J.R.R. Tolkien");
        sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 5);

        resetSearchField();

        System.out.println("Cerco autore: J.K. Rowling");
        clickOn("#searchField").write("J.K. Rowling");
        sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().isEmpty());

        resetSearchField();

        System.out.println("Cerco ISBN: 9780451524935");
        clickOn("#searchType").clickOn("ISBN");
        clickOn("#searchField").write("9780451524935");
        sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 1);

        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().get(0).getTitle().equals("1984"));

        resetSearchField();

        System.out.println("Cerco Genere: Fantasy");
        clickOn("#searchType").clickOn("Genere");
        clickOn("#searchField").write("Fantasy");
        sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 6);

        resetSearchField();

        System.out.println("Cerco Anno: 1954");
        clickOn("#searchType").clickOn("Anno");
        clickOn("#searchField").write("1954");
        sleep(1000);
        FxAssert.verifyThat("#bookCatalog", (TableView<Book> t) -> t.getItems().size() == 2);

        resetSearchField();
    }

    @Test
    public void test4_RemoveBook(){
        System.out.println("--- TEST 4: RIMOZIONE LIBRO ---");

        int initialSize = lookup("#bookCatalog").queryTableView().getItems().size();

        System.out.println("Seleziono il libro: 1984");
        clickOn("9780451524935");
        sleep(1500);

        System.out.println("Clicco su Rimuovi");
        clickOn("#btnRemove");

        sleep(2000);

        FxAssert.verifyThat("#bookCatalog", (TableView<Loan> t) -> t.getItems().size() == initialSize - 1);
    }

    @Test
    public void test5_RemoveBookError(){
        System.out.println("--- TEST 5: RIMOZIONE LIBRO FALLITA ---");

        int initialSize = lookup("#bookCatalog").queryTableView().getItems().size();

        System.out.println("Non seleziono un libro");
        System.out.println("Clicco su Rimuovi");
        clickOn("#btnRemove");

        sleep(2000);

        FxAssert.verifyThat("#bookCatalog", (TableView<Loan> t) -> t.getItems().size() == initialSize);
    }

    @Test
    public void test6_ModifyBook(){
        System.out.println("--- TEST 6: MODIFICA LIBRO ---");

        int initialSize = lookup("#bookCatalog").queryTableView().getItems().size();

        System.out.println("Seleziono il libro: 1984");
        clickOn("9780451524935");
        sleep(1000);

        System.out.println("Clicco su Modifica");
        clickOn("#btnModify");
        sleep(1500);

        //clickOn("#copiesField").write("5");
        //sleep(1000);

        //clickOn("#btnConfirm");
        FxAssert.verifyThat("Modifica Libro", NodeMatchers.isVisible());

        System.out.println("Chiudo modale...");
        clickOn("#btnCancel");
        sleep(1000);
    }

    @Test
    public void test7_AddBook(){
        System.out.println("--- TEST 6: AGGIUNTA LIBRO ---");

        System.out.println("Clicco su Aggiungi");
        clickOn("#btnAdd");
        sleep(1000);

        FxAssert.verifyThat("Aggiungi Nuovo Libro", NodeMatchers.isVisible());

        System.out.println("Chiudo modale...");
        clickOn("#btnCancel");
        sleep(1000);
    }

    @Test
    public void test8_NavigationHome() {
        System.out.println("--- TEST 8: NAVIGAZIONE HOME ---");

        sleep(1000);
        System.out.println("Clicco Home...");
        clickOn("#btnHome");
        sleep(2000);

        FxAssert.verifyThat("Biblioteca Universitaria", NodeMatchers.isVisible());
        System.out.println("Homepage raggiunta.");
    }

}

