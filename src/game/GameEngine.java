package game;

import cards.Card;
import cards.Deck;
import gui.integration.GameListener;
import gui.integration.GameState;
import gui.integration.PlayerState;
import java.util.*;
import players.POSITIONS;
import players.Player;
/**
 * This version is GUI driven
 * -no scanner input
 * -uses GameListener to notify GUI of state changes and request player actions
 * 
 * Currently supports:
 * -4 player pass and play
 * -FOLD, CHECK, CALL, ALL_IN (no raises)
 */
public class GameEngine{
    private final Table table;
    private final Deck deck;
    private final CheckHand handChecker;
    //connect engine to GUI
    private GameListener listener;
    //index of button in table.getPlayers()
    private int dealerIndex = 0; // Tracks dealer position

    public GameEngine(){
        this.table = new Table();
        this.deck = new Deck();
        this.handChecker = new CheckHand();        
    }
   /*  public void startDemoHandForGUI(java.util.List<String> names) {
        // Clear any existing players/community if needed

        // Create players
        for (String name : names) {
            Player p = new Player(name);
            table.addPlayer(p);
        }

        this.numPlayers = names.size();

        // Assign positions and blinds, then deal a full board
        assignPositions();
        postBlinds();
        preFlop();  // deal hole cards
        flop();
        turn();
        river();
    } */
    public Table getTable(){
        return table;
    }
    public void setListener(GameListener listener){
        this.listener = listener;
    }
    private void ensurePlayers(){
        if(!table.getPlayers().isEmpty()){
            return;
        }
        for(int i = 0; i < 4; i++){
            Player p = new Player("Player " + (i + 1));
            table.addPlayer(p);
        }
    }

    public void startPassAndPlayHand(){
        ensurePlayers();

        resetPlayersForNewHand();
        assignPositions();
        postBlinds();

        preFlop();  // deal hole cards
        bet(true);

        if(table.getPlayersInHand() > 1){
            flop();
            bet(false);
        }
        if(table.getPlayersInHand() > 1){
            turn();
            bet(false);
        }
        if(table.getPlayersInHand() > 1){
            river();
            bet(false);
        }
        // Showdown
        handleShowdown();

        // Move dealer button
        dealerIndex = (dealerIndex + 1) % table.getPlayers().size();
    }
    /*
    * Resets hand flags on players: folded, all-in, current bet, hand cards
     */
    private void resetPlayersForNewHand(){
        for(Player p : table.getPlayers()){
            p.resetForNewHand();
        }
        table.resetForNewHand();
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
        notifyState(null);
    }

    //start of game -- deal two cards to each player
    public void preFlop(){
        //deal one card to each player, twice
        for(int i = 0; i < 2; i++){
            for(Player p : table.getPlayers()){
                p.giveCard(deck.deal());
            }
        }
        notifyState(null);
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
        notifyState(null);
    }

    //deal 1 community card
    public void turn(){
        deck.deal();//burn card
        Card c = deck.deal();
        c.flip();//face up
        table.addCommunityCard(c);
        System.out.println("\n=== TURN ===");
        displayCommunityCards();
        notifyState(null);
    }

    //easier understanding of game function/flow
    public void river(){ 
        deck.deal();//burn card
        Card c = deck.deal();
        c.flip();//face up
        table.addCommunityCard(c);
        System.out.println("\n=== RIVER ===");
        displayCommunityCards();
        notifyState(null);
    }
    


    private void displayCommunityCards(){
        System.out.print("River: ");
        for(Card c : table.getCommunityCards()){
            System.out.print(c + " | ");
        }
        System.out.println();
    }

    /*private GameState buildGameState(int activePlayerIndex){
        GameState gs = new GameState();
        gs.pot = table.getPot();
        gs.communityCards = new ArrayList<>(table.getCommunityCards());
        gs.activePlayerSeat = activePlayerIndex;

        gs.players = new ArrayList<>();
        List<Player> players = table.getPlayers(); 

        for(int i = 0; i < players.size(); i++){
            Player p = players.get(i);

            PlayerState ps = new PlayerState(i);
            ps.name = p.getName();
            ps.chips = p.getChips();
            ps.hole1 = p.getHand().size() > 0 ? p.getHand().get(0) : null;
            ps.hole2 = p.getHand().size() > 1 ? p.getHand().get(1) : null;
            ps.folded = p.isFolded();
            ps.allIn = p.isAllIn();
            //pass and play rules:
            //only acting player cards are face up
            ps.faceUp = (i == activePlayerIndex); // only show active player's cards
            ps.active = (i == activePlayerIndex);

            gs.players.add(ps);
        }
        return gs;
    }*/
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
            if(currentPlayer.isFolded() || currentPlayer.isAllIn()){
                // Skip folded or all-in players
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                if(currentPlayerIndex == startIndex){
                    firstRound = false;
                }
                continue;
            }
            if(table.getPlayersInHand() <= 1){
                // Only one player remains, end betting
                break;
            }
            notifyState(currentPlayer);

            int amountToCall = table.getCurrentBet() - currentPlayer.getCurrentBet();
            //ask GUI for action
            String action = (listener != null) ?
                listener.requestPlayerAction(currentPlayer) : null;
            if(action == null){
                //Defensive : treat null as check/fold
                action = (amountToCall == 0 ? "CHECK" : "FOLD");
            } 
            action = action.toUpperCase();

