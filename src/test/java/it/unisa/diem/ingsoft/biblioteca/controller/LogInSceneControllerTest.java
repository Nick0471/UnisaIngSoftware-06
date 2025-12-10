package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.exception.UnsetPasswordException;
import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.WindowMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class LogInSceneControllerTest extends ApplicationTest {

    @Mock
    private PasswordService passwordService;

    private LogInSceneController controller;

    /**
     * Metodo helper per rallentare l'esecuzione e rendere visibile il test
     */
    private void slowExecution(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/ingsoft/biblioteca/view/LogInScene.fxml"));
        loader.setControllerFactory(param -> new LogInSceneController(passwordService));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
        stage.toFront();
    }

    @Test
    public void testLoginSuccess() {
        System.out.println("--- TEST: Login Success ---");

        when(passwordService.check("passwordCorretta")).thenReturn(true);

        clickOn("#insertedPassword").write("passwordCorretta");
        slowExecution(500);

        clickOn("#btnLogin");
        waitForFxEvents();

        // Aspetta un attimo prima di chiudere il test (anche se qui non c'è popup)
        slowExecution(1000);

        verify(passwordService).check("passwordCorretta");

        try {
            Window window = window("POP-UP");
            assert false : "Non dovrebbe apparire alcun popup di errore in caso di successo";
        } catch (Exception e) {
            // Successo
        }
    }

    @Test
    public void testLoginFailure_WrongPassword() {
        System.out.println("--- TEST: Login Fallito (Password Errata) ---");

        when(passwordService.check("passwordErrata")).thenReturn(false);

        clickOn("#insertedPassword").write("passwordErrata");
        slowExecution(500);

        clickOn("#btnLogin");
        waitForFxEvents();

        // --- MODIFICA QUI: Rallenta per vedere il POP-UP ---
        System.out.println("Visualizzazione Pop-up Errore Password...");
        slowExecution(3000); // 3 secondi di attesa
        // --------------------------------------------------

        verify(passwordService).check("passwordErrata");

        Window popupWindow = window("POP-UP");
        FxAssert.verifyThat(popupWindow, WindowMatchers.isShowing());
        FxAssert.verifyThat("La password inserita non è corretta.", LabeledMatchers.hasText("La password inserita non è corretta."));
    }

    @Test
    public void testLoginFailure_UnsetPasswordException() {
        System.out.println("--- TEST: Login Fallito (Password Non Settata nel DB) ---");

        when(passwordService.check(anyString())).thenThrow(new UnsetPasswordException());

        clickOn("#insertedPassword").write("qualsiasiCosa");
        slowExecution(500);

        clickOn("#btnLogin");
        waitForFxEvents();


        System.out.println("Visualizzazione Pop-up Eccezione...");
        slowExecution(3000); // 3 secondi di attesa
        // --------------------------------------------------

        Window popupWindow = window("POP-UP");
        FxAssert.verifyThat(popupWindow, WindowMatchers.isShowing());
        FxAssert.verifyThat("Non è presente alcuna password nel database", LabeledMatchers.hasText("Non è presente alcuna password nel database"));
    }


}