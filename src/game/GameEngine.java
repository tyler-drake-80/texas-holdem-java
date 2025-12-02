package game;

import cards.Card;
import cards.Deck;
import java.util.*;
import players.POSITIONS;
import players.Player;

public class GameEngine{
    private final Table table;
    private final Deck deck;
    private final Scanner sc;
    private final CheckHand handChecker;
    private int numPlayers; // No longer final with default value
    private int dealerIndex = 0; // Tracks dealer position

    public GameEngine(){
        this.table = new Table();
        this.deck = new Deck();
        this.sc = new Scanner(System.in);
        this.handChecker = new CheckHand();        
    }

    /**
     * Assign positions to players based on dealer button
     */
    private void assignPositions(){
        List<Player> players = table.getPlayers();
        int numPlayers = players.size();
        
        for(int i = 0; i < numPlayers; i++){
            if(i == dealerIndex){
                players.get(i).setPosition(POSITIONS.BUTTON);
            } else if(i == (dealerIndex + 1) % numPlayers){
                players.get(i).setPosition(POSITIONS.SMALL_BLIND);
            } else if(i == (dealerIndex + 2) % numPlayers){
                players.get(i).setPosition(POSITIONS.BIG_BLIND);
            } else {
                players.get(i).setPosition(POSITIONS.UNDER_THE_GUN);
            }
        }
    }

    /**
     * Post blinds before the hand starts
     */
    private void postBlinds(){
        List<Player> players = table.getPlayers();
        
        for(Player p : players){
            if(p.getPosition() == POSITIONS.SMALL_BLIND){
                int sbAmount = p.placeBet(table.getSmallBlind());
                table.addToPot(sbAmount);
                System.out.println(p.getName() + " posts small blind: $" + sbAmount);
            } else if(p.getPosition() == POSITIONS.BIG_BLIND){
                int bbAmount = p.placeBet(table.getBigBlind());
                table.addToPot(bbAmount);
                table.setCurrentBet(table.getBigBlind());
                System.out.println(p.getName() + " posts big blind: $" + bbAmount);
            }
        }
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
        System.out.println("\n=== FLOP ===");
        displayCommunityCards();
    }

    //deal 1 community card
    public void turn(){
        deck.deal();//burn card
        Card c = deck.deal();
        c.flip();//face up
        table.addCommunityCard(c);
        System.out.println("\n=== TURN ===");
        displayCommunityCards();
    }

    //easier understanding of game function/flow
    public void river(){ 
        deck.deal();//burn card
        Card c = deck.deal();
        c.flip();//face up
        table.addCommunityCard(c);
        System.out.println("\n=== RIVER ===");
        displayCommunityCards();
    }

    private void displayCommunityCards(){
        System.out.print("River: ");
        for(Card c : table.getCommunityCards()){
            System.out.print(c + " | ");
        }
        System.out.println();
    }

