package it.unisa.diem.ingsoft.biblioteca.controller;

import it.unisa.diem.ingsoft.biblioteca.Database;
import it.unisa.diem.ingsoft.biblioteca.Scenes;
import it.unisa.diem.ingsoft.biblioteca.model.User;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;

import static it.unisa.diem.ingsoft.biblioteca.Views.USER_PATH;


public class UserSceneControllerTest extends ApplicationTest {
    private UserService userService;

    @Override
    public void start(Stage stage){
        Database db = Database.inMemory();

        this.userService= new DatabaseUserService(db);
        ServiceRepository serviceRepository = new ServiceRepository(null, this.userService, null, null);

        try {
            this.setUp();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FXMLLoader loader = Scenes.setupLoader(USER_PATH, serviceRepository);
        Parent root =  loader.getRoot();

        Scene scene = new Scene(root);

        stage.setTitle("Visualizzazione lista utenti");
        stage.setScene(scene);
        stage.show();
    }


    public void setUp() throws Exception {
        if(!this.userService.existsById("1234567890") && !this.userService.existsByEmail("b.altieri2@studenti.unisa.it"))
            this.userService.register(new User("1234567890","b.altieri2@studenti.unisa.it","Bianca","Altieri"));


        if(!this.userService.existsById("0512103578") && !this.userService.existsByEmail("m.rossi1@studenti.unisa.it"))
            this.userService.register(new User("0512103578", "m.rossi1@studenti.unisa.it", "Mario", "Rossi"));

        if(!this.userService.existsById("1122334455") && !this.userService.existsByEmail("ale.rossi@studenti.unisa.it"))
            this.userService.register(new User("1122334455", "ale.rossi@studenti.unisa.it", "Alessandro", "Rossi"));

        if(!this.userService.existsById("AB12345678") && !this.userService.existsByEmail("g.verdi5@studenti.unisa.it"))
            this.userService.register(new User("AB12345678", "g.verdi5@studenti.unisa.it", "Giulia", "Verdi"));


        if(!this.userService.existsById("06127000XY") && !this.userService.existsByEmail("l.bianchi@studenti.unisa.it"))
            this.userService.register(new User("06127000XY", "l.bianchi@studenti.unisa.it", "Luca", "Bianchi"));


        if(!this.userService.existsById("0123456789") && !this.userService.existsByEmail("a.neri99@studenti.unisa.it"))
            this.userService.register(new User("0123456789", "a.neri99@studenti.unisa.it", "Anna", "Neri"));

        if(!this.userService.existsById("M123456789") && !this.userService.existsByEmail("f.esposito@studenti.unisa.it"))
            this.userService.register(new User("M123456789", "f.esposito@studenti.unisa.it", "Francesco", "Esposito"));
    }
}