            switch(action){
                case "FOLD":
                    currentPlayer.fold();
                    System.out.println(currentPlayer.getName() + " folds.");
                    break;
                    
                case "CHECK":
                    if(amountToCall > 0){
                        if(amountToCall <= currentPlayer.getChips()){
                            int paid = currentPlayer.placeBet(amountToCall);
                            table.addToPot(paid);
                            System.out.println(currentPlayer.getName() + " (illegal CHECK) auto-calls $" + amountToCall);
                        } else{
                            currentPlayer.fold();
                            System.out.println(currentPlayer.getName() + " cannot check and folds.");
                        }
                    } else{
                        System.out.println(currentPlayer.getName() + " checks.");
                    }
                    break;
                    
                case "CALL":
                    if(amountToCall > 0){
                        int paid = currentPlayer.placeBet(amountToCall);
                        table.addToPot(paid);
                    }
                    System.out.println(currentPlayer.getName() + " calls $" + amountToCall + " (total bet: $" + currentPlayer.getCurrentBet() + ")");
                    break;
               /* --------Implement later-------     
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
                */ 
                case "ALLIN":
                    int allInAmount = currentPlayer.getChips();
                    if(allInAmount > 0){
                        int paidAllIn = currentPlayer.placeBet(allInAmount);
                        table.addToPot(paidAllIn);
                        if(currentPlayer.getCurrentBet() > table.getCurrentBet()){
                            table.setCurrentBet(currentPlayer.getCurrentBet());
                            lastRaiserIndex = currentPlayerIndex;
                        }
                    }
                    System.out.println(currentPlayer.getName() + " goes ALL-IN with $" + allInAmount + " (total bet: $" + currentPlayer.getCurrentBet() + ")");
                    break;
            }
            int prevIndex = currentPlayerIndex;
            // Move to next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            
            // Check if we've completed the circle
            if(currentPlayerIndex == startIndex){
                firstRound = false;
            }

            boolean allMatched = true;
            int activePlayers = 0;
            int currentBet = table.getCurrentBet();

            for(Player p : table.getPlayers()){
                if(!p.isFolded() && !p.isAllIn()){
                    activePlayers++;
                    if(p.getCurrentBet() != currentBet){
                        allMatched = false;
                        break;
                    }
                }
            }
            if(activePlayers <= 1){
                // Only one player remains
                bettingComplete = true;
            } else if(allMatched && !firstRound){
                // All active players have matched the current bet and we've gone around at least once
                if(lastRaiserIndex == -1){
                    bettingComplete = true;
                } else if(prevIndex == lastRaiserIndex){
                    bettingComplete = true;
                }
            }

            // Check if betting round is complete
            //bettingComplete = isBettingRoundComplete();
            
        }
        table.resetCurrentBet();
        notifyState(null);
    }

    /*private boolean isBettingRoundComplete(){
        List<Player> players = table.getPlayers();
        int activePlayers = 0;
        int currentBet = table.getCurrentBet();

        for(Player p : table.getPlayers()){
            if(!p.isFolded() && !p.isAllIn()){
                activePlayers++;
                if(p.getCurrentBet() < currentBet){
                    return false;
                }
            }
        }
        return activePlayers <= 1 || activePlayers > 0;
    }*/
    
    private void handleShowdown(){
        List<Player> players = table.getPlayers();
        List<Player> contenders = new ArrayList<>();
        for(Player p : players){
            if(!p.isFolded()){
                contenders.add(p);
            }
        }
        if(contenders.size() == 1){
            Player winner = contenders.get(0);
            //award entire pot to winner
            winner.addChips(table.getPot());
            System.out.println(winner.getName() + " wins the pot of $" + table.getPot() + " by default (all others folded).");
            notifyState(null);
            return;
        }
        //multiple people to showdown
        List<HAND_WEIGHT> weights = new ArrayList<>();
        for(Player p : contenders){
            HAND_WEIGHT weight = handChecker.checkWeight(p, table.getCommunityCards());
            weights.add(weight);
        }
        //TODO:
        // -find the best HAND_WEIGHT(s)
        // -split pot if tie
        //update chips accordingly
        notifyState(null);
    }
    /**
     * Build and send current game state to GUI
     * @param actingPlayer player whose turn it is, null if none
     */
    private void notifyState(Player actingPlayer){
        if(listener == null) return;

        GameState gs = new GameState();
        gs.players = new ArrayList<>();
        gs.communityCards = new ArrayList<>(table.getCommunityCards());
        gs.pot = table.getPot();

        gs.activePlayerSeat = -1;
        gs.currentPlayerSeat = -1;

        List<Player> players = table.getPlayers();

        for(int i = 0; i < players.size(); i++){
            Player p = players.get(i);

            PlayerState ps = new PlayerState(i);
            ps.seat = i;
            ps.name = p.getName();
            ps.chips = p.getChips();
            ps.folded = p.isFolded();
            ps.allIn = p.isAllIn();

            List<Card> hand = p.getHand();
            ps.hole1 = (hand.size() > 0) ? hand.get(0) : null;
            ps.hole2 = (hand.size() > 1) ? hand.get(1) : null;

            boolean showdown = (table.getCommunityCards().size() == 5);
            ps.faceUp = (actingPlayer != null && p == actingPlayer) || showdown;
            ps.active = (actingPlayer != null && p == actingPlayer);
            if(ps.active){
                gs.currentPlayerSeat = i;
                gs.activePlayerSeat = i;
            }
            gs.players.add(ps); 
        }
        listener.onStateUpdated(gs);
    }
    /**
     * Get valid action from player
     * NO LONGER NEEDED WITH GUI
     */
    /*private String getPlayerAction(Player player){
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
    }*/

    /*public void start(){
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
                HAND_WEIGHT weight = handChecker.checkWeight(p, table.getCommunityCards());
                weights.add(weight);
            }
        }
        
        // TODO: Determine winner and award pot
        System.out.println("\nFinal Pot: $" + table.getPot());
    }*/
}
