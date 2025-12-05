package gui.view;

import cards.Card;
import gui.integration.PlayerState;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;    
/**
 * TablePanel arranges all poker table components:
 *  -8 PlayerPanels positioned in an oval around the table
 *  -central BoardPanel for community cards + pot
 * 
 * Acts as main poker table visual layout.
 */
public class TablePanel extends JPanel{
    private List<PlayerPanel> playerPanels;
    private BoardPanel boardPanel;
    private static final int MAX_PLAYERS = 8;
    //label for winner display
    private JLabel winnerLabel;
    

    public TablePanel(){
        setLayout(null); // Use absolute positioning for oval arrangement
        setBackground(new Color(0, 100, 0)); // green felt table
        
        playerPanels = new ArrayList<>();
        
        // Create 8 player panels
        for(int i = 0; i < MAX_PLAYERS; i++){
            PlayerPanel panel = new PlayerPanel("Player " + (i + 1), i);
            panel.setVisible(false); // Initially hidden
            playerPanels.add(panel);
            add(panel);
        }

        boardPanel = new BoardPanel();
        add(boardPanel);

        winnerLabel = new JLabel("", SwingConstants.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 26));
        winnerLabel.setForeground(Color.YELLOW);
        winnerLabel.setOpaque(true);
        winnerLabel.setBackground(new Color(0,0,0,150));
        setComponentZOrder(winnerLabel, 0);//bring to front
        add(winnerLabel);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        
        int width = getWidth();
        int height = getHeight();
        
        winnerLabel.setBounds(width / 2, height / 2, 600, 50);
        // Center area for board (community cards and pot)
        int centerX = width / 2;
        int centerY = height / 2;
        int boardWidth = 500;
        int boardHeight = 240;
        boardPanel.setBounds(centerX - boardWidth/2, centerY - boardHeight/2, 
                            boardWidth, boardHeight);
        
        // Oval parameters for player positioning
        int ovalWidth = width - 280;
        int ovalHeight = height - 280;
        int ovalCenterX = width / 2;
        int ovalCenterY = height / 2;
        
        // Position players in an oval
        for(int i = 0; i < playerPanels.size(); i++){
            if(!playerPanels.get(i).isVisible()) continue;
            
            // Calculate angle for this player (start from bottom, go clockwise)
            double angle = Math.PI / 2 + (2 * Math.PI * i / playerPanels.size());
            
            // Calculate position on oval
            int x = ovalCenterX + (int)(ovalWidth / 2 * Math.cos(angle));
            int y = ovalCenterY - (int)(ovalHeight / 2 * Math.sin(angle));
            
            // Player panel size
            int panelWidth = 175;
            int panelHeight = 180;
            
            playerPanels.get(i).setBounds(x - panelWidth/2, y - panelHeight/2, 
                                         panelWidth, panelHeight);
        }
    }

    /**
     * Applies a state update from game engine
     */
    public void applyTableState(TableState state){
        if (state == null) return;

        // Hide all panels first
        for(PlayerPanel panel : playerPanels){
            panel.setVisible(false);
        }

        // Update visible players
        for(PlayerState ps : state.players){
            if(ps.seatIndex < 0 || ps.seatIndex >= MAX_PLAYERS) continue;
            
            PlayerPanel panel = playerPanels.get(ps.seatIndex);
            panel.setVisible(true);
            
            if(ps.name != null){
                panel.setPlayerName(ps.name);
            }
            panel.setChips(ps.chips);
            panel.setCards(ps.hole1, ps.hole2, ps.cardsFaceUp);
            panel.setActive(ps.active);
            panel.setHandRanking(ps.handRanking);
            panel.setDealer(ps.seatIndex == state.dealerSeat);
        }
        
        boardPanel.setCommunityCards(state.communityCards);
        boardPanel.setPot(state.pot);
        
        if(state.winnerText != null && !state.winnerText.isEmpty()){
            winnerLabel.setText(state.winnerText);
            winnerLabel.setVisible(true);
            winnerLabel.setBackground(Color.RED);
            winnerLabel.repaint();
            winnerLabel.revalidate();
            setComponentZOrder(winnerLabel, 0); // bring to front
        } else {
            winnerLabel.setVisible(false);
        }
        revalidate();
        repaint();
    }

    /**
     * Helper class to represent a player's state at the table.
     */
    public static class PlayerState{
        public int seatIndex;
        public String name;
        public int chips;
        public Card hole1;
        public Card hole2;
        public boolean cardsFaceUp;
        public boolean active;
        public String handRanking; // e.g. "Pair", "Flush"

        public PlayerState(int seatIndex){
            this.seatIndex = seatIndex;
        }

        public PlayerState seat(int index) {this.seatIndex = index; return this;}
        public PlayerState name(String name) {this.name = name; return this;}
        public PlayerState chips(int chips) {this.chips = chips; return this;}
        public PlayerState cards(Card c1, Card c2, boolean faceUp) {
            this.hole1 = c1; 
            this.hole2 = c2; 
            this.cardsFaceUp = faceUp; 
            return this;
        }
        public PlayerState active(boolean active) {this.active = active; return this;}
        public PlayerState handRanking(String ranking) {this.handRanking = ranking; return this;}
    }
    
    /**
     * Represents the overall state of the table.
     */
    public static class TableState{
        public List<PlayerState> players = new ArrayList<>();
        public List<Card> communityCards = new ArrayList<>();
        public int pot;

        public int dealerSeat;
        public String winnerText = ""; // Text to display winner info
            
        public TableState pot(int amount){
            this.pot = amount;
            return this;
        }
    }
}
