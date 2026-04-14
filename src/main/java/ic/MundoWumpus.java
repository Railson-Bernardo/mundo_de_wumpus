package ic;

import java.util.Random;

public class MundoWumpus {

    /*
        O labirinto deve ser representado por uma matriz 6 x 6.   ✔
        O agente sempre inicia na posição [1, 1] do labirinto. [0, 0] no codigo  ✔
        A posição [1,1] também representa a saída do labirinto.   

        O agente pode executar as seguintes ações:   
            o Mover_para_frente;   
            o Virar_a_direita (rotação de 90°);   
            o Pegar_objeto – Para pegar o outro (se ele existir) na sala em que o agente se encontra;   
            o Atirar_flecha – Para lançar a flecha em linha reta na direção que o agente esta olhando;   
            o Subir – Para sair da caverna (a ação somente pode ser executada na sala  [1,1]);   
        •Cada ação executada possui o custo de -1. Os demais eventos possuem os seguintes custos/recompensas:   
        o Pegar ouro = +1000;   
        o Cair em um poço = -1000;   
        o Ser devorado pelo Wumpus = -1000;   
        o Atirar Flecha = -10;   
        
        O agente possui os seguintes sensores:   
        o Em salas adjacentes ao Wumpus, exceto diagonal, o agente sente o fedor do Wumpus;   
        o Em salas adjacentes a um poço, exceto diagonal, o agente sente uma brisa;   
        o Em salas adjacentes a um morcego gigante, exceto diagonal, o agente ouve os gritos do morcego;   
        o Em salas onde existe ouro o agente percebe o brilho do ouro;   
        o Ao caminhar contra uma parede o agente sente um impacto. As laterais do labirinto são paredes;   
        o Quando o Wumpus morre o agente ouve um grito;   
        
        O labirinto possui os seguintes elementos:   ✔
        2 Wumpus;   ✔
        4 Poços;   ✔
        3 Pedras de ouro;   ✔
        2 Morcegos;     ✔

        A posição inicial dos elementos do labirinto deve ser sorteada aleatoriamente
        no início do programa.   ✔
        
        O jogo acaba quando o agente sair do labirinto ou
        quando ele morrer para o Wumpus ou ao cair em um poço.   
        
        Ao entrar em uma sala onde existe um morcego, o agente é carregado pelo   
        morcego para um lugar aleatório do labirinto, podendo ser um local seguro, um   
        poço, a sala de um Wumpus ou a sala onde existe outro morcego. Ou seja, o   
        local onde o agente será teleportado deve ser sorteado. Após levar o agente, o   
        morcego retorna para a sala onde ele estava originalmente.  

    */
    // Constantes dos elementos
    enum Elementos {WUMPUS, BURACO, OURO, MORCEGO, Q_VAZIO, Q_INICIAL}

    // Configurações padrões do labirinto
    private static final int TAMANHO = 6;
    private static final int QTD_WUMPUS = 2;
    private static final int QTD_BURACOS = 4;
    private static final int QTD_OUROS = 3;
    private static final int QTD_MOCEGOS = 2;

    private Elementos[][] labirinto = new Elementos[TAMANHO][TAMANHO];
    private boolean[][] visitado = new boolean[TAMANHO][TAMANHO];

    // Estados iniciais do agente(jogador)
    private int posicaoX = 0, posicaoY = 0;     // Posição inicial do agente (1,1 no labirinto)
    private int pontuacao = 0;                  // Pontuação inicial do agente
    private int flecha = 1;                      // O agente tem apenas uma flecha
    private boolean vivo = true;                // Estado do agente
    private boolean saiu = false;               // Estado de saída do labirinto

    public void MundoWumpus(){
        inicializarLabirinto();
    }

    private void inicializarLabirinto() {
        // Preencher o labirinto com espaços vazios
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                labirinto[i][j] = Elementos.Q_VAZIO;
            }
        }

        // Colocar o agente na posição inicial
        labirinto[posicaoX][posicaoY] = Elementos.Q_INICIAL;

        // Adicionar os elementos no labirinto
        adicionarElementos(Elementos.WUMPUS, QTD_WUMPUS);
        adicionarElementos(Elementos.BURACO, QTD_BURACOS);
        adicionarElementos(Elementos.OURO, QTD_OUROS);
        adicionarElementos(Elementos.MORCEGO, QTD_MOCEGOS);

    }

    private void adicionarElementos(Elementos tipo, int quantidade) {
        
        Random rand = new Random();
        int inseridos = 0;

        while (inseridos < quantidade) {
            int x = rand.nextInt(TAMANHO);
            int y = rand.nextInt(TAMANHO);
            
            if (labirinto[x][y] == Elementos.Q_VAZIO && !(x == 0 && y == 0)) { // Evitar colocar elementos na posição inicial
                labirinto[x][y] = tipo;
                inseridos++;
            }
        }
    }

    public int getPosicaoX() {
        return posicaoX;
    }

    public int getPosicaoY() {
        return posicaoY;
    }

    public int getPontuacao() {
        return pontuacao;
    }
    
    public Elementos getElemento(int x, int y) {
        return labirinto[x][y];
    }

    public void registrarAcao() {
        // Implementar a lógica para registrar as ações do agente e atualizar o estado do jogo
        this.pontuacao -= 1; // Custo de cada ação
    }

     /*
                Controles

        [D] 	    | GIRAR PARA DIREITA
        [A]		    | GIRAR PARA ESQUERDA
        [W] 	    | MOVER
        [E]		    | EQUIPAR FLECHA
        [ENTER]	    | EXECUTAR AÇÃO

    */

    private void girarDireita(){

    }

    private void girarEsquerda(){

    }

    private void mover(){

    }

    private void equiparFlecha(){

    }

    // Pegar ouro; Atirar flecha; Subir
    private void executarAcao(){

    }

    private void pegarOuro(){

    }

    private void sairLabirinto(){
        
    }

}
