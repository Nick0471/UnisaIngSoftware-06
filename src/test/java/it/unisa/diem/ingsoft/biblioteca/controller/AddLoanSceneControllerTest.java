package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.exception.LoanException;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddLoanSceneControllerTest extends ApplicationTest {

    @Mock private LoanService loanService;
    @Mock private UserService userService;
    @Mock private BookService bookService;
    @Mock private ServiceRepository serviceRepository;

    private AddLoanSceneController controller;

    private void slowExecution(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/ingsoft/biblioteca/view/AddLoanScene.fxml"));
        Parent root = loader.load();
        controller = loader.getController();

        lenient().when(serviceRepository.getLoanService()).thenReturn(loanService);
        lenient().when(serviceRepository.getUserService()).thenReturn(userService);
        lenient().when(serviceRepository.getBookService()).thenReturn(bookService);
        controller.setServices(serviceRepository);stage.setScene(new Scene(root));

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

    @Test
    public void test3_SearchBookFound() {
        System.out.println("--- TEST 3: RICERCA LIBRO (TROVATO) ---");
        String isbn = "9780618391110";

        when(bookService.existsByIsbn(isbn)).thenReturn(true);

        clickOn("#isbnField").write(isbn);
        clickOn("#btnSearchBook");

        verify(bookService).existsByIsbn(isbn);

        FxAssert.verifyThat("#isbnField", (TextField t) -> t.isDisabled());
        sleep(2000);
    }

    @Test
    public void test4_SearchBookNotFound() {
        System.out.println("--- TEST 4: RICERCA LIBRO (NON TROVATO) ---");
        String isbn = "9780618391110";

        when(bookService.existsByIsbn(isbn)).thenReturn(false);

        clickOn("#isbnField").write(isbn);
        clickOn("#btnSearchBook");

        verify(bookService).existsByIsbn(isbn);

        FxAssert.verifyThat("#isbnField", (TextField t) -> !t.isDisabled());

        sleep(2000);
        type(KeyCode.ENTER);
    }

    @Test
    public void test5_InitializationDates() {
        System.out.println("--- TEST 5: VERIFICA DATE DEFAULT ---");
        DatePicker start = lookup("#loanDatePicker").query();
        DatePicker end = lookup("#returnDatePicker").query();

        assertEquals(LocalDate.now(), start.getValue());
        assertEquals(LocalDate.now().plusDays(30), end.getValue());
        sleep(2000);
    }

    @Test
    public void test6_ConfirmLoanDateError(){
        System.out.println("--- TEST 6: DATA FINE PRECEDENTE A INIZIO ---");

        DatePicker start = lookup("#loanDatePicker").query();
        DatePicker end = lookup("#returnDatePicker").query();

        interact(() -> {
            start.setValue(LocalDate.now());
            end.setValue(LocalDate.now().minusDays(10));
        });

        clickOn("#userMatricolaField").write("0612708994");
        clickOn("#isbnField").write("9780618391110");

        clickOn("#btnConfirm");

        try {
            verify(loanService, never()).register(anyString(), anyString(), any(), any());
        } catch (LoanException e) {
            e.printStackTrace();
        }

        sleep(2000);
        type(KeyCode.ENTER);
    }

    @Test
    public void test7_ConfirmLoanLimitExceeded() {
        System.out.println("--- TEST 7: LIMITE 3 PRESTITI RAGGIUNTO ---");
        String user = "0612708994";
        String isbn = "9780618391110";

        clickOn("#userMatricolaField").write(user);
        clickOn("#isbnField").write(isbn);

        when(userService.existsById(user)).thenReturn(true);
        when(loanService.countById(user)).thenReturn(3);

        clickOn("#btnConfirm");

        try {
            verify(loanService, never()).register(anyString(), anyString(), any(), any());
        } catch (LoanException e) {
            e.printStackTrace();
        }

        sleep(2000);
        type(KeyCode.ENTER);
    }

    @Test
    public void test8_ConfirmLoanCopiesExhausted() {
        System.out.println("--- TEST 8: COPIE DEL LIBRO ESAURITE ---");
        String user = "0612708994";
        String isbn = "9780618391110";

        clickOn("#userMatricolaField").write(user);
        clickOn("#isbnField").write(isbn);

        when(userService.existsById(user)).thenReturn(true);
        when(loanService.countById(user)).thenReturn(0);
        when(bookService.existsByIsbn(isbn)).thenReturn(true);
        when(bookService.countRemainingCopies(isbn)).thenReturn(0);

        clickOn("#btnConfirm");


        try {
            verify(loanService, never()).register(anyString(), anyString(), any(), any());
        } catch (LoanException e) {
            e.printStackTrace();
        }

        sleep(2000);
        type(KeyCode.ENTER);
    }

    @Test
    public void test9_ConfirmLoanSuccess() throws Exception {
        System.out.println("--- TEST 9: REGISTRAZIONE CORRETTA ---");
        String user = "0612708994";
        String isbn = "9780618391110";

        clickOn("#userMatricolaField").write(user);
        clickOn("#isbnField").write(isbn);

        when(userService.existsById(user)).thenReturn(true);
        when(loanService.countById(user)).thenReturn(0);
        when(bookService.existsByIsbn(isbn)).thenReturn(true);
        when(bookService.countRemainingCopies(isbn)).thenReturn(5);

        clickOn("#btnConfirm");

        verify(loanService, times(1)).register(
                eq(user),
                eq(isbn),
                eq(LocalDate.now()),
                eq(LocalDate.now().plusDays(30))
        );

        sleep(2000);
        type(KeyCode.ENTER);
    }
}
