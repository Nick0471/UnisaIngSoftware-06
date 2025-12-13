package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.service.DatabasePasswordService;

import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;
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
import static it.unisa.diem.ingsoft.biblioteca.Views.USER_PATH;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordChangeControllerTest extends ApplicationTest {

    private PasswordService passwordService;

    @Override
    public void start(Stage stage) {
        Database db = Database.inMemory();

        this.passwordService = new DatabasePasswordService(db);

        ServiceRepository serviceRepository = new ServiceRepository(this.passwordService, null, null, null);

        FXMLLoader loader = Scenes.setupLoader(EDIT_PASSWORD_PATH, serviceRepository);
        Parent root = loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Cambia PAssword");
        stage.setScene(scene);
        stage.show();

        this.passwordService.change("OldPassword");
    }


    @Test
    public void test1_ChangePasswordSuccess() {
        System.out.println("--- TEST 1: CAMBIO PASSWORD CON SUCCESSO---");

        this.clickOn("#currentPassword").write("OldPassword");

        this.clickOn("#newPassword").write("NewPass123");
        this.clickOn("#newPasswordConfirm").write("NewPass123");
        this.sleep(500);

        this.clickOn("#btnUpdate");
        this.sleep(500);

        FxAssert.verifyThat("Password aggiornata correttamente.", NodeMatchers.isVisible());

        this.clickOn("OK");
    }


    @Test
    public void test2_OldWrongPassword() {
        System.out.println("--- TEST 2: VECCHIA PASSWORD ERRATA ---");

        this.clickOn("#currentPassword").write("OldDDD");

        this.clickOn("#newPassword").write("NewPass123");
        this.clickOn("#newPasswordConfirm").write("NewPass123");
        this.sleep(500);

        this.clickOn("#btnUpdate");
        this.sleep(500);

        FxAssert.verifyThat("La password vecchia inserita non è corretta.", NodeMatchers.isVisible());

        this.clickOn("OK");

    }


    @Test
    public void test3_NewPasswordsMismatch() {
        System.out.println("--- TEST 3: LE DUE PASSWORD NON COINCIDONO ---");

        this.clickOn("#currentPassword").write("OldPassword");

        this.clickOn("#newPassword").write("New");
        this.clickOn("#newPasswordConfirm").write("NewPass");
        this.sleep(500);

        this.clickOn("#btnUpdate");
        this.sleep(500);

        FxAssert.verifyThat("Le due password non coincidono.", NodeMatchers.isVisible());

        this.clickOn("OK");
    }


    @Test
    public void test4_NewPasswordLengthError() {
        System.out.println("--- TEST 4: LUNGHEZZA PASSWORD ERRATA (TROPPO CORTA) ---");

        this.clickOn("#currentPassword").write("OldPassword");

        this.clickOn("#newPassword").write("12345");
        this.clickOn("#newPasswordConfirm").write("12345");
        this.sleep(500);

        this.clickOn("#btnUpdate");
        this.sleep(500);

        FxAssert.verifyThat("La nuova password deve essere da 6 a 10 caratteri.", NodeMatchers.isVisible());

        this.clickOn("OK");
    }
}

