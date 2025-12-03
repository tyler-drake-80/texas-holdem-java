package gui.view;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import players.Player;
/**
 * ActionPanel is the GUI component that displays player actions and options.
 * CHECK, CALL, FOLD, RAISE, ALL_IN buttons.
 * 
 * GUIListener listens for button clicks to determine player's action.
 */
public class ActionPanel extends JPanel{
    
    private final JButton foldButton = new JButton("Fold");
    private final JButton checkButton = new JButton("Check");
    private final JButton callButton = new JButton("Call");
    private final JButton raiseButton = new JButton("Raise");
    private final JButton allInButton = new JButton("All-in");

    /**To send selected action back to GUIListener */
    private Consumer<String> actionConsumer;

     public ActionPanel(){
        setLayout(new FlowLayout());

        add(foldButton);
        add(checkButton);
        add(callButton);
        add(raiseButton);
        add(allInButton);

        foldButton.addActionListener(e -> send("FOLD"));
        checkButton.addActionListener(e -> send("CHECK"));
        callButton.addActionListener(e -> send("CALL"));
        raiseButton.addActionListener(e -> send("RAISE"));
        allInButton.addActionListener(e -> send("ALL_IN"));

        disableAll();
     }

     public void setActionConsumer(Consumer<String> consumer){
        this.actionConsumer = consumer;
     }

     private void send(String action){
        if(actionConsumer != null){
            actionConsumer.accept(action);
        }
     }

     /** Enable/disable button availability based on game rules */
     public void enableForPlayer(Player p){
        //all set to true for now, will have to implement funcs like canCheck(), canCall(), etc in player later.
        foldButton.setEnabled(true);
        checkButton.setEnabled(true);
        callButton.setEnabled(true);
        raiseButton.setEnabled(true);
        allInButton.setEnabled(true);
        setVisible(true);
     }
     /** Hide all buttons */
     public void disableAll(){
        foldButton.setEnabled(false);
        checkButton.setEnabled(false);
        callButton.setEnabled(false);
        raiseButton.setEnabled(false);
        allInButton.setEnabled(false);
        setVisible(false);
     }
}
