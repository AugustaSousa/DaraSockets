package src.model;

import java.util.Arrays;

public class Jogo {
    private Tabuleiro tabuleiro;
    private int[] pecasRestantes;      // Peças que cada jogador ainda tem para colocar
    private int[] score; // Pontuação
    private int jogadorAtual;  // Quem está jogando agora (0 ou 1)
    private int euSou;    // Qual jogador sou eu (-1 = não definido)
    private boolean fasePosicionamento; // true = colocando peças, false = movendo
    private boolean aguardandoCaptura;  // Se deve capturar após formar linha
    private int linhaFormada;   // Linha que foi formada (para destaque)
    private int vencedor;   // -1 = sem vencedor, 0 ou 1 = vencedor
    private String meuNome;
    
    public Jogo() {
        this.tabuleiro = new Tabuleiro(5, 6); // 5 linhas, 6 colunas
        this.pecasRestantes = new int[]{12, 12};
        this.score = new int[]{0, 0};
        this.jogadorAtual = 0;
        this.euSou = -1;
        this.fasePosicionamento = true;
        this.aguardandoCaptura = false;
        this.linhaFormada = -1;
        this.vencedor = -1;
        this.meuNome = "";
    }
    
    // jogadas
    
    //Posiciona uma peça na fase de colocação
    public boolean posicionarPeca(int posicao) {
        if (!fasePosicionamento) return false;
        if (pecasRestantes[jogadorAtual] <= 0) return false;
        
        if (tabuleiro.posicionarPeca(jogadorAtual, posicao)) {
            pecasRestantes[jogadorAtual]--;
            
            // Verifica se terminou a fase de posicionamento
            if (pecasRestantes[0] == 0 && pecasRestantes[1] == 0) {
                fasePosicionamento = false;
                System.out.println("Fase de movimentação iniciada!");
            }
            
            alternarJogador();
            return true;
        }
        return false;
    }
    
    // Move uma peça na fase de movimentação
    public boolean moverPeca(int origem, int destino) {
        if (fasePosicionamento) return false;
        if (aguardandoCaptura) return false;
        
        if (tabuleiro.moverPeca(origem, destino, jogadorAtual)) {
            // Verifica se formou linha
            if (tabuleiro.verificarLinha(destino, jogadorAtual)) {
                aguardandoCaptura = true;
                linhaFormada = destino / tabuleiro.getColunas();
                System.out.println("Jogador " + jogadorAtual + " formou linha!");
            } else {
                alternarJogador();
            }
            
            verificarVencedor();
            return true;
        }
        return false;
    }
    
    // Captura uma peça do oponente após formar linha
    public boolean capturarPeca(int posicao) {
        if (!aguardandoCaptura) return false;
        
        int oponente = 1 - jogadorAtual;
        if (tabuleiro.capturarPeca(posicao, oponente)) {
            aguardandoCaptura = false;
            alternarJogador();
            verificarVencedor();
            return true;
        }
        return false;
    }
    
    // Seleciona uma peça (para movimentação)
    // Retorna true se a peça foi selecionada, false se foi movimento
    public boolean selecionarPeca(int posicao) {
        if (fasePosicionamento) return false;
        if (aguardandoCaptura) return false;
        if (tabuleiro.getCasa(posicao) != jogadorAtual) return false;
        
        // Marca como selecionada (valor 2 temporário)
        tabuleiro.setCasa(posicao, 2);
        return true;
    }
    
    // Desseleciona a peça atual
    public void desselecionarPeca(int posicao) {
        if (tabuleiro.getCasa(posicao) == 2) {
            tabuleiro.setCasa(posicao, jogadorAtual);
        }
    }
    
    private void alternarJogador() {
        jogadorAtual = (jogadorAtual == 0) ? 1 : 0;
    }
    
    private void verificarVencedor() {
        int pecasJogador0 = tabuleiro.contarPecas(0);
        int pecasJogador1 = tabuleiro.contarPecas(1);
        
        if (pecasJogador0 <= 2) {
            vencedor = 1;
            score[1]++;
        } else if (pecasJogador1 <= 2) {
            vencedor = 0;
            score[0]++;
        }
    }
    
    // recebe jogadas do oponente
    
    public void processarJogadaOponente(String tipo, String dados) {
        switch (tipo) {
            case "posicionamento":
                int pos = Integer.parseInt(dados);
                posicionarPeca(pos);
                break;
            case "jogada":
                String[] mov = dados.split(",");
                moverPeca(Integer.parseInt(mov[0]), Integer.parseInt(mov[1]));
                break;
            case "captura":
                int captura = Integer.parseInt(dados);
                capturarPeca(captura);
                break;
        }
    }
    
    
    public void reset() {
        tabuleiro.limpar();
        pecasRestantes = new int[]{12, 12};
        jogadorAtual = 0;
        fasePosicionamento = true;
        aguardandoCaptura = false;
        linhaFormada = -1;
        vencedor = -1;
    }
    
    public void resetCompleto() {
        reset();
        score = new int[]{0, 0};
        euSou = -1;
        meuNome = "";
    }
    
    // getters
    
    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public int[] getPecasRestantes() { return pecasRestantes; }
    public int[] getScore() { return score; }
    public int getJogadorAtual() { return jogadorAtual; }
    public int getEuSou() { return euSou; }
    public boolean isFasePosicionamento() { return fasePosicionamento; }
    public boolean isAguardandoCaptura() { return aguardandoCaptura; }
    public int getLinhaFormada() { return linhaFormada; }
    public int getVencedor() { return vencedor; }
    public String getMeuNome() { return meuNome; }
    public boolean isMinhaVez() { return euSou == jogadorAtual; }
    public boolean isJogoAtivo() { return vencedor == -1; }
    
    // setters
    
    public void setEuSou(int euSou) { this.euSou = euSou; }
    public void setMeuNome(String nome) { this.meuNome = nome; }
    public void setScore(int[] score) { this.score = score; }
    
    // ui
    // estados
    public String getStatusText() {
        if (vencedor != -1) {
            if (vencedor == euSou) return "VOCÊ VENCEU!";
            return "VOCÊ PERDEU!";
        }
        
        if (fasePosicionamento) {
            if (isMinhaVez()) return "Sua vez - Coloque uma peça!";
            return "Aguardando oponente colocar peça...";
        } else if (aguardandoCaptura) {
            if (isMinhaVez()) return "CAPTURE! Clique em uma peça do oponente!";
            return "Oponente vai capturar...";
        } else {
            if (isMinhaVez()) return "Sua vez - Mova uma peça!";
            return "Aguardando oponente mover...";
        }
    }
}