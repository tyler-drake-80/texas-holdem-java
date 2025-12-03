package gui.view;

import cards.Card;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Font;

/**
 * PlayerPanel visually represents a single poker player at the table.
 * Displays:
 * -Player name
 * -Chip count
 * -Two hole cards
 * -highlight border when it's that player's turn
 * 
 * Only displays data provided by the controller via setters
 */
public class PlayerPanel extends JPanel{
    private JLabel nameLabel;
    private JLabel chipsLabel;
    private CardPanel card1Panel;
    private CardPanel card2Panel;      

    private Border defaultBorder;
    private Border activeBorder;
    /**
     * Constructs a PlayerPanel with default empty card slots and labels
     * @param name player display name
     */
    public PlayerPanel(String name){
        setOpaque(false);
        setLayout(new BorderLayout());
        //player info
        nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 16f));

        chipsLabel = new JLabel("Chips: 0", SwingConstants.CENTER); 
        chipsLabel.setFont(chipsLabel.getFont().deriveFont(Font.PLAIN, 14f));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        infoPanel.add(nameLabel);
        infoPanel.add(chipsLabel);

        add(infoPanel, BorderLayout.NORTH);
        //hole cards
        JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        cardsPanel.setOpaque(false);

        card1Panel = new CardPanel(null, false);
        card2Panel = new CardPanel(null, false);

        cardsPanel.add(card1Panel);
        cardsPanel.add(card2Panel);

        add(cardsPanel, BorderLayout.CENTER);
        //border for highlighting active player
        defaultBorder = new LineBorder(Color.DARK_GRAY, 1);
        activeBorder = new LineBorder(Color.YELLOW, 3);
        setBorder(defaultBorder);
    }
    
    public void setPlayerName(String name){
        nameLabel.setText(name);
    }
    public void setChips(int chips){
        chipsLabel.setText("Chips: " + chips);
    }
    public void setCards(Card card1, Card card2, boolean faceUp){
        card1Panel.setCard(card1, faceUp);
        card2Panel.setCard(card2, faceUp);
    }
    public void setActive(boolean isActive){
            setBorder(isActive ? activeBorder : defaultBorder);
    }

}
