package ic;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class SecondaryController {

    @FXML
    private GridPane areaLabirinto;

    @FXML
    private Label narrarEventos;

    @FXML
    private Label pontuacaoJogo;

    @FXML
    private Button reiniciar_jogo;

    @FXML
    private Tooltip tooltip_controles;

    @FXML
    private Button voltar_tela_inicial;

    private MundoWumpus jogo;

    @FXML
    void reiniciarJogo(MouseEvent event) {
        jogo = new MundoWumpus();
        narrarEventos.setText("Jogo reiniciado!");
        atualizarLabirinto();
    }

    @FXML
    void voltarTelaInicial(MouseEvent event) throws IOException {
        App.setRoot("primary");
    }

    @FXML
    private void initialize() {
        jogo = new MundoWumpus(); 
        atualizarLabirinto();
    }

    public void atualizarLabirinto() {
        // Implementar a lógica para atualizar a visualização do labirinto com base no estado atual do jogo
        areaLabirinto.getChildren().clear();
        pontuacaoJogo.setText("Pontuação: " + jogo.getPontuacao());

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                StackPane cell = new StackPane();
                cell.setStyle("-fx-border-color: #fcc100; -fx-background-color: #3b014a;");

                // Logica para mostrar apenas o que o agente conhece
                if (i== jogo.getPosicaoX() && j == jogo.getPosicaoY()) {
                    ImageView agenteImage = new ImageView(new Image(getClass().getResourceAsStream("/src/main/resources/imagens/Homem de Terno.png")));
                    agenteImage.setFitWidth(50);
                    agenteImage.setFitHeight(50);
                    cell.getChildren().add(agenteImage);
                }
                areaLabirinto.add(cell, j, i);
            }
        }
    }
}
