package gui.integration;

import gui.view.*;
import players.Player;

/**
 *GameListener is the communication layer between GameEngine and GUI. 
 * 1. GameEngine -> GUI:
 *  - notify about game state changes with onStateUpdated(GameState)
 * 
 * 2. GUI -> GameEngine:
 *  - send player actions with onPlayerAction(PlayerAction)
 * 
 * The GUIListener class implements this interface.
 */
public interface GameListener {
    /**
     * Called whenever the GameEngine has new state to display.
     * @param state The fully-updated state to display.
     */
    void onStateUpdated(GameState state);

    /**
     * Called whenever the GameEngine is awaiting the next hand to start.
     */
    void onAwaitNextHand();

    /**
     * Called whenever the GUI has a new player action to send to the GameEngine.
     * @param action The player action to process.
     * @return a string representing the chosen action: "CHECK", "CALL", "RAISE", "FOLD", "ALL_IN"
     */
    String requestPlayerAction(Player player);
}
