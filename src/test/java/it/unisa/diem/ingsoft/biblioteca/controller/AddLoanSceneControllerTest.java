package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddLoanSceneControllerTest extends ApplicationTest {

    @Mock private LoanService loanService;
    @Mock private UserService userService;
    @Mock private BookService bookService;

    private AddLoanSceneController controller;

    private void slowExecution(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/ingsoft/biblioteca/view/AddLoanScene.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setAddLoanServices(loanService, userService, bookService);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    public void test1_SearchUserFound() {
        System.out.println("--- TEST 1: RICERCA UTENTE (TROVATO) ---");
        String matricola = "0612708994";

        when(userService.existsById(matricola)).thenReturn(true);

        clickOn("#userMatricolaField").write(matricola);
        clickOn("#btnSearchUser");

        verify(userService).existsById(matricola);

        FxAssert.verifyThat("#userMatricolaField", (TextField t) -> t.isDisabled());
        sleep(2000);
    }

    @Test
    public void test2_SearchUserNotFound() {
        System.out.println("--- TEST 2: RICERCA UTENTE (NON TROVATO) ---");
        String matricola = "0612708994";

        when(userService.existsById(matricola)).thenReturn(false);

        clickOn("#userMatricolaField").write(matricola);
        clickOn("#btnSearchUser");

        verify(userService).existsById(matricola);

        FxAssert.verifyThat("#userMatricolaField", (TextField t) -> !t.isDisabled());

        sleep(2000);
        type(KeyCode.ENTER);
    }

}
