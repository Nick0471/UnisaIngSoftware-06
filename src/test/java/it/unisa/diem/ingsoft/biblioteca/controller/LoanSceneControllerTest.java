package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.model.*;
import it.unisa.diem.ingsoft.biblioteca.service.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;

import java.time.LocalDate;

import static it.unisa.diem.ingsoft.biblioteca.Views.LOAN_PATH;

public class LoanSceneControllerTest extends ApplicationTest {
    private BookService bookService;
    private UserService userService;
    private LoanService loanService;

    private void slowExecution(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    @Override
    public void start(Stage stage){
        Database db = Database.inMemory();

        this.bookService = new DatabaseBookService(db);
        this.userService = new DatabaseUserService(db);
        this.loanService = new DatabaseLoanService(db);
        ServiceRepository serviceRepository = new ServiceRepository(null, this.userService, this.bookService, this.loanService);

        FXMLLoader loader = Scenes.setupLoader(LOAN_PATH, serviceRepository);
        Parent root =  loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Visualizzazione prestiti");
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() throws Exception{
        for(int i = 1; i <= 7; i++) {
            String matricola = "061270000" + i;
            if(!this.userService.existsById(matricola)) {
                this.userService.register(new User(matricola, "user."+i+"@studenti.unisa.it", "Name"+i, "Surname"+i));
            }
        }

        if(!userService.existsById("0612708994")) {
            userService.register(new User("0612708994", "nick@studenti.unisa.it", "Nick", "Test"));
        }

        for(int i = 1; i <= 7; i++) {
            String isbn = "000" + i + "000000000";
            if(!bookService.existsByIsbn(isbn)) {
                bookService.add(new Book(isbn, "Title " + i, "Author", 2020, 5, 5, "Genre", "Desc"));
            }
        }

        if(!bookService.existsByIsbn("THE_SILMARILLION")) {
            bookService.add(new Book("9780618391110", "THE_SILMARILLION", "J.R.R. Tolkien", 1977, 5, 5, "Fantasy", "Raccolta di miti e leggende della Terra di Mezzo che narra la creazione del mondo e le epoche precedenti al Signore degli Anelli."));
        }

        int countEspiredLoans = 0;
        for(int i = 1; i <= 7; i++) {
            String userId = "061270000" + i;
            String isbn = "000 " + i + "000000000";
            LocalDate start = LocalDate.now().minusDays(i*10);
            LocalDate deadline;

            if(countEspiredLoans < 3) {
                deadline = LocalDate.now().minusDays(i*10);
            }else{
                deadline = LocalDate.now().plusDays(i*10);
            }
            countEspiredLoans += 1;

            if(!loanService.has(userId, isbn)) {
                loanService.register(userId, isbn, start, deadline);
            }
        }
    }

    @Test
    public void test1_InitializationAndRedRows() {
        System.out.println("--- TEST 1: CARICAMENTO E COLORI ---");
        slowExecution(1000);

        FxAssert.verifyThat("#loanTable", (TableView<Loan> t) -> t.getItems().size() == 7);

        System.out.println("Controllo visivo righe rosse...");
        moveTo("0612700001"); slowExecution(800);
        moveTo("0612700002"); slowExecution(800);
        moveTo("0612700003"); slowExecution(800);
    }
}
