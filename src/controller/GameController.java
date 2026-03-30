package src.controller;

import src.model.Jogo;
import src.model.Tabuleiro;
import src.network.Cliente;
import src.view.Ui;
import java.awt.Color;

public class GameController {
    private Jogo jogo;
    private Cliente cliente;
    private Ui ui;
    private String meuNome;
    private String oponenteNome;
    private boolean corEscolhida;
    private Color minhaCor;
    private Color corAdversaria;
    
    // Cores disponíveis
    private Color[] cores = {
        new Color(255, 68, 68),   // Vermelho
        new Color(68, 255, 68),   // Verde
        new Color(68, 68, 255),   // Azul
        new Color(255, 255, 68),  // Amarelo
        new Color(255, 68, 255),  // Magenta
        new Color(68, 255, 255)   // Ciano
    };
    
    private String[] nomesCores = {"Vermelho", "Verde", "Azul", "Amarelo", "Magenta", "Ciano"};
    
    public GameController(String nome) {
        this.meuNome = nome;
        this.jogo = new Jogo();
        this.ui = new Ui(this);
        this.cliente = new Cliente("localhost", 5056, nome, this);
        this.corEscolhida = false;
        
        ui.iniciar();
    }
    
    // rede
    
    public void onMensagemRecebida(String mensagem) {
        System.out.println("Recebido: " + mensagem);
        String[] partes = mensagem.split("@", 2);
        String comando = partes[0];
        String dados = partes.length > 1 ? partes[1] : "";
        
        switch (comando) {
            case "tuEhPrimeiro":
                jogo.setEuSou(0);
                oponenteNome = dados;
                ui.setOponenteNome(oponenteNome);
                ui.atualizarTitulo(meuNome + " vs " + oponenteNome);
                ui.mudarEstado("SelecionandoCor");
                ui.mostrarMensagem("Você será o primeiro a jogar! Oponente: " + oponenteNome);
                break;
                
            case "tuEhSegundo":
                jogo.setEuSou(1);
                oponenteNome = dados;
                ui.setOponenteNome(oponenteNome);
                ui.atualizarTitulo(meuNome + " vs " + oponenteNome);
                ui.mudarEstado("EsperandoCorAdversaria");
                ui.mostrarMensagem("Você será o segundo a jogar! Oponente: " + oponenteNome);
                break;
                
            case "ESCOLHER_ORDEM":
                ui.mudarEstado("OrdemJogada");
                ui.mostrarDialogoOrdem();
                break;
                
            case "recebeCorAdversaria":
                int idxCor = Integer.parseInt(dados);
                receberCorOponente(idxCor);
                break;
                
            case "posicionamento":
                jogo.processarJogadaOponente("posicionamento", dados);
                ui.atualizarTabuleiro();
                ui.atualizarStatus();
                break;
                
            case "jogada":
                jogo.processarJogadaOponente("jogada", dados);
                ui.atualizarTabuleiro();
                ui.atualizarStatus();
                break;
                
            case "captura":
                jogo.processarJogadaOponente("captura", dados);
                ui.atualizarTabuleiro();
                ui.atualizarStatus();
                break;
                
            case "empate":
                ui.mostrarDialogoEmpate();
                break;
                
            case "empateAceito":
                ui.mostrarMensagem("Oponente aceitou o empate! Nova partida iniciada.");
                resetarPartida();
                break;
                
            case "empateNegado":
                ui.mostrarMensagem("Oponente recusou o empate.");
                break;
                
            case "queroNovamente":
                ui.mostrarDialogoRevanche();
                break;
                
            case "rematchAceito":
                ui.mostrarMensagem("Revanche aceita! Nova partida iniciada.");
                resetarPartida();
                break;
                
            case "rematchNegado":
                ui.mostrarMensagem("Oponente não quer jogar novamente.");
                break;
                
            case "queroParar":
                ui.mostrarMensagem("Oponente encerrou o jogo.");
                ui.fechar();
                break;
                
            case "exit":
                ui.mostrarMensagem("Conexão encerrada pelo oponente.");
                ui.fechar();
                break;
                
            case "chat":
                ui.adicionarMensagemChat(dados);
                break;
        }
        
        // Verifica vitória
        if (jogo.getVencedor() != -1) {
            ui.mostrarDialogoFimJogo(jogo.getVencedor() == jogo.getEuSou());
        }
    }
    
