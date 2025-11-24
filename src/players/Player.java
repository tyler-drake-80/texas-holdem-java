package players;
import java.util.*;
import cards.*;

public class Player{

    private final List<Card> hand;
    private final String name;
    private boolean folded = false;

    public Player(String name){
        this.name = name;
        hand = new ArrayList<>();
    }

    public void giveCard(Card c){
        hand.add(c);
    }

    public void clearHand(){
        hand.clear();
        folded = false;
    }

    public List<Card> getHand(){
        return hand;
    }

    public void fold(){
        folded = true;
    }

    public boolean isFolded(){
        return folded;
    }

    public String getName(){
        return name;
    }

}