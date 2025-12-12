package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import static it.unisa.diem.ingsoft.biblioteca.Views.USER_PATH;


public class UserSceneControllerTest extends ApplicationTest {

    private UserService userService;
    private LoanService loanService;
    private BookService bookService;

    @Override
    public void start(Stage stage) {
        Database db = Database.inMemory();

        this.userService = new DatabaseUserService(db);
        this.loanService = new DatabaseLoanService(userService, bookService , db);
        this.bookService = new DatabaseBookService(db);

        ServiceRepository serviceRepository = new ServiceRepository(null, this.userService, this.bookService, this.loanService);

        try {
            this.setUp();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FXMLLoader loader = Scenes.setupLoader(USER_PATH, serviceRepository);
        Parent root = loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Visualizzazione lista utenti");
        stage.setScene(scene);
        stage.show();
    }


    public void setUp() throws Exception {
        if (!this.userService.existsById("1234567890") && !this.userService.existsByEmail("b.altieri2@studenti.unisa.it"))
            this.userService.register(new User("1234567890", "b.altieri2@studenti.unisa.it", "Bianca", "Altieri"));


        if (!this.userService.existsById("0512103578") && !this.userService.existsByEmail("m.rossi1@studenti.unisa.it"))
            this.userService.register(new User("0512103578", "m.rossi1@studenti.unisa.it", "Mario", "Rossi"));

        if (!this.userService.existsById("1122334455") && !this.userService.existsByEmail("ale.rossi@studenti.unisa.it"))
            this.userService.register(new User("1122334455", "ale.rossi@studenti.unisa.it", "Alessandro", "Rossi"));

        if (!this.userService.existsById("AB12345678") && !this.userService.existsByEmail("g.verdi5@studenti.unisa.it"))
            this.userService.register(new User("AB12345678", "g.verdi5@studenti.unisa.it", "Giulia", "Verdi"));


        if (!this.userService.existsById("06127000XY") && !this.userService.existsByEmail("l.bianchi@studenti.unisa.it"))
            this.userService.register(new User("06127000XY", "l.bianchi@studenti.unisa.it", "Luca", "Bianchi"));


        if (!this.userService.existsById("0123456789") && !this.userService.existsByEmail("a.neri99@studenti.unisa.it"))
            this.userService.register(new User("0123456789", "a.neri99@studenti.unisa.it", "Anna", "Neri"));

        if (!this.userService.existsById("M123456789") && !this.userService.existsByEmail("f.esposito@studenti.unisa.it"))
            this.userService.register(new User("M123456789", "f.esposito@studenti.unisa.it", "Francesco", "Esposito"));
    }


    private void resetSearchField() {
        this.doubleClickOn("#searchField");
        this.push(KeyCode.CONTROL, KeyCode.A);
        this.push(KeyCode.DELETE);
        this.sleep(1000);

        FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == 7);

    }