    public void onErroRede(String erro) {
        System.err.println("Erro de rede: " + erro);
        ui.mostrarMensagem("Erro de conexão: " + erro);
        ui.fechar();
    }
    
    // ui
    
    public void enviarOrdem(String ordem) {
        cliente.enviar(ordem);
    }
    
    public void enviarCorEscolhida(int idxCor) {
        if (!corEscolhida) {
            minhaCor = cores[idxCor];
            corEscolhida = true;
            cliente.enviar("recebeCorAdversaria@" + idxCor);
            ui.setMinhaCor(minhaCor);
            
            // Se já recebeu a cor do oponente, inicia o jogo
            if (corAdversaria != null) {
                ui.mudarEstado("Jogando");
                ui.atualizarStatus();
            } else {
                ui.mudarEstado("EsperandoCorAdversaria");
            }
        }
    }
    
    private void receberCorOponente(int idxCor) {
        corAdversaria = cores[idxCor];
        ui.setCorAdversaria(corAdversaria);
        
        if (corEscolhida) {
            ui.mudarEstado("Jogando");
            ui.atualizarStatus();
            ui.mostrarMensagem("Jogo iniciado! " + (jogo.isMinhaVez() ? "Sua vez!" : "Aguarde o oponente..."));
        } else {
            ui.mudarEstado("SelecionandoCor");
            ui.mostrarMensagem("Cor do oponente: " + nomesCores[idxCor] + ". Agora escolha a sua cor!");
        }
        ui.atualizarTabuleiro();
    }
    
    public void realizarJogada(int posicao) {
        if (!jogo.isMinhaVez()) {
            ui.mostrarMensagem("Aguarde sua vez!");
            return;
        }
        
        if (jogo.getVencedor() != -1) return;
        
        boolean sucesso = false;
        String tipoJogada = "";
        String dados = "";
        
        if (jogo.isFasePosicionamento()) {
            sucesso = jogo.posicionarPeca(posicao);
            if (sucesso) {
                tipoJogada = "posicionamento";
                dados = String.valueOf(posicao);
            }
        } else if (jogo.isAguardandoCaptura()) {
            sucesso = jogo.capturarPeca(posicao);
            if (sucesso) {
                tipoJogada = "captura";
                dados = String.valueOf(posicao);
            }
        }
        
        if (sucesso) {
            cliente.enviar(tipoJogada + "@" + dados);
            ui.atualizarTabuleiro();
            ui.atualizarStatus();
        }
    }
    
    public void realizarMovimento(int origem, int destino) {
        if (!jogo.isMinhaVez()) return;
        if (jogo.getVencedor() != -1) return;
        
        if (jogo.moverPeca(origem, destino)) {
            cliente.enviar("jogada@" + origem + "," + destino);
            ui.atualizarTabuleiro();
            ui.atualizarStatus();
        }
    }
    
    public void enviarChat(String mensagem) {
        if (!mensagem.trim().isEmpty()) {
            cliente.enviar("chat@" + meuNome + ": " + mensagem);
            ui.adicionarMensagemChat(meuNome + ": " + mensagem);
        }
    }
    
    public void pedirEmpate() {
        if (jogo.isFasePosicionamento()) {
            ui.mostrarMensagem("Aguarde o fim da colocação das peças!");
        } else {
            cliente.enviar("empate@empate");
            ui.mostrarMensagem("Pedido de empate enviado!");
        }
    }
    
    public void desistir() {
        cliente.enviar("queroParar@queroParar");
        ui.fechar();
    }
    
    public void aceitarEmpate() {
        cliente.enviar("empateAceito@empateAceito");
        resetarPartida();
    }
    
    public void recusarEmpate() {
        cliente.enviar("empateNegado@empateNegado");
    }
    
    public void aceitarRevanche() {
        cliente.enviar("rematchAceito@rematchAceito");
        resetarPartida();
    }
    
    public void recusarRevanche() {
        cliente.enviar("rematchNegado@rematchNegado");
        ui.fechar();
    }
    
    private void resetarPartida() {
        jogo.reset();
        corEscolhida = false;
        minhaCor = null;
        corAdversaria = null;
        ui.resetarUI();
        ui.mudarEstado("OrdemJogada");
    }
    
    // getters
    
    public Jogo getJogo() { return jogo; }
    public String getMeuNome() { return meuNome; }
    public Color getMinhaCor() { return minhaCor; }
    public Color getCorAdversaria() { return corAdversaria; }
    public Color[] getCores() { return cores; }
    public String[] getNomesCores() { return nomesCores; }
}