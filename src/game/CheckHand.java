package game;

import players.*;
import cards.*;
import java.util.*;

public class CheckHand{

    public CheckHand(){ }

    /**
     * Returns WEIGHT of hand that player has
     * Evaluates the best 5-card hand from the player's 2 cards + 5 community cards
     */
    public HAND_WEIGHT checkHand(Player p, List<Card> communityCards){
        return checkWeight(p, communityCards);
    }

    /**
     * Alias for checkHand - used by GUI code
     */
    public HAND_WEIGHT checkWeight(Player p, List<Card> communityCards){
        List<Card> fullHand = new ArrayList<>();
        List<Card> playerCards = p.getHand();
        fullHand.addAll(playerCards);
        fullHand.addAll(communityCards);
        return evaluateHand(fullHand);
    }
    
    /**
     * Overloaded method that accepts player's hole cards directly
     */
    public HAND_WEIGHT checkWeight(List<Card> playerCards, List<Card> communityCards){
        List<Card> fullHand = new ArrayList<>();
        fullHand.addAll(playerCards);
        fullHand.addAll(communityCards);
        return evaluateHand(fullHand);
    }
    
    /**
     * Evaluates the best 5-card hand from a list of cards
     */
    private HAND_WEIGHT evaluateHand(List<Card> fullHand){
        
        // Sort cards by value (descending order)
        fullHand.sort((a, b) -> b.getValue().getValue() - a.getValue().getValue());
        
        // Check for each hand type from highest to lowest
        if(isRoyalFlush(fullHand)){
            return HAND_WEIGHT.ROYAL_FLUSH;
        } else if(isStraightFlush(fullHand)){
            return HAND_WEIGHT.STRAIGHT_FLUSH;
        } else if(isFourOfAKind(fullHand)){
            return HAND_WEIGHT.FOUR_OF_A_KIND;
        } else if(isFullHouse(fullHand)){
            return HAND_WEIGHT.FULL_HOUSE;
        } else if(isFlush(fullHand)){
            return HAND_WEIGHT.FLUSH;
        } else if(isStraight(fullHand)){
            return HAND_WEIGHT.STRAIGHT;
        } else if(isThreeOfAKind(fullHand)){
            return HAND_WEIGHT.THREE_OF_A_KIND;
        } else if(isTwoPair(fullHand)){
            return HAND_WEIGHT.TWO_PAIR;
        } else if(isPair(fullHand)){
            return HAND_WEIGHT.PAIR;
        } else {
            return HAND_WEIGHT.HIGH_CARD;
        }
    }

    /**
     * Check for Royal Flush (A, K, Q, J, 10 of same suit)
     */
    private boolean isRoyalFlush(List<Card> cards){
        if(!isFlush(cards)) return false;
        
        // Check if we have A, K, Q, J, 10
        Set<Integer> values = new HashSet<>();
        for(Card c : cards){
            values.add(c.getValue().getValue());
        }
        
        return values.contains(14) && values.contains(13) && 
               values.contains(12) && values.contains(11) && 
               values.contains(10);
    }

