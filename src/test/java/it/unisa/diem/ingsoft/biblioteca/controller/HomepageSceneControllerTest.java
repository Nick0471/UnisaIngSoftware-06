package it.unisa.diem.ingsoft.biblioteca.controller;

import static it.unisa.diem.ingsoft.biblioteca.Views.HOMEPAGE_PATH;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseAuthService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HomepageSceneControllerTest extends ApplicationTest {
    @Override
    public void start(Stage stage){
        Database db = Database.inMemory();
        UserService userService = new DatabaseUserService(db);
        BookService bookService = new DatabaseBookService(db);
        LoanService loanService = new DatabaseLoanService(userService, bookService, db);
        AuthService authService = new DatabaseAuthService(db);

        ServiceRepository serviceRepository = new ServiceRepository(authService, userService, bookService, loanService);

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
        this.clickOn("#btnBook");
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
        this.clickOn("#btnLoan");
        this.sleep(1000);

        FxAssert.verifyThat("#loanTable", NodeMatchers.isVisible());
    }


    @Test
    public void test4_GoToViewProfile() {
        System.out.println("--- TEST 1: GESTIONE PASSWORD ---");
        this.clickOn("#btnProfile");
        this.sleep(1000);

        FxAssert.verifyThat("#currentPassword", NodeMatchers.isVisible());
    }
}
