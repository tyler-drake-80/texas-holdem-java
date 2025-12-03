package gui.integration;

import cards.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 * Loads card images directly from the file system under ./resources/.
 * No classpath resources required.
 */
public class CardImageLoader {
    private static final String IMAGE_DIR = "resources/Cards/Classic/";
    private static final String BACK_FILE = "resources/Backs/back.png";

    private static final Map<String, ImageIcon> cardImageCache = new HashMap<>();

    public static void loadAllCards() {
        System.out.println("Loading card images...");

        for (SUIT s : SUIT.values()) {
            for (VALUE v : VALUE.values()) {
                String code = suitToPrefix(s) + valueToNumber(v);  // e.g. "h10"
                String filename = IMAGE_DIR + code + ".png";

                ImageIcon icon = new ImageIcon(filename);

                if (icon.getIconWidth() <= 0) {
                    System.err.println("Error loading image file: " + filename);
                    continue;
                }

                cardImageCache.put(code, icon);
            }
        }

        System.out.println("Loaded " + cardImageCache.size() + " card images.");
    }

    public static ImageIcon getCardImage(Card card) {
        String code = suitToPrefix(card.getSuit()) + valueToNumber(card.getValue());
        return cardImageCache.get(code);
    }

    public static ImageIcon getBackImage() {
        return new ImageIcon(BACK_FILE);
    }

    private static String suitToPrefix(SUIT suit) {
        switch (suit) {
            case HEARTS:   return "h";
            case DIAMONDS: return "d";
            case CLUBS:    return "c";
            case SPADES:   return "s";
            default:       return "";
        }
    }

    private static String valueToNumber(VALUE value) {
        switch (value) {
            case ACE:   return "01";
            case TWO:   return "02";
            case THREE: return "03";
            case FOUR:  return "04";
            case FIVE:  return "05";
            case SIX:   return "06";
            case SEVEN: return "07";
            case EIGHT: return "08";
            case NINE:  return "09";
            case TEN:   return "10";
            case JACK:  return "11";
            case QUEEN: return "12";
            case KING:  return "13";
            default:    return "";
        }
    }
}
