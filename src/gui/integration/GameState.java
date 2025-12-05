package gui.integration;

import cards.Card;
import java.util.List;
/**
 * DTO representing the current state of the game, needed for rendering the GUI.
 * 
 * Creates a game state and sends it via GameListener.onStateUpdated()
 * allowing TablePanel to display cards, chips, pot, and active player.
 *  */ 
public class GameState {
    public List<PlayerState> players;
    public List<Card> communityCards;
    public int pot;
    public int activePlayerSeat;
    public int currentPlayerSeat;
    public String winnerText = "";//winner announcement
    public int dealerSeat;
    public boolean waitingForNextHand = false;

}
