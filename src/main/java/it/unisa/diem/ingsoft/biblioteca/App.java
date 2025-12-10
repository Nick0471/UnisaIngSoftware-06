package it.unisa.diem.ingsoft.biblioteca;

import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.service.BookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseBookService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseLoanService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabasePasswordService;
import it.unisa.diem.ingsoft.biblioteca.service.DatabaseUserService;
import it.unisa.diem.ingsoft.biblioteca.service.LoanService;
import it.unisa.diem.ingsoft.biblioteca.service.PasswordService;
import it.unisa.diem.ingsoft.biblioteca.service.ServiceRepository;
import it.unisa.diem.ingsoft.biblioteca.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static final String DATABASE_CONNECTION_URL = "jdbc:sqlite:database.db";

    @Override
    public void start(Stage primaryStage) {
        Optional<Database> databaseOpt = connectToDatabase();

        if (databaseOpt.isEmpty()) {
            System.err.println("La connessione al database è fallita. Il software sara' terminato.");
            return;
        }

        Database database = databaseOpt.get();
        UserService userService = new DatabaseUserService(database);
        PasswordService passwordService = new DatabasePasswordService(database);
        BookService bookService = new DatabaseBookService(database);
        LoanService loanService = new DatabaseLoanService(database);
        ServiceRepository serviceRepository = new ServiceRepository(passwordService, userService,
                bookService, loanService);

        // password di default
        if (!passwordService.isPresent()) {
            passwordService.change("admin");
        }

        FXMLLoader loader = Scenes.setupLoader(Views.LOGIN_PATH, serviceRepository);
        Parent root = Scenes.getRoot(loader);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Biblioteca Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Optional<Database> connectToDatabase() {
        int i = 0;
        Optional<Database> databaseOpt = Optional.empty();

        while (databaseOpt.isEmpty() && i < 3) {
            try {
                Thread.sleep(3000);
            } catch(InterruptedException e) {}

            databaseOpt = Database.connect(DATABASE_CONNECTION_URL); 
            i++;

            System.out.println("Tentativo di connessione al database. Tentativo numero: " + i);
        }

        return databaseOpt;
    }
}
