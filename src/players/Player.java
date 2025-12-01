package players;
import java.util.*;
import cards.*;

public class Player{

    private final List<Card> hand;
    private final String name;
    private boolean folded = false;
    //starting amount
    private int chips = 1000;
    private POSITIONS position;
    // Track how much this player has bet in the current betting round
    private int currentBet = 0;
    // Track total bet across all rounds (for pot calculation)
    private int totalBet = 0;

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

    public int getChips(){
        return chips;
    }

    public void addChips(int amount){
        chips += amount;
    }

    public void removeChips(int amount){
        chips -= amount;
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
    
    public POSITIONS getPosition(){
        return position;
    }
    
    public void setPosition(POSITIONS p){
        this.position = p;
    }

    // Betting methods
    public int getCurrentBet(){
        return currentBet;
    }

    public int getTotalBet(){
        return totalBet;
    }

    /**
     * Place a bet. Returns the actual amount bet.
     * @param amount The amount to bet
     * @return The actual amount bet (might be less if player doesn't have enough chips)
     */
    public int placeBet(int amount){
        int actualBet = Math.min(amount, chips);
        chips -= actualBet;
        currentBet += actualBet;
        totalBet += actualBet;
        return actualBet;
    }

    /**
     * Reset the current bet at the start of a new betting round
     */
    public void resetCurrentBet(){
        currentBet = 0;
    }

    /**
     * Reset for a new hand
     */
    public void resetForNewHand(){
        currentBet = 0;
        totalBet = 0;
        folded = false;
        hand.clear();
    }

    /**
     * Check if player is all-in
     */
    public boolean isAllIn(){
        return chips == 0 && !folded;
    }
}
