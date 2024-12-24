module org.example.java_sem {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.java_sem to javafx.fxml;
    exports org.example.java_sem;
}