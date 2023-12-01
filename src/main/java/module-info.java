module com.example.virtualmachine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.virtualmachine to javafx.fxml;
    exports com.example.virtualmachine;
}