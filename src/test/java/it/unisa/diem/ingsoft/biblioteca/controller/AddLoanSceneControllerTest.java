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
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;

import static it.unisa.diem.ingsoft.biblioteca.Views.LOAN_PATH;

public class AddLoanSceneControllerTest extends ApplicationTest{
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
        if(!userService.existsById("0612708994")) {
            userService.register(new User("0612708994", "nick@studenti.unisa.it", "Nick", "Test"));
        }

        if(!bookService.existsByIsbn("9780618391110")) {
            bookService.add(new Book("9780618391110", "THE_SILMARILLION", "J.R.R. Tolkien", 1977, 5, 5, "Fantasy", "Raccolta di miti e leggende della Terra di Mezzo che narra la creazione del mondo e le epoche precedenti al Signore degli Anelli."));
        }
    }

    @BeforeEach
    public void openView(){
        sleep(2000);
        clickOn("#btnAdd");
    }

    @Test
    public void test1_UserFound(){
        System.out.println("--- TEST 1: UTENTE TROVATO ---");

        clickOn("#userMatricolaField").write("0612708994");
        clickOn("#btnSearchUser");

        FxAssert.verifyThat("#userMatricolaField", (javafx.scene.control.TextField t) -> t.isDisabled());
        sleep(500);
    }

    @Test
    public void test2_UserNotFound(){
        System.out.println("--- TEST 2: UTENTE NON TROVATO ---");

        clickOn("#userMatricolaField").write("0154279227");
        clickOn("#btnSearchUser");

        FxAssert.verifyThat("#userMatricolaField", (javafx.scene.control.TextField t) -> !t.isDisabled());
        sleep(500);
    }

    @Test
    public void test3_BookFound(){
        System.out.println("--- TEST 3: LIBRO TROVATO ---");

        clickOn("#isbnField").write("9780618391110");
        clickOn("#btnSearchBook");

        FxAssert.verifyThat("#isbnField", (javafx.scene.control.TextField t) -> t.isDisabled());
        sleep(500);
    }

    @Test
    public void test4_BookNotFound(){
        System.out.println("--- TEST 4: LIBRO NON TROVATO ---");

        clickOn("#isbnField").write("9780618391120");
        clickOn("#btnSearchBook");

        FxAssert.verifyThat("#isbnField", (javafx.scene.control.TextField t) -> !t.isDisabled());
        sleep(500);
    }

    @Test
    public void test5_AddNewLoan(){
        System.out.println("--- TEST 5: AGGIUNTA NUOVO PRESTITO ---");

        clickOn("#userMatricolaField").write("0612708994");
        clickOn("#isbnField").write("9780618391110");
        sleep(500);

        clickOn("#btnConfirm");

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 1);
        sleep(500);
    }

    @Test
    public void test6_AbortOperation(){
        System.out.println("--- TEST 5: OPERAZIONE ABORTITA ---");

        clickOn("#userMatricolaField").write("0612708994");
        clickOn("#isbnField").write("9780618391110");
        sleep(500);

        clickOn("#btnCancel");

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().isEmpty());
        sleep(500);
    }
}
