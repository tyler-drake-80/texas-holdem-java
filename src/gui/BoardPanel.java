package gui;

import cards.Card;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
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
        //pot display
        potLabel = new JLabel("Pot: 0", SwingConstants.CENTER);
        potLabel.setFont(potLabel.getFont().deriveFont(Font.BOLD, 16f));
        potLabel.setForeground(Color.WHITE);

        add(potLabel, BorderLayout.NORTH);
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
    }

    public void setPot(int amount){
        potLabel.setText("Pot: $" + amount);
    }
    
}
