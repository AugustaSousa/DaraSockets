package src.view;

import src.controller.GameController;
import src.model.Jogo;
import src.model.Tabuleiro;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Canvas extends JPanel {
    
    private GameController controller;
    private Jogo jogo;
    
    private int cellWidth = 70;
    private int cellHeight = 70;
    private int offsetX = 80;
    private int offsetY = 80;
    
    private String estado = "";
    private int posicaoSelecionada = -1;
    private Color minhaCor;
    private Color corAdversaria;
    
    public Canvas(GameController controller) {
        this.controller = controller;
        this.jogo = controller.getJogo();
        this.setBackground(new Color(240, 240, 245));
        this.setPreferredSize(new Dimension(600, 550));
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                processarClique(e.getX(), e.getY());
            }
        });
    }
    
    private void processarClique(int x, int y) {
        switch (estado) {
            case "Jogando":
                processarCliqueJogando(x, y);
                break;
            case "SelecionandoCor":
                processarCliqueCor(x, y);
                break;
        }
    }
    
    private void processarCliqueJogando(int x, int y) {
        int coluna = (x - offsetX) / cellWidth;
        int linha = (y - offsetY) / cellHeight;
        
        if (linha >= 0 && linha < 5 && coluna >= 0 && coluna < 6) {
            int posicao = linha * 6 + coluna;
            int valor = jogo.getTabuleiro().getCasa(posicao);
            
            if (jogo.isAguardandoCaptura()) {
                controller.realizarJogada(posicao);
                posicaoSelecionada = -1;
                repaint();
            } else if (jogo.isFasePosicionamento()) {
                if (valor == -1) {
                    controller.realizarJogada(posicao);
                    repaint();
                } else {
                    mostrarMensagem("Posição já ocupada!");
                }
            } else {
                if (posicaoSelecionada == -1) {
                    if (valor == jogo.getEuSou()) {
                        posicaoSelecionada = posicao;
                        repaint();
                    } else {
                        mostrarMensagem("Selecione uma peça sua!");
                    }
                } else {
                    controller.realizarMovimento(posicaoSelecionada, posicao);
                    posicaoSelecionada = -1;
                    repaint();
                }
            }
        }
    }
    
    private void processarCliqueCor(int x, int y) {
        // Área onde as cores são desenhadas (centralizada)
        int startY = 180;
        int circleSize = 55;
        int spacing = 15;
        int totalWidth = controller.getCores().length * (circleSize + spacing) - spacing;
        int startX = (getWidth() - totalWidth) / 2;
        
        for (int i = 0; i < controller.getCores().length; i++) {
            int corX = startX + i * (circleSize + spacing);
            int corY = startY;
            
            if (x >= corX && x <= corX + circleSize && y >= corY && y <= corY + circleSize) {
                controller.enviarCorEscolhida(i);
                repaint();
                break;
            }
        }
    }
    
    private void mostrarMensagem(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        switch (estado) {
            case "Jogando":
                desenharTabuleiro(g2d);
                desenharPecas(g2d);
                desenharInfo(g2d);
                break;
            case "SelecionandoCor":
                desenharSelecaoCor(g2d);
                break;
            case "EsperandoCorAdversaria":
                desenharMensagem(g2d, "Aguardando escolha do oponente...");
                break;
            case "OrdemJogada":
                desenharMensagem(g2d, "Definindo ordem de jogada...");
                break;
            default:
                desenharMensagem(g2d, "Aguardando conexão...");
                break;
        }
    }
    
    private void desenharTabuleiro(Graphics2D g2d) {
        // Fundo do tabuleiro
        g2d.setColor(new Color(210, 180, 140));
        g2d.fillRect(offsetX - 5, offsetY - 5, 6 * cellWidth + 10, 5 * cellHeight + 10);
        
        // Bordas
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        // Linhas horizontais
        for (int i = 0; i <= 5; i++) {
            g2d.drawLine(offsetX, offsetY + i * cellHeight, 
                        offsetX + 6 * cellWidth, offsetY + i * cellHeight);
        }
        
        // Linhas verticais
        for (int i = 0; i <= 6; i++) {
            g2d.drawLine(offsetX + i * cellWidth, offsetY, 
                        offsetX + i * cellWidth, offsetY + 5 * cellHeight);
        }
        
        // Números das posições
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
        g2d.setColor(Color.DARK_GRAY);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                int x = offsetX + j * cellWidth + 5;
                int y = offsetY + i * cellHeight + 20;
                g2d.drawString(String.valueOf(i * 6 + j), x, y);
            }
        }
    }
    
    private void desenharPecas(Graphics2D g2d) {
        Tabuleiro tabuleiro = jogo.getTabuleiro();
        int[] casas = tabuleiro.getCasas();
        
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                int idx = i * 6 + j;
                int valor = casas[idx];
                
                if (valor != -1 && valor != 2) {
                    int x = offsetX + j * cellWidth + cellWidth / 2;
                    int y = offsetY + i * cellHeight + cellHeight / 2;
                    int raio = cellWidth / 3;
                    
                    Color cor;
                    if (valor == jogo.getEuSou()) {
                        cor = minhaCor != null ? minhaCor : new Color(255, 68, 68);
                    } else {
                        cor = corAdversaria != null ? corAdversaria : new Color(68, 68, 255);
                    }
                    
                    g2d.setColor(cor);
                    g2d.fillOval(x - raio, y - raio, raio * 2, raio * 2);
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawOval(x - raio, y - raio, raio * 2, raio * 2);
                    
                    if (posicaoSelecionada == idx) {
                        g2d.setColor(new Color(255, 255, 100));
                        g2d.setStroke(new BasicStroke(3));
                        g2d.drawOval(x - raio - 3, y - raio - 3, raio * 2 + 6, raio * 2 + 6);
                    }
                }
            }
        }
    }
    
    private void desenharInfo(Graphics2D g2d) {
        int[] pecas = jogo.getPecasRestantes();
        int meuNumero = jogo.getEuSou();
        
        Color minhaCorAtual = minhaCor != null ? minhaCor : new Color(255, 68, 68);
        Color corOponente = corAdversaria != null ? corAdversaria : new Color(68, 68, 255);
        
        // Suas peças (esquerda)
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Suas peças: " + pecas[meuNumero], 20, 30);
        for (int i = 0; i < Math.min(pecas[meuNumero], 8); i++) {
            g2d.setColor(minhaCorAtual);
            g2d.fillOval(20, 40 + i * 22, 25, 25);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(20, 40 + i * 22, 25, 25);
        }
        
        // Peças do oponente (direita)
        g2d.setColor(Color.BLACK);
        g2d.drawString("Peças do oponente: " + pecas[1 - meuNumero], getWidth() - 150, 30);
        for (int i = 0; i < Math.min(pecas[1 - meuNumero], 8); i++) {
            g2d.setColor(corOponente);
            g2d.fillOval(getWidth() - 60, 40 + i * 22, 25, 25);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(getWidth() - 60, 40 + i * 22, 25, 25);
        }
        
        // Fase do jogo
        String fase = jogo.isFasePosicionamento() ? "COLOCAÇÃO" : "MOVIMENTAÇÃO";
        g2d.setColor(new Color(70, 130, 200));
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("FASE: " + fase, getWidth() / 2 - 45, 25);
        
        // Mensagem de captura
        if (jogo.isAguardandoCaptura() && jogo.isMinhaVez()) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("CLIQUE EM UMA PEÇA DO OPONENTE PARA CAPTURAR.", 
                           getWidth() / 2 - 210, getHeight() - 20);
        }
    }
    
    private void desenharSelecaoCor(Graphics2D g2d) {
        // Fundo semi-transparente
        g2d.setColor(new Color(255, 255, 255, 230));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Título
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(new Color(70, 130, 200));
        String titulo = "Escolha a cor das suas peças";
        FontMetrics fm = g2d.getFontMetrics();
        int tituloX = (getWidth() - fm.stringWidth(titulo)) / 2;
        g2d.drawString(titulo, tituloX, 120);
        
        // Sub-título com instrução
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.setColor(new Color(100, 100, 100));
        String instrucao = "Clique em uma das cores abaixo";
        int instrucaoX = (getWidth() - g2d.getFontMetrics().stringWidth(instrucao)) / 2;
        g2d.drawString(instrucao, instrucaoX, 150);
        
        // Cores em linha horizontal centralizada
        Color[] cores = controller.getCores();
        String[] nomesCores = controller.getNomesCores();
        int circleSize = 60;
        int spacing = 20;
        int totalWidth = cores.length * (circleSize + spacing) - spacing;
        int startX = (getWidth() - totalWidth) / 2;
        int startY = 200;
        
        for (int i = 0; i < cores.length; i++) {
            int x = startX + i * (circleSize + spacing);
            int y = startY;
            
            // Círculo da cor
            g2d.setColor(cores[i]);
            g2d.fillOval(x, y, circleSize, circleSize);
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x, y, circleSize, circleSize);
            
            // Nome da cor abaixo
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            g2d.setColor(new Color(80, 80, 80));
            fm = g2d.getFontMetrics();
            int nomeX = x + (circleSize - fm.stringWidth(nomesCores[i])) / 2;
            g2d.drawString(nomesCores[i], nomeX, y + circleSize + 15);
        }
        
        // Mensagem de aguardo (se já recebeu cor do oponente)
        if (corAdversaria != null && minhaCor == null) {
            g2d.setFont(new Font("Arial", Font.ITALIC, 12));
            g2d.setColor(new Color(70, 130, 70));
            String msg = "✓ Cor do oponente já definida! Escolha a sua cor.";
            int msgX = (getWidth() - g2d.getFontMetrics().stringWidth(msg)) / 2;
            g2d.drawString(msg, msgX, startY + circleSize + 50);
        }
    }
    
    private void desenharMensagem(Graphics2D g2d, String mensagem) {
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(new Color(70, 130, 200));
        
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(mensagem)) / 2;
        int y = getHeight() / 2;
        
        g2d.drawString(mensagem, x, y);
    }
    
    // ========== Getters e Setters ==========
    
    public void setEstado(String estado) {
        this.estado = estado;
        repaint();
    }
    
    public void setMinhaCor(Color cor) {
        this.minhaCor = cor;
        repaint();
    }
    
    public void setCorAdversaria(Color cor) {
        this.corAdversaria = cor;
        repaint();
    }
    
    public void resetar() {
        estado = "";
        posicaoSelecionada = -1;
        minhaCor = null;
        corAdversaria = null;
        repaint();
    }
}