package game;

import cards.Card;
import cards.Deck;
import players.Player;
import java.util.*;

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
    //deal 3 community cards
    public void flop(){
        deck.deal();//burn card
        for(int i = 0; i < 3; i++){//deal three community cards
            Card c = deck.deal();
            c.flip();//face up
            table.addCommunityCard(c);
        }
    }
    //deal 1 community card
    public void turn(){
        deck.deal();//burn card
        Card c = deck.deal();
        c.flip();//face up
        table.addCommunityCard(c);
    }
    //easier understanding of game function/flow
    public void river(){ turn(); }

    public void bet(){
        //implement
        List<Player> players = table.getPlayers();
        for(Player p : players){
            if(!p.isFolded()){
                System.out.print(p.getName() + ", do you want to fold? (y/n): ");
                String response = sc.nextLine();
                if(response.equalsIgnoreCase("y")){
                    p.fold();
                    System.out.println(p.getName() + " has folded.");
                } else {
                    System.out.println(p.getName() + " stays in the game.");
                }
            }
        }
    }
    public void start(){
        System.out.println("Welcome to Texas Hold'em!");

        for(int i = 0; i < numPlayers; i++){
            System.out.print("Enter name for Player " + (i+1) + ": ");
            String name = sc.nextLine();
            Player p = new Player(name);
            table.addPlayer(p);
        }
        System.out.println("Starting game");
        //further game logic to be implemented
        preFlop();
        bet();
        flop();
        bet();
        turn();
        bet();
        river();
        bet();
        table.showAllHands();
        Player winner = handChecker.findWinner(table);
        System.out.println("The winner is " + winner.getName() + "!");
    }    
    
}