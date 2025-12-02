package players;

import java.util.*;
import cards.*;

public class Player{

    private final List<Card> hand;
    private final String name;
    private boolean folded = false;
    //starting amount (placeholder)
    private int chips = 1000;
    private POSITIONS position;

    public Player(String name){
        this.name = name;
        this.hand = new ArrayList<>();
    }

    public void giveCard(Card c){
        this.hand.add(c);
    }

    public void clearHand(){
        this.hand.clear();
        this.folded = false;
    }

    public int getChips(){
        return chips;
    }

    public void addChips(int amount){
        this.chips += amount;
    }

    public void removeChips(int amount){
        this.chips -= amount;
    }

    public List<Card> getHand(){
        return hand;
    }

    public void fold(){
        this.folded = true;
    }

    public boolean isFolded(){
        return folded;
    }

    public String getName(){
        return name;
    }
    public POSITIONS getPosition(){
        return position;
    }
    public void setPosition(POSITIONS p){
        this.position = p;
    }

}