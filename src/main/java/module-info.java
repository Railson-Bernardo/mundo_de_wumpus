module ic {
    requires javafx.controls;
    requires javafx.fxml;

    opens ic to javafx.fxml;
    exports ic;
}
