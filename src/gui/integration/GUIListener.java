package gui.integration;

import players.Player;
import gui.view.*;

public class GUIListener implements GameListener {
    private final TablePanel tablePanel;
    private final ActionPanel actionPanel;
    
    /**This variable stores the result returned when user clicks a button */
    private volatile String pendingAction = null;

    public GUIListener(TablePanel tablePanel, ActionPanel actionPanel) {
        this.tablePanel = tablePanel;
        this.actionPanel = actionPanel;

        actionPanel.setActionConsumer(action -> {
                pendingAction = action;
        });
    }
    @Override
    public void onStateUpdated(GameState state) {
        // Implementation for updating the GUI with the new game state
        tablePanel.applyTableState(convertState(state));
    }

    private TablePanel.TableState convertState(GameState gs) {
        // Convert GameState to TableState for the TablePanel
        TablePanel.TableState ts = new TablePanel.TableState();

        for(PlayerState ps : gs.players){
            TablePanel.PlayerState pstate = new TablePanel.PlayerState(ps.seat);
            pstate.name = ps.name;
            pstate.chips = ps.chips;
            pstate.hole1 = ps.hole1;
            pstate.hole2 = ps.hole2;
            pstate.cardsFaceUp = ps.faceUp;
            pstate.active = ps.active;

            ts.players.add(pstate);
        }
        ts.communityCards = gs.communityCards;
        ts.pot = gs.pot;
        return ts;
    }

    @Override
    public String requestPlayerAction(Player player) {
        //Enable buttons appropriate for action
        actionPanel.enableForPlayer(player);
        pendingAction = null;

        while(pendingAction == null) {
            try {
                Thread.sleep(20); // Sleep briefly to avoid busy-waiting
            } catch (Exception ignored) {}
        }
        actionPanel.disableAll();
        return pendingAction;
    }
    
}
