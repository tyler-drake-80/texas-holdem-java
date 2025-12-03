package gui.view;

import cards.Card;
import gui.integration.CardImageLoader;
import java.awt.BorderLayout;
import javax.swing.*;
/**
 * Represents a single card displayed on the table.
 * 
 * Responsibilities:
 * - Display either face up or face down card using CardImageLoader.
 * - Update displayed card when engine state changes.
 * - Render nothing when card is null
 * 
 * Simple wrapper around a JLabel that holds a card image
 */
public class CardPanel extends JPanel{
    private JLabel cardLabel; //displays the card image
    private Card card; //the card being displayed
    private boolean faceUp;

    /**
     * Creates a CardPanel to display a card.
     * @param card the card to display (null for empty)
     * @param faceUp true to show face up, false for face down
     */
    public CardPanel(Card card, boolean faceUp){
        this.card = card;
        this.faceUp = faceUp;
        setOpaque(false);
        setLayout(new BorderLayout());

        cardLabel = new JLabel();
        cardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardLabel.setVerticalAlignment(SwingConstants.CENTER);

        add(cardLabel, BorderLayout.CENTER);
        updateIcon();
    }

    /**
     * Sets the card and face-up status to display.
     * @param card the card to display (null for empty)
     * @param faceUp true to show face up, false for face down
     */
    public void setCard(Card card, boolean faceUp){
        this.card = card;
        this.faceUp = faceUp;
        updateIcon();
    }

    /**
     * Sets whether the card is face up or face down.
     * @param faceUp true to show face up, false for face down
     */
    public void setFaceUp(boolean faceUp){
        this.faceUp = faceUp;
        updateIcon();
    }
    /**
     * updates the JLabel icon based on current card and faceUp state
     */
    private void updateIcon(){
        if(card == null)
            cardLabel.setIcon(null);
        else if(faceUp)
            cardLabel.setIcon(CardImageLoader.getCardImage(card));
        else
            cardLabel.setIcon(CardImageLoader.getBackImage());
    }
}
