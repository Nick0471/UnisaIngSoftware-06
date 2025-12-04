module it.unisa.diem.ingsoft.biblioteca {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    requires transitive java.sql;
    requires transitive org.jdbi.v3.core;
    requires transitive org.xerial.sqlitejdbc;

    requires transitive jbcrypt;
    requires javafx.graphics;
    requires it.unisa.diem.ingsoft.biblioteca;

    opens it.unisa.diem.ingsoft.biblioteca to javafx.fxml;
    exports it.unisa.diem.ingsoft.biblioteca;
    exports it.unisa.diem.ingsoft.biblioteca.Controller;
    opens it.unisa.diem.ingsoft.biblioteca.Controller to javafx.fxml;
    exports it.unisa.diem.ingsoft.biblioteca.Service;
    opens it.unisa.diem.ingsoft.biblioteca.Service to javafx.fxml;
}
