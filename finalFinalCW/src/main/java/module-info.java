module com.example.finalfinalcw {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.finalfinalcw to javafx.fxml;
    exports com.example.finalfinalcw;
}