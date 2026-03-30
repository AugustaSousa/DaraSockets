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
        setSize(900, 750);
        setLayout(new BorderLayout());
        
        // Painel superior com botões
        JPanel panelTop = new JPanel();
        btnDesistir = new JButton("Desistir");
        btnEmpate = new JButton("Pedir Empate");
        btnDesistir.addActionListener(this);
        btnEmpate.addActionListener(this);
        panelTop.add(btnDesistir);
        panelTop.add(btnEmpate);
        add(panelTop, BorderLayout.NORTH);
        
        // Canvas do jogo
        canvas = new Canvas(controller);
        add(canvas, BorderLayout.CENTER);
        
        // Painel direito (chat + status)
        JPanel panelRight = new JPanel(new BorderLayout());
        panelRight.setPreferredSize(new Dimension(280, 0));
        
        // Chat
        JPanel panelChat = new JPanel(new BorderLayout());
        panelChat.setBorder(BorderFactory.createTitledBorder("Chat"));
        textAreaChat = new JTextArea();
        textAreaChat.setEditable(false);
        textAreaChat.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panelChat.add(new JScrollPane(textAreaChat), BorderLayout.CENTER);
        
        JPanel panelEnvio = new JPanel(new BorderLayout());
        textFieldChat = new JTextField();
        btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(this);
        panelEnvio.add(textFieldChat, BorderLayout.CENTER);
        panelEnvio.add(btnEnviar, BorderLayout.EAST);
        panelChat.add(panelEnvio, BorderLayout.SOUTH);
        
        panelRight.add(panelChat, BorderLayout.CENTER);
        
        // Status
        JPanel panelStatus = new JPanel(new BorderLayout());
        panelStatus.setBorder(BorderFactory.createTitledBorder("Status"));
        textAreaStatus = new JTextArea();
        textAreaStatus.setEditable(false);
        textAreaStatus.setFont(new Font("Monospaced", Font.PLAIN, 12));
        panelStatus.add(new JScrollPane(textAreaStatus), BorderLayout.CENTER);
        panelStatus.setPreferredSize(new Dimension(280, 150));
        panelRight.add(panelStatus, BorderLayout.SOUTH);
        
        add(panelRight, BorderLayout.EAST);
        
        // Console
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Console"));
        panelLog.setPreferredSize(new Dimension(0, 120));
        textAreaLog = new JTextArea();
        textAreaLog.setEditable(false);
        textAreaLog.setFont(new Font("Monospaced", Font.PLAIN, 10));
        panelLog.add(new JScrollPane(textAreaLog), BorderLayout.CENTER);
        add(panelLog, BorderLayout.SOUTH);
        
        // Redireciona System.out para o console
        TextAreaOutputStream consoleStream = new TextAreaOutputStream(textAreaLog, "LOG");
        System.setOut(new PrintStream(consoleStream));
    }
    
    // Controle de estados
    
    public void mudarEstado(String estado) {
        canvas.setEstado(estado);
        atualizarStatus();
    }
    
    public void atualizarStatus() {
        textAreaStatus.setText(controller.getJogo().getStatusText());
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
    
    // Dialogos
    
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
            controller.enviarOrdem("queroNovamente");
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
    
    // eventos
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        
        if (comando.equals("Desistir")) {
            int resposta = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja desistir?",
                "Desistir",
                JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                controller.desistir();
            }
        } else if (comando.equals("Pedir Empate")) {
            controller.pedirEmpate();
        } else if (comando.equals("Enviar")) {
            controller.enviarChat(textFieldChat.getText());
            textFieldChat.setText("");
        }
    }
    
    public Canvas getCanvas() { return canvas; }
}