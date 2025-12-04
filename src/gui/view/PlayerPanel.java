package gui.view;

import cards.Card;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * PlayerPanel visually represents a single poker player at the table.
 * Displays:
 * -Player name
 * -Chip count
 * -Two hole cards
 * -Hand ranking (when available)
 * -Highlight border when it's that player's turn
 */
public class PlayerPanel extends JPanel{
    private JLabel nameLabel;
    private JLabel chipsLabel;
    private JLabel holeCardsTextLabel;
    private JLabel handRankingLabel;
    private CardPanel card1Panel;
    private CardPanel card2Panel;      
    private int seatIndex;
    private Card currentCard1;
    private Card currentCard2;
    private boolean currentFaceUp;

    private Border defaultBorder;
    private Border activeBorder;
    
    public PlayerPanel(String name, int seatIndex){
        this.seatIndex = seatIndex;
        setOpaque(true);
        setBackground(new Color(40, 40, 40, 200)); // Semi-transparent dark background
        setLayout(new BorderLayout(5, 5));
        
        // Player info panel (name, chips, hand ranking)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        chipsLabel = new JLabel("$0", SwingConstants.CENTER);
        chipsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        chipsLabel.setForeground(Color.YELLOW);
        chipsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        holeCardsTextLabel = new JLabel("", SwingConstants.CENTER);
        holeCardsTextLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        holeCardsTextLabel.setForeground(Color.WHITE);
        holeCardsTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        handRankingLabel = new JLabel("", SwingConstants.CENTER);
        handRankingLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        handRankingLabel.setForeground(Color.CYAN);
        handRankingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(chipsLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(holeCardsTextLabel);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(handRankingLabel);

        add(infoPanel, BorderLayout.NORTH);
        
        // Hole cards panel
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
        cardsPanel.setOpaque(false);

        card1Panel = new CardPanel(null, false);
        card2Panel = new CardPanel(null, false);

        cardsPanel.add(card1Panel);
        cardsPanel.add(card2Panel);

        add(cardsPanel, BorderLayout.CENTER);
        
        // Borders for highlighting active player
        defaultBorder = new LineBorder(new Color(80, 80, 80), 2);
        activeBorder = new LineBorder(Color.YELLOW, 3);
        setBorder(defaultBorder);
    }
    
    public void setPlayerName(String name){
        nameLabel.setText(name);
    }
    
    public void setChips(int chips){
        chipsLabel.setText("$" + chips);
    }
    
    public void setCards(Card card1, Card card2, boolean faceUp){
        this.currentCard1 = card1;
        this.currentCard2 = card2;
        this.currentFaceUp = faceUp;
        
        card1Panel.setCard(card1, faceUp);
        card2Panel.setCard(card2, faceUp);
        
        updateHoleCardsText();
    }
    
    /**
     * Updates the text display of hole cards
     */
    private void updateHoleCardsText(){
        if(currentCard1 == null || currentCard2 == null || !currentFaceUp){
            holeCardsTextLabel.setText("");
            return;
        }
        
        String card1Text = formatCard(currentCard1);
        String card2Text = formatCard(currentCard2);
        holeCardsTextLabel.setText(card1Text + " " + card2Text);
    }
    
    /**
     * Formats a card as text with suit symbol
     */
    private String formatCard(Card card){
        if(card == null) return "?";
        
        String value;
        switch(card.getValue()){
            case ACE:   value = "A"; break;
            case KING:  value = "K"; break;
            case QUEEN: value = "Q"; break;
            case JACK:  value = "J"; break;
            case TEN:   value = "T"; break;
            case NINE:  value = "9"; break;
            case EIGHT: value = "8"; break;
            case SEVEN: value = "7"; break;
            case SIX:   value = "6"; break;
            case FIVE:  value = "5"; break;
            case FOUR:  value = "4"; break;
            case THREE: value = "3"; break;
            case TWO:   value = "2"; break;
            default:    value = "?"; break;
        }
        
        String suit;
        switch(card.getSuit()){
            case HEARTS:   suit = "♥"; break;
            case DIAMONDS: suit = "♦"; break;
            case CLUBS:    suit = "♣"; break;
            case SPADES:   suit = "♠"; break;
            default:       suit = "?";
        }
        
        return value + suit;
    }
    
    public void setActive(boolean isActive){
        setBorder(isActive ? activeBorder : defaultBorder);
    }
    
    public void setHandRanking(String ranking){
        if(ranking != null && !ranking.isEmpty()){
            handRankingLabel.setText(ranking);
        } else {
            handRankingLabel.setText("");
        }
    }
}
