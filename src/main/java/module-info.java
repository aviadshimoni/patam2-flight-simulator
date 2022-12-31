module com.example.finalpro {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.finalpro to javafx.fxml;
    exports com.example.finalpro;
}