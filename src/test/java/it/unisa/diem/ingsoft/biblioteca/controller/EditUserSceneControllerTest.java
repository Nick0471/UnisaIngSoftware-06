package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.model.Book;
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

import static it.unisa.diem.ingsoft.biblioteca.Views.USER_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EditUserSceneControllerTest extends ApplicationTest{

    private UserService userService;

    @Override
    public void start(Stage stage) {
        Database db = Database.inMemory();

        this.userService = new DatabaseUserService(db);

        ServiceRepository serviceRepository = new ServiceRepository(null, this.userService,null, null);

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
        if (!this.userService.existsById("1234567890") && !this.userService.existsByEmail("g.altieri2@studenti.unisa.it"))
            this.userService.register(new User("1234567890", "g.altieri2@studenti.unisa.it", "Giorgia", "Altieri"));


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


    @Test
    public void test1_AddNewUserSuccess(){
        System.out.println("--- TEST 1: AGGIUNTA NUOVO UTENTE---");

        int initialSize = this.lookup("#userTable").queryTableView().getItems().size();

        this.clickOn("#btnAdd");
        this.sleep(500);

        this.clickOn("#idField").write("E123456789");
        this.clickOn("#surnameField.").write("Altieri");
        this.clickOn("#nameField").write("Bianca");
        this.clickOn("#emailField").write("b.altieri@studenti.unisa.it");

        this.clickOn("#btnConfirm");
        this.sleep(1000);

        this.clickOn("OK");
        this.sleep(1000);

        FxAssert.verifyThat("#userTable", (TableView<Book> t) -> t.getItems().size() == initialSize + 1);
    }


    @Test
    public void test2_AddNewUserWithWrongEmail(){
        System.out.println("--- TEST 1: AGGIUNTA NUOVO UTENTE---");

        int initialSize = this.lookup("#userTable").queryTableView().getItems().size();

        this.clickOn("#btnAdd");
        this.sleep(500);

        this.clickOn("#idField").write("E123456789");
        this.clickOn("#surnameField.").write("Altieri");
        this.clickOn("#nameField").write("Bianca");
        this.clickOn("#emailField").write("b.altieri@virgilio.it");

        this.clickOn("#btnConfirm");
        this.sleep(1000);

        this.clickOn("OK");
        this.sleep(1000);

        FxAssert.verifyThat("#userTable", (TableView<Book> t) -> t.getItems().size() == initialSize);
    }

    @Test
    public void test3_AddNewUserWithWrongId(){
        System.out.println("--- TEST 1: AGGIUNTA NUOVO UTENTE---");

        int initialSize = this.lookup("#userTable").queryTableView().getItems().size();

        this.clickOn("#btnAdd");
        this.sleep(500);

        this.clickOn("#idField").write("E12");
        this.clickOn("#surnameField.").write("Altieri");
        this.clickOn("#nameField").write("Bianca");
        this.clickOn("#emailField").write("b.altieri@studenti.unisa.it");

        this.clickOn("#btnConfirm");
        this.sleep(1000);

        this.clickOn("OK");
        this.sleep(1000);

        FxAssert.verifyThat("#userTable", (TableView<Book> t) -> t.getItems().size() == initialSize);
    }


    @Test
    public void test4_ModifyUser() {
        System.out.println("--- TEST 3: MODIFICA UTENTE ---");

        this.clickOn("1234567890");

        this.clickOn("#btnModify");
        this.sleep(500);

        FxAssert.verifyThat("#idField", (javafx.scene.control.TextField t) -> t.getText().equals("1234567890"));
        FxAssert.verifyThat("#idField", (javafx.scene.control.TextField t) -> t.isDisabled());


        this.doubleClickOn("#nameField").write("GiorgiaMaria");

        this.clickOn("#btnConfirm");
        this.sleep(1000);
        this.clickOn("OK");


        User updateUser = this.userService.getByIdContaining("1234567890").get();
        assertEquals("GiorgiaMaria", updateUser.getName());
    }


    @Test
    public void test5_EmptyFields() {
        System.out.println("--- TEST 4: VALIDAZIONE CAMPI VUOTI ---");

        int initialSize = this.lookup("#userTable").queryTableView().getItems().size();

        this.clickOn("#btnAdd");
        this.sleep(500);

        this.clickOn("#btnConfirm");
        this.sleep(500);

        this.clickOn("OK");


        FxAssert.verifyThat("#userTable", (TableView<Book> t) -> t.getItems().size() == initialSize);
    }



    @Test
    public void test6_AbortOperation() {
        System.out.println("--- TEST 2: ANNULLA INSERIMENTO ---");

        int initialSize = this.lookup("#userTable").queryTableView().getItems().size();

        this.clickOn("#btnAdd");
        this.sleep(500);

        this.doubleClickOn("#idField").write("1234");

        this.clickOn("#btnCancel");
        this.sleep(500);

        FxAssert.verifyThat("#userTable", (TableView<Book> t) -> t.getItems().size() == initialSize);
    }








}
