package ic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 600, 400);
        stage.setTitle("👺 🤷‍♂️ 💰 Mundo de Wumpus 💸 🏃‍♂️ 👹");
        stage.setScene(scene);
        stage.show();
        scene.setOnKeyPressed(event -> {
            // Implementar a lógica para capturar as teclas pressionadas e registrar as ações do agente
            System.out.println("Tecla pressionada: " + event.getCode());
        });
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}