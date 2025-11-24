package game; 
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
public enum HAND_WEIGHT {
    HIGH_CARD(1),
    PAIR(2),
    TWO_PAIR(3),
    THREE_OF_A_KIND(4),
    STRAIGHT(5),
    FLUSH(6),
    FULL_HOUSE(7),
    FOUR_OF_A_KIND(8),
    STRAIGHT_FLUSH(9),
    ROYAL_FLUSH(10);

    private final int weight;

    HAND_WEIGHT(int w){
        this.weight = w;
    }

    public int getWeight(){
        return this.weight;
    }
}
