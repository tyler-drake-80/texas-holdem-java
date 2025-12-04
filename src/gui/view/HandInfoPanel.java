package gui.view;

import cards.Card;
import game.HAND_WEIGHT;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Panel that displays information about player hands and community cards.
 * Shows:
 * - Each player's hole cards (when revealed)
 * - Each player's best hand ranking
 * - Community cards (flop, turn, river)
 */
public class HandInfoPanel extends JPanel {
    private JTextArea handInfoArea;
    private JPanel communityCardsPanel;
    private JLabel[] communityCardLabels;

    public HandInfoPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(40, 40, 40));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(300, 0)); // Fixed width

        // Title
        JLabel titleLabel = new JLabel("Hand Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        // Community cards section
        JPanel communitySection = new JPanel(new BorderLayout(5, 5));
        communitySection.setOpaque(false);
        
        JLabel communityLabel = new JLabel("Community Cards:", SwingConstants.LEFT);
        communityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        communityLabel.setForeground(Color.YELLOW);
        communitySection.add(communityLabel, BorderLayout.NORTH);

        communityCardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        communityCardsPanel.setOpaque(false);
        communityCardLabels = new JLabel[5];
        for (int i = 0; i < 5; i++) {
            communityCardLabels[i] = new JLabel("--");
            communityCardLabels[i].setFont(new Font("Monospaced", Font.BOLD, 14));
            communityCardLabels[i].setForeground(Color.WHITE);
            communityCardsPanel.add(communityCardLabels[i]);
        }
        communitySection.add(communityCardsPanel, BorderLayout.CENTER);

        add(communitySection, BorderLayout.CENTER);

        // Hand rankings text area
        handInfoArea = new JTextArea();
        handInfoArea.setEditable(false);
        handInfoArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        handInfoArea.setBackground(new Color(30, 30, 30));
        handInfoArea.setForeground(Color.WHITE);
        handInfoArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(handInfoArea);
        scrollPane.setPreferredSize(new Dimension(280, 400));
        add(scrollPane, BorderLayout.SOUTH);

        clearDisplay();
    }

    /**
     * Updates the community cards display
     */
    public void setCommunityCards(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                communityCardLabels[i].setText("--");
                communityCardLabels[i].setForeground(Color.GRAY);
            }
            return;
        }

        for (int i = 0; i < 5; i++) {
            if (i < cards.size()) {
                Card card = cards.get(i);
                String cardStr = formatCard(card);
                communityCardLabels[i].setText(cardStr);
                communityCardLabels[i].setForeground(getCardColor(card));
            } else {
                communityCardLabels[i].setText("--");
                communityCardLabels[i].setForeground(Color.GRAY);
            }
        }
    }

    /**
     * Updates player hand information
     */
    public void updatePlayerHands(Map<String, PlayerHandInfo> playerHands) {
        StringBuilder sb = new StringBuilder();
        sb.append("PLAYER HANDS\n");
        sb.append("═══════════════════════\n\n");

        for (Map.Entry<String, PlayerHandInfo> entry : playerHands.entrySet()) {
            String playerName = entry.getKey();
            PlayerHandInfo info = entry.getValue();

            sb.append(playerName).append("\n");
            
            if (info.folded) {
                sb.append("  Status: FOLDED\n\n");
                continue;
            }

            if (info.holeCard1 != null && info.holeCard2 != null) {
                sb.append("  Cards: ")
                  .append(formatCard(info.holeCard1))
                  .append(" ")
                  .append(formatCard(info.holeCard2))
                  .append("\n");
            } else {
                sb.append("  Cards: Hidden\n");
            }

            if (info.handRanking != null) {
                sb.append("  Hand: ").append(formatHandRanking(info.handRanking)).append("\n");
            }

            sb.append("\n");
        }

        handInfoArea.setText(sb.toString());
        handInfoArea.setCaretPosition(0); // Scroll to top
    }

    /**
     * Clears all displays
     */
    public void clearDisplay() {
        setCommunityCards(null);
        handInfoArea.setText("Waiting for game to start...\n\nHand information will appear here.");
    }

    /**
     * Formats a card for text display
     */
    private String formatCard(Card card) {
        if (card == null) return "??";
        
        String value;
        switch (card.getValue()) {
            case ACE:   value = "A"; break;
            case KING:  value = "K"; break;
            case QUEEN: value = "Q"; break;
            case JACK:  value = "J"; break;
            case TEN:   value = "T"; break;
            default:    value = String.valueOf(card.getValue().ordinal() + 1);
        }

        String suit;
        switch (card.getSuit()) {
            case HEARTS:   suit = "♥"; break;
            case DIAMONDS: suit = "♦"; break;
            case CLUBS:    suit = "♣"; break;
            case SPADES:   suit = "♠"; break;
            default:       suit = "?";
        }

        return value + suit;
    }

    /**
     * Gets the display color for a card based on suit
     */
    private Color getCardColor(Card card) {
        if (card == null) return Color.WHITE;
        
        switch (card.getSuit()) {
            case HEARTS:
            case DIAMONDS:
                return new Color(255, 100, 100); // Red
            case CLUBS:
            case SPADES:
                return Color.WHITE; // White
            default:
                return Color.WHITE;
        }
    }

    /**
     * Formats hand ranking for display
     */
    private String formatHandRanking(HAND_WEIGHT ranking) {
        switch (ranking) {
            case ROYAL_FLUSH:    return "Royal Flush";
            case STRAIGHT_FLUSH: return "Straight Flush";
            case FOUR_OF_A_KIND: return "Four of a Kind";
            case FULL_HOUSE:     return "Full House";
            case FLUSH:          return "Flush";
            case STRAIGHT:       return "Straight";
            case THREE_OF_A_KIND:return "Three of a Kind";
            case TWO_PAIR:       return "Two Pair";
            case PAIR:           return "Pair";
            case HIGH_CARD:      return "High Card";
            default:             return "Unknown";
        }
    }

    /**
     * Helper class to hold player hand information
     */
    public static class PlayerHandInfo {
        public Card holeCard1;
        public Card holeCard2;
        public HAND_WEIGHT handRanking;
        public boolean folded;

        public PlayerHandInfo(Card card1, Card card2, HAND_WEIGHT ranking, boolean folded) {
            this.holeCard1 = card1;
            this.holeCard2 = card2;
            this.handRanking = ranking;
            this.folded = folded;
        }
    }
}
