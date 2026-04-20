package ic;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

public class PrimaryController {

    @FXML
    private Button fechar_jogo;

    @FXML
    private Button jogar;

    @FXML
    private RadioButton selecionarAgente;

    @FXML
    private BorderPane tela_inicial;

    @FXML
    private Tooltip tooltip;

    @FXML
    private Tooltip tooltip_fechar;

    @FXML
    private Tooltip tooltip_jogar;

    @FXML
    void fecharJogo(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    void iniciarJogo(MouseEvent event) throws IOException {
        App.setRoot("secondary", 710, 730);
    }

    @FXML
    void jogarComAgente(MouseEvent event) {

    }

}
