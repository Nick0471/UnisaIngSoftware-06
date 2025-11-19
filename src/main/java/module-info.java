module it.unisa.diem.ingsoft.biblioteca {
    requires javafx.controls;
    requires javafx.fxml;


    opens it.unisa.diem.ingsoft.biblioteca to javafx.fxml;
    exports it.unisa.diem.ingsoft.biblioteca;
}