    @Test
    public void test1_Initialization() {
        System.out.println("--- TEST 1: CARICAMENTO LISTA UTENTI ---");
        this.sleep(1000);
        // Verifica che ci siano 7 utenti caricati
        FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == 7);
    }

    @Test
    public void test2_Sorting() {
        System.out.println("--- TEST 2: ORDINAMENTO ---");
        this.sleep(500);

        System.out.println("Ordino per Cognome");
        this.clickOn("Cognome");
        this.sleep(1000);

        System.out.println("Ordino per Matricola");
        this.clickOn("Matricola");
        this.sleep(1000);
    }

    @Test
    public void test3_SearchFunctionality() {
        System.out.println("--- TEST 3: FILTRI DI RICERCA ---");

        // 1. Ricerca per Matricola
        System.out.println("Cerco Matricola: 1234567890");
        this.clickOn("#searchType").clickOn("Matricola");
        this.clickOn("#searchField").write("1234567890");
        this.sleep(1000);
        FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == 1);

        this.resetSearchField();

        // 2. Ricerca per Email
        System.out.println("Cerco Email parziale: rossi");
        this.clickOn("#searchType").clickOn("Email");
        this.clickOn("#searchField").write("rossi");
        this.sleep(1000);
        // Dovrebbe trovare sia m.rossi1 che ale.rossi
        FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == 2);

        this.resetSearchField();

        // 3. Ricerca Complessa (Cognome + Nome)
        System.out.println("Cerco Cognome: Rossi");
        this.clickOn("#searchType").clickOn("Cognome");
        // Nota: Quando si seleziona "Cognome", nel controller appare il campo #searchFieldSecondary

        this.clickOn("#searchField").write("Rossi"); // Scrivo nel campo Cognome
        this.sleep(500);
        // Ora filtro ulteriormente per Nome usando il secondo campo che è apparso
        this.clickOn("#searchFieldSecondary").write("Mario");
        this.sleep(1000);

        FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == 1);
        FxAssert.verifyThat("#userTable", (TableView<User> t) -> "0512103578".equals(t.getItems().get(0).getId()));

        // Pulizia manuale del secondo campo per evitare problemi nei test successivi
        this.doubleClickOn("#searchFieldSecondary").push(KeyCode.DELETE);
        this.resetSearchField();
    }

    @Test
    public void test4_RemoveUser() {
        System.out.println("--- TEST 4: RIMOZIONE UTENTE ---");

        int initialSize = this.lookup("#userTable").queryTableView().getItems().size();

        System.out.println("Seleziono l'utente con matricola AB12345678");
        this.clickOn("AB12345678"); // TestFX clicca sulla cella con questo testo
        this.sleep(1000);

        System.out.println("Clicco su Rimuovi");
        this.clickOn("#btnRemove");
        this.sleep(1000);

        // Chiudo l'eventuale popup di conferma/successo (se presente e bloccante, altrimenti TestFX continua)
        // Nel tuo controller appare un popup "Utente rimosso correttamente".
        // TestFX preme SPACE/ENTER per chiudere gli alert standard, ma il tuo è una nuova Stage.
        // Poiché è un popup non modale o gestito custom, verifichiamo solo la tabella.

        FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == initialSize - 1);
    }

    @Test
    public void test5_ModifyUser() {
        System.out.println("--- TEST 5: MODIFICA UTENTE ---");

        System.out.println("Seleziono un utente");
        this.clickOn("1234567890");
        this.sleep(500);

        System.out.println("Clicco su Modifica");
        this.clickOn("#btnModify");
        this.sleep(1000);

        // Verifica che la modale si sia aperta controllando il titolo o un elemento
        FxAssert.verifyThat("Modifica Utente", NodeMatchers.isVisible());

        System.out.println("Chiudo modale...");
        this.clickOn("#btnCancel");
        this.sleep(1000);
    }

    @Test
    public void test6_AddUser() {
        System.out.println("--- TEST 6: AGGIUNTA UTENTE ---");

        System.out.println("Clicco su Aggiungi");
        this.clickOn("#btnAdd");
        this.sleep(1000);

        FxAssert.verifyThat("Aggiungi Utente", NodeMatchers.isVisible());

        System.out.println("Chiudo modale...");
        this.clickOn("#btnCancel");
        this.sleep(1000);
    }

    @Test
    public void test7_UserProfile() {
        System.out.println("--- TEST 7: PROFILO UTENTE ---");

        System.out.println("Seleziono un utente");
        this.clickOn("1234567890");

        System.out.println("Clicco su Account Utente");
        this.clickOn("#btnUserProfile");
        this.sleep(1000);

        FxAssert.verifyThat("Account Utente", NodeMatchers.isVisible());

        System.out.println("Chiudo finestra profilo...");
        this.clickOn("#btnClose");
        this.sleep(1000);
    }

    @Test
    public void test8_NavigationHome() {
        System.out.println("--- TEST 8: NAVIGAZIONE HOME ---");

        this.sleep(1000);
        System.out.println("Clicco Home...");
        this.clickOn("#btnHome");
        this.sleep(2000);

        // Verifica che siamo tornati alla Homepage
        FxAssert.verifyThat("Biblioteca Universitaria", NodeMatchers.isVisible());
    }
}


