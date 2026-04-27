package ic;

import java.io.IOException;

import ic.MundoWumpus.Elementos;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class SecondaryController {

    @FXML
    private GridPane areaLabirinto;

    @FXML
    private Label narrarEventos;

    @FXML
    private Label pontuacaoJogo;

    @FXML
    @SuppressWarnings("unused")
    private Button reiniciar_jogo;

    @FXML
    @SuppressWarnings("unused")
    private Tooltip tooltip_controles;

    @FXML
    @SuppressWarnings("unused")
    private Button voltar_tela_inicial;

    private MundoWumpus jogo;
    private Agente agenteJogo;

    @FXML
    @SuppressWarnings("unused")
    void reiniciarJogo(MouseEvent event) {
        initialize(); // Reinicia o jogo e atualiza o labirinto
        // jogo = new MundoWumpus();
        // jogo.setUltimoEvento("Jogo reiniciado!");
        // atualizarLabirinto();
    }

    @FXML
    @SuppressWarnings("unused")
    void voltarTelaInicial(MouseEvent event) throws IOException {
        App.setRoot("primary", 600, 430);
    }

    @FXML
    @SuppressWarnings("unused")
    private void initialize() {
        jogo = new MundoWumpus();
        atualizarLabirinto();
        if (App.isAgentMode()) {
            new Thread(() -> {
                agenteJogo = new Agente(jogo, SecondaryController.this);
                agenteJogo.executar();
            }, "Agente-Automatico").start();
        }
    }

    @SuppressWarnings("exports")
    public ImageView adicionarImagem(StackPane painel, String caminhoImagem, double rotacao){
        try{
            var caminho = getClass().getResourceAsStream(caminhoImagem);
            if (caminho == null) {
                System.out.println("⚠️ IMAGEM NÃO ENCONTRADA: " + caminhoImagem);
                return null;
            }
            ImageView img = new ImageView(new javafx.scene.image.Image(caminho));
            img.setFitWidth(100);
            img.setFitHeight(100);
            painel.getChildren().add(img);
            return img;
        } catch (Exception e){
            System.out.println("Erro ao carregar imagem: " + caminhoImagem);
            return null;
        }
    }

    private String caminhos(Elementos elemento) {
        return switch (elemento) {
            case WUMPUS -> "/imagens/wumpus.png";
            case BURACO -> "/imagens/buraco.png";
            case OURO -> "/imagens/ouro.png";
            case MORCEGO -> "/imagens/morcego.png";
            case Q_VAZIO -> "/imagens/salas.png";
            default -> null;
        };
    }


    public void atualizarLabirinto() {
        areaLabirinto.getChildren().clear();
        pontuacaoJogo.setText(jogo.getPontuacao() + " Pontos");

        if (!jogo.isVivo() || jogo.isSaiu()) {
            jogo.revelarMapa();
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                StackPane celula = new StackPane();
                celula.setStyle("-fx-border-color: #fcc100; -fx-background-color: transparent;");
                // celula.setPrefSize(60, 60);

                if (!jogo.isVisitado(i, j)){
                    adicionarImagem(celula, "/imagens/nevoa.png", 0);
                } else {
                    // Adiciona os elementos do labirinto
                    MundoWumpus.Elementos e = jogo.getElemento(i, j);
                    if(e != MundoWumpus.Elementos.Q_VAZIO){
                        adicionarImagem(celula, caminhos(e), 0);
                    } else if (i == 0 && j == 0){
                        // Imagem de Inicio/Saída
                        adicionarImagem(celula, "/imagens/inicio.png", 0);
                    } else {
                        adicionarImagem(celula, caminhos(e), 0);
                    }
                }

                // Adiciona o agente na posição
                if(i == jogo.getPosicaoX() && j == jogo.getPosicaoY()){
                    ImageView agenteImagem = adicionarImagem(celula, "/imagens/agente.png", 0);
                    agenteImagem.setRotate(jogo.getDirecao() * 90);
                }

                // Adiciona aviso perceptivo na sala, se houver
                if (jogo.isVisitado(i, j)) {
                    String avisoSala = jogo.avisos(i, j);
                    if (!avisoSala.isBlank()) {
                        Label avisoLabel = new Label(avisoSala);
                        avisoLabel.setTextFill(Color.web("#fcc100"));
                        avisoLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-background-color: rgba(0, 0, 0, 0); -fx-background-radius: 5;");
                        avisoLabel.setWrapText(true);
                        avisoLabel.setMaxWidth(90);
                        StackPane.setAlignment(avisoLabel, Pos.BOTTOM_CENTER);
                        celula.getChildren().add(avisoLabel);
                    }
                }

                areaLabirinto.add(celula, j, i);
            }
        }

        // Atualiza narração após renderizar o labirinto
        String evento = jogo.getUltimoEvento();
        if (!evento.isBlank()) {
            narrarEventos.setText(evento);
        } else if (jogo.isVivo() == false) {
            narrarEventos.setText(evento.isBlank() ? "" : evento);
        }
    }

    @FXML
    void moverFrente(){
        jogo.moverFrente();
        atualizarLabirinto();
    }

    @FXML
    void girarDireita(){
        jogo.girarDireita();
        atualizarLabirinto();
    }

    @FXML
    void girarEsquerda(){
        jogo.girarEsquerda();
        atualizarLabirinto();
    }

    @FXML
    void equiparFlecha(){
        if (!jogo.isVivo() || jogo.isSaiu()) {
            return; // Não permite equipar/desequipar após fim de jogo
        }
        jogo.equiparFlecha();
        if (jogo.getFlechaEquipada() && jogo.getFlecha() > 0) {
            jogo.setUltimoEvento("Flecha Equipada 🏹");
        } else if (!jogo.getFlechaEquipada() && jogo.getFlecha() > 0) {
            jogo.setUltimoEvento("Flecha Desequipada 💅");
        } else if (jogo.getFlecha() == 0) {
            jogo.setUltimoEvento("Sem Flechas Para Equipar 😥");
        }
        atualizarLabirinto();
    }

    @FXML
    void executarAcao(){
        jogo.executarAcao();
        if(jogo.isSaiu()){
            jogo.setUltimoEvento("Você Saiu do Labirinto  Parabéns 🕺✨");
        }
        atualizarLabirinto();
    }
    
}
