package src.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import src.controller.GameController;

public class Cliente {
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private String apelido;
    private boolean conectado;
    private GameController controller;
    
    public Cliente(String host, int porta, String apelido, GameController controller) {
        this.apelido = apelido;
        this.controller = controller;
        
        try {
            this.socket = new Socket(host, porta);
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());
            
            // Envia identificação
            output.writeUTF(apelido);
            output.flush();
            
            this.conectado = true;
            
            // Inicia thread de recebimento
            new Thread(this::receberMensagens).start();
            
        } catch (Exception e) {
            controller.onErroRede("Erro ao conectar: " + e.getMessage());
        }
    }
    
    private void receberMensagens() {
        try {
            while (conectado) {
                String mensagem = input.readUTF();
                controller.onMensagemRecebida(mensagem);
            }
        } catch (Exception e) {
            if (conectado) {
                controller.onErroRede("Conexão perdida: " + e.getMessage());
            }
        }
    }
    
    public void enviar(String mensagem) {
        try {
            if (conectado && output != null) {
                output.writeUTF(mensagem);
                output.flush();
            }
        } catch (Exception e) {
            controller.onErroRede("Erro ao enviar: " + e.getMessage());
        }
    }
    
    public void desconectar() {
        conectado = false;
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            // Ignora
        }
    }
    
    public boolean isConectado() { return conectado; }
    public String getApelido() { return apelido; }
}