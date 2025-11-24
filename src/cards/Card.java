package cards;

public class Card{
    private final SUIT suit;
    private final VALUE val;
    private boolean isFaceUp = false;

    public Card(SUIT s, VALUE v){
        this.suit = s;
        this.val = v;
    }

    public VALUE getValue(){ return this.val; }

    public SUIT getSuit(){ return this.suit; }

    public boolean isFaceUp(){ return this.isFaceUp; }
    
    public boolean flip(){
        this.isFaceUp = !this.isFaceUp;
        return this.isFaceUp;
    }

    @Override
    public String toString(){ return this.val + " of " + this.suit; }
}