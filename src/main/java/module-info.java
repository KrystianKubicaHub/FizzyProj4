module org.example.fizzyproj4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.fizzyproj4 to javafx.fxml;
    exports org.example.fizzyproj4;
}