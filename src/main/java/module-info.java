module ic {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens ic to javafx.fxml;
    exports ic;
}