    /**
     * Main betting logic
     * @param isPreFlop true if this is pre-flop betting, false otherwise
     */
    public void bet(boolean isPreFlop){
        List<Player> players = table.getPlayers();
        
        // Determine starting position
        int startIndex;
        if(isPreFlop){
            // Pre-flop: start after big blind
            startIndex = (dealerIndex + 3) % players.size();
        } else {
            // Post-flop: start after dealer
            startIndex = (dealerIndex + 1) % players.size();
        }

        int currentPlayerIndex = startIndex;
        int lastRaiserIndex = -1;
        boolean bettingComplete = false;
        
        // Track if we've gone around once for pre-flop (so big blind gets option to raise)
        boolean firstRound = true;

        while(!bettingComplete){
            Player currentPlayer = players.get(currentPlayerIndex);

            // Skip folded or all-in players
            if(currentPlayer.isFolded() || currentPlayer.isAllIn()){
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                
                // Check if we've completed the circle
                if(currentPlayerIndex == startIndex){
                    firstRound = false;
                }
                
                continue;
            }

            // Check if only one player remains
            if(table.getPlayersInHand() == 1){
                bettingComplete = true;
                break;
            }

            // Calculate amount needed to call
            int amountToCall = table.getCurrentBet() - currentPlayer.getCurrentBet();

            // Display player's turn
            System.out.println("\n" + currentPlayer.getName() + "'s turn");
            System.out.println("Chips: $" + currentPlayer.getChips());
            System.out.println("You've bet this round: $" + currentPlayer.getCurrentBet());
            if(amountToCall > 0){
                System.out.println("Amount to call: $" + amountToCall);
            }
            System.out.println("Your cards: " + currentPlayer.getHand());
            
            // Show community cards if any
            if(!table.getCommunityCards().isEmpty()){
                displayCommunityCards();
            }

            System.out.println("Pot: $" + table.getPot());

            // Get player action
            String action = getPlayerAction(currentPlayer);
            
            switch(action.toUpperCase()){
                case "FOLD":
                    currentPlayer.fold();
                    System.out.println(currentPlayer.getName() + " folds.");
                    break;
                    
                case "CHECK":
                    System.out.println(currentPlayer.getName() + " checks.");
                    break;
                    
                case "CALL":
                    int callAmount = table.getCurrentBet() - currentPlayer.getCurrentBet();
                    int actualCall = currentPlayer.placeBet(callAmount);
                    table.addToPot(actualCall);
                    System.out.println(currentPlayer.getName() + " calls $" + actualCall + " (total bet: $" + currentPlayer.getCurrentBet() + ")");
                    break;
                    
                case "RAISE":
                    System.out.print("Raise to (total bet amount): $");
                    int raiseAmount = sc.nextInt();
                    sc.nextLine(); // consume newline
                    
                    int amountToAdd = raiseAmount - currentPlayer.getCurrentBet();
                    if(amountToAdd <= 0 || raiseAmount <= table.getCurrentBet()){
                        System.out.println("Invalid raise amount. Must be greater than current bet of $" + table.getCurrentBet());
                        continue;
                    }
                    
                    int actualRaise = currentPlayer.placeBet(amountToAdd);
                    table.addToPot(actualRaise);
                    table.setCurrentBet(currentPlayer.getCurrentBet());
                    lastRaiserIndex = currentPlayerIndex;
                    System.out.println(currentPlayer.getName() + " raises to $" + currentPlayer.getCurrentBet());
                    break;
                    
                case "ALLIN":
                    int allInAmount = currentPlayer.getChips();
                    currentPlayer.placeBet(allInAmount);
                    table.addToPot(allInAmount);
                    
                    if(currentPlayer.getCurrentBet() > table.getCurrentBet()){
                        table.setCurrentBet(currentPlayer.getCurrentBet());
                        lastRaiserIndex = currentPlayerIndex;
                    }
                    System.out.println(currentPlayer.getName() + " goes ALL-IN with $" + allInAmount + " (total bet: $" + currentPlayer.getCurrentBet() + ")");
                    break;
            }

            // Move to next player
            int prevIndex = currentPlayerIndex;
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            
            // Check if we've completed the circle
            if(currentPlayerIndex == startIndex){
                firstRound = false;
            }

            // Check if betting round is complete
            boolean allMatched = true;
            int activePlayers = 0;
            
            for(Player p : players){
                if(!p.isFolded() && !p.isAllIn()){
                    activePlayers++;
                    if(p.getCurrentBet() != table.getCurrentBet()){
                        allMatched = false;
                        break;
                    }
                }
            }

            // Betting complete if:
            // 1. Only one active player remains, OR
            // 2. All active players have matched the current bet AND
            //    - Either no one has raised (lastRaiserIndex == -1), OR
            //    - Action has returned to the last raiser
            if(activePlayers <= 1){
                bettingComplete = true;
            } else if(allMatched && !firstRound){
                if(lastRaiserIndex == -1){
                    // No one raised, everyone checked/called
                    bettingComplete = true;
                } else if(prevIndex == lastRaiserIndex){
                    // Action just completed with the last raiser
                    bettingComplete = true;
                }
            }
        }

        // Reset current bets for next round
        table.resetCurrentBet();
        System.out.println("\n--- Betting round complete. Pot: $" + table.getPot() + " ---\n");
    }

    /**
     * Get valid action from player
     */
    private String getPlayerAction(Player player){
        int amountToCall = table.getCurrentBet() - player.getCurrentBet();
        
        while(true){
            System.out.print("Action: ");
            
            // Show available actions
            if(amountToCall == 0){
                System.out.print("[CHECK / RAISE / FOLD / ALLIN]: ");
            } else if(amountToCall >= player.getChips()){
                System.out.print("[FOLD / ALLIN]: ");
            } else {
                System.out.print("[FOLD / CALL ($" + amountToCall + ") / RAISE / ALLIN]: ");
            }
            
            String action = sc.nextLine().toUpperCase();
            
            // Validate action
            if(action.equals("FOLD")){
                return action;
            } else if(action.equals("CHECK") && amountToCall == 0){
                return action;
            } else if(action.equals("CALL") && amountToCall > 0 && amountToCall <= player.getChips()){
                return action;
            } else if(action.equals("RAISE") && player.getChips() > amountToCall){
                return action;
            } else if(action.equals("ALLIN")){
                return action;
            } else {
                System.out.println("Invalid action. Please try again.");
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
        
        System.out.println("\nStarting game with " + numPlayers + " players");
        System.out.println("Blinds: $" + table.getSmallBlind() + "/$" + table.getBigBlind());
        System.out.println("Each player starts with $1000");
        
        // Assign positions and post blinds
        assignPositions();
        postBlinds();
        
        System.out.println("\n=== DEALING CARDS ===");
        preFlop();
        
        bet(true); // Pre-flop betting
        
        // Only continue if more than one player remains
        if(table.getPlayersInHand() > 1){
            flop();
            bet(false); // Post-flop betting
        }
        
        if(table.getPlayersInHand() > 1){
            turn();
            bet(false); // Post-turn betting
        }
        
        if(table.getPlayersInHand() > 1){
            river();
            bet(false); // Post-river betting
        }
        
        // Showdown
        System.out.println("\n=== SHOWDOWN ===");
        List<HAND_WEIGHT> weights = new ArrayList<>();
        for(Player p : table.getPlayers()){
            if(!p.isFolded()){
                System.out.println(p.getName() + "'s hand:");
                for(Card c : p.getHand()){
                    System.out.println("  " + c);
                }
                HAND_WEIGHT weight = handChecker.checkHand(p, table.getCommunityCards());
                weights.add(weight);
            }
        }
        
        // TODO: Determine winner and award pot
        System.out.println("\nFinal Pot: $" + table.getPot());
    }    
}
