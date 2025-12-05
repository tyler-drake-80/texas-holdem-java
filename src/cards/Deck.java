package cards;

import java.util.*;
/**
 * Represents a standard deck of 52 playing cards
 */
public class Deck{
    private final List<Card> cards;
    private int index;

    public Deck(){
        index = 0;
        cards = new ArrayList<>();
        //initialize deck of 52 cards 
        for(SUIT s : SUIT.values()){
            for(VALUE v : VALUE.values()){
                cards.add(new Card(s, v));
            }
        }
        Collections.shuffle(cards);
    }
    public void shuffle(){
        Collections.shuffle(cards);
    }

    public void reset(){
        index = 0;
        Collections.shuffle(cards);
    }

    public Card deal(){
        if(index >= cards.size()){
            throw new IllegalStateException("Deck out of cards!");
        }
        return cards.get(index++);
    }
}