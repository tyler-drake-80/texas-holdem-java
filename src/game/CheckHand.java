package game;

import cards.*;
import java.util.*;

public class CheckHand{
    /*
       10. Royal flush - A, K, Q, J, 10 of SAME SUIT
        9. Straight flush - straight in SAME SUIT
        8. Four of a kind - same values, diff suit
        7. Full house - Three cards of one val, pair of another
        6. Flush - All cards same suit
        5. Straight - five cards of consecutive value
        4. Three of a kind - three card same value
        3. Two pair
        2. Pair
        1. High card 
    */

   //returns WEIGHT of hand that player has
   public HAND_WEIGHT checkHand(List<Card> playerCards, List<Card> communityCards){
        List<Card> fullHand = new ArrayList<>();
        fullHand.addAll(playerCards);
        fullHand.addAll(communityCards);
        // Logic to evaluate the best hand from fullHand goes here
        return HAND_WEIGHT.HIGH_CARD; // Placeholder return value
         
   }
}
