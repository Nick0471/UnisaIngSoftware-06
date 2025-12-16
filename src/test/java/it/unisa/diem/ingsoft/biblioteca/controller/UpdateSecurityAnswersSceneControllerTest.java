package it.unisa.diem.ingsoft.biblioteca.controller;

import static it.unisa.diem.ingsoft.biblioteca.Views.UPDATE_ANSWERS_PATH;
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

public class UpdateSecurityAnswersSceneControllerTest extends ApplicationTest {

    private AuthService authService;

    @Override
    public void start(Stage stage) {
        Database db = Database.inMemory();

        this.authService = new DatabaseAuthService(db);
        this.authService.setup("admin", "GRUPPO06", "INGEGNERIA SOFTWARE", "NICOLA CAPUANO");

        ServiceRepository serviceRepository = new ServiceRepository(this.authService, null, null, null);
        FXMLLoader loader = Scenes.setupLoader(UPDATE_ANSWERS_PATH, serviceRepository);
        Parent root = loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Modifica Risposte di Sicurezza");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void test1_UpdateAllAnswers() {
        System.out.println("--- TEST 1: AGGIORNAMENTO DI TUTTE LE RISPOSTE ---");

        this.clickOn("#answer1Field").write("Nuova1");
        this.clickOn("#answer2Field").write("Nuova2");
        this.clickOn("#answer3Field").write("Nuova3");
        this.sleep(500);

        this.clickOn("#btnSave");
        this.sleep(1000);
        this.clickOn("OK");

        assertTrue(this.authService.checkAnswer("Nuova1", 1), "La risposta 1 non è stata aggiornata correctly");
        assertTrue(this.authService.checkAnswer("Nuova2", 2), "La risposta 2 non è stata aggiornata correctly");
        assertTrue(this.authService.checkAnswer("Nuova3", 3), "La risposta 3 non è stata aggiornata correctly");
    }

    @Test
    public void test2_UpdateSingleAnswer() {
        System.out.println("--- TEST 2: AGGIORNAMENTO DI UNA SOLA RISPOSTA ---");

        this.clickOn("#answer2Field").write("Nuova");
        this.sleep(500);

        this.clickOn("#btnSave");
        this.sleep(1000);
        this.clickOn("OK");

        assertTrue(this.authService.checkAnswer("Nuova", 2), "La risposta 2 dovrebbe essere cambiata");
        assertTrue(this.authService.checkAnswer("GRUPPO06", 1), "La risposta 1 dovrebbe essere rimasta invariata");
        assertTrue(this.authService.checkAnswer("NICOLA CAPUANO", 3), "La risposta 3 dovrebbe essere rimasta invariata");
    }

    @Test
    public void test3_EmptyFieldsError() {
        System.out.println("--- TEST 3: TENTATIVO DI SALVATAGGIO CAMPI VUOTI ---");

        this.sleep(500);

        this.clickOn("#btnSave");
        this.sleep(1000);

        FxAssert.verifyThat("Compila almeno un campo per aggiornare le risposte.", NodeMatchers.isVisible());

        this.clickOn("OK");
    }

    @Test
    public void test4_CancelOperation() {
        System.out.println("--- TEST 4: ANNULLA OPERAZIONE ---");

        this.clickOn("#answer1Field").write("Nuova");
        this.sleep(500);

        this.clickOn("#btnCancel");
        this.sleep(1000);

        assertTrue(this.authService.checkAnswer("GRUPPO06", 1), "La risposta non doveva cambiare dopo l'annullamento");
        FxAssert.verifyThat("Modifica Credenziali", NodeMatchers.isVisible());
    }
}