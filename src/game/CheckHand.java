package game;

import players.*;
import cards.*;
import java.util.*;

public class CheckHand{

    public CheckHand(){ }

   //returns WEIGHT of hand that player has
   public HAND_WEIGHT checkHand(Player p, List<Card> communityCards){
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
