package ic;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Stage stage;
    private static SecondaryController secondaryController;

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {
        App.stage = stage;
        scene = new Scene(loadFXML("primary"), 600, 410);
        stage.setTitle("👺 🤷‍♂️ 💰 Mundo de Wumpus 💸 🏃‍♂️ 👹");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/imagens/agente.png")));
        stage.show();
        scene.getRoot().requestFocus();
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (secondaryController != null) {
                switch (event.getCode()){
                    case W -> { 
                        secondaryController.moverFrente();
                        event.consume();
                    }
                    case A -> { 
                        secondaryController.girarEsquerda();
                        event.consume();
                    }
                    case D -> { 
                        secondaryController.girarDireita();
                        event.consume();
                    }
                    case E -> { 
                        secondaryController.equiparFlecha();
                        event.consume();
                    }
                    case ENTER -> { 
                        secondaryController.executarAcao();
                        event.consume();
                    }
                    default -> {
                    }
                }
            }
        });
    }

    static void setRoot(String fxml, double largura, double altura) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        scene.setRoot(root);
        
        if (fxml.equals("secondary")) {
            secondaryController = fxmlLoader.getController();
        }
        
        stage.setWidth(largura);
        stage.setHeight(altura);
        stage.centerOnScreen();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}