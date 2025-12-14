package it.unisa.diem.ingsoft.biblioteca.controller;

import static it.unisa.diem.ingsoft.biblioteca.Views.USER_PATH;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
import it.unisa.diem.ingsoft.biblioteca.model.Loan;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;


public class UserSceneControllerTest extends ApplicationTest {

    private UserService userService;
    private LoanService loanService;
    private BookService bookService;

    @Override
    public void start(Stage stage) {
        Database db = Database.inMemory();

        this.userService = new DatabaseUserService(db);
        this.bookService = new DatabaseBookService(db);
        this.loanService = new DatabaseLoanService(this.userService, this.bookService , db);


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

            //RICERCA PER MATRICOLA
            System.out.println("Cerco Matricola: 1234567890");
            this.clickOn("#searchType").clickOn("Matricola ");
            this.clickOn("#searchField").write("1234567890");
            this.sleep(1000);
            FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == 1);

            this.resetSearchField(); // Resetto per il prossimo test


            //RICERCA PER EMAIL
            System.out.println("Cerco Email parziale: rossi");
            this.clickOn("#searchType").clickOn("Email ");
            this.clickOn("#searchField").write("rossi");
            this.sleep(1000);

            FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == 2);

            this.resetSearchField();



            //RICERCA PER NOME E COGNOME
            System.out.println("Cerco Cognome: Rossi e Nome: Mario");
            this.clickOn("#searchType").clickOn("Cognome ");

            this.sleep(1000);

            // Aspetta che il secondo campo sia visibile prima di interagire
            FxAssert.verifyThat("#searchFieldSecondary", NodeMatchers.isVisible());

            // Scrivo nel campo Cognome
            this.clickOn("#searchField").write("Rossi");
            this.sleep(1000); // Do tempo al filtro di agire


            // Scrivo nel campo Nome
            this.clickOn("#searchFieldSecondary").write("Mario");
            this.sleep(1000); // Do tempo al filtro di agire


            FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == 1);

            // Rimetto il filtro su Matricola per nascondere il secondo campo e resettare
            this.clickOn("#searchType").clickOn("Matricola ");
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
        this.clickOn("OK");
        this.sleep(1000);

        FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == initialSize - 1);
    }




    @Test
    public void test5_RemoveUserError(){
        System.out.println("--- TEST 5: RIMOZIONE UTENTE FALLITA ---");

        int initialSize = this.lookup("#userTable").queryTableView().getItems().size();

        System.out.println("Non seleziono un utente");
        System.out.println("Clicco su Rimuovi");
        this.clickOn("#btnRemove");
        this.clickOn("OK");
        this.sleep(1000);

        FxAssert.verifyThat("#userTable", (TableView<Loan> t) -> t.getItems().size() == initialSize);
    }





    @Test
    public void test6_RemoveUserWithActiveLoan() throws Exception {
        System.out.println("--- TEST: PREVENZIONE RIMOZIONE UTENTE CON PRESTITI ---");


        // RECUPERO DI UN UTENTE DAL DATABASE
        User user = this.userService.getById("1122334455").get();


        // CREAZIONE LIBRO E PRESTITO ATTIVO
        Book book = new Book("2222222222222", "Titolo", "autore", 2020,2,2, "genere", "descrizione");
        this.bookService.add(book);
        LocalDate dataInizio = LocalDate.now();
        LocalDate dataFine = LocalDate.now().plusMonths(1);
        this.loanService.register(user.getId(), book.getIsbn(), dataInizio, dataFine);


        // CREAZIONE LIBRO E PRESTITO SCADUTO PER LO STESSO UTENTE
        Book bookScaduto = new Book("3333333333333", "Libro Vecchio", "Autore X", 2015, 1, 1, "Storico", "Desc");
        this.bookService.add(bookScaduto);
        LocalDate dataInizioScaduto = LocalDate.now().minusMonths(3);
        LocalDate dataFineScaduta = LocalDate.now().minusMonths(1); // La data di fine è passata -> SCADUTO
        this.loanService.register(user.getId(), bookScaduto.getIsbn(), dataInizioScaduto, dataFineScaduta);


        //VISUALIZZAZIONE ACCOUNT UTENTE
        System.out.println("Seleziono un utente");
        this.clickOn("1122334455");

        System.out.println("Clicco su Account Utente");
        this.clickOn("#btnUserProfile");
        this.sleep(1000);

        FxAssert.verifyThat("Account Utente", NodeMatchers.isVisible());

        System.out.println("Chiudo finestra profilo...");
        this.clickOn("#btnClose");
        this.sleep(1000);




        //RIMOZIONE UTENTE CON PRESTITI ATTIVI
        // Conto quanti utenti ci sono prima dell'operazione
        int initialSize = this.lookup("#userTable").queryTableView().getItems().size();

        System.out.println("Seleziono l'utente con prestiti attivi: ");
        this.clickOn("1122334455"); // Seleziono la riga tramite la matricola
        this.sleep(500);

        System.out.println("Clicco su Rimuovi");
        this.clickOn("#btnRemove");
        this.sleep(500); // Aspetto che appaia l'Alert


        FxAssert.verifyThat("Non puoi rimuovere un utente che ha ancora prestiti attivi", NodeMatchers.isVisible());

        this.clickOn("OK");
        this.sleep(500);

        // Verifico che la dimensione della tabella NON sia cambiata
        FxAssert.verifyThat("#userTable", (TableView<User> t) -> t.getItems().size() == initialSize);

    }




    @Test
    public void test7_ModifyUser() {
        System.out.println("--- TEST 5: MODIFICA UTENTE ---");

        System.out.println("Seleziono un utente");
        this.clickOn("1234567890");
        this.sleep(500);

        System.out.println("Clicco su Modifica");
        this.clickOn("#btnModify");
        this.sleep(1000);

        FxAssert.verifyThat("Modifica Utente", NodeMatchers.isVisible());

        System.out.println("Chiudo scena ...");
        this.clickOn("#btnCancel");
        this.sleep(1000);
    }



    @Test
    public void test8_AddUser() {
        System.out.println("--- TEST 6: AGGIUNTA UTENTE ---");

        System.out.println("Clicco su Aggiungi");
        this.clickOn("#btnAdd");
        this.sleep(1000);

        FxAssert.verifyThat("Aggiungi Utente", NodeMatchers.isVisible());

        System.out.println("Chiudo scena...");
        this.clickOn("#btnCancel");
        this.sleep(1000);
    }



    @Test
    public void test9_NavigationHome() {
        System.out.println("--- TEST 8: NAVIGAZIONE HOME ---");

        this.sleep(1000);
        System.out.println("Clicco Home...");
        this.clickOn("#btnHome");
        this.sleep(2000);

        FxAssert.verifyThat("Biblioteca Universitaria", NodeMatchers.isVisible());
    }
}


