README.txt – Texas Hold’em Poker (Java)

Authors:
 -Tyler Drake    
 -Steven Kester
 
## Status
Feature-complete for local pass-and-play (2–8 players). Some edge-case bugs remain.

## Known Issues
See `BUGS.md`.

1. Overview

This project is a fully playable Texas Hold’em Poker game implemented in Java using Swing for the GUI. The game 
simulates a 4-player pass-and-play environment, featuring card dealing, betting rounds, hand evaluation, showdown, 
pot awarding, and multi-hand continuation.

Run with:

java -jar hwx.jar

If unpacked, compile with:
javac -cp class -d class src\cards\*.java src\game\*.java src\gui\app\*.java src\gui\integration\*.java src\gui\view\*.java src\players\*.java

And pack with:
jar cvfm hwx.jar MANIFEST.MF -C class . -C src . -C resources . README.txt
**Ensure MANIFEST is present and main entry is GUIGame

2. Game Rules & Notes (Beyond Standard Poker Rules)

These details clarify behaviors UNIQUE to this implementation:

------Game Structure-------

4 players are auto-created: Player 1 – Player 4.

Each receives 1000 starting chips by default (modifiable via setup window).

A dealer button (BTN) rotates each hand.

Blinds:

Small Blind = 10

Big Blind = 20

------Betting Behaviors------

Allowed actions: Fold, Check, Call, Raise (custom amount), All-In

Raises use a pop-up dialog where the user enters a numeric raise amount.

Betting auto-progresses to next player after each action.

Illegal or malformed raise input is rejected with an error dialog.

-----Card Visibility------

Only the active player sees their own cards face-up.

Other players’ cards remain face-down except during showdown.

At showdown, all cards are revealed and hand strengths are displayed.

-----Showdown & Pot Splitting------

Hands are ranked using a custom evaluator:
Royal Flush → High Card

Ties result in pot splitting, including correct handling of odd remainders.

Winner text appears as a centered label on the table.

------Multi-Hand Gameplay------

After the pot is awarded, a Next Hand button appears.

The game reuses player chip counts (no reset).

The deck automatically resets and shuffles for each hand.

3. How to Use the Interface
-----Starting the Game-----

Run the jar:

java -jar hwx.jar


A setup dialog appears allowing you to:

Choose number of players (2–8 supported)

Choose starting chip count

Press Start, and the table GUI opens.

------During a Hand------

The active player’s panel highlights with a yellow border.

Use the bottom action panel to select:
- Fold
- Check
- Call
- Raise (opens numeric input dialog)
- All-In


-----Showdown-----

Winner announcement appears centered on the table.

Cards are revealed.

The Next Hand button appears; click it to continue the game.

4. Implementation Notes
-----Major Components-----

GameEngine – complete poker logic (dealing, betting, hand evaluation, showdown).

GUIListener – communication layer between Swing UI and GameEngine.

TablePanel / PlayerPanel / BoardPanel – full graphical display of players, cards, pot, and dealer button.

ActionPanel – action buttons + custom raise pop-up + Next Hand button.

CardImageLoader – loads all 52 card PNGs.

-----Threading-------

GameEngine runs in a separate thread, preventing GUI freezing.

GUI waits for button actions using synchronized locks, not busy-waiting.

5. Extra Features Implemented

These go beyond a minimal assignment solution:

Custom Raise Amounts (entered via GUI pop-up)

Variable player amount at table

Dealer Button Indicator (“BTN”) for the correct player

Winner Label with Semi-Transparent Background

Dynamic Hand Ranking Display (“Flush”, “Two Pair”, etc.)

Auto-resetting deck & multi-hand gameplay loop

Card images displayed graphically

6. Packaging Notes (Jar Contents)

The submitted jar contains:

/game/*.class, /game/*.java

/gui/*.class, /gui/*.java

/players/*.class, /players/*.java

/cards/*.class, /cards/*.java

/resources/cards/*.png

README.txt

MANIFEST.MF with the correct Main-Class entry

7. Credits / Resources Used

Card image source:

https://opengameart.org/content/playing-cards-vector-png

8. Known Limitations

Game is built for pass-and-play

GUI layout tested primarily at 1400×900 resolution.

AI opponents not implemented.