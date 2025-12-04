package gui.integration;

import cards.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Loads card images directly from the file system under ./resources/.
 * No classpath resources required.
 */
public class CardImageLoader {
    private static final String IMAGE_DIR = "resources/Cards/Classic/"; 
    private static final String BACK_DIR = "resources/Backs/back.png"; 

    private static final int CARD_WIDTH = 80;
    private static final int CARD_HEIGHT = 120;

    private static final Map<String, ImageIcon> cardImageCache = new HashMap<>();
    private static ImageIcon backImage;

    public static void loadAllCards() {
        System.out.println("Loading card images...");

        for(SUIT s : SUIT.values()){
            for(VALUE v : VALUE.values()){
                String code = suitToPrefix(s) + valueToNumber(v);
                String filename = code + ".png";

                ImageIcon icon = loadAndScale(IMAGE_DIR + filename);
                if(icon != null){
                    cardImageCache.put(code, icon);
                } else {
                    System.err.println("Failed to load image for " + code);
                }
            }
        }
        backImage = loadAndScale(BACK_DIR);
        System.out.println("Loaded " + cardImageCache.size() + " card images.");
    }
    private static ImageIcon loadAndScale(String path){
        try{
            BufferedImage img = ImageIO.read(new File(path));
            if(img == null) return null;
            Image scaled = img.getScaledInstance(
                 CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static ImageIcon getCardImage(Card card) {
        String code = suitToPrefix(card.getSuit()) + valueToNumber(card.getValue());
        return cardImageCache.get(code);
    }
    public static ImageIcon getBackImage(){
        return backImage;
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
    private static String valueToNumber(VALUE value){
        switch(value){
            case ACE: return "01";
            case TWO: return "02";
            case THREE: return "03";
            case FOUR: return "04";
            case FIVE: return "05";
            case SIX: return "06";
            case SEVEN: return "07";
            case EIGHT: return "08";
            case NINE: return "09";
            case TEN: return "10";
            case JACK: return "11";
            case QUEEN: return "12";
            case KING:  return "13";
            default:    return "";
        }
    }
}
