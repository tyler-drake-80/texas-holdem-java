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
 * GameEngine is responsible for pushing state updates into the GUI
 */
public class GUIGame extends JFrame{
    private final TablePanel tablePanel;
    private final ActionPanel actionPanel;
    private final GameEngine engine;
    private int startingChips = 1000;
    private int numberOfPlayers = 4;

    public GUIGame(){
        //configure window
        setTitle("Texas Hold'em Poker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        //load card images
        CardImageLoader.loadAllCards();

        //initialize panels
        tablePanel = new TablePanel();
        actionPanel = new ActionPanel();
        
        add(tablePanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        //initialize game engine
        engine = new GameEngine();
        
        GUIListener guiListener = new GUIListener(tablePanel, actionPanel);
        engine.setListener(guiListener);
    }

    public void startGame(){
        // Show setup dialog before starting
        GameSetupDialog.GameSetupResult setup = GameSetupDialog.showDialog(this);
        
        if (setup == null) {
            // User cancelled
            System.exit(0);
            return;
        }
        
        // Store game settings
        startingChips = setup.startingChips;
        numberOfPlayers = setup.numberOfPlayers;
        
        // Configure engine with settings
        engine.setStartingChips(startingChips);
        engine.setNumberOfPlayers(numberOfPlayers);
        
        setVisible(true);
        
        // Start game thread
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

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);

            TablePanel.PlayerState ps = new TablePanel.PlayerState(i);
            ps.name = p.getName();
            ps.chips = p.getChips();

            // grab first two hole cards if present
            List<Card> hand = new ArrayList<>(p.getHand());
            if (hand.size() > 0) ps.hole1 = hand.get(0);
            if (hand.size() > 1) ps.hole2 = hand.get(1);

            ps.cardsFaceUp = true;
            ps.active = false;

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