    /**
     * Check for Straight Flush (5 consecutive cards of same suit)
     */
    private boolean isStraightFlush(List<Card> cards){
        // Group cards by suit
        Map<SUIT, List<Card>> suitGroups = new HashMap<>();
        for(Card c : cards){
            suitGroups.computeIfAbsent(c.getSuit(), k -> new ArrayList<>()).add(c);
        }
        
        // Check each suit for a straight
        for(List<Card> suitCards : suitGroups.values()){
            if(suitCards.size() >= 5){
                suitCards.sort((a, b) -> b.getValue().getValue() - a.getValue().getValue());
                if(isStraight(suitCards)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check for Four of a Kind
     */
    private boolean isFourOfAKind(List<Card> cards){
        Map<Integer, Integer> valueCounts = getValueCounts(cards);
        return valueCounts.containsValue(4);
    }

    /**
     * Check for Full House (Three of a kind + Pair)
     */
    private boolean isFullHouse(List<Card> cards){
        Map<Integer, Integer> valueCounts = getValueCounts(cards);
        boolean hasThree = valueCounts.containsValue(3);
        boolean hasPair = valueCounts.containsValue(2);
        
        // Also check for two sets of three (counts as full house)
        int threeCount = 0;
        for(int count : valueCounts.values()){
            if(count == 3) threeCount++;
        }
        
        return (hasThree && hasPair) || (threeCount >= 2);
    }

    /**
     * Check for Flush (5 cards of same suit)
     */
    private boolean isFlush(List<Card> cards){
        Map<SUIT, Integer> suitCounts = new HashMap<>();
        for(Card c : cards){
            suitCounts.put(c.getSuit(), suitCounts.getOrDefault(c.getSuit(), 0) + 1);
        }
        
        for(int count : suitCounts.values()){
            if(count >= 5) return true;
        }
        return false;
    }

    /**
     * Check for Straight (5 consecutive cards)
     */
    private boolean isStraight(List<Card> cards){
        // Get unique values and sort them
        Set<Integer> uniqueValues = new TreeSet<>(Collections.reverseOrder());
        for(Card c : cards){
            uniqueValues.add(c.getValue().getValue());
        }
        
        List<Integer> values = new ArrayList<>(uniqueValues);
        
        // Check for regular straight
        for(int i = 0; i <= values.size() - 5; i++){
            boolean isStraight = true;
            for(int j = 0; j < 4; j++){
                if(values.get(i + j) - values.get(i + j + 1) != 1){
                    isStraight = false;
                    break;
                }
            }
            if(isStraight) return true;
        }
        
        // Check for Ace-low straight (A, 2, 3, 4, 5)
        if(uniqueValues.contains(14) && uniqueValues.contains(5) && 
           uniqueValues.contains(4) && uniqueValues.contains(3) && 
           uniqueValues.contains(2)){
            return true;
        }
        
        return false;
    }

    /**
     * Check for Three of a Kind
     */
    private boolean isThreeOfAKind(List<Card> cards){
        Map<Integer, Integer> valueCounts = getValueCounts(cards);
        return valueCounts.containsValue(3);
    }

    /**
     * Check for Two Pair
     */
    private boolean isTwoPair(List<Card> cards){
        Map<Integer, Integer> valueCounts = getValueCounts(cards);
        int pairCount = 0;
        for(int count : valueCounts.values()){
            if(count == 2) pairCount++;
        }
        return pairCount >= 2;
    }

    /**
     * Check for Pair
     */
    private boolean isPair(List<Card> cards){
        Map<Integer, Integer> valueCounts = getValueCounts(cards);
        return valueCounts.containsValue(2);
    }

    /**
     * Helper method to count occurrences of each card value
     */
    private Map<Integer, Integer> getValueCounts(List<Card> cards){
        Map<Integer, Integer> valueCounts = new HashMap<>();
        for(Card c : cards){
            int value = c.getValue().getValue();
            valueCounts.put(value, valueCounts.getOrDefault(value, 0) + 1);
        }
        return valueCounts;
    }

    /**
     * Get the high card value from a hand
     */
    public int getHighCard(List<Card> cards){
        int highest = 0;
        for(Card c : cards){
            if(c.getValue().getValue() > highest){
                highest = c.getValue().getValue();
            }
        }
        return highest;
    }

    /**
     * Compare two hands to determine winner
     * Returns: 1 if hand1 wins, -1 if hand2 wins, 0 if tie
     */
    public int compareHands(Player p1, Player p2, List<Card> communityCards){
        HAND_WEIGHT weight1 = checkHand(p1, communityCards);
        HAND_WEIGHT weight2 = checkHand(p2, communityCards);
        
        if(weight1.getWeight() > weight2.getWeight()){
            return 1;
        } else if(weight2.getWeight() > weight1.getWeight()){
            return -1;
        } else {
            // Same hand type - compare high cards
            List<Card> fullHand1 = new ArrayList<>();
            fullHand1.addAll(p1.getHand());
            fullHand1.addAll(communityCards);
            
            List<Card> fullHand2 = new ArrayList<>();
            fullHand2.addAll(p2.getHand());
            fullHand2.addAll(communityCards);
            
            int high1 = getHighCard(fullHand1);
            int high2 = getHighCard(fullHand2);
            
            return Integer.compare(high1, high2);
        }
    }
}
