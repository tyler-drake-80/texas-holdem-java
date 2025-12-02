package gui;

import cards.Card;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;    
/**
 * TablePanel arranges all poker table components:
 *  -4 PlayerPanels around the table
 *  -central BoardPanel for community cards + pot
 * 
 * Acts as main poker table visual layout.
 */
public class TablePanel extends JPanel{
    private PlayerPanel northPlayer;
    private PlayerPanel southPlayer;    
    private PlayerPanel eastPlayer;
    private PlayerPanel westPlayer;

    private BoardPanel boardPanel;

    public TablePanel(){
        setLayout(new BorderLayout());
        setBackground(new Color(0, 100, 0));//green felt table
        //placeholder players until engine updates them
        northPlayer = new PlayerPanel("Player 3");
        southPlayer = new PlayerPanel("Player 1");
        eastPlayer = new PlayerPanel("Player 2");
        westPlayer = new PlayerPanel("Player 4");

        boardPanel = new BoardPanel();

        add(northPlayer, BorderLayout.NORTH);
        add(southPlayer, BorderLayout.SOUTH);
        add(eastPlayer, BorderLayout.EAST);
        add(westPlayer, BorderLayout.WEST);
        add(boardPanel, BorderLayout.CENTER);
    }

    //called from game engine
    /**
     * Applies a state update from game engine
     * Updates:
     * -Player names
     * -Chip counts
     * -Hole cards
     * -Active player highlight
     * -Community cards
     * -Pot size
     * @param state the current table state
     */
    public void applyTableState(TableState state){
        if (state == null) return;

        for(PlayerState ps : state.players){
            PlayerPanel panel = getPanelForSeat(ps.seatIndex);
            if(panel == null) continue;
            if(ps.name != null){
                panel.setPlayerName(ps.name);
            }
            panel.setChips(ps.chips);
            panel.setCards(ps.card1, ps.card2, ps.cardsFaceUp);
            panel.setActive(ps.isActive);
        }
        boardPanel.setCommunityCards(state.communityCards);
        boardPanel.setPot(state.pot);
    }

    /**
     * Maps a seat index to a table position.
     * 0 = north, 1 = east, 2 = south, 3 = west
     * @param seatIndex the seat index wheere the player is seated
     * @return the PlayerPanel at that seat
     */
    private PlayerPanel getPanelForSeat(int seatIndex){
        switch(seatIndex){
            case 0: return southPlayer;
            case 1: return eastPlayer;
            case 2: return northPlayer;
            case 3: return westPlayer;
            default: return null;
        }
    }
    /**
     * Helper class to represent a player's state at the table.
     */
    public static class PlayerState{
        public int seatIndex;
        public String name;
        public int chips;
        public Card card1;
        public Card card2;
        public boolean cardsFaceUp;
        public boolean isActive;

        public PlayerState(int seatIndex){
            this.seatIndex = seatIndex;
        }

        public PlayerState seat(int index) {this.seatIndex = index; return this;}
        public PlayerState name(String name) {this.name = name; return this;}
        public PlayerState chips(int chips) {this.chips = chips; return this;}
        public PlayerState cards(Card c1, Card c2, boolean faceUp) {
            this.card1 = c1; 
            this.card2 = c2; 
            this.cardsFaceUp = faceUp; 
            return this;
        }
        public PlayerState active(boolean isActive) {this.isActive = isActive; return this;}

    }
    /**
     * Represents the overall state of the table.
     * The controller fills this object and the TablePanel displays it
     */
    public static class TableState{
        public List<PlayerState> players = new ArrayList<>();
        public List<Card> communityCards = new ArrayList<>();
        public int pot;

        public TableState pot(int amount){
            this.pot = amount;
            return this;
        }
    }
}
