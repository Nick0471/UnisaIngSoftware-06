package it.unisa.diem.ingsoft.biblioteca.controller;

import static it.unisa.diem.ingsoft.biblioteca.Views.LOAN_PATH;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class LoanSceneControllerTest extends ApplicationTest {
    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    @Override
    public void start(Stage stage){
        Database db = Database.inMemory();

        this.bookService = new DatabaseBookService(db);
        this.userService = new DatabaseUserService(db);
        this.loanService = new DatabaseLoanService(this.userService, this.bookService, db);
        ServiceRepository serviceRepository = new ServiceRepository(null, this.userService, this.bookService, this.loanService);

        try {
            this.setUp();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FXMLLoader loader = Scenes.setupLoader(LOAN_PATH, serviceRepository);
        Parent root =  loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Visualizzazione prestiti");
        stage.setScene(scene);
        stage.show();
    }

    public void setUp() throws Exception{
        for(int i = 1; i <= 7; i++) {
            String matricola = "061270000" + i;
            if(!this.userService.existsById(matricola)) {
                this.userService.register(new User(matricola, "user."+i+"@studenti.unisa.it", "Name"+i, "Surname"+i));
            }
        }

        for(int i = 1; i <= 7; i++) {
            String isbn = "000" + i + "000000000";
            if(!this.bookService.existsByIsbn(isbn)) {
                this.bookService.add(new Book(isbn, "Title " + i, "Author", 2020, 5, 5, "Genre", "Desc"));
            }
        }

        int countEspiredLoans = 0;
        for(int i = 1; i <= 7; i++) {
            String userId = "061270000" + i;
            String isbn = "000" + i + "000000000";
            LocalDate start = LocalDate.now().minusDays(i*10);
            LocalDate deadline;

            if(countEspiredLoans < 3) {
                deadline = LocalDate.now().minusDays(i*10);
                countEspiredLoans += 1;
            }else{
                deadline = LocalDate.now().plusDays(i*10);
            }

            if(!this.loanService.isActive(userId, isbn)) {
                this.loanService.register(userId, isbn, start, deadline);
            }
        }
    }

    private void resetSearchField(){
        this.doubleClickOn("#searchField").push(KeyCode.DELETE);
        this.sleep(1000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 7);
    }

    @Test
    public void test1_InitializationAndRedRows() {
        System.out.println("--- TEST 1: CARICAMENTO E COLORI ---");
        this.sleep(1000);

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 7);

        System.out.println("Controllo visivo righe rosse...");
        this.moveTo("0612700001"); this.sleep(800);
        this.moveTo("0612700002"); this.sleep(800);
        this.moveTo("0612700003"); this.sleep(800);
    }

    @Test
    public void test2_Sorting() {
        System.out.println("--- TEST 2: ORDINAMENTO ---");

        System.out.println("Ordino per Scadenza...");
        this.clickOn("Scadenza");
        this.sleep(1500);

        System.out.println("Ordino per Scadenza decrescente...");
        this.clickOn("Scadenza");
        this.sleep(1500);

        System.out.println("Ordino per ISBN...");
        this.clickOn("ISBN Libro");
        this.sleep(1500);

        System.out.println("Ordino per ISBN decrescente...");
        this.clickOn("ISBN Libro");
        this.sleep(1500);
    }

    @Test
    public void test3_SearchFunctionality() {
        System.out.println("--- TEST 3: FILTRI DI RICERCA ---");
        this.sleep(500);

        System.out.println("Cerco matricola senza criterio di ricerca: 0612700003");
        this.clickOn("#searchField").write("0612700003");
        this.sleep(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 1);

        this.resetSearchField();

        System.out.println("Cerco ISBN: 0003000000000");
        this.clickOn("#searchType").clickOn("ISBN ");
        this.clickOn("#searchField").write("0003000000000");
        this.sleep(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 1);

        this.resetSearchField();

        System.out.println("Cerco ISBN inesistente: 9780618391110");
        this.clickOn("#searchField").write("9780618391110");
        this.sleep(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().isEmpty());

        this.resetSearchField();

        System.out.println("Cerco matricola: 0612700003");
        this.clickOn("#searchType").clickOn("Matricola ");
        this.clickOn("#searchField").write("0612700003");
        this.sleep(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 1);

        this.resetSearchField();

        System.out.println("Cerco matricola inesistente: 0612708994");
        this.clickOn("#searchField").write("0612708994");
        this.sleep(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().isEmpty());
    }

    @Test
    public void test4_ReturnLoan(){
        System.out.println("--- TEST 4: RESTITUZIONE PRESTITO ---");

        int initialSize = this.lookup("#loanTable").queryTableView().getItems().size();

        System.out.println("Seleziono il prestito di 0612700006...");
        this.clickOn("0612700006");
        this.sleep(1500);

        System.out.println("Clicco su Restituisci...");
        this.clickOn("#btnReturn");
        this.clickOn("OK");

        this.sleep(2000);

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == initialSize - 1);
    }

    @Test
    public void test5_ReturnLoanError(){
        System.out.println("--- TEST 4: RESTITUZIONE PRESTITO NON SELEZIONATO ---");

        int initialSize = this.lookup("#loanTable").queryTableView().getItems().size();

        System.out.println("Clicco su Restituisci...");
        this.clickOn("#btnReturn");
        this.sleep(500);

        this.clickOn("OK");
        this.sleep(500);

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == initialSize);
    }

    @Test
    public void test6_OpenModal() {
        System.out.println("--- TEST 6: APERTURA MODALE ---");

        System.out.println("Apro modale...");
        this.clickOn("#btnAdd");
        this.sleep(1500);

        FxAssert.verifyThat("Registra Nuovo Prestito", NodeMatchers.isVisible());

        System.out.println("Chiudo modale...");
        this.clickOn("#btnCancel");
        this.sleep(1000);
    }

    @Test
    public void test7_NavigationHome() {
        System.out.println("--- TEST 7: NAVIGAZIONE HOME ---");

        this.sleep(1000);
        System.out.println("Clicco Home...");
        this.clickOn("#btnHome");
        this.sleep(2000);

        FxAssert.verifyThat("Biblioteca Universitaria", NodeMatchers.isVisible());
        System.out.println("Homepage raggiunta.");

        this.sleep(1000);
    }
}
