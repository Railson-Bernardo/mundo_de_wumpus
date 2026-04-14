# Mundo de Wumpus
    Repositório da disciplina de Inteligência Computacional.
    Objetivo implementar o jogo mundo de wumpus

### Descrição
    O Mundo do Wumpus é caracterizado por um labirinto repleto de abismos e pelo monstro wumpus.
    O objetivo é sair da caverna com a maior quantidade de ouro possível.
    No interior da caverna, deve-se ficar atento as indicações de perigo, uma vez que o agente não conhece a localização dos obstáculos.
    Para identificar o perigo, o agente é dotado de percepções que o ajudam a sentir brisas, cheiros e ouvir barulhos.

### Imformações Adicionais
    O labirinto deve ser representado por uma matriz 6 x 6.
    O inicio e a saída ficam na posição [1, 1] do labirinto.
    O agente pode mover-se pelo labirinto, pegar o ouro, atirar flecha e sair do labirinto.
    A ação de movimento possui o custo de -1, pegar ouro +1000, cair em um buraco -1000, ser devorado -1000, atirar flecha -10.
    Em salas adjacentes aos seus respectivos eventos, exeto diagonal, o agente deve ser capaz de sentir o fedor do wumpus, sentir a brisa do abismo, ouvir barulho de morcego, ver o brilho do ouro.
    As laterais do labirinto são paredes e ao caminhar contra elas o agente sente um impacto.
    O agente ouve um grito quando o wumpus morre.
    O labirinto de possuir 2 wumpus, 4 buracos, 2 morcegos, 3 ouros.
    A posição dos elementos é aleatoria.
    Ao entrar em salas de morcego gigante, o agente é levado para outra sala aleatoria, após levar o agente o morcego retorna para sua sala original.
    O jogo acaba quando o agente sair do labirinto ou quando morrer.

    
