package it.unisa.diem.ingsoft.biblioteca.controller;

import static it.unisa.diem.ingsoft.biblioteca.Views.EDIT_PASSWORD_PATH;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

public class EditPasswordControllerTest extends ApplicationTest {

    private AuthService authService;

    @Override
    public void start(Stage stage) {
        Database db = Database.inMemory();

        this.authService = new DatabaseAuthService(db);

        ServiceRepository serviceRepository = new ServiceRepository(this.authService, null, null, null);

        FXMLLoader loader = Scenes.setupLoader(EDIT_PASSWORD_PATH, serviceRepository);
        Parent root = loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Cambio Password");
        stage.setScene(scene);
        stage.show();

        if (!authService.isPresent()) {
            authService.setup("admin", "GRUPPO06", "INGEGNERIA SOFTWARE", "NICOLA CAPUANO");
        }
    }

    @Test
    public void test1_ChangePasswordSuccess() {
        System.out.println("--- TEST 1: CAMBIO PASSWORD CON SUCCESSO ---");

        this.clickOn("#currentPassword").write("admin");

        this.clickOn("#newPassword").write("Tolkien");
        this.clickOn("#newPasswordConfirm").write("Tolkien");
        this.sleep(500);

        this.clickOn("#btnUpdate");
        this.sleep(1000);

        boolean passwordChanged = this.authService.checkPassword("Tolkien");
        assertTrue(passwordChanged, "La password nel DB dovrebbe essere aggiornata a 'Tolkien'");
    }

    @Test
    public void test2_NewPasswordsMismatch() {
        System.out.println("--- TEST 2: LE DUE PASSWORD NON COINCIDONO ---");

        this.clickOn("#currentPassword").write("admin");

        this.clickOn("#newPassword").write("Tolkien");
        this.clickOn("#newPasswordConfirm").write("tolkien");
        this.sleep(500);

        this.clickOn("#btnUpdate");
        this.sleep(500);

        FxAssert.verifyThat("Le due password non coincidono.", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(500);
    }

    @Test
    public void test3_NewPasswordLengthError() {
        System.out.println("--- TEST 3: LUNGHEZZA PASSWORD ERRATA (TROPPO CORTA) ---");

        this.clickOn("#currentPassword").write("admin");

        this.clickOn("#newPassword").write("ciao");
        this.clickOn("#newPasswordConfirm").write("ciao");
        this.sleep(500);

        this.clickOn("#btnUpdate");
        this.sleep(500);

        FxAssert.verifyThat("La nuova password deve essere da 6 a 10 caratteri.", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(500);
    }

    @Test
    public void test4_OldPasswordWrong() {
        System.out.println("--- TEST 4: VECCHIA PASSWORD ERRATA ---");

        this.clickOn("#currentPassword").write("Tolkien");

        this.clickOn("#newPassword").write("THE_HOBBIT");
        this.clickOn("#newPasswordConfirm").write("THE_HOBBIT");
        this.sleep(500);

        this.clickOn("#btnUpdate");
        this.sleep(500);

        FxAssert.verifyThat("La password vecchia inserita non è corretta.", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(500);
    }

    @Test
    public void test5_AbortOperation() {
        System.out.println("--- TEST 5: ANNULLA OPERAZIONE ---");

        this.clickOn("#btnReturn");
        this.sleep(500);

        FxAssert.verifyThat("Bentornato!", NodeMatchers.isVisible());
    }

    @Test
    public void test6_GoToSecurityQuestions() {
        System.out.println("--- TEST 6: VAI A RISPOSTE SICUREZZA ---");

        this.clickOn("#btnUpdateQuestions");
        this.sleep(1000);

        FxAssert.verifyThat("Gestione Sicurezza", NodeMatchers.isVisible());
    }
}