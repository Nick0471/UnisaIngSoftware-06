package it.unisa.diem.ingsoft.biblioteca.controller;

import static it.unisa.diem.ingsoft.biblioteca.Views.NEW_PASSWORD_PATH;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.service.AuthService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseAuthService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NewPasswordSceneControllerTest extends ApplicationTest {

    private AuthService authService;

    @Override
    public void start(Stage stage) {
        Database db = Database.inMemory();

        this.authService = new DatabaseAuthService(db);

        //simulo le risposte di default salvate nel database
        this.authService.setup("oldPass", "R1", "R2", "R3");

        ServiceRepository serviceRepository = new ServiceRepository(this.authService, null, null, null);

        FXMLLoader loader = Scenes.setupLoader(NEW_PASSWORD_PATH, serviceRepository);
        Parent root = loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Nuova Password");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void test1_UpdatePasswordSuccess() {
        System.out.println("--- TEST 1: AGGIORNAMENTO PASSWORD CON SUCCESSO ---");

        this.clickOn("#newPassword").write("NewPass1");
        this.clickOn("#newPasswordConfirm").write("NewPass1");
        this.sleep(1000);

        this.clickOn("#btnUpdate");
        this.sleep(1000);


    }

    @Test
    public void test2_PasswordMismatch() {
        System.out.println("--- TEST 2: PASSWORD NON COINCIDENTI ---");

        this.clickOn("#newPassword").write("PasswordA");
        this.clickOn("#newPasswordConfirm").write("PasswordB");
        this.sleep(1000);

        this.clickOn("#btnUpdate");
        this.sleep(1000);

        FxAssert.verifyThat("Le due password non coincidono.", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(1000);
    }

    @Test
    public void test3_EmptyFields() {
        System.out.println("--- TEST 3: CAMPI VUOTI ---");

        this.sleep(1000);

        this.clickOn("#btnUpdate");
        this.sleep(1000);

        FxAssert.verifyThat("Compila tutti i campi obligatori", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(1000);
    }

    @Test
    public void test4_PasswordTooShort() {
        System.out.println("--- TEST 4: PASSWORD TROPPO CORTA ---");

        this.clickOn("#newPassword").write("123");
        this.clickOn("#newPasswordConfirm").write("123");
        this.sleep(1000);

        this.clickOn("#btnUpdate");
        this.sleep(1000);

        FxAssert.verifyThat("La nuova password deve essere da 6 a 10 caratteri.", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(1000);
    }
}
