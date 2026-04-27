package ic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import javafx.application.Platform;

public class Agente {

    private final MundoWumpus jogoAgente;
    private final SecondaryController controller;
    private final int[][] vizinhos = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}};
    private final Status[][] memoria = new Status[6][6];
    private Posicao brilhoOrigem = null;
    private final List<Posicao> brilhoTentativas = new ArrayList<>();

    private enum Status {
        UNKNOWN,
        SAFE,
        VISITED,
        RISKY
    }

    public Agente(MundoWumpus jogoAgente, SecondaryController controller) {
        this.jogoAgente = jogoAgente;
        this.controller = controller;
        for (Status[] linha : memoria) {
            Arrays.fill(linha, Status.UNKNOWN);
        }
        memoria[0][0] = Status.SAFE;
    }

    public void executar() {
        while (jogoAgente.isVivo() && !jogoAgente.isSaiu()) {
            atualizarConhecimento();
            String avisoAtual = jogoAgente.avisos();

            // Pegar ouro se estiver nesta sala
            if (jogoAgente.getElemento(jogoAgente.getPosicaoX(), jogoAgente.getPosicaoY()) == MundoWumpus.Elementos.OURO) {
                runOnFxThread(controller::executarAcao);
                esperar(500);
                atualizarConhecimento();
                continue;
            }

            // Se estamos no ponto inicial e não há mais ouro, sair
            if (jogoAgente.getPosicaoX() == 0 && jogoAgente.getPosicaoY() == 0 && !temOuroRestante()) {
                runOnFxThread(controller::executarAcao);
                esperar(500);
                break;
            }

            // Se já coletou todos os ouros, retornar para a base primeiro
            if (!temOuroRestante()) {
                Posicao destinoBase = buscarCaminhoPara(0, 0);
                if (destinoBase != null) {
                    moverPara(destinoBase);
                    continue;
                }
            }

            int avisos = contarAvisos(avisoAtual);
            boolean podeArriscar = avisos < 2 || avisoAtual.contains("Barulho");

            // Priorizar movimento direto à frente quando possível
            Posicao frente = obterFrente();
            if (frente != null && !jogoAgente.isVisitado(frente.x, frente.y)
                    && (memoria[frente.x][frente.y] == Status.SAFE
                            || (memoria[frente.x][frente.y] == Status.UNKNOWN && podeArriscar))) {
                moverPara(frente);
                continue;
            }

            // Se o aviso inclui brilho, tentar um vizinho desconhecido específico e voltar caso não haja ouro
            if (avisoAtual.contains("Brilho")) {
                Posicao brilhoDestino = buscarVizinhoBrilho();
                if (brilhoDestino != null) {
                    Posicao origem = new Posicao(jogoAgente.getPosicaoX(), jogoAgente.getPosicaoY());
                    moverPara(brilhoDestino);
                    if (jogoAgente.getElemento(brilhoDestino.x, brilhoDestino.y) != MundoWumpus.Elementos.OURO) {
                        brilhoTentativas.add(brilhoDestino);
                        moverPara(origem);
                    } else {
                        runOnFxThread(controller::executarAcao);
                    }
                    continue;
                }
            }

            Posicao destino = procurarProximoDestino();
            if (destino == null && podeArriscar) {
                destino = buscarVizinhoDesconhecido();
            }
            if (destino == null && podeArriscar) {
                destino = buscarDestinoArriscado(avisoAtual);
            }
            if (destino == null) {
                destino = buscarCaminhoPara(0, 0);
            }
            if (destino == null) {
                break;
            }

            moverPara(destino);
            esperar(500);
        }
    }

    private boolean temOuroRestante() {
        for (int x = 0; x < memoria.length; x++) {
            for (int y = 0; y < memoria.length; y++) {
                if (jogoAgente.getElemento(x, y) == MundoWumpus.Elementos.OURO) {
                    return true;
                }
            }
        }
        return false;
    }

    private void atualizarConhecimento() {
        int x = jogoAgente.getPosicaoX();
        int y = jogoAgente.getPosicaoY();
        memoria[x][y] = Status.VISITED;
        String aviso = jogoAgente.avisos();

        if (aviso.isBlank()) {
            // Se não há avisos, vizinhos são seguros
            for (int d = 0; d < vizinhos.length; d++) {
                int nx = x + vizinhos[d][0];
                int ny = y + vizinhos[d][1];
                if (estaDentro(nx, ny) && memoria[nx][ny] == Status.UNKNOWN) {
                    memoria[nx][ny] = Status.SAFE;
                }
            }
        } else {
            boolean apenasBrilho = aviso.equals("Brilho");
            boolean apenasBarulho = aviso.equals("Barulho");
            for (int d = 0; d < vizinhos.length; d++) {
                int nx = x + vizinhos[d][0];
                int ny = y + vizinhos[d][1];
                if (!estaDentro(nx, ny) || memoria[nx][ny] != Status.UNKNOWN) {
                    continue;
                }
                if (apenasBrilho || apenasBarulho) {
                    memoria[nx][ny] = Status.SAFE;
                } else {
                    memoria[nx][ny] = Status.RISKY;
                }
            }
        }
    }

    private Posicao procurarProximoDestino() {
        Posicao atual = new Posicao(jogoAgente.getPosicaoX(), jogoAgente.getPosicaoY());
        Posicao melhor = null;
        int menorDist = Integer.MAX_VALUE;

        for (int x = 0; x < memoria.length; x++) {
            for (int y = 0; y < memoria[x].length; y++) {
                if ((memoria[x][y] == Status.SAFE || memoria[x][y] == Status.VISITED)
                        && temVizinhoDesconhecido(x, y)) {
                    Posicao destino = buscarCaminhoPara(x, y);
                    if (destino != null) {
                        int dist = Math.abs(atual.x - x) + Math.abs(atual.y - y);
                        if (dist < menorDist) {
                            menorDist = dist;
                            melhor = destino;
                        }
                    }
                }
            }
        }
        return melhor;
    }

    private Posicao buscarVizinhoDesconhecido() {
        int x = jogoAgente.getPosicaoX();
        int y = jogoAgente.getPosicaoY();
        for (int d = 0; d < vizinhos.length; d++) {
            int nx = x + vizinhos[d][0];
            int ny = y + vizinhos[d][1];
            if (estaDentro(nx, ny) && memoria[nx][ny] == Status.UNKNOWN) {
                return new Posicao(nx, ny);
            }
        }
        return null;
    }

    private Posicao buscarVizinhoBrilho() {
        int x = jogoAgente.getPosicaoX();
        int y = jogoAgente.getPosicaoY();
        for (int d = 0; d < vizinhos.length; d++) {
            int nx = x + vizinhos[d][0];
            int ny = y + vizinhos[d][1];
            Posicao vizinho = new Posicao(nx, ny);
            if (estaDentro(nx, ny) && memoria[nx][ny] == Status.UNKNOWN && !brilhoTentativas.contains(vizinho)) {
                return vizinho;
            }
        }
        return null;
    }

    private Posicao buscarDestinoArriscado(String avisoAtual) {
        int x = jogoAgente.getPosicaoX();
        int y = jogoAgente.getPosicaoY();

        if (avisoAtual.contains("Barulho")) {
            for (int d = 0; d < vizinhos.length; d++) {
                int nx = x + vizinhos[d][0];
                int ny = y + vizinhos[d][1];
                if (estaDentro(nx, ny) && memoria[nx][ny] == Status.RISKY && !jogoAgente.isVisitado(nx, ny)) {
                    return new Posicao(nx, ny);
                }
            }
        }

        for (int d = 0; d < vizinhos.length; d++) {
            int nx = x + vizinhos[d][0];
            int ny = y + vizinhos[d][1];
            if (estaDentro(nx, ny) && memoria[nx][ny] == Status.RISKY) {
                return new Posicao(nx, ny);
            }
        }
        return null;
    }

    private boolean temVizinhoDesconhecido(int x, int y) {
        for (int d = 0; d < vizinhos.length; d++) {
            int nx = x + vizinhos[d][0];
            int ny = y + vizinhos[d][1];
            if (estaDentro(nx, ny) && memoria[nx][ny] == Status.UNKNOWN) {
                return true;
            }
        }
        return false;
    }

    private boolean estaDentro(int x, int y) {
        return x >= 0 && x < memoria.length && y >= 0 && y < memoria.length;
    }

    private Posicao buscarCaminhoPara(int alvoX, int alvoY) {
        int inicioX = jogoAgente.getPosicaoX();
        int inicioY = jogoAgente.getPosicaoY();
        boolean[][] visitado = new boolean[memoria.length][memoria.length];
        Posicao[][] pai = new Posicao[memoria.length][memoria.length];
        Queue<Posicao> fila = new ArrayDeque<>();

        fila.add(new Posicao(inicioX, inicioY));
        visitado[inicioX][inicioY] = true;

        while (!fila.isEmpty()) {
            Posicao atual = fila.remove();
            if (atual.x == alvoX && atual.y == alvoY) {
                return primeiroPasso(pai, alvoX, alvoY, inicioX, inicioY);
            }

            for (int d = 0; d < vizinhos.length; d++) {
                int nx = atual.x + vizinhos[d][0];
                int ny = atual.y + vizinhos[d][1];
                if (estaDentro(nx, ny) && !visitado[nx][ny]
                        && (memoria[nx][ny] == Status.SAFE || memoria[nx][ny] == Status.VISITED)) {
                    visitado[nx][ny] = true;
                    pai[nx][ny] = atual;
                    fila.add(new Posicao(nx, ny));
                }
            }
        }
        return null;
    }

    private Posicao primeiroPasso(Posicao[][] pai, int x, int y, int inicioX, int inicioY) {
        Posicao atual = new Posicao(x, y);
        while (!(atual.x == inicioX && atual.y == inicioY)) {
            Posicao anterior = pai[atual.x][atual.y];
            if (anterior == null) {
                return null;
            }
            if (anterior.x == inicioX && anterior.y == inicioY) {
                return atual;
            }
            atual = anterior;
        }
        return atual;
    }

    private void moverPara(Posicao destino) {
        int atualX = jogoAgente.getPosicaoX();
        int atualY = jogoAgente.getPosicaoY();
        if (Math.abs(atualX - destino.x) + Math.abs(atualY - destino.y) == 1) {
            moverUmPasso(destino.x, destino.y);
            return;
        }

        List<Posicao> caminho = reconstruirCaminho(destino.x, destino.y);
        if (caminho == null) {
            moverUmPasso(destino.x, destino.y);
            return;
        }
        for (Posicao passo : caminho) {
            moverUmPasso(passo.x, passo.y);
            if (!jogoAgente.isVivo() || jogoAgente.isSaiu()) {
                return;
            }
        }
    }

    private List<Posicao> reconstruirCaminho(int destinoX, int destinoY) {
        int inicioX = jogoAgente.getPosicaoX();
        int inicioY = jogoAgente.getPosicaoY();
        boolean[][] visitado = new boolean[memoria.length][memoria.length];
        Posicao[][] pai = new Posicao[memoria.length][memoria.length];
        Queue<Posicao> fila = new ArrayDeque<>();

        fila.add(new Posicao(inicioX, inicioY));
        visitado[inicioX][inicioY] = true;

        while (!fila.isEmpty()) {
            Posicao atual = fila.remove();
            if (atual.x == destinoX && atual.y == destinoY) {
                List<Posicao> caminho = new ArrayList<>();
                Posicao passo = atual;
                while (!(passo.x == inicioX && passo.y == inicioY)) {
                    caminho.add(passo);
                    passo = pai[passo.x][passo.y];
                }
                java.util.Collections.reverse(caminho);
                return caminho;
            }
            for (int d = 0; d < vizinhos.length; d++) {
                int nx = atual.x + vizinhos[d][0];
                int ny = atual.y + vizinhos[d][1];
                if (estaDentro(nx, ny) && !visitado[nx][ny]
                        && (memoria[nx][ny] == Status.SAFE || memoria[nx][ny] == Status.VISITED)) {
                    visitado[nx][ny] = true;
                    pai[nx][ny] = atual;
                    fila.add(new Posicao(nx, ny));
                }
            }
        }
        return null;
    }

    private void moverUmPasso(int x, int y) {
        int alvoDirecao = 0;
        if (x > jogoAgente.getPosicaoX()) {
            alvoDirecao = 0; // Baixo
        } else if (x < jogoAgente.getPosicaoX()) {
            alvoDirecao = 2; // Cima
        } else if (y > jogoAgente.getPosicaoY()) {
            alvoDirecao = 3; // Direita
        } else if (y < jogoAgente.getPosicaoY()) {
            alvoDirecao = 1; // Esquerda
        }

        // Girar até apontar na direção correta
        while (jogoAgente.getDirecao() != alvoDirecao) {
            runOnFxThread(controller::girarDireita);
            esperar(250);
        }
        
        // Mover para frente (mesma ação do jogador)
        runOnFxThread(controller::moverFrente);
        esperar(250);
        atualizarConhecimento();
    }


    private int contarAvisos(String aviso) {
        if (aviso == null || aviso.isBlank()) {
            return 0;
        }
        return aviso.split("\\R+").length;
    }

    private void runOnFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
            return;
        }
        final java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                action.run();
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void esperar(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Posicao obterFrente() {
        int x = jogoAgente.getPosicaoX();
        int y = jogoAgente.getPosicaoY();
        int direcao = jogoAgente.getDirecao();
        int nx = x;
        int ny = y;
        switch (direcao) {
            case 0 -> nx++;
            case 1 -> ny--;
            case 2 -> nx--;
            case 3 -> ny++;
        }
        if (estaDentro(nx, ny)) {
            return new Posicao(nx, ny);
        }
        return null;
    }

    private static class Posicao {
        final int x;
        final int y;

        Posicao(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Posicao)) return false;
            Posicao posicao = (Posicao) o;
            return x == posicao.x && y == posicao.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}

