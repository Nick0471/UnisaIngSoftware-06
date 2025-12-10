package it.unisa.diem.ingsoft.biblioteca;

import java.net.URL;
import java.util.Optional;

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
import javafx.fxml.FXMLLoader;

public class App {
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
            System.err.println("Impossibile connettersi al database dopo 3 tentativi.");
            return;
        }

        Database database = databaseOpt.get();
        UserService userService = new DatabaseUserService(database);
        PasswordService passwordService = new DatabasePasswordService(database);
        BookService bookService = new DatabaseBookService(database);
        LoanService loanService = new DatabaseLoanService(database);
        ServiceRepository serviceRepository = new ServiceRepository(passwordService, userService,
                bookService, loanService);

        URL loginUrl = App.class.getResource("login.fxml");
        FXMLLoader loader = new FXMLLoader(loginUrl);
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
