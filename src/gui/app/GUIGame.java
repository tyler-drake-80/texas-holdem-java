package gui.app;

import cards.*;
import game.GameEngine;
import game.Table;
import gui.integration.*;
import gui.view.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import players.Player;
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
    private final ActionPanel actionPanel;
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
        actionPanel = new ActionPanel();
        add(tablePanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

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
        GUIListener guiListener = new GUIListener(tablePanel, actionPanel);
        engine.setListener(guiListener);

    }

    public void startGame(){
        setVisible(true);
        //need to convert engine.start() to a stepwise process
        //will call engine.startHand(), engine.nextBettingRound(), etc
        //once we build the listener + action panel, call will be:
        //engine.start(); -- from here
        //GUIListener guiListener = new GUIListener(tablePanel, actionPanel);
        //engine.setListener(guiListener);
        Thread gameThread = new Thread(() -> {
            engine.startPassAndPlayHand();
        });
        gameThread.start();
    }
/**
     * Helper to convert the current GameEngine / Table state into
     * the TablePanel.TableState used by the GUI.
     */
    private TablePanel.TableState buildTableStateFromEngine() {
        Table table = engine.getTable();
        List<Player> players = table.getPlayers();
        List<Card> community = table.getCommunityCards();

        TablePanel.TableState ts = new TablePanel.TableState();

        // Map engine player list -> 4 seats: 0=N,1=E,2=S,3=W
        for (int i = 0; i < players.size() && i < 4; i++) {
            Player p = players.get(i);

            TablePanel.PlayerState ps = new TablePanel.PlayerState(i);
            ps.name = p.getName();
            ps.chips = p.getChips();

            // grab first two hole cards if present
            List<Card> hand = new ArrayList<>(p.getHand());
            if (hand.size() > 0) ps.hole1 = hand.get(0);
            if (hand.size() > 1) ps.hole2 = hand.get(1);

            ps.cardsFaceUp = true;   // show everyone for now
            ps.active = false;       // no betting turn yet

            ts.players.add(ps);
        }

        ts.communityCards = community;
        ts.pot = table.getPot();

        return ts;
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
