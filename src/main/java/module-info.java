module com.example.groupchat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.groupchat to javafx.fxml;
    exports com.example.groupchat;
}