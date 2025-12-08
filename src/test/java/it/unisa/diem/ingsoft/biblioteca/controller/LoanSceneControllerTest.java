package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanSceneControllerTest extends ApplicationTest {

    @Mock private LoanService loanService;
    @Mock private UserService userService;
    @Mock private BookService bookService;

    private LoanSceneController controller;

    // Lista MUTABILE per simulare la cancellazione
    private List<Loan> dynamicLoans;

    private void slowExecution(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unisa/diem/ingsoft/biblioteca/view/LoanScene.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    public void setupData() {
        // Creiamo una ArrayList modificabile con 7 elementi
        dynamicLoans = new ArrayList<>(Arrays.asList(
                // 3 SCADUTI (Rosso)
                new Loan("ISBN-001", "0612700001", LocalDate.now().minusDays(60), LocalDate.now().minusDays(30)),
                new Loan("ISBN-002", "0612700002", LocalDate.now().minusDays(50), LocalDate.now().minusDays(20)),
                new Loan("ISBN-003", "0612700003", LocalDate.now().minusDays(40), LocalDate.now().minusDays(10)),
                // 4 ATTIVI
                new Loan("ISBN-004", "0612700004", LocalDate.now().minusDays(10), LocalDate.now().plusDays(20)),
                new Loan("ISBN-005", "0612700005", LocalDate.now().minusDays(5),  LocalDate.now().plusDays(25)),
                new Loan("ISBN-006", "0612700006", LocalDate.now().minusDays(2),  LocalDate.now().plusDays(28)),
                new Loan("ISBN-007", "0612700007", LocalDate.now().minusDays(1),  LocalDate.now().plusDays(30))
        ));
    }

    @Test
    public void test1_InitializationAndRedRows() {
        System.out.println("--- TEST 1: CARICAMENTO E COLORI ---");
        when(loanService.getAll()).thenReturn(dynamicLoans);
        interact(() -> controller.setLoanServices(loanService, userService, bookService));

        slowExecution(1000);

        // Verifica numero righe
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 7);

        // Evidenzia i 3 scaduti
        System.out.println("Controllo visivo righe rosse...");
        moveTo("0612700001"); slowExecution(800);
        moveTo("0612700002"); slowExecution(800);
        moveTo("0612700003"); slowExecution(800);
    }

    @Test
    public void test2_Sorting() {
        System.out.println("--- TEST 2: ORDINAMENTO ---");
        when(loanService.getAll()).thenReturn(dynamicLoans);
        interact(() -> controller.setLoanServices(loanService, userService, bookService));
        slowExecution(1000);

        System.out.println("Ordino per Scadenza...");
        clickOn("Scadenza");
        slowExecution(1500);

        System.out.println("Ordino per ISBN...");
        clickOn("ISBN Libro");
        slowExecution(1500);
    }

    @Test
    public void test3_SearchFunctionality() {
        System.out.println("--- TEST 3: FILTRI DI RICERCA ---");
        when(loanService.getAll()).thenReturn(dynamicLoans);

        // Mock Dinamico per i filtri
        lenient().when(loanService.getByUserId(anyString())).thenAnswer(inv -> {
            String arg = inv.getArgument(0);
            return dynamicLoans.stream().filter(l -> l.getUserId().contains(arg)).toList();
        });

        lenient().when(loanService.getByBookIsbn(anyString())).thenAnswer(inv -> {
            String arg = inv.getArgument(0);
            return dynamicLoans.stream().filter(l -> l.getBookIsbn().contains(arg)).toList();
        });

        interact(() -> controller.setLoanServices(loanService, userService, bookService));
        slowExecution(1000);

        // Cerca Matricola
        System.out.println("Cerco matricola: 0612700003");
        clickOn("#searchType").clickOn("Matricola");
        clickOn("#searchField").write("0612700003");
        slowExecution(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 1);

        // Reset
        doubleClickOn("#searchField").push(KeyCode.DELETE);
        slowExecution(1000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 7);

        // Cerca ISBN
        System.out.println("Cerco ISBN: ISBN-004");
        clickOn("#searchType").clickOn("ISBN");
        clickOn("#searchField").write("ISBN-004");
        slowExecution(2000);
        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 1);
    }

    @Test
    public void test4_ReturnLoan_RowDisappears() throws Exception {
        System.out.println("--- TEST 4: RESTITUZIONE E AGGIORNAMENTO TABELLA ---");

        // 1. Setup Iniziale
        when(loanService.getAll()).thenReturn(dynamicLoans);

        // 2. TRUCCO IMPORTANTE: Quando viene chiamato complete(), rimuoviamo l'elemento dalla lista
        doAnswer(invocation -> {
            String userId = invocation.getArgument(0);
            String isbn = invocation.getArgument(1);

            // Rimuoviamo l'elemento dalla lista "mock"
            dynamicLoans.removeIf(l -> l.getUserId().equals(userId) && l.getBookIsbn().equals(isbn));
            return null;
        }).when(loanService).complete(anyString(), anyString(), any(LocalDate.class));

        interact(() -> controller.setLoanServices(loanService, userService, bookService));

        slowExecution(1500);
        int initialSize = lookup("#loanTable").queryTableView().getItems().size();
        System.out.println("Righe iniziali: " + initialSize);

        // 3. Seleziono la riga da rimuovere (es. 0612700006)
        System.out.println("Seleziono il prestito di 0612700006...");
        clickOn("0612700006");
        slowExecution(1000);

        // 4. Clicco restituisci
        System.out.println("Clicco su Restituisci...");
        clickOn("#btnReturn");

        // Aspetto che la tabella si aggiorni
        slowExecution(2000);

        // 5. Verifica che la riga sia sparita
        int finalSize = lookup("#loanTable").queryTableView().getItems().size();
        System.out.println("Righe finali: " + finalSize);

        if (finalSize != initialSize - 1) {
            throw new AssertionError("ERRORE GRAFICO: La riga non è stata rimossa dalla tabella!");
        } else {
            System.out.println("SUCCESSO: La riga è stata rimossa correttamente.");
        }

        verify(loanService).complete(eq("0612700006"), eq("ISBN-006"), any(LocalDate.class));
    }

    @Test
    public void test5_OpenModal() {
        System.out.println("--- TEST 5: APERTURA MODALE ---");
        when(loanService.getAll()).thenReturn(dynamicLoans);
        interact(() -> controller.setLoanServices(loanService, userService, bookService));

        javafx.stage.Window mainWindow = lookup("#loanTable").query().getScene().getWindow();

        System.out.println("Apro modale...");
        clickOn("#btnAdd");
        slowExecution(1500);

        FxAssert.verifyThat("#userMatricolaField", (javafx.scene.control.TextField t) -> t.isVisible());

        System.out.println("Chiudo modale...");
        clickOn("#btnCancel");
        slowExecution(1000);

        interact(() -> mainWindow.requestFocus());
    }

    @Test
    public void test6_NavigationHome() {
        System.out.println("--- TEST 6: NAVIGAZIONE HOME ---");
        when(loanService.getAll()).thenReturn(dynamicLoans);
        interact(() -> controller.setLoanServices(loanService, userService, bookService));

        slowExecution(1000);
        System.out.println("Clicco Home...");
        clickOn("#btnHome");
        slowExecution(2000);

        FxAssert.verifyThat("#btnBook", (javafx.scene.control.Button b) -> b.isVisible());
        System.out.println("Homepage raggiunta.");
    }
}