package ic;

import java.util.Random;

public class MundoWumpus {

    // Constantes dos elementos
    public enum Elementos {WUMPUS, BURACO, OURO, MORCEGO, Q_VAZIO, Q_INICIAL}

    // Configurações padrões do labirinto
    private static final int TAMANHO = 6;
    private static final int QTD_WUMPUS = 2;
    private static final int QTD_BURACOS = 4;
    private static final int QTD_OUROS = 3;
    private static final int QTD_MOCEGOS = 2;

    private final Elementos[][] labirinto = new Elementos[TAMANHO][TAMANHO];
    private final boolean[][] visitado = new boolean[TAMANHO][TAMANHO];

    // Estados iniciais do agente(jogador)
    private int posicaoX = 0, posicaoY = 0;     // Posição inicial do agente (1,1 no labirinto)
    private int pontuacao = 0;                  // Pontuação inicial do agente
    private int flecha = 1;                      // O agente tem apenas uma flecha
    private boolean vivo = true;                // Estado do agente
    private boolean saiu = false;               // Estado de saída do labirinto
    private String ultimoEvento = "";          // Última narração de evento do jogo

    public MundoWumpus(){
        inicializarLabirinto();
    }

    private void inicializarLabirinto() {
        // Preencher o labirinto com espaços vazios
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                labirinto[i][j] = Elementos.Q_VAZIO;
                visitado[i][j] = false;
            }
        }
        visitado[0][0] = true; // Marcar a posição inicial como visitada
        ultimoEvento = "Você entrou no labirinto 🕵️‍♂️";

        // Adicionar os elementos no labirinto
        adicionarElementos(Elementos.WUMPUS, QTD_WUMPUS);
        adicionarElementos(Elementos.BURACO, QTD_BURACOS);
        adicionarElementos(Elementos.OURO, QTD_OUROS);
        adicionarElementos(Elementos.MORCEGO, QTD_MOCEGOS);

    }

    private void adicionarElementos(Elementos tipo, int quantidade) {
        
        Random rand = new Random();
        int inseridos = 0;

        int xA, yA;
        do {
            xA = rand.nextInt(2) + 1;
            yA = rand.nextInt(2) + 1; 
        } while(xA == yA);

        while (inseridos < quantidade) {
            int x = rand.nextInt(TAMANHO);
            int y = rand.nextInt(TAMANHO);
            
            // Evitar colocar elementos nas primeiras salas para garantir inicio "seguro"
            if (labirinto[x][y] == Elementos.Q_VAZIO && !(x == 0 && y == 0) && !(x == 0 && y == 1) 
                && !(x == 1 && y == 0) && !(x == 1 && y == 1) && !(x == xA && y == yA)) {
                labirinto[x][y] = tipo;
                inseridos++;
            }
        }
    }

    private int direcao = 0;
    public void girarDireita(){
        if(!vivo || saiu == true){
            return; // O agente está morto ou saiu, não pode girar
        }
        direcao = (direcao + 1 + 4) % 4; // Girar para direita
    }

    public void girarEsquerda(){
        if(!vivo || saiu == true){
            return; // O agente está morto ou saiu, não pode girar
        }
        direcao = (direcao - 1 + 4) % 4; // Girar para esquerda 
    }

    public void moverFrente(){
        if(!vivo || saiu == true){
            return; // O agente está morto ou saiu, não pode se mover
        }
        ultimoEvento = "";
        int novoX = posicaoX;
        int novoY = posicaoY;

        switch (direcao){
            // Andar Para Baixo
            case 0 -> novoX ++;
            // Andar Para Esquerda
            case 1 -> novoY --;
            // Andar Para Cima
            case 2 -> novoX --;
            // Andar Para Direita
            case 3 -> novoY ++;
        }

        if(novoX >= 0 && novoX < TAMANHO && novoY >= 0 && novoY < TAMANHO){
            posicaoX = novoX;
            posicaoY = novoY;
            visitado[posicaoX][posicaoY] = true; // Revelar sala visitada
            pontuacao -= 1; // Custo de mover
            verificarEstado();
        } else {
            ultimoEvento = "Batir Em Algo Grande e Grosso... Uma Parede Talvez";
        }
    }

    private void verificarEstado(){
        Elementos atual = labirinto[posicaoX][posicaoY];
        if(null != atual)switch (atual) {
            case WUMPUS -> {
                vivo = false;
                pontuacao -= 1000; // Penalidade por morrer
                ultimoEvento = "Você Foi Devorado Pelo Wumpus 👻";
            }
            case BURACO -> {
                vivo = false;
                pontuacao -= 1000; // Penalidade por morrer
                ultimoEvento = "Você Caiu em um Buraco 🕳☠";
            }
            case MORCEGO -> {
                posicaoX = new Random().nextInt(TAMANHO);
                posicaoY = new Random().nextInt(TAMANHO);
                visitado[posicaoX][posicaoY] = true; // Revelar nova posição após ser transportado
                ultimoEvento = "Um Morcego Te Levou Para Outro Lugar 🦇🤦‍♂️";
                verificarEstado(); // Verificar o estado após ser transportado pelo morcego
            }
            default -> {
            }
        }
    }

    private boolean flechaEquipada = false;
    public void equiparFlecha(){
        if(flecha > 0){
            this.flechaEquipada = !this.flechaEquipada; // Alterna entre equipado e não equipado
        }
    }

    // Pegar ouro; Atirar flecha; Subir
    public void executarAcao(){
        if (labirinto[posicaoX][posicaoY] == Elementos.OURO) {
            pegarOuro();
        } else if (posicaoX == 0 && posicaoY == 0) {
            sairLabirinto();
        } else if (flechaEquipada && flecha > 0) {
            atirarFlecha();
        }
    }

    public void atirarFlecha(){
        if(flecha > 0){
            flecha--;
            pontuacao -= 10; // Custo de atirar a flecha
            flechaEquipada = false;

            int dx = 0, dy = 0;
            switch (direcao) {
                // Baixo
                case 0 -> dx = 1;
                // Esquerda
                case 1 -> dy = -1;
                // Cima
                case 2 -> dx = -1;
                // Direita
                case 3 -> dy = 1;
            }

            int alvoX = posicaoX + dx;
            int alvoY = posicaoY + dy;
            boolean acertou = false;

            while(alvoX >= 0 && alvoX < TAMANHO && alvoY >= 0 && alvoY < TAMANHO){
                if(labirinto[alvoX][alvoY] == Elementos.WUMPUS){
                    labirinto[alvoX][alvoY] = Elementos.Q_VAZIO; // Matar o Wumpus
                    acertou = true;
                    break;
                }
                alvoX += dx;
                alvoY += dy;
            }

            if(acertou){
                ultimoEvento = "Você Ouve Um Grito de Morte 😖";
            } else {
                ultimoEvento = "A flecha não acertou nada... 😕";
            }
        }
    }

    public void pegarOuro(){
        if(labirinto[posicaoX][posicaoY] == Elementos.OURO){
            pontuacao += 1000; // Recompensa por pegar ouro
            labirinto[posicaoX][posicaoY] = Elementos.Q_VAZIO; // Remover o ouro da sala
            ultimoEvento = "Você encontrou ouro 🤑";
        }
    }

    public void sairLabirinto(){
        if(flechaEquipada == true){
            ultimoEvento = "Flecha equipada! Você precisa desequipar para sair 🏃‍♂️";
        } else if(posicaoX == 0 && posicaoY == 0){
            saiu = true;
        }
    }

    public String avisos(int x, int y){
        StringBuilder aviso = new StringBuilder();

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        boolean fedor = false, brisa = false, grito = false, brilho = false;
        for (int i = 0; i < 4; i++){
            int nx = x + dx[i];
            int ny = y + dy[i];

            if(nx >= 0 && nx < TAMANHO && ny >= 0 && ny < TAMANHO){
                Elementos vizinho = labirinto[nx][ny];
                if(null != vizinho) switch (vizinho) {
                    case WUMPUS -> fedor = true;
                    case BURACO -> brisa = true;
                    case MORCEGO -> grito = true;
                    case OURO -> brilho = true;
                    default -> {
                    }
                } 
            }
        }
        if(fedor) {
            aviso.append("Fedor\n");
        }
        if(brisa) {
            aviso.append("Brisa\n");
        }
        if(grito) {
            aviso.append("Barulho\n");
        }
        if(brilho) {
            aviso.append("Brilho\n");
        }
        return aviso.toString().trim();
    }

    public String avisos(){
        return avisos(posicaoX, posicaoY);
    }

    public void revelarMapa(){
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                visitado[i][j] = true;
            }
        }
    }

    public int getFlecha(){
        return flecha;
    }

    public boolean getFlechaEquipada() {
        return flechaEquipada;
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

    public boolean isVivo(){
        return vivo;
    }

    public int getDirecao() {
        return direcao;
    }

    public String getUltimoEvento() {
        return ultimoEvento;
    }

    public void setUltimoEvento(String evento) {
        this.ultimoEvento = evento;
    }

    public boolean isVisitado(int x, int y){
        return visitado[x][y];
    }

    public boolean isSaiu() {
        return saiu;
    }
}
