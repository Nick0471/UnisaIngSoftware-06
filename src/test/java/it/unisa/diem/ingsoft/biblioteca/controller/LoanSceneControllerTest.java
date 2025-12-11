package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.model.*;
import it.unisa.diem.ingsoft.biblioteca.service.*;
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

import java.time.LocalDate;

import static it.unisa.diem.ingsoft.biblioteca.Views.LOAN_PATH;

public class LoanSceneControllerTest extends ApplicationTest {
    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    @Override
    public void start(Stage stage){
        Database db = Database.inMemory();

        this.bookService = new DatabaseBookService(db);
        this.userService = new DatabaseUserService(db);
        this.loanService = new DatabaseLoanService(db);
        ServiceRepository serviceRepository = new ServiceRepository(null, this.userService, this.bookService, this.loanService);

        try {
            setUp();
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
            if(!bookService.existsByIsbn(isbn)) {
                bookService.add(new Book(isbn, "Title " + i, "Author", 2020, 5, 5, "Genre", "Desc"));
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

            if(!loanService.has(userId, isbn)) {
                loanService.register(userId, isbn, start, deadline);
            }
        }
    }

    private void resetSearchField(){
        doubleClickOn("#searchField").push(KeyCode.DELETE);
        sleep(1000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 7);
    }

    @Test
    public void test1_InitializationAndRedRows() {
        System.out.println("--- TEST 1: CARICAMENTO E COLORI ---");
        sleep(1000);

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 7);

        System.out.println("Controllo visivo righe rosse...");
        moveTo("0612700001"); sleep(800);
        moveTo("0612700002"); sleep(800);
        moveTo("0612700003"); sleep(800);
    }

    @Test
    public void test2_Sorting() {
        System.out.println("--- TEST 2: ORDINAMENTO ---");

        System.out.println("Ordino per Scadenza...");
        clickOn("Scadenza");
        sleep(1500);

        System.out.println("Ordino per Scadenza decrescente...");
        clickOn("Scadenza");
        sleep(1500);

        System.out.println("Ordino per ISBN...");
        clickOn("ISBN Libro");
        sleep(1500);

        System.out.println("Ordino per ISBN decrescente...");
        clickOn("ISBN Libro");
        sleep(1500);
    }

    @Test
    public void test3_SearchFunctionality() {
        System.out.println("--- TEST 3: FILTRI DI RICERCA ---");

        System.out.println("Cerco matricola: 0612700003");
        clickOn("#searchType").clickOn("Matricola");
        clickOn("#searchField").write("0612700003");
        sleep(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 1);

        resetSearchField();

        System.out.println("Cerco matricola inesistente: 0612708994");
        clickOn("#searchField").write("0612708994");
        sleep(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().isEmpty());

        resetSearchField();

        System.out.println("Cerco ISBN: 0003000000000");
        clickOn("#searchType").clickOn("ISBN");
        clickOn("#searchField").write("0003000000000");
        sleep(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 1);

        resetSearchField();

        System.out.println("Cerco ISBN inesistente: 9780618391110");
        clickOn("#searchField").write("9780618391110");
        sleep(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().isEmpty());
    }

    @Test
    public void test4_ReturnLoan(){
        System.out.println("--- TEST 4: RESTITUZIONE PRESTITO ---");

        int initialSize = lookup("#loanTable").queryTableView().getItems().size();

        System.out.println("Seleziono il prestito di 0612700006...");
        clickOn("0612700006");
        sleep(1500);

        System.out.println("Clicco su Restituisci...");
        clickOn("#btnReturn");

        sleep(2000);

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == initialSize - 1);
    }

    @Test
    public void test5_ReturnLoanError(){
        System.out.println("--- TEST 4: RESTITUZIONE PRESTITO NON SELEZIONATO ---");

        int initialSize = lookup("#loanTable").queryTableView().getItems().size();

        System.out.println("Clicco su Restituisci...");
        clickOn("#btnReturn");

        sleep(2000);

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == initialSize);
    }

    @Test
    public void test6_OpenModal() {
        System.out.println("--- TEST 6: APERTURA MODALE ---");

        System.out.println("Apro modale...");
        clickOn("#btnAdd");
        sleep(1500);

        FxAssert.verifyThat("Registra Nuovo Prestito", NodeMatchers.isVisible());

        System.out.println("Chiudo modale...");
        clickOn("#btnCancel");
        sleep(1000);
    }

    @Test
    public void test7_NavigationHome() {
        System.out.println("--- TEST 7: NAVIGAZIONE HOME ---");

        sleep(1000);
        System.out.println("Clicco Home...");
        clickOn("#btnHome");
        sleep(2000);

        FxAssert.verifyThat("Biblioteca Universitaria", NodeMatchers.isVisible());
        System.out.println("Homepage raggiunta.");
    }
}
