package it.unisa.diem.ingsoft.biblioteca;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import it.unisa.diem.ingsoft.biblioteca.controller.GuiController;
import it.unisa.diem.ingsoft.biblioteca.exception.DatabaseUnreachableException;
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

import static javafx.application.Application.launch;

public class App extends Application {
    private static final String DATABASE_CONNECTION_URL = "jdbc:sqlite:database.db";

    public static void main(String[] args) {
        int connectionTries = 0;
        Optional<Database> databaseOpt = connectToDatabase();

        while (databaseOpt.isEmpty() && connectionTries < 3) {
            try {
                Thread.sleep(3000);
            } catch(InterruptedException e) {}

            databaseOpt = connectToDatabase();
            connectionTries++;
        }

        if (databaseOpt.isEmpty()) {
            System.err.println("La connessione al database è fallita dopo 3 tentativi.");
            System.err.println("Il software terminerà.");
            return;
        }

        Database database = databaseOpt.get();
        UserService userService = new DatabaseUserService(database);
        PasswordService passwordService = new DatabasePasswordService(database);
        BookService bookService = new DatabaseBookService(database);
        LoanService loanService = new DatabaseLoanService(database);
        ServiceRepository serviceRepository = new ServiceRepository(passwordService, userService,
                bookService, loanService);

        if (!passwordService.isPresent()) {
            passwordService.change("admin");
        }

        launch(args);

    }


    public void start(Stage primaryStage) {

        try {
            URL loginUrl = App.class.getResource("/it/unisa/diem/ingsoft/biblioteca/view/LogInScene.fxml");


            FXMLLoader loader = new FXMLLoader(loginUrl);


            Parent root = loader.load();
            Object controller = loader.getController();

            if (controller instanceof GuiController) {
                ((GuiController) controller).setServices(serviceRepository);
            }

            Scene scene = new Scene(root);
            primaryStage.setTitle("Biblioteca Login");
            primaryStage.setScene(scene);
            primaryStage.show();

        }catch (IOException e) {
            e.printStackTrace();
            // Gestisci l'errore, magari mostrando un alert o chiudendo l'app
        }
    }



    private static Optional<Database> connectToDatabase() {
        Optional<Database> database = Optional.empty();

        try {
			Database db = Database.connect(DATABASE_CONNECTION_URL);
            database = Optional.of(db);
		} catch (DatabaseUnreachableException e) {
			e.printStackTrace();
		}

        return database;
    }
}
