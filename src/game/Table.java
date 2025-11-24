package game;

import cards.*;
import players.*;
import java.util.*; 

public class Table{
    private final List<Player> players;
    private final List<Card> communityCards;
    public int pot = 0;

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
}