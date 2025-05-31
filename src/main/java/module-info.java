module com.example.examify {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.xerial.sqlitejdbc;

    // Otwieramy pakiety z kontrolerami, żeby FXMLLoader mógł je wstrzyknąć
    opens com.example.examify.ui.controllers to javafx.fxml;
    // Jeżeli w katalogu com.example.examify masz jakieś @FXML-annotowane klasy:
    opens com.example.examify to javafx.fxml;
    exports com.example.examify;
}
