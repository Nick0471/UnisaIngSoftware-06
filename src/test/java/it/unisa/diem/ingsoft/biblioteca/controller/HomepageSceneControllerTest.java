package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;

public class HomepageSceneControllerTest extends ApplicationTest {
    @Override
    public void start(Stage stage){
        Database db = Database.inMemory();
        UserService userService = new DatabaseUserService(db);
        BookService bookService = new DatabaseBookService(db);
        LoanService loanService = new DatabaseLoanService(userService, bookService, db);
        PasswordService passwordService = new DatabasePasswordService(db);

        ServiceRepository serviceRepository = new ServiceRepository(passwordService, userService, bookService, loanService);

        FXMLLoader loader = Scenes.setupLoader(HOMEPAGE_PATH, serviceRepository);
        Parent root =  loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Homepage");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void test1_GoToViewBooks() {
        System.out.println("--- TEST 1: CATALOGO LIBRI ---");
        clickOn("#btnBook");
        this.sleep(1000);

        FxAssert.verifyThat("#bookCatalog", NodeMatchers.isVisible());
    }

    @Test
    public void test2_GoToViewUsers() {
        System.out.println("--- TEST 2: LISTA UTENTI ---");
        this.clickOn("#btnUser");
        this.sleep(1000);

        FxAssert.verifyThat("#userTable", NodeMatchers.isVisible());
    }

    @Test
    public void test3_GoToViewLoans() {
        System.out.println("--- TEST 1: PRESTITI ATTIVI ---");
        clickOn("#btnLoan");
        this.sleep(1000);

        FxAssert.verifyThat("#loanTable", NodeMatchers.isVisible());
    }


    @Test
    public void test4_GoToViewProfile() {
        System.out.println("--- TEST 1: GESTIONE PASSWORD ---");
        clickOn("#btnProfile");
        this.sleep(1000);

        FxAssert.verifyThat("#currentPassword", NodeMatchers.isVisible());
    }
}
