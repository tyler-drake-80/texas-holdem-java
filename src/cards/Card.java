package cards;

public class Card{
    private final SUIT suit;
    private final VALUE val;

    public Card(SUIT s, VALUE v){
        this.suit = s;
        this.val = v;
    }

    public VALUE getValue(){ return this.val; }

    public SUIT getSuit(){ return this.suit; }

    @Override
    public String toString(){ return this.val + " of " + this.suit; }
}