package gui.view;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;
import players.Player;

/**
 * ActionPanel shows all action buttons for the current player.
 */
public class ActionPanel extends JPanel {

    private final JButton foldButton = new JButton("Fold");
    private final JButton checkButton = new JButton("Check");
    private final JButton callButton = new JButton("Call");
    private final JButton raiseButton = new JButton("Raise");
    private final JButton allInButton = new JButton("All-in");
    private final JButton nextHandButton = new JButton("Next Hand");

    /** Send selected action back to GUIListener */
    private Consumer<String> actionConsumer;
    /**
     * Construct the ActionPanel with buttons and listeners
     */
    public ActionPanel() {
        setLayout(new FlowLayout());
        setBackground(new Color(30, 30, 30));

        add(foldButton);
        add(checkButton);
        add(callButton);
        add(raiseButton);
        add(allInButton);
        add(nextHandButton);

        foldButton.addActionListener(e -> send("FOLD"));
        checkButton.addActionListener(e -> send("CHECK"));
        callButton.addActionListener(e -> send("CALL"));
        raiseButton.addActionListener(e -> handleRaise());
        allInButton.addActionListener(e -> send("ALL_IN"));
        nextHandButton.addActionListener(e -> send("NEXT_HAND"));
        nextHandButton.setVisible(false); // only show when hand is over

        disableAll(); // start disabled, but still visible
    }
    
    public void showNextHandButton(){
        nextHandButton.setEnabled(true);
        nextHandButton.setVisible(true);
    }
    public void hideNextHandButton(){
        nextHandButton.setEnabled(false);
        nextHandButton.setVisible(false);
    }
    /**
     * Handle the Raise action by prompting for an amount
     * and sending it back to the listener.
     */
   private void handleRaise() {
      JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
      JDialog dialog = new JDialog(topFrame, "Enter raise amount", true);
      dialog.setLayout(new BorderLayout(10,10));

      JLabel lbl = new JLabel("Raise amount:", SwingConstants.CENTER);
      lbl.setFont(new Font("Arial", Font.BOLD, 14));

      JTextField input = new JTextField(10);
      input.setHorizontalAlignment(JTextField.CENTER);
      input.setFont(new Font("Arial", Font.PLAIN, 14));

      JButton ok = new JButton("Confirm raise");
      ok.addActionListener(e -> {
         String text = input.getText().trim();
         if(text.matches("\\d+")){
            send("RAISE:" + text);
            dialog.dispose();
         }else{
            JOptionPane.showMessageDialog(dialog, "Enter a valid number");
         }
      });
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout(new GridLayout(2,1,5,5));
      centerPanel.add(lbl);
      centerPanel.add(input);

      dialog.add(centerPanel, BorderLayout.CENTER);
      dialog.add(ok, BorderLayout.SOUTH);
      dialog.setSize(300,150);
      dialog.setLocationRelativeTo(topFrame);
      dialog.setAlwaysOnTop(true);
      dialog.setVisible(true);

   }
    public void setActionConsumer(Consumer<String> consumer) {
        this.actionConsumer = consumer;
    }

    /**
     * Send action to consumer if set
     * @param action the action string to send
     */
    private void send(String action) {
        if (actionConsumer != null) {
            actionConsumer.accept(action);
        }
    }

    /**
     * Enable buttons valid for this player.
     * GameEngine defines legality; this GUI only reflects it.
     */
    public void enableForPlayer(Player p) {
        // (Engine rules applied outside; GUI only enables controls)
        foldButton.setEnabled(true);
        checkButton.setEnabled(true);
        callButton.setEnabled(true);
        raiseButton.setEnabled(true);
        allInButton.setEnabled(true);

        // IMPORTANT: never hide panel; just re-enable it
        setVisible(true);
        revalidate();
        repaint();
    }
public void showOnlyNextHand() {
    foldButton.setEnabled(false);
    checkButton.setEnabled(false);
    callButton.setEnabled(false);
    raiseButton.setEnabled(false);
    allInButton.setEnabled(false);

    nextHandButton.setVisible(true);
    nextHandButton.setEnabled(true);
}

    /**
     * Disable all buttons (but keep panel visible)
     */
    public void disableAll() {
        foldButton.setEnabled(false);
        checkButton.setEnabled(false);
        callButton.setEnabled(false);
        raiseButton.setEnabled(false);
        allInButton.setEnabled(false);

        // DO NOT HIDE — this causes GUI freezes
        // setVisible(false);   <--- REMOVED

        revalidate();
        repaint();
    }
}
