package src.model;

import java.util.Arrays;

public class Tabuleiro {
    private int linhas;
    private int colunas;
    private int[] casas; // -1 = vazio, 0 = jogador 0, 1 = jogador 1, 2 = selecionado
    
    public Tabuleiro(int linhas, int colunas) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.casas = new int[linhas * colunas];
        limpar();
    }
    
    public void limpar() {
        Arrays.fill(casas, -1);
    }
    
    public int getCasa(int posicao) {
        if (posicao < 0 || posicao >= casas.length) return -2;
        return casas[posicao];
    }
    
    public void setCasa(int posicao, int valor) {
        if (posicao >= 0 && posicao < casas.length) {
            casas[posicao] = valor;
        }
    }
    
    public boolean posicionarPeca(int jogador, int posicao) {
        if (posicao < 0 || posicao >= casas.length) return false;
        if (casas[posicao] != -1) return false;
        
        casas[posicao] = jogador;
        return true;
    }
    
    public boolean removerPeca(int posicao) {
        if (posicao < 0 || posicao >= casas.length) return false;
        if (casas[posicao] == -1) return false;
        
        casas[posicao] = -1;
        return true;
    }
    
    public boolean moverPeca(int origem, int destino, int jogador) {
        if (origem < 0 || origem >= casas.length) return false;
        if (destino < 0 || destino >= casas.length) return false;
        if (casas[origem] != jogador) return false;
        if (casas[destino] != -1) return false;
        if (!isMovimentoAdjacente(origem, destino)) return false;
        
        casas[destino] = jogador;
        casas[origem] = -1;
        return true;
    }
    
    public boolean capturarPeca(int posicao, int oponente) {
        if (posicao < 0 || posicao >= casas.length) return false;
        if (casas[posicao] != oponente) return false;
        
        casas[posicao] = -1;
        return true;
    }
    
    public boolean isMovimentoAdjacente(int origem, int destino) {
        int linhaOrigem = origem / colunas;
        int colunaOrigem = origem % colunas;
        int linhaDestino = destino / colunas;
        int colunaDestino = destino % colunas;
        
        int diffLinha = Math.abs(linhaOrigem - linhaDestino);
        int diffColuna = Math.abs(colunaOrigem - colunaDestino);
        
        return (diffLinha + diffColuna == 1);
    }
    
    /**
     * Verifica se a posição faz parte de uma linha de 3 peças
     */
    public boolean verificarLinha(int posicao, int jogador) {
        int linha = posicao / colunas;
        int coluna = posicao % colunas;
        
        // Verifica horizontal
        int count = 1;
        for (int c = coluna - 1; c >= 0; c--) {
            if (casas[linha * colunas + c] == jogador) count++;
            else break;
        }
        for (int c = coluna + 1; c < colunas; c++) {
            if (casas[linha * colunas + c] == jogador) count++;
            else break;
        }
        if (count >= 3) return true;
        
        // Verifica vertical
        count = 1;
        for (int l = linha - 1; l >= 0; l--) {
            if (casas[l * colunas + coluna] == jogador) count++;
            else break;
        }
        for (int l = linha + 1; l < linhas; l++) {
            if (casas[l * colunas + coluna] == jogador) count++;
            else break;
        }
        
        return count >= 3;
    }
    
    /**
     * Retorna o tamanho da linha que a posição faz parte
     * Usado para a REGRA 2: não pode formar linha de 4 ou mais
     * Exemplo: se retornar 3 → pode capturar
     *          se retornar 4 ou 5 → jogada inválida
     */
    public int getTamanhoLinha(int posicao, int jogador) {
        int linha = posicao / colunas;
        int coluna = posicao % colunas;
        
        // Verifica horizontal
        int countHorizontal = 1;
        // Para esquerda
        for (int c = coluna - 1; c >= 0; c--) {
            if (casas[linha * colunas + c] == jogador) countHorizontal++;
            else break;
        }
        // Para direita
        for (int c = coluna + 1; c < colunas; c++) {
            if (casas[linha * colunas + c] == jogador) countHorizontal++;
            else break;
        }
        
        // Verifica vertical
        int countVertical = 1;
        // Para cima
        for (int l = linha - 1; l >= 0; l--) {
            if (casas[l * colunas + coluna] == jogador) countVertical++;
            else break;
        }
        // Para baixo
        for (int l = linha + 1; l < linhas; l++) {
            if (casas[l * colunas + coluna] == jogador) countVertical++;
            else break;
        }
        
        // Retorna o maior tamanho (horizontal ou vertical)
        return Math.max(countHorizontal, countVertical);
    }
    
    public int contarPecas(int jogador) {
        int count = 0;
        for (int casa : casas) {
            if (casa == jogador) count++;
        }
        return count;
    }
    
    public int getLinhas() { return linhas; }
    public int getColunas() { return colunas; }
    public int getTotalCasas() { return casas.length; }
    public int[] getCasas() { return casas; }
}