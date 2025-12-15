package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseAuthService;
import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import static it.unisa.diem.ingsoft.biblioteca.Views.LOGIN_PATH;


public class LogInSceneControllerTest extends ApplicationTest {

    private AuthService passwordService;

    @Override
    public void start(Stage stage) {
        Database db = Database.inMemory();

        this.passwordService = new DatabaseAuthService(db);

        ServiceRepository serviceRepository = new ServiceRepository(this.passwordService, null, null, null);

        FXMLLoader loader = Scenes.setupLoader(LOGIN_PATH, serviceRepository);
        Parent root = loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("LogIn");
        stage.setScene(scene);
        stage.show();


    }

    @Test
    public void test1_LogInSuccess(){
        System.out.println("--- TEST 1: LOGIN SUCCESSO---");

        //inserisco una password nel database
        this.passwordService.changePassword("OldPassword");

        this.clickOn("#insertedPassword").write("OldPassword");
        this.sleep(1000);

        this.clickOn("#btnLogin");
        this.sleep(1000);

    }


    @Test
    public void test2_PasswordMissing(){
        System.out.println("--- TEST 2: NON E' PRESENTE NESSUNA PASSWORD NEL DATABASE ---");

        //nel database non ho inserito nessuna password

        this.clickOn("#insertedPassword").write("OldPassword");
        this.sleep(1000);

        this.clickOn("#btnLogin");
        this.sleep(1000);

        FxAssert.verifyThat("Non è presente alcuna password nel database", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(1000);
    }


    @Test
    public void test3_WrongPassword(){
        System.out.println("--- TEST 3: LA PASSWORD INSERITA E' ERRATA ---");

        //inserisco una password nel database
        this.passwordService.changePassword("OldPassword");

        this.clickOn("#insertedPassword").write("Old");
        this.sleep(1000);

        this.clickOn("#btnLogin");
        this.sleep(1000);

        FxAssert.verifyThat("La password inserita non è corretta.", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(1000);
    }


}
