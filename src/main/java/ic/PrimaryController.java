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
    @SuppressWarnings("unused")
    private Button fechar_jogo;

    @FXML
    @SuppressWarnings("unused")
    private Button jogar;

    @FXML
    @SuppressWarnings("unused")
    private RadioButton selecionarAgente;

    @FXML
    @SuppressWarnings("unused")
    private BorderPane tela_inicial;

    @FXML
    @SuppressWarnings("unused")
    private Tooltip tooltip;

    @FXML
    @SuppressWarnings("unused")
    private Tooltip tooltip_fechar;

    @FXML
    @SuppressWarnings("unused")
    private Tooltip tooltip_jogar;

    @FXML
    @SuppressWarnings("unused")
    void fecharJogo(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    @SuppressWarnings("unused")
    void iniciarJogo(MouseEvent event) throws IOException {
        App.setAgentMode(selecionarAgente.isSelected());
        App.setRoot("secondary", 710, 730);
    }

    @FXML
    @SuppressWarnings("unused")
    void jogarComAgente(MouseEvent event) {
        // O estado do botão é usado quando a partida é iniciada.
    }

    // public void escolherModoJogo(){
    //     if(jogarComAgente().isSelected()){
    //         // Iniciar o jogo com o agente
    //     } else {
    //         iniciarJogo();
    //     }
    // }
}
