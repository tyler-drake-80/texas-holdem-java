package gui;

import cards.*;
import java.util.HashMap;
import java.util.Map;
import java.awt.*;
import javax.swing.*;

public class CardImageLoader {
    private static final String IMAGE_PATH = "/Cards/Classic/"; 
    private static final Map<String, ImageIcon> cardImageCache = new HashMap<>();

    public static void loadAllCards(){
        System.out.println("Loading card images...");

        for(SUIT s : SUIT.values()){
            for(VALUE v : VALUE.values()){
                String code = suitToPrefix(s) + valueToNumber(v);
                String filename = code + ".png";

                try{
                    ImageIcon icon = new ImageIcon(
                        CardImageLoader.class.getResource(IMAGE_PATH + filename));

                    if(icon.getIconWidth() == -1){
                        throw new Exception("Image not found: " + filename);
                    }

                    cardImageCache.put(code, icon);
                
                } catch (Exception e) {
                    System.err.println("Error loading image for " + filename + ": " + e.getMessage());
                }
            }
        }
        System.out.println("Loaded " + cardImageCache.size() + " card images.");
    }

    public static ImageIcon getCardImage(Card card){
        String code = suitToPrefix(card.getSuit()) + valueToNumber(card.getValue());
        return cardImageCache.get(code);
    }

    private static String suitToPrefix(SUIT suit){
        switch(suit){
            case HEARTS: return "h";
            case DIAMONDS: return "d";
            case CLUBS: return "c";
            case SPADES: return "s";
            default: return "";
        }
    }
    private static String valueToNumber(VALUE value){
        switch(value){
            case ACE: return "1";
            case TWO: return "2";
            case THREE: return "3";
            case FOUR: return "4";
            case FIVE: return "5";
            case SIX: return "6";
            case SEVEN: return "7";
            case EIGHT: return "8";
            case NINE: return "9";
            case TEN: return "10";
            case JACK: return "11";
            case QUEEN: return "12";
            case KING: return "13";
            default: return "";
        }
    }
}
