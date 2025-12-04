package gui.view;

import cards.Card;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
/**
 * BoardPanel displays the community cards and pot size.
 * 
 * Components:
 * - Five CardPanels for community cards
 * - JLabel for pot size
 * 
 * Only displays information supplied by TablePanel
 */
public class BoardPanel extends JPanel{
    private CardPanel[] communityPanels;
    private JLabel potLabel;
    private JLabel riverTextLabel;

    public BoardPanel(){
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel cardsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        cardsRow.setOpaque(false);
        //community card row
        communityPanels = new CardPanel[5];
        for(int i = 0; i < 5; i++){
            communityPanels[i] = new CardPanel(null, false);
            cardsRow.add(communityPanels[i]);
        }

        add(cardsRow, BorderLayout.CENTER);
        
        // Pot and river text panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        
        //pot display
        potLabel = new JLabel("Pot: 0", SwingConstants.CENTER);
        potLabel.setFont(potLabel.getFont().deriveFont(Font.BOLD, 16f));
        potLabel.setForeground(Color.WHITE);
        potLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // River text display
        riverTextLabel = new JLabel("River: - - - - -", SwingConstants.CENTER);
        riverTextLabel.setFont(riverTextLabel.getFont().deriveFont(Font.PLAIN, 14f));
        riverTextLabel.setForeground(Color.LIGHT_GRAY);
        riverTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        topPanel.add(potLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(riverTextLabel);

        add(topPanel, BorderLayout.NORTH);
    }
    /**
     * updates visible community cards
     * @param cards list of community cards in deal order
     */
    public void setCommunityCards(List<Card> cards){
        for(int i = 0; i < communityPanels.length; i++){
            if(cards != null && i < cards.size()){
                communityPanels[i].setCard(cards.get(i), true);
            } else {
                communityPanels[i].setCard(null, false);
            }
        }
        updateRiverText(cards);
    }
    
    /**
     * Updates the text display of river cards
     */
    private void updateRiverText(List<Card> cards){
        StringBuilder riverText = new StringBuilder("River: ");
        
        for(int i = 0; i < 5; i++){
            if(cards != null && i < cards.size()){
                Card card = cards.get(i);
                riverText.append(formatCard(card));
            } else {
                riverText.append("-");
            }
            
            if(i < 4){
                riverText.append(" ");
            }
        }
        
        riverTextLabel.setText(riverText.toString());
    }
    
    /**
     * Formats a card as text with suit symbol
     */
    private String formatCard(Card card){
        if(card == null) return "-";
        
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

    public void setPot(int amount){
        potLabel.setText("Pot: $" + amount);
    }
    
}
