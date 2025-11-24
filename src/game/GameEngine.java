package game;

import cards.Deck;
import players.Player;
import java.util.*;
/*
    Each player dealt two cards (face down)
    Then three 'community cards' flopped (middle of table)
    First betting round ensues, big blind first
*/
public class GameEngine{
    private final Table table;
    private final Deck deck;
    private final Scanner sc;
    private final CheckHand handChecker;
    private final int numPlayers = 4;

    public GameEngine(){
        this.table = new Table();
        this.deck = new Deck();
        this.sc = new Scanner(System.in);
        this.handChecker = new CheckHand();        
    }
    //start of game -- deal two cards to each player
    public void preFlop(){
        //deal one card to each player, twice
        for(int i = 0; i < 2; i++){
            for(Player p : table.getPlayers()){
                p.giveCard(deck.deal());
            }
        }
    }
    public void start(){
        System.out.println("Welcome to Texas Hold'em!");

        for(int i = 1; i <= numPlayers; i++){
            System.out.print("Enter name for Player " + i + ": ");
            String name = sc.nextLine();
            Player p = new Player(name);
            table.addPlayer(p);
        }
        System.out.println("Starting game");
        //further game logic to be implemented
        preFlop();
    }    
}