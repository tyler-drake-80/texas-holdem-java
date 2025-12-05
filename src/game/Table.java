package game;

import cards.*;
import players.*;
import java.util.*; 
/**
 * Represents the poker table, managing players, community cards, pot, and betting state.
 * Includes methods to reset for new hands, track active players, and display hands.
 */
public class Table{
    private final List<Player> players;
    private final List<Card> communityCards;
    private int pot = 0;
    private int currentBet = 0; // The current bet that needs to be matched
    private int smallBlind = 10;
    private int bigBlind = 20;
    private String winnerText = "";

    public Table(){
        players = new ArrayList<>();
        communityCards = new ArrayList<>();
    }

    public void addPlayer(Player p){
        players.add(p);
    }

    public List<Player> getPlayers(){
        return players;
    }

    public List<Card> getCommunityCards(){
        return communityCards;
    }

    public void addCommunityCard(Card c){
        communityCards.add(c);
    }
    
    public void clearCommunityCards(){
        communityCards.clear();
    }
    
    public int getPot(){
        return pot;
    }
    
    public void addToPot(int amount){
        pot += amount;
    }

    public int getCurrentBet(){
        return currentBet;
    }

    public void setCurrentBet(int bet){
        this.currentBet = bet;
    }

    public int getSmallBlind(){
        return smallBlind;
    }

    public int getBigBlind(){
        return bigBlind;
    }

    public void setSmallBlind(int amount){
        this.smallBlind = amount;
    }

    public void setBigBlind(int amount){
        this.bigBlind = amount;
    }

    /**
     * Reset the current bet at the start of a new betting round
     */
    public void resetCurrentBet(){
        currentBet = 0;
        for(Player p : players){
            p.resetCurrentBet();
        }
    }

    /**
     * Reset for a new hand
     */
    public void resetForNewHand(){
        pot = 0;
        currentBet = 0;
        communityCards.clear();
        for(Player p : players){
            p.resetForNewHand();
        }
    }

    /**
     * Get the number of active players (not folded and not all-in)
     */
    public int getActivePlayers(){
        int count = 0;
        for(Player p : players){
            if(!p.isFolded() && !p.isAllIn()){
                count++;
            }
        }
        return count;
    }

    /**
     * Get the number of players still in the hand (not folded)
     */
    public int getPlayersInHand(){
        int count = 0;
        for(Player p : players){
            if(!p.isFolded()){
                count++;
            }
        }
        return count;
    }
    
    public void showAllHands(){
        for(Player p : players){
            //for later gui implementation -- wont matter for now
            for(Card c : p.getHand()){
                if(!c.isFaceUp()){
                    c.flip();
                }
            }
            System.out.println(p.getName() + "'s hand: " + p.getHand());
        }
    }

    public void setWinnerText(String text){
        this.winnerText = text;
    }
    public String getWinnerText(){
        return this.winnerText;
    }
}
