module com.example.virtualmachine {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.virtualmachine to javafx.fxml;
    opens com.example.virtualmachine.model to javafx.base;
    exports com.example.virtualmachine;
}