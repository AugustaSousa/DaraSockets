package src.view;

import src.controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

public class Ui extends JFrame implements ActionListener {
    
    private GameController controller;
    private Canvas canvas;
    
    private JTextArea textAreaChat;
    private JTextField textFieldChat;
    private JButton btnEnviar;
    private JButton btnDesistir;
    private JButton btnEmpate;
    private JTextArea textAreaStatus;
    private JTextArea textAreaLog;
    
    private String meuNome;
    private String oponenteNome;
    
    // Cores para o tema claro
    private Color corFundo = new Color(240, 240, 245);
    private Color corPainel = new Color(255, 255, 255);
    private Color corBorda = new Color(200, 200, 210);
    private Color corTexto = new Color(50, 50, 60);
    private Color corDestaque = new Color(70, 130, 200);
    
    public Ui(GameController controller) {
        this.controller = controller;
        this.meuNome = controller.getMeuNome();
        setupUI();
    }
    
    public void iniciar() {
        setVisible(true);
    }
    
    private void setupUI() {
        setTitle(meuNome + " - Aguardando oponente...");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());
        
        // Painel principal com fundo claro
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(corFundo);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);
        
        // ========== CENTRO: Canvas do jogo ==========
        canvas = new Canvas(controller);
        canvas.setPreferredSize(new Dimension(550, 500));
        canvas.setBackground(corPainel);
        canvas.setBorder(BorderFactory.createLineBorder(corBorda, 1));
        mainPanel.add(canvas, BorderLayout.CENTER);
        
        // ========== PAINEL DIREITO (Status + Console + Botões) ==========
        JPanel panelRight = new JPanel();
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.Y_AXIS));
        panelRight.setPreferredSize(new Dimension(280, 0));
        panelRight.setBackground(corFundo);
        panelRight.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        // --- Painel de Status ---
        JPanel panelStatus = criarPanelEstilizado(corPainel);
        panelStatus.setLayout(new BorderLayout());
        panelStatus.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(corBorda, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblStatus = new JLabel("STATUS DO JOGO");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(corDestaque);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        panelStatus.add(lblStatus, BorderLayout.NORTH);
        
        textAreaStatus = new JTextArea();
        textAreaStatus.setEditable(false);
        textAreaStatus.setFont(new Font("Monospaced", Font.BOLD, 13));
        textAreaStatus.setBackground(corPainel);
        textAreaStatus.setForeground(corTexto);
        textAreaStatus.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        textAreaStatus.setAlignmentX(CENTER_ALIGNMENT);
        panelStatus.add(textAreaStatus, BorderLayout.CENTER);
        
        panelStatus.setMaximumSize(new Dimension(280, 120));
        panelRight.add(panelStatus);
        panelRight.add(Box.createVerticalStrut(10));
        
        // --- Painel de Console ---
        JPanel panelLog = criarPanelEstilizado(corPainel);
        panelLog.setLayout(new BorderLayout());
        panelLog.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(corBorda, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblLog = new JLabel("CONSOLE");
        lblLog.setFont(new Font("Arial", Font.BOLD, 14));
        lblLog.setForeground(corDestaque);
        lblLog.setHorizontalAlignment(SwingConstants.CENTER);
        panelLog.add(lblLog, BorderLayout.NORTH);
        
        textAreaLog = new JTextArea();
        textAreaLog.setEditable(false);
        textAreaLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textAreaLog.setBackground(corPainel);
        textAreaLog.setForeground(corTexto);
        textAreaLog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollLog = new JScrollPane(textAreaLog);
        scrollLog.setBorder(BorderFactory.createEmptyBorder());
        scrollLog.getViewport().setBackground(corPainel);
        scrollLog.setPreferredSize(new Dimension(280, 150));
        panelLog.add(scrollLog, BorderLayout.CENTER);
        
        panelLog.setMaximumSize(new Dimension(280, 200));
        panelRight.add(panelLog);
        panelRight.add(Box.createVerticalStrut(10));
        
        // --- Painel de Botões (Desistir e Empate) ---
        JPanel panelBotoes = criarPanelEstilizado(corPainel);
        panelBotoes.setLayout(new GridLayout(2, 1, 5, 10));
        panelBotoes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(corBorda, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        btnDesistir = criarBotaoEstilizado("DESISTIR", new Color(220, 80, 80));
        btnEmpate = criarBotaoEstilizado("PEDIR EMPATE", new Color(80, 120, 200));
        
        panelBotoes.add(btnDesistir);
        panelBotoes.add(btnEmpate);
        
        panelBotoes.setMaximumSize(new Dimension(280, 100));
        panelRight.add(panelBotoes);
        
        mainPanel.add(panelRight, BorderLayout.EAST);
        
        // ========== PAINEL INFERIOR (Chat) ==========
        JPanel panelBottom = criarPanelEstilizado(corPainel);
        panelBottom.setLayout(new BorderLayout());
        panelBottom.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(corBorda, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panelBottom.setPreferredSize(new Dimension(0, 140));
        
        JLabel lblChat = new JLabel("CHAT");
        lblChat.setFont(new Font("Arial", Font.BOLD, 14));
        lblChat.setForeground(corDestaque);
        panelBottom.add(lblChat, BorderLayout.NORTH);
        
        // Área de mensagens do chat
        textAreaChat = new JTextArea();
        textAreaChat.setEditable(false);
        textAreaChat.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textAreaChat.setBackground(corPainel);
        textAreaChat.setForeground(corTexto);
        textAreaChat.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollChat = new JScrollPane(textAreaChat);
        scrollChat.setBorder(BorderFactory.createEmptyBorder());
        scrollChat.getViewport().setBackground(corPainel);
        panelBottom.add(scrollChat, BorderLayout.CENTER);
        
        // Painel de envio de mensagem
        JPanel panelEnvio = new JPanel(new BorderLayout());
        panelEnvio.setBackground(corPainel);
        panelEnvio.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        textFieldChat = new JTextField();
        textFieldChat.setBackground(corPainel);
        textFieldChat.setForeground(corTexto);
        textFieldChat.setCaretColor(corTexto);
        textFieldChat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(corBorda, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        btnEnviar = criarBotaoEstilizado("Enviar", new Color(70, 130, 70));
        
        panelEnvio.add(textFieldChat, BorderLayout.CENTER);
        panelEnvio.add(btnEnviar, BorderLayout.EAST);
        panelBottom.add(panelEnvio, BorderLayout.SOUTH);
        
        mainPanel.add(panelBottom, BorderLayout.SOUTH);
        
        // Adiciona listeners
        btnDesistir.addActionListener(this);
        btnEmpate.addActionListener(this);
        btnEnviar.addActionListener(this);
        textFieldChat.addActionListener(this);
        
        // Redireciona System.out para o console
        TextAreaOutputStream consoleStream = new TextAreaOutputStream(textAreaLog, "");
        System.setOut(new PrintStream(consoleStream));
        
        System.out.println("=== Jogo Dara Iniciado ===");
        System.out.println("Jogador: " + meuNome);
    }
    
    private JPanel criarPanelEstilizado(Color cor) {
        JPanel panel = new JPanel();
        panel.setBackground(cor);
        return panel;
    }
    
    private JButton criarBotaoEstilizado(String texto, Color corFundo) {
        JButton botao = new JButton(texto);
        botao.setBackground(corFundo);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Arial", Font.BOLD, 13));
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(corFundo.darker(), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efeito hover
        botao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                botao.setBackground(corFundo.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                botao.setBackground(corFundo);
            }
        });
        
        return botao;
    }
    
    // ========== Controle de estados ==========
    
    public void mudarEstado(String estado) {
        canvas.setEstado(estado);
        atualizarStatus();
    }
    
    public void atualizarStatus() {
        String status = controller.getJogo().getStatusText();
        textAreaStatus.setText(status);
        textAreaStatus.setAlignmentX(CENTER_ALIGNMENT);
    }
    
    public void atualizarTabuleiro() {
        canvas.repaint();
    }
    
    public void setOponenteNome(String nome) {
        this.oponenteNome = nome;
    }
    
    public void atualizarTitulo(String titulo) {
        setTitle(titulo);
    }
    
    public void setMinhaCor(Color cor) {
        canvas.setMinhaCor(cor);
    }
    
    public void setCorAdversaria(Color cor) {
        canvas.setCorAdversaria(cor);
    }
    
    public void resetarUI() {
        canvas.resetar();
        textAreaChat.setText("");
    }
    
    // ========== Diálogos ==========
    
    public void mostrarDialogoOrdem() {
        String[] opcoes = {"SER PRIMEIRO", "SER SEGUNDO", "ALEATÓRIO"};
        int escolha = JOptionPane.showOptionDialog(this,
            "Como deseja definir a ordem de jogo?",
            "Ordem de jogada",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opcoes,
            opcoes[0]);
        
        String ordem;
        if (escolha == 0) {
            ordem = "PRIMEIRO";
        } else if (escolha == 1) {
            ordem = "SEGUNDO";
        } else {
            ordem = "ALEATORIO";
        }
        
        controller.enviarOrdem(ordem);
    }
    
    public void mostrarDialogoEmpate() {
        int resposta = JOptionPane.showConfirmDialog(this,
            "Seu oponente propôs empate. Você aceita?",
            "Proposta de empate",
            JOptionPane.YES_NO_OPTION);
        
        if (resposta == JOptionPane.YES_OPTION) {
            controller.aceitarEmpate();
        } else {
            controller.recusarEmpate();
        }
    }
    
    public void mostrarDialogoRevanche() {
        int resposta = JOptionPane.showConfirmDialog(this,
            "Seu oponente propôs uma nova partida. Você aceita?",
            "Proposta de revanche",
            JOptionPane.YES_NO_OPTION);
        
        if (resposta == JOptionPane.YES_OPTION) {
            controller.aceitarRevanche();
        } else {
            controller.recusarRevanche();
        }
    }
    
    public void mostrarDialogoFimJogo(boolean venceu) {
        String mensagem = venceu ? "Parabéns! Você venceu!" : "Você perdeu!";
        int resposta = JOptionPane.showConfirmDialog(this,
            mensagem + "\nDeseja jogar novamente?",
            venceu ? "Vitória!" : "Derrota!",
            JOptionPane.YES_NO_OPTION);
        
        if (resposta == JOptionPane.YES_OPTION) {
            controller.enviarOrdem("queroRevanche");
        } else {
            controller.desistir();
        }
    }
    
    public void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void adicionarMensagemChat(String mensagem) {
        textAreaChat.append(mensagem + "\n");
        textAreaChat.setCaretPosition(textAreaChat.getDocument().getLength());
    }
    
    public void fechar() {
        dispose();
        System.exit(0);
    }
    
    // ========== Eventos ==========
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        
        if (comando.equals("DESISTIR")) {
            int resposta = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja desistir?",
                "Desistir",
                JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                controller.desistir();
            }
        } else if (comando.equals("PEDIR EMPATE")) {
            controller.pedirEmpate();
        } else if (comando.equals("Enviar") || e.getSource() == textFieldChat) {
            controller.enviarChat(textFieldChat.getText());
            textFieldChat.setText("");
        }
    }
    
    public Canvas getCanvas() { return canvas; }
}