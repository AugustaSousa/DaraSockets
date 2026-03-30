package src.network;

import java.io.*;
import java.net.*;

public class Servidor {
    
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5056);
        
        System.out.println("=== SERVIDOR DARA ===");
        System.out.println("Aguardando Jogador 1...");
        System.out.println("Porta: 5056");

        Socket jogador1 = null;
        Socket jogador2 = null;
        
        try {
            jogador1 = serverSocket.accept();
            DataInputStream in1 = new DataInputStream(jogador1.getInputStream());
            DataOutputStream out1 = new DataOutputStream(jogador1.getOutputStream());
            final String apelido1 = in1.readUTF();
            System.out.println("[OK] Jogador 1 conectado: " + apelido1);
            
            System.out.println("Aguardando Jogador 2...");
            
            jogador2 = serverSocket.accept();
            DataInputStream in2 = new DataInputStream(jogador2.getInputStream());
            DataOutputStream out2 = new DataOutputStream(jogador2.getOutputStream());
            final String apelido2 = in2.readUTF();
            System.out.println("[OK] Jogador 2 conectado: " + apelido2);
            System.out.println("=== PARTIDA INICIADA! ===");
            System.out.println(apelido1 + " vs " + apelido2);
            
            out1.writeUTF("escolherOrdem");
            out1.flush();
            
            String escolha = in1.readUTF();
            System.out.println(apelido1 + " escolheu: " + escolha);
            
            if (escolha.equals("PRIMEIRO")) {
                out1.writeUTF("primeiroAJogar@" + apelido2);
                out2.writeUTF("segundoAJogar@" + apelido1);
                System.out.println("Ordem: " + apelido1 + " é PRIMEIRO, " + apelido2 + " é SEGUNDO");
            } else if (escolha.equals("SEGUNDO")) {
                out1.writeUTF("segundoAJogar@" + apelido2);
                out2.writeUTF("primeiroAJogar@" + apelido1);
                System.out.println("Ordem: " + apelido1 + " é SEGUNDO, " + apelido2 + " é PRIMEIRO");
            } else {
                int rand = (int)(Math.random() * 2);
                if (rand == 0) {
                    out1.writeUTF("primeiroAJogar@" + apelido2);
                    out2.writeUTF("segundoAJogar@" + apelido1);
                } else {
                    out1.writeUTF("segundoAJogar@" + apelido2);
                    out2.writeUTF("primeiroAJogar@" + apelido1);
                }
            }
            out1.flush();
            out2.flush();
            
            final DataOutputStream out1Final = out1;
            final DataOutputStream out2Final = out2;
            final DataInputStream in1Final = in1;
            final DataInputStream in2Final = in2;
            
            Thread encaminhaJ1paraJ2 = new Thread(() -> {
                try {
                    while (true) {
                        String msg = in1Final.readUTF();
                        System.out.println(apelido1 + " -> " + apelido2 + ": " + msg);
                        out2Final.writeUTF(msg);
                        out2Final.flush();
                    }
                } catch (IOException e) {
                    System.out.println(apelido1 + " desconectou!");
                }
            });
            
            Thread encaminhaJ2paraJ1 = new Thread(() -> {
                try {
                    while (true) {
                        String msg = in2Final.readUTF();
                        System.out.println(apelido2 + " -> " + apelido1 + ": " + msg);
                        out1Final.writeUTF(msg);
                        out1Final.flush();
                    }
                } catch (IOException e) {
                    System.out.println(apelido2 + " desconectou!");
                }
            });
            
            encaminhaJ1paraJ2.start();
            encaminhaJ2paraJ1.start();
            
            encaminhaJ1paraJ2.join();
            encaminhaJ2paraJ1.join();
            
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            if (jogador1 != null) jogador1.close();
            if (jogador2 != null) jogador2.close();
        } finally {
            serverSocket.close();
            System.out.println("Servidor encerrado.");
        }
    }
}