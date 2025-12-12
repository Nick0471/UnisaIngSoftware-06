package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import static it.unisa.diem.ingsoft.biblioteca.Views.LOAN_PATH;

public class AddLoanSceneControllerTest extends ApplicationTest{
    private BookService bookService;
    private UserService userService;

    @Override
    public void start(Stage stage){
        Database db = Database.inMemory();

        this.bookService = new DatabaseBookService(db);
        this.userService = new DatabaseUserService(db);
        LoanService loanService = new DatabaseLoanService(this.userService, this.bookService, db);
        ServiceRepository serviceRepository = new ServiceRepository(null, this.userService, this.bookService, loanService);

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
        if(!this.userService.existsById("0612708994")) {
            this.userService.register(new User("0612708994", "nick@studenti.unisa.it", "Nick", "Test"));
        }

        if(!this.bookService.existsByIsbn("9780618391110")) {
            this.bookService.add(new Book("9780618391110", "The Silmarillion", "J.R.R. Tolkien", 1977, 5, 5, "Fantasy", "Raccolta di miti e leggende della Terra di Mezzo che narra la creazione del mondo e le epoche precedenti al Signore degli Anelli."));
        }
    }

    @BeforeEach
    public void openView(){
        this.sleep(2000);
        this.clickOn("#btnAdd");
    }

    @Test
    public void test1_UserFound(){
        System.out.println("--- TEST 1: UTENTE TROVATO ---");

        this.clickOn("#userMatricolaField").write("0612708994");
        this.sleep(1000);
        this.clickOn("0612708994 - Nick Test");
        this.sleep(500);

        FxAssert.verifyThat("#userMatricolaField", (javafx.scene.control.TextField t) -> t.isDisabled());
        this.sleep(500);
    }

    @Test
    public void test2_ResetUserSelection(){
        System.out.println("--- TEST 2: RESET SELEZIONE UTENTE ---");

        this.clickOn("#userMatricolaField").write("0612708994");
        this.sleep(1000);
        this.clickOn("0612708994 - Nick Test");

        this.clickOn("#btnResetUser");
        this.sleep(500);

        FxAssert.verifyThat("#userMatricolaField", (javafx.scene.control.TextField t) -> !t.isDisabled());
        this.sleep(500);
    }

    @Test
    public void test3_BookFound(){
        System.out.println("--- TEST 3: LIBRO TROVATO ---");

        this.clickOn("#isbnField").write("9780618391110");
        this.sleep(1000);
        this.clickOn("9780618391110 - The Silmarillion");
        this.sleep(500);

        FxAssert.verifyThat("#isbnField", (javafx.scene.control.TextField t) -> t.isDisabled());
        this.sleep(500);
    }

    @Test
    public void test4_BookNotFound(){
        System.out.println("--- TEST 4: RESET SLEZIONE LIBRO ---");

        this.clickOn("#isbnField").write("9780618391110");
        this.sleep(1000);
        this.clickOn("9780618391110 - The Silmarillion");
        this.sleep(500);

        this.clickOn("#btnResetBook");
        this.sleep(500);

        FxAssert.verifyThat("#isbnField", (javafx.scene.control.TextField t) -> !t.isDisabled());
        this.sleep(500);
    }

    @Test
    public void test5_AddNewLoan(){
        System.out.println("--- TEST 5: AGGIUNTA NUOVO PRESTITO ---");

        this.clickOn("#userMatricolaField").write("0612708994");
        this.clickOn("0612708994 - Nick Test");
        this.clickOn("#isbnField").write("9780618391110");
        this.clickOn("9780618391110 - The Silmarillion");
        this.sleep(500);

        this.clickOn("#btnConfirm");
        this.sleep(500);

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 1);
        this.sleep(500);
    }

    @Test
    public void test6_AbortOperation(){
        System.out.println("--- TEST 5: OPERAZIONE ABORTITA ---");

        this.clickOn("#userMatricolaField").write("0612708994");
        this.clickOn("#isbnField").write("9780618391110");
        this.sleep(500);

        this.clickOn("#btnCancel");

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().isEmpty());
        this.sleep(500);
    }

    @Test
    public void test7_ConfirmWithoutSelection() {
        System.out.println("--- TEST 7: ERRORE SELEZIONE MANCANTE ---");

        this.clickOn("#userMatricolaField").write("0612708994");
        this.clickOn("#isbnField").write("9780618391110");

        this.push(KeyCode.ESCAPE);

        this.clickOn("#btnConfirm");
        this.sleep(500);
        this.clickOn("OK");

        FxAssert.verifyThat("#userMatricolaField", NodeMatchers.isVisible());
    }
}
