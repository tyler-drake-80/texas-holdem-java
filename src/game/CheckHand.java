package game;

import players.*;
import cards.*;
import java.util.*;

public class CheckHand{

    public CheckHand(){ }

    public List<Player> getPlayers(Table t){
        return t.getPlayers();
    }

    //this will NOT handle ties...
    public Player findWinner(Table t){
        List<Player> players = t.getPlayers();
        Map<Player, HAND_WEIGHT> playerHands = new HashMap<>();
        //populate map with players and their hand weights
        for(Player p : players){
            HAND_WEIGHT hw = checkWeight(p, t.getCommunityCards());
            playerHands.put(p, hw);
        }

        //determine highest hand weight 
        Map.Entry<Player, HAND_WEIGHT> winningEntry = null;
        for(Map.Entry<Player, HAND_WEIGHT> entry : playerHands.entrySet()){
            if(winningEntry == null || entry.getValue().ordinal() > winningEntry.getValue().ordinal()){
                winningEntry = entry;
            }
        }
        return winningEntry.getKey();
    }
    //returns WEIGHT of hand that player has
    public HAND_WEIGHT checkWeight(Player p, List<Card> communityCards){
        List<Card> fullHand = new ArrayList<>();
        List<Card> playerCards = p.getHand();
        fullHand.addAll(playerCards);
        fullHand.addAll(communityCards);
        // Logic to evaluate the best hand from fullHand goes here
        for(Card c : fullHand){
            System.out.println(c);
        }
        return HAND_WEIGHT.HIGH_CARD; // Placeholder return value
   }
}
