module org.example.java_sem {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires com.google.gson;
    requires java.sql;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires org.slf4j;


    opens org.example.java_sem to javafx.fxml;
    exports org.example.java_sem;
}