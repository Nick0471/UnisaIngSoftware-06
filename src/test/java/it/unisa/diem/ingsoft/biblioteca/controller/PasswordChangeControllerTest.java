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

import static it.unisa.diem.ingsoft.biblioteca.Views.EDIT_PASSWORD_PATH;


public class PasswordChangeControllerTest extends ApplicationTest {

    private AuthService passwordService;

    @Override
    public void start(Stage stage) {
        Database db = Database.inMemory();

        this.passwordService = new DatabaseAuthService(db);

        ServiceRepository serviceRepository = new ServiceRepository(this.passwordService, null, null, null);

        FXMLLoader loader = Scenes.setupLoader(EDIT_PASSWORD_PATH, serviceRepository);
        Parent root = loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Cambia PAssword");
        stage.setScene(scene);
        stage.show();

        this.passwordService.changePassword("OldPassword");
    }


    @Test
    public void test1_ChangePasswordSuccess() {
        System.out.println("--- TEST 1: CAMBIO PASSWORD CON SUCCESSO---");

        this.clickOn("#newPassword").write("NewPass123");
        this.clickOn("#newPasswordConfirm").write("NewPass123");
        this.sleep(1000);

        this.clickOn("#btnUpdate");
        this.sleep(1000);

    }




    @Test
    public void test2_NewPasswordsMismatch() {
        System.out.println("--- TEST 2: LE DUE PASSWORD NON COINCIDONO ---");

        this.clickOn("#newPassword").write("New");
        this.clickOn("#newPasswordConfirm").write("NewPass");
        this.sleep(1000);

        this.clickOn("#btnUpdate");
        this.sleep(1000);

        FxAssert.verifyThat("Le due password non coincidono.", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(1000);
    }


    @Test
    public void test3_NewPasswordLengthError() {
        System.out.println("--- TEST 3: LUNGHEZZA PASSWORD ERRATA (TROPPO CORTA) ---");

        this.clickOn("#currentPassword").write("OldPassword");

        this.clickOn("#newPassword").write("12345");
        this.clickOn("#newPasswordConfirm").write("12345");
        this.sleep(1000);

        this.clickOn("#btnUpdate");
        this.sleep(1000);

        FxAssert.verifyThat("La nuova password deve essere da 6 a 10 caratteri.", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(1000);
    }


    @Test
    public void test5_AbortOperation() {
        System.out.println("--- TEST 5: ANNULLA OPERAZIONE ---");

        this.clickOn("#btnReturn");
        this.sleep(1000);
    }
}

