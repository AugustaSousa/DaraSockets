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
        this.setBackground(new Color(240, 220, 180));
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
        int startX = (jogo.getEuSou() == 0) ? 100 : 350;
        
        for (int i = 0; i < controller.getCores().length; i++) {
            int corX = startX + (i % 3) * 90;
            int corY = 100 + (i / 3) * 90;
            
            if (x >= corX && x <= corX + 50 && y >= corY && y <= corY + 50) {
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
        g2d.setColor(new Color(200, 160, 120));
        g2d.fillRect(offsetX - 5, offsetY - 5, 6 * cellWidth + 10, 5 * cellHeight + 10);
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        
        for (int i = 0; i < 5; i++) {
            g2d.drawRect(offsetX, offsetY + i * cellHeight, 6 * cellWidth, cellHeight);
        }
        for (int i = 0; i < 6; i++) {
            g2d.drawRect(offsetX + i * cellWidth, offsetY, cellWidth, 5 * cellHeight);
        }
        
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
                
                if (valor != -1) {
                    int x = offsetX + j * cellWidth + cellWidth / 2;
                    int y = offsetY + i * cellHeight + cellHeight / 2;
                    int raio = cellWidth / 3;
                    
                    Color cor;
                    if (valor == 2) {
                        cor = Color.YELLOW;
                    } else if (valor == jogo.getEuSou()) {
                        cor = minhaCor != null ? minhaCor : Color.RED;
                    } else {
                        cor = corAdversaria != null ? corAdversaria : Color.BLUE;
                    }
                    
                    g2d.setColor(cor);
                    g2d.fillOval(x - raio, y - raio, raio * 2, raio * 2);
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawOval(x - raio, y - raio, raio * 2, raio * 2);
                    
                    if (posicaoSelecionada == idx) {
                        g2d.setColor(Color.YELLOW);
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
        
        Color minhaCorAtual = minhaCor != null ? minhaCor : Color.RED;
        Color corOponente = corAdversaria != null ? corAdversaria : Color.BLUE;
        
        // jogador
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Suas peças: " + pecas[meuNumero], 20, 30);
        for (int i = 0; i < Math.min(pecas[meuNumero], 8); i++) {
            g2d.setColor(minhaCorAtual);
            g2d.fillOval(20, 40 + i * 22, 25, 25);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(20, 40 + i * 22, 25, 25);
        }
        
        // oponente
        g2d.setColor(Color.BLACK);
        g2d.drawString("Peças do oponente: " + pecas[1 - meuNumero], getWidth() - 150, 30);
        for (int i = 0; i < Math.min(pecas[1 - meuNumero], 8); i++) {
            g2d.setColor(corOponente);
            g2d.fillOval(getWidth() - 60, 40 + i * 22, 25, 25);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(getWidth() - 60, 40 + i * 22, 25, 25);
        }
        
        // linha de captura
        if (jogo.isAguardandoCaptura() && jogo.getLinhaFormada() >= 0) {
            g2d.setColor(new Color(255, 200, 100, 100));
            int y = offsetY + jogo.getLinhaFormada() * cellHeight;
            g2d.fillRect(offsetX, y, 6 * cellWidth, cellHeight);
        }
        
        // informações de fase
        String fase = jogo.isFasePosicionamento() ? "COLOCAÇÃO" : "MOVIMENTAÇÃO";
        g2d.setColor(Color.BLACK);
        g2d.drawString("Fase: " + fase, getWidth() / 2 - 50, 25);
        
        if (jogo.isAguardandoCaptura() && jogo.isMinhaVez()) {
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString("CLIQUE EM UMA PEÇA DO OPONENTE PARA CAPTURAR!", 
                           getWidth() / 2 - 200, getHeight() - 20);
        }
    }
    
    private void desenharSelecaoCor(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Escolha a cor das suas peças:", 180, 50);
        
        int startX = (jogo.getEuSou() == 0) ? 100 : 350;
        Color[] cores = controller.getCores();
        
        for (int i = 0; i < cores.length; i++) {
            int x = startX + (i % 3) * 90;
            int y = 100 + (i / 3) * 90;
            
            g2d.setColor(cores[i]);
            g2d.fillOval(x, y, 50, 50);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(x, y, 50, 50);
        }
    }
    
    private void desenharMensagem(Graphics2D g2d, String mensagem) {
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.BLUE);
        
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(mensagem)) / 2;
        int y = getHeight() / 2;
        
        g2d.drawString(mensagem, x, y);
    }
    
    // setters
    
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