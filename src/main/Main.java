package src.main;

import src.controller.GameController;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        String apelido = JOptionPane.showInputDialog(null, 
            "Digite seu apelido:", 
            "Identificação do Jogador", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (apelido == null || apelido.trim().isEmpty()) {
            apelido = "Jogador";
        }
        
        new GameController(apelido);
    }
}