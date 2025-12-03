package gui.app;

import cards.*;
import game.GameEngine;
import gui.integration.*;
import gui.view.*;
import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
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
        buildDemoTableState();
        //need to convert engine.start() to a stepwise process
        //will call engine.startHand(), engine.nextBettingRound(), etc
        //once we build the listener + action panel, call will be:
        //engine.start(); -- from here
    }

    private void buildDemoTableState() {
        TablePanel.TableState state = new TablePanel.TableState();

        // --- Create 4 fake players: seats 0=N, 1=E, 2=S, 3=W ---

        TablePanel.PlayerState north = new TablePanel.PlayerState(0);
        north.name = "North";
        north.chips = 1500;
        north.hole1 = new Card(SUIT.HEARTS, VALUE.ACE);
        north.hole2 = new Card(SUIT.HEARTS, VALUE.KING);
        north.cardsFaceUp = true;
        north.active = false;
        state.players.add(north);

        TablePanel.PlayerState east = new TablePanel.PlayerState(1);
        east.name = "East";
        east.chips = 1200;
        east.hole1 = new Card(SUIT.CLUBS, VALUE.TWO);
        east.hole2 = new Card(SUIT.CLUBS, VALUE.THREE);
        east.cardsFaceUp = true;
        east.active = false;
        state.players.add(east);

        TablePanel.PlayerState south = new TablePanel.PlayerState(2);
        south.name = "South (You)";
        south.chips = 2000;
        south.hole1 = new Card(SUIT.SPADES, VALUE.TEN);
        south.hole2 = new Card(SUIT.SPADES, VALUE.JACK);
        south.cardsFaceUp = true;   // later you’ll flip others face-down
        south.active = true;        // highlight this player
        state.players.add(south);

        TablePanel.PlayerState west = new TablePanel.PlayerState(3);
        west.name = "West";
        west.chips = 900;
        west.hole1 = new Card(SUIT.DIAMONDS, VALUE.FIVE);
        west.hole2 = new Card(SUIT.DIAMONDS, VALUE.SIX);
        west.cardsFaceUp = true;
        west.active = false;
        state.players.add(west);

    // --- Community cards (flop, turn, river) ---

        state.communityCards = Arrays.asList(
            new Card(SUIT.HEARTS, VALUE.TEN),
            new Card(SUIT.CLUBS, VALUE.JACK),
            new Card(SUIT.SPADES, VALUE.QUEEN),
            new Card(SUIT.DIAMONDS, VALUE.KING),
            new Card(SUIT.HEARTS, VALUE.NINE)
        );
        state.pot = 320;

        // --- Push state into the table so it can render ---
        tablePanel.applyTableState(state);
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