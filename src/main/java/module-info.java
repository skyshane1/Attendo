module com.example.attendo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.ooxml;


    opens com.example.attendo to javafx.fxml;
    exports com.example.attendo;
}