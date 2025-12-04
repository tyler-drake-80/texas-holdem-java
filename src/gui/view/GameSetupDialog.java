package gui.view;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for setting up game parameters before starting.
 * Allows user to choose:
 * - Starting chips (100-10000)
 * - Number of players (2-4)
 */
public class GameSetupDialog extends JDialog {
    private JSpinner chipsSpinner;
    private JSpinner playersSpinner;
    private boolean confirmed = false;

    public GameSetupDialog(Frame parent) {
        super(parent, "Game Setup", true);
        setLayout(new BorderLayout(10, 10));
        setSize(400, 250);
        setLocationRelativeTo(parent);

        // Main panel with settings
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Texas Hold'em Setup", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        settingsPanel.add(titleLabel, gbc);

        // Starting chips
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        JLabel chipsLabel = new JLabel("Starting Chips:");
        chipsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        settingsPanel.add(chipsLabel, gbc);

        gbc.gridx = 1;
        SpinnerNumberModel chipsModel = new SpinnerNumberModel(1000, 100, 10000, 100);
        chipsSpinner = new JSpinner(chipsModel);
        chipsSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        settingsPanel.add(chipsSpinner, gbc);

        // Number of players
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel playersLabel = new JLabel("Number of Players:");
        playersLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        settingsPanel.add(playersLabel, gbc);

        gbc.gridx = 1;
        SpinnerNumberModel playersModel = new SpinnerNumberModel(4, 2, 8, 1);
        playersSpinner = new JSpinner(playersModel);
        playersSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        settingsPanel.add(playersSpinner, gbc);

        add(settingsPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setPreferredSize(new Dimension(120, 35));
        startButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getStartingChips() {
        return (Integer) chipsSpinner.getValue();
    }

    public int getNumberOfPlayers() {
        return (Integer) playersSpinner.getValue();
    }

    public static GameSetupResult showDialog(Frame parent) {
        GameSetupDialog dialog = new GameSetupDialog(parent);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            return new GameSetupResult(
                dialog.getStartingChips(),
                dialog.getNumberOfPlayers()
            );
        }
        return null;
    }

    public static class GameSetupResult {
        public final int startingChips;
        public final int numberOfPlayers;

        public GameSetupResult(int startingChips, int numberOfPlayers) {
            this.startingChips = startingChips;
            this.numberOfPlayers = numberOfPlayers;
        }
    }
}
