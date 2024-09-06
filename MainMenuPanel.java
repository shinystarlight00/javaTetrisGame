import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {
    // reference to the parent tetris game frame
    private final TetrisGame parentFrame;

    // constructor to initialize the main menu panel
    public MainMenuPanel(TetrisGame parentFrame, final int width, final int height) {
        this.parentFrame = parentFrame;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        this.parentFrame.setBounds(x, y, width, height);
        parentFrame.setSize(width, height); // Set width & height of screen
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); // creates gridbagconstraint object to control the layout
        // constraints
        gbc.insets = new Insets(5, 5, 5, 5);

        // create config title label
        JLabel titleLabel = new JLabel("Tetris");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // create and config start game btn
        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> parentFrame.startGame());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(startButton, gbc);

        // create and config options btn
        JButton optionsButton = new JButton("Options");
        optionsButton.addActionListener(e -> parentFrame.showConfigPanel());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(optionsButton, gbc);

        // create and config high scores btn
        JButton highScoresButton = new JButton("High Scores");
        highScoresButton.addActionListener(e -> parentFrame.showHighScores());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(highScoresButton, gbc);

        // create and config exit btn
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> parentFrame.exitGame());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(exitButton, gbc);
    }
}