package gui;

import javax.swing.*;

public class PokerMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUIGame gui = new GUIGame();
            gui.startGame();
        });
    }    
}
