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
    private int startingChips = 1000; // Default starting chips
    private int numberOfPlayers = 4; // Default number of players (2-8)
    //Stored winner text for GUI
    //private String lastWinnerMessage = "";

    public GameEngine(){
        this.table = new Table();
        this.deck = new Deck();
        this.handChecker = new CheckHand();        
    }

    public Table getTable(){
        return table;
    }
    public void setListener(GameListener listener){
        this.listener = listener;
    }
    
    public void setStartingChips(int chips){
        this.startingChips = chips;
    }
    
    public void setNumberOfPlayers(int players){
        this.numberOfPlayers = players;
    }
    private void ensurePlayers(){
        if(!table.getPlayers().isEmpty()){
            return;
        }
        for(int i = 0; i < numberOfPlayers; i++){
            Player p = new Player("Player " + (i + 1), startingChips);
            table.addPlayer(p);
        }
    }

    public void startPassAndPlayHand(){
        ensurePlayers();

        table.setWinnerText("");//reset previous winner message
        
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

    /**
     * Main betting logic
     * @param isPreFlop true if this is pre-flop betting, false otherwise
     */
    public void bet(boolean isPreFlop){
        List<Player> players = table.getPlayers();
        
        // Determine starting position
        int startIndex = isPreFlop
                ? (dealerIndex + 3) % players.size() //UTG
                : (dealerIndex + 1) % players.size();//left of dealer
        int currentPlayerIndex = startIndex;
        int lastRaiserIndex = -1;
        boolean firstLoop = true;

        while(true){
            if(table.getPlayersInHand() <= 1){
                table.resetCurrentBet();
                notifyState(null);
                break; // Hand over
            }
            Player p = players.get(currentPlayerIndex);
            if(!p.isFolded() && !p.isAllIn()){

                int amountToCall = table.getCurrentBet() - p.getCurrentBet();
                notifyState(p);

                // Wait for player's action via listener
                String action = listener.requestPlayerAction(p).toUpperCase();

                //handle user-entered raise
                
                if (action.startsWith("RAISE")) {
                    String[] parts = action.split(":");
                    if (parts.length < 2) {
                        System.out.println("ERROR: RAISE received without amount");
                        continue; 
                    }

                    int raiseAmount;
                    try {
                        raiseAmount = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("ERROR: Invalid raise amount");
                        continue;
                    }

                    int callAmount = table.getCurrentBet() - p.getCurrentBet();
                    int totalPutIn = callAmount + raiseAmount;

                    // Cap to player's stack
                    totalPutIn = Math.min(totalPutIn, p.getChips());

                    int contributed = p.placeBet(totalPutIn);
                    table.addToPot(contributed);

                    // Update current bet if this is now the largest
                    if (p.getCurrentBet() > table.getCurrentBet()) {
                        table.setCurrentBet(p.getCurrentBet());
                        lastRaiserIndex = currentPlayerIndex;
                    }

                    // *** CRITICAL FIX ***  
                    // Advance the turn immediately and continue loop normally
                    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
                    firstLoop = false;
                    notifyState(null);
                    continue;  
                }


                
                switch(action.toUpperCase()){
                    case "FOLD":
                        p.fold();
                        System.out.println(p.getName() + " folds.");
                        break;

                    case "CHECK":
                        if(amountToCall > 0){
                            int paid = p.placeBet(amountToCall);
                            table.addToPot(paid);
                        }
                        System.out.println(p.getName() + " checks.");
                        break;

                    case "CALL":
                        if(amountToCall > 0){
                            int paid = p.placeBet(amountToCall);
                            table.addToPot(paid);
                        }
                        System.out.println(p.getName() + " calls.");
                        break;

                    case "ALL_IN":
                        int allInAmount = p.getChips();
                        int betAmount = p.placeBet(allInAmount);
                        table.addToPot(betAmount);
                        System.out.println(p.getName() + " goes all-in: $" + betAmount);

                        if(p.getCurrentBet() > table.getCurrentBet()){
                            table.setCurrentBet(p.getCurrentBet());
                            lastRaiserIndex = currentPlayerIndex;
                        }
                        break;
                    default:
                        System.out.println("Invalid action received: " + action);
                }
            }

            //int prevIndex = currentPlayerIndex;
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            // Check if betting round is complete
            if(!firstLoop && currentPlayerIndex == startIndex){
                boolean allBetsEqual = true;
                //int targetBet = table.getCurrentBet();
                for(Player x : players){
                    if(!x.isFolded() && !x.isAllIn()){
                        if(x.getCurrentBet() != table.getCurrentBet()){
                            allBetsEqual = false;
                            break;
                        }
                    }
                }
                if(allBetsEqual){
                    break; // Betting round complete
                }
            }
            if(lastRaiserIndex != -1 && currentPlayerIndex == lastRaiserIndex && !firstLoop){
                break;//betting round complete
            }
            firstLoop = false;

        }
        table.resetCurrentBet();
        notifyState(null);
    }

    private void handleShowdown(){
        List<Player> players = table.getPlayers();
        List<Player> contenders = new ArrayList<>();
        for(Player p : players){
            if(!p.isFolded()){
                contenders.add(p);
            }
        }
        
        if(contenders.isEmpty()){
            table.setWinnerText("All players folded. No winner.");
            notifyState(null,true);
            return;
        }
        if(contenders.size() == 1){
            Player winner = contenders.get(0);
            //award entire pot to winner
            winner.addChips(table.getPot());
            String msg = winner.getName() + " wins the pot of $" + table.getPot();
            table.setWinnerText(msg);
            System.out.println(winner.getName() + " wins the pot of $" + table.getPot() + " by default (all others folded).");
            notifyState(null,true);
            return;
        }
        
        // Multiple players to showdown - evaluate hands
        System.out.println("\n=== SHOWDOWN ===");
        Map<Player, HAND_WEIGHT> playerHands = new HashMap<>();
        
        for(Player p : contenders){
            HAND_WEIGHT weight = handChecker.checkWeight(p, table.getCommunityCards());
            playerHands.put(p, weight);
            System.out.println(p.getName() + "'s hand: " + p.getHand() + " - " + weight);
        }
        
        // Find the best hand weight
        int bestWeight = -1;
        for(HAND_WEIGHT weight : playerHands.values()){
            if(weight.getWeight() > bestWeight){
                bestWeight = weight.getWeight();
            }
        }
        
        // Find all players with the best hand
        List<Player> winners = new ArrayList<>();
        for(Player p : contenders){
            if(playerHands.get(p).getWeight() == bestWeight){
                winners.add(p);
            }
        }
        
        // If there's a tie on hand rank, compare high cards
        if(winners.size() > 1){
            winners = breakTie(winners);
        }
        
        StringBuilder resultText = new StringBuilder();
        
        if(winners.size() == 1){
            Player winner = winners.get(0);
            winner.addChips(table.getPot());
            String msg = winner.getName() + " wins the pot of $" + table.getPot() + " with " + playerHands.get(winner);
            resultText.append(msg);
            System.out.println("\n" + winner.getName() + " wins the pot of $" + table.getPot() + " with " + playerHands.get(winner));
        } else {
            // Award pot (split if necessary)
            int potShare = table.getPot() / winners.size();
            int remainder = table.getPot() % winners.size();
            System.out.println("\nPot is split among winners:");
            resultText.append("Pot split: ");
            for(int i = 0; i < winners.size(); i++){
                Player winner = winners.get(i);
                int award = potShare + (i == 0 ? remainder : 0); // Give remainder to first winner
                winner.addChips(award);
                System.out.println("  " + winner.getName() + " receives $" + award);
                if (i > 0) resultText.append(", ");
                resultText.append(winner.getName()).append(" ($").append(award).append(")");
            }
        }
        table.setWinnerText(resultText.toString());
        notifyState(null,true);
    }
    
    /**
     * Break tie between players with same hand rank by comparing high cards
     * Returns list of winners (may still be multiple if exact tie)
     */
    private List<Player> breakTie(List<Player> tiedPlayers){
        if(tiedPlayers.size() <= 1) return tiedPlayers;
        
        // Get all cards for each player
        Map<Player, List<Card>> playerCards = new HashMap<>();
        for(Player p : tiedPlayers){
            List<Card> allCards = new ArrayList<>();
            allCards.addAll(p.getHand());
            allCards.addAll(table.getCommunityCards());
            // Sort by value descending
            allCards.sort((a, b) -> b.getValue().getValue() - a.getValue().getValue());
            playerCards.put(p, allCards);
        }
        
        // Compare high cards
        int maxHighCard = -1;
        for(List<Card> cards : playerCards.values()){
            int highCard = cards.get(0).getValue().getValue();
            if(highCard > maxHighCard){
                maxHighCard = highCard;
            }
        }
        
        List<Player> winners = new ArrayList<>();
        for(Map.Entry<Player, List<Card>> entry : playerCards.entrySet()){
            if(entry.getValue().get(0).getValue().getValue() == maxHighCard){
                winners.add(entry.getKey());
            }
        }
        
        return winners;
    }
    /**
     * Build and send current game state to GUI
     * @param actingPlayer player whose turn it is, null if none
     * @param revealAll if true, all players' cards are face up (showdown)
     */
    private void notifyState(Player actingPlayer, boolean revealAll){
        if(listener == null) return;

        GameState gs = new GameState();
        gs.players = new ArrayList<>();
        gs.communityCards = new ArrayList<>(table.getCommunityCards());
        gs.pot = table.getPot();
        gs.winnerText = table.getWinnerText();

        gs.activePlayerSeat = -1;
        gs.currentPlayerSeat = -1;
        gs.dealerSeat = dealerIndex;

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

            ps.faceUp = (actingPlayer != null && p == actingPlayer) || revealAll;
            ps.active = (actingPlayer != null && p == actingPlayer);

            if(ps.active){
                gs.currentPlayerSeat = i;
                gs.activePlayerSeat = i;
            }
            gs.players.add(ps); 
        }
        listener.onStateUpdated(gs);
    }
    private void notifyState(Player actingPlayer){
        notifyState(actingPlayer, false);
    }
}
