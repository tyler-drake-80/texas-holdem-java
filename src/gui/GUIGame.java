package gui;

import cards.*;
import game.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import javax.swing.border.Border;
/**
 * Top-level swing window containing the poker table GUI.
 * Initializes:
 *  -Swing frame
 *  -TablePanel (visual layout for players + board)
 *  -the GameEngine (logic)
 * 
 * GameEngine is responsible for pushing state upodates into the GUI
 * once listener interface is implemented.
 */
public class GUIGame extends JFrame{
    private final TablePanel tablePanel;
    private final GameEngine engine;

    public GUIGame(){
        //configure window
        setTitle("Texas Hold'em Poker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //load card images
        CardImageLoader.loadAllCards();

        //initialize table panel
        tablePanel = new TablePanel();
        add(tablePanel, BorderLayout.CENTER);

        //initialize game engine
        engine = new GameEngine();
        /**this is where GUI <-> Engine
         * GameListener will notify GUI of:
         *  -card deals
         *  -betting actions
         *  -state updates (pot, folded players, etc)
         * 
         * WILL WRITE LISTENER CLASS NEXT
         */
        //engine.setListener(new GUIListener(tablePanel, actionPanel));

    }

    public void startGame(){
        setVisible(true);
        //need to convert engine.start() to a stepwise process
        //will call engine.startHand(), engine.nextBettingRound(), etc
        //once we build the listener + action panel, call will be:
        //engine.start(); -- from here
    }

    //main method to launch GUI WITHOUT building launcher
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            GUIGame game = new GUIGame();
            game.startGame();
        });
    }
}
