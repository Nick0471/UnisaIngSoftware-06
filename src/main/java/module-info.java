module it.unisa.diem.ingsoft.biblioteca {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    requires transitive java.sql;
    requires transitive org.jdbi.v3.core;
    requires transitive org.xerial.sqlitejdbc;

    requires transitive jbcrypt;
    requires javafx.graphics;
    requires javafx.base;

    opens it.unisa.diem.ingsoft.biblioteca to javafx.fxml;
    exports it.unisa.diem.ingsoft.biblioteca;
    exports it.unisa.diem.ingsoft.biblioteca.controller;
    opens it.unisa.diem.ingsoft.biblioteca.controller to javafx.fxml;
    exports it.unisa.diem.ingsoft.biblioteca.service;
    opens it.unisa.diem.ingsoft.biblioteca.service to javafx.fxml;
    exports it.unisa.diem.ingsoft.biblioteca.model;
    opens it.unisa.diem.ingsoft.biblioteca.model to javafx.fxml;
    exports it.unisa.diem.ingsoft.biblioteca.exception;

    requires org.slf4j;
}
