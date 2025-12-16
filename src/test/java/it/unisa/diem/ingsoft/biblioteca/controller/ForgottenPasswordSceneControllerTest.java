package it.unisa.diem.ingsoft.biblioteca.controller;

import static it.unisa.diem.ingsoft.biblioteca.Views.FORGOTTEN_PASSWORD_PATH;

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


public class ForgottenPasswordSceneControllerTest extends ApplicationTest {

    private AuthService authService;

    @Override
    public void start(Stage stage) {
        Database db = Database.inMemory();

        this.authService = new DatabaseAuthService(db);

        //simulo le risposte di default salvate nel database
        this.authService.setup("admin", "R1", "R2", "R3");

        ServiceRepository serviceRepository = new ServiceRepository(this.authService, null, null, null);

        FXMLLoader loader = Scenes.setupLoader(FORGOTTEN_PASSWORD_PATH, serviceRepository);
        Parent root = loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Recupero Password");
        stage.setScene(scene);
        stage.show();
    }


    @Test
    public void test1_VerifyAnswersSuccess() {
        System.out.println("--- TEST 1: VERIFICA RISPOSTE CON SUCCESSO ---");

        this.clickOn("#answer1Field").write("R1");
        this.clickOn("#answer2Field").write("R2");
        this.clickOn("#answer3Field").write("R3");
        this.sleep(1000);

        this.clickOn("#btnVerify");
        this.sleep(1000);


    }


    @Test
    public void test2_WrongAnswers() {
        System.out.println("--- TEST 2: RISPOSTE ERRATE ---");

        this.clickOn("#answer1Field").write("Sbagliata");
        this.clickOn("#answer2Field").write("R2");
        this.clickOn("#answer3Field").write("R3");
        this.sleep(1000);

        this.clickOn("#btnVerify");
        this.sleep(1000);

        FxAssert.verifyThat("I campi non sono corretti", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(1000);
    }


    @Test
    public void test3_EmptyFieldsError() {
        System.out.println("--- TEST 3: CAMPI VUOTI ---");

        this.sleep(1000);

        this.clickOn("#btnVerify");
        this.sleep(1000);

        FxAssert.verifyThat("Completa tutti i campi obligatori", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(1000);
    }


    @Test
    public void test4_AbortOperation() {
        System.out.println("--- TEST 4: ANNULLA OPERAZIONE ---");

        this.clickOn("#btnCancel");
        this.sleep(1000);
    }
}
