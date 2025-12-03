package gui;

import cards.Card;

/**
 * Snapshot of a single player's state at a given point in time.
 * Used inside of GameState and applied to PlayerPanel.
 */
public class PlayerState {
   public int seat;         // 0=N, 1=E, 2=S, 3=W
   public String name;
   public int chips;
   public Card hole1;
   public Card hole2;
   public boolean folded;
   public boolean allIn;
   public boolean faceUp;
   public boolean active;   //highlight border
   
   public PlayerState(int seat) { this.seat = seat; }
}
