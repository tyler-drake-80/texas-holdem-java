package gui.integration;

import cards.Card;
import game.CheckHand;
import game.GameEngine;
import game.HAND_WEIGHT;
import gui.view.*;
import players.Player;

import java.util.List;

public class GUIListener implements GameListener {
    private final TablePanel tablePanel;
    private final ActionPanel actionPanel;
    private final CheckHand handChecker;
    private final GameEngine engine; 

    /** This variable stores the result returned when user clicks a betting button */
    private volatile String pendingAction = null;

    // Lock for synchronization between game thread and EDT
    private final Object actionLock = new Object();

    public GUIListener(TablePanel tablePanel, ActionPanel actionPanel, GameEngine engine) {
        this.tablePanel = tablePanel;
        this.actionPanel = actionPanel;
        this.engine = engine;
        this.handChecker = new CheckHand();

        actionPanel.setActionConsumer(action -> {
            if ("NEXT_HAND".equals(action)) {
                // User clicked "Next Hand" at showdown:
                // 1) wake the game thread (waiting in waitForNextHand)
                // 2) hide the button
                engine.resumeNextHand();
                actionPanel.hideNextHandButton();
                // clear any stale pendingAction
                synchronized (actionLock) {
                    pendingAction = null;
                }
            } else {
                // Normal betting actions (FOLD/CHECK/CALL/RAISE/ALL_IN)
                synchronized (actionLock) {
                    pendingAction = action;
                    actionLock.notifyAll();
                }
            }
        });
    }
    
    @Override
    public void onStateUpdated(GameState state) {
        // Update table display
        tablePanel.applyTableState(convertState(state));
    }

    private TablePanel.TableState convertState(GameState gs) {
        // Convert GameState to TableState for the TablePanel
        TablePanel.TableState ts = new TablePanel.TableState();

        for (gui.integration.PlayerState ps : gs.players) {
            TablePanel.PlayerState pstate = new TablePanel.PlayerState(ps.seat);
            pstate.name = ps.name;
            pstate.chips = ps.chips;
            pstate.hole1 = ps.hole1;
            pstate.hole2 = ps.hole2;
            pstate.cardsFaceUp = ps.faceUp;
            pstate.active = ps.active;
            
            // Calculate and set hand ranking if cards are visible and community cards exist
            if (ps.faceUp && ps.hole1 != null && ps.hole2 != null && !gs.communityCards.isEmpty()) {
                List<Card> playerCards = List.of(ps.hole1, ps.hole2);
                HAND_WEIGHT handRanking = handChecker.checkWeight(playerCards, gs.communityCards);
                pstate.handRanking = formatHandRanking(handRanking);
            } else {
                pstate.handRanking = "";
            }

            ts.players.add(pstate);
        }
        // winner + dealer
        ts.winnerText = gs.winnerText;
        ts.communityCards = gs.communityCards;
        ts.pot = gs.pot;
        ts.dealerSeat = gs.dealerSeat;
        return ts;
    }

    @Override
    public void onAwaitNextHand() {
        // Hand is over, winner text is shown.
        // Disable betting buttons and show "Next Hand".
        actionPanel.disableAll();
        actionPanel.showNextHandButton();
    } 

    /**
     * Formats hand ranking for display
     */
    private String formatHandRanking(HAND_WEIGHT ranking) {
        if (ranking == null) return "";
        
        switch (ranking) {
            case ROYAL_FLUSH:    return "Royal Flush";
            case STRAIGHT_FLUSH: return "Straight Flush";
            case FOUR_OF_A_KIND: return "Four of a Kind";
            case FULL_HOUSE:     return "Full House";
            case FLUSH:          return "Flush";
            case STRAIGHT:       return "Straight";
            case THREE_OF_A_KIND:return "Three of a Kind";
            case TWO_PAIR:       return "Two Pair";
            case PAIR:           return "Pair";
            case HIGH_CARD:      return "High Card";
            default:             return "";
        }
    }

    @Override
    public String requestPlayerAction(Player player) {
        // Normal betting turn:
        // hide Next Hand button (should not be clickable mid-hand)
        actionPanel.hideNextHandButton();
        actionPanel.enableForPlayer(player);

        String result;
        synchronized (actionLock) {
            pendingAction = null;

            while (pendingAction == null) {
                try {
                    actionLock.wait();
                } catch (InterruptedException ignored) {}
            }

            result = pendingAction;
            pendingAction = null;
        }

        actionPanel.disableAll();
        return result;
    }
}
