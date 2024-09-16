import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.PrivateKey;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TetrisGame extends JFrame {
    // Music Pointer
    AudioPlr music = new AudioPlr("SFX/originalOST.wav", true);

    // Window sizing
    private static final int width = 400;
    private static final int height = 800;

    // High score
    private List<HighScore> highScores;

    // Main Attributes
    private TetrisBoard board; // the tetris board where the game is played
    private TetrisBoard board2; // the tetris board where the game is played

    private int fieldWidth = 10; // width of the tetris board
    private int fieldHeight = 20; // height of the tetris board
    private int gameLevel = 1; // sets starting game level
    private boolean soundEffectOn = false; // turns sound effects on/off
    private boolean aiPlay = false; // flag to enable ai play
    private boolean extendMode = false; // extend mode??

    
    private JButton playPauseButton;
    private JButton backButton;
    private JPanel buttonPanel;
    private JPanel topPanel;

    private KeyListener keyListener;

    // Wait Static Function
    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    // Class constructor
    public TetrisGame() {
        initUI(); // Initialize the UI after the splash screen
        music.playAudio(); // Play Music On Startup
        this.highScores = new ArrayList<>();
        
        //user 1
        highScores.add(new HighScore("user 1", 1600));
        highScores.add(new HighScore("user 2", 1300));
        highScores.add(new HighScore("user 3", 400));
    }

    // Method to initialize the user interface
    private void initUI() {
        showMainMenu(); // show main menu after init
    }

    // Method to start the game
    public void startGame() {
        getContentPane().removeAll(); // removes existing elements on ContentPane

        removeKeyListener(keyListener); // remove key listener

        if (board != null) {
            remove(board); // removes existing board if one is present
        }
        
        // Setting up JFrame properties
        setTitle("Tetris");
        setLayout(new BorderLayout());

        topPanel = new JPanel(new GridLayout(1, 2));    //initial tetris main panel
        
        if (extendMode) {

            setSize(width*2, height); // set the size of the window
            
            createAndStartBoard2();     // create and start extended player's game
        } else {
            setSize(width, height); // set the size of the window
        }
        
        createAndStartBoard();  // create and start origin player's game

        add(topPanel, BorderLayout.CENTER); // Add the top panel to the center of the BorderLayout

        if (extendMode) {

            createOptionPanel();    // create option panel - back, pause button
        }
        
        setFocusable(true); // make the board focusable for key events
        requestFocusInWindow(); // request focus on the board

        keyListener = new TAdapter();   // create key listener

        addKeyListener(keyListener);    // add key listener
        
        revalidate(); // refresh the layout
        repaint(); // repaint the window to reflect changes
    }

    private void createAndStartBoard() {
        board = new TetrisBoard(this, fieldWidth, fieldHeight, gameLevel, isMusicOn(), soundEffectOn, aiPlay, false);
        
        topPanel.add(board); // add the tetris board to the center of the window

        board.startGame(); // start the game on the board
    }

    private void createAndStartBoard2() {
        board2 = new TetrisBoard(this, fieldWidth, fieldHeight, gameLevel, isMusicOn(), soundEffectOn, aiPlay, extendMode);
        
        topPanel.add(board2); // add the tetris board to the center of the window
        
        board2.startGame(); // start the game on the board
    }
    

    public ImageIcon createIcon(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(30, 30, Image.SCALE_SMOOTH); // Scale image to fit the button
        return new ImageIcon(scaledImage);
    }

    private void createOptionPanel() {
        playPauseButton = new JButton(createIcon("Image/Buttons/pause.png"));
        backButton = new JButton(createIcon("Image/Buttons/back.png"));
        
        playPauseButton.setFocusable(false);
        backButton.setFocusable(false);
        
        playPauseButton.addActionListener(e -> togglePlayPause());
        backButton.addActionListener(e -> backToMainMenu());
        
        // Adjust layout to ensure the buttons are visible
        buttonPanel = new JPanel(new GridLayout(1, 2));  // Use GridLayout for even distribution
            
        buttonPanel.setPreferredSize(new Dimension(getWidth(), 50));  // Adjust height for buttons
        buttonPanel.add(backButton);
        buttonPanel.add(playPauseButton);

        // Ensure the button panel is added to the correct position in the layout
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void togglePlayPause() {
        if (board.isPaused()) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    private void backToMainMenu() {
        board.timer.stop();
        board2.timer.stop();

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to return to the main menu?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            showMainMenu();
        } else {
            if (!board.isPaused()) {
                board.timer.start();
                board2.timer.start();
            }
        }
    }

    // Method to update the game settings
    public void updateSettings(int width, int height, int level, boolean music, boolean soundEffect, boolean ai, boolean extend) {
        this.fieldWidth = width;
        this.fieldHeight = height;
        this.gameLevel = level;
        //this.musicOn = music; //not needed
        this.soundEffectOn = soundEffect;
        this.aiPlay = ai;
        this.extendMode = extend;
    }

    // Method to show the config panel
    public void showConfigPanel() {
        getContentPane().removeAll();
        ConfigPanel configPanel = new ConfigPanel(this); // creates config panel
        getContentPane().add(configPanel); // add the config panel
        revalidate(); // refresh layout
        repaint(); // repaint the window to reflect changes
    }

    // Method to show the main menu
    public void showMainMenu() {
        getContentPane().removeAll();
        MainMenuPanel mainMenuPanel = new MainMenuPanel(this, width, height); // new main menu
        getContentPane().add(mainMenuPanel); // add to content pane
        revalidate(); // refresh layout
        repaint(); // repaint the window to reflect changes
    }

    // Method to restart the game
    public void restartGame() {
        startGame();
    }

    public void resumeGame() {
        board.resumeGame();
        if (extendMode) {
            board2.resumeGame();
        }
        playPauseButton.setIcon(createIcon("Image/Buttons/pause.png"));  // Change icon to pause
    }

    public void pauseGame() {
        board.pause();
        if (extendMode) {
            board2.pause();
        }
        playPauseButton.setIcon(createIcon("Image/Buttons/play.png"));  // Change icon to play
    }

    public void showGameOverDialog(boolean extend) {
        board.timer.stop();
        board2.timer.stop();

        String winnerMessage = "";

        if (extend) {
            winnerMessage = "Player 1 won!";
        } else {
            winnerMessage = "Player 2 won!";
        }

        int option = JOptionPane.showOptionDialog(this, winnerMessage + " Would you like to play again?", "Game Over",
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
            new String[]{"Restart", "Main Menu"}, "Restart");
        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            showMainMenu();
        }
    }

      // method called when an exit btn is clicked
    public void exitGame(){
        if (this.board != null) {
            this.board.pauseGame();

            if(this.extendMode) {
                this.board2.pauseGame();
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit the Game?", "Confirm", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);;
        } else {}
    }   

    // Method to handle game over event
    public void gameOver() {
        // show dialog box when game over
        int option = JOptionPane.showOptionDialog(this,
                "Game Over\nYour Score: " + board.getScore(), // displays final score
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new Object[] { "Try again", "Main Menu" }, // options for the user to restart or go to main menu
                "Restart");

        if (option == JOptionPane.YES_OPTION) {
            restartGame();
        } else {
            showMainMenu();
        }
    }

    // Getter methods for settings
    public int getFieldWidth() {
        return fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public boolean isMusicOn() {
        return music.isMusicOn();
    }

    public boolean isSoundEffectOn() {
        return soundEffectOn;
    }

    public boolean isAiPlay() {
        return aiPlay;
    }

    public boolean isExtendMode() {
        return extendMode;
    }

    // High score methods
    public void addHighScore(String name, int score) {
        highScores.add(new HighScore(name, score));
        // Optionally, sort the high scores by score in descending order
        highScores.sort((hs1, hs2) -> Integer.compare(hs2.getScore(), hs1.getScore()));
    }

    public List<HighScore> getHighScores() {
        return highScores;
    }

    public void saveHighScores(List<HighScore> highScores) {
        this.highScores = highScores;
        // Optionally save to a file or database
    }

    public void showHighScores() {
        getContentPane().removeAll();
        HighScorePanel highScorePanel = new HighScorePanel(this, highScores);
        getContentPane().add(highScorePanel);
        revalidate();
        repaint();
    }
    
    // Main method starts the game & splash screen
    public static void main(String[] args) {
        // Start splash screen
        SplashScreen splash = new SplashScreen(5000, width, height);
        splash.showSplash();
    
        // Start Main Menu
        SwingUtilities.invokeLater(() -> {
            TetrisGame game = new TetrisGame();
            game.setResizable(false); // to avoid distorted blocks
            game.setVisible(true);
            
            // Add Closing Event
            game.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Handle close operation
            game.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    game.exitGame(); // Calls exitGame method on window close
                }
            });
        });
    }

    // key event that move block
    private void handleSinglePlayerControls(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            board.moveLeft();
        } else if (key == KeyEvent.VK_RIGHT) {
            board.moveRight();
        } else if (key == KeyEvent.VK_UP) {
            board.rotate();
        } else if (key == KeyEvent.VK_DOWN) {
            board.drop();
        }
    }

    private void handleTwoPlayerControls(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_A) {
            board2.moveLeft();
        } else if (key == KeyEvent.VK_D) {
            board2.moveRight();
        } else if (key == KeyEvent.VK_W) {
            board2.rotate();
        } else if (key == KeyEvent.VK_S) {
            board2.drop();
        }
    }

    private void handleGameStatusControls(KeyEvent e) {
        int key = e.getKeyCode();
        
        if (key == KeyEvent.VK_P) {
            if (board.isPaused()) {
                resumeGame();
            }
            else {
                pauseGame();
            }
        } else if (key == KeyEvent.VK_M) {
            if(isMusicOn()){
                music.endAudio();
            }
            else{
                music.playAudio();
            }
        }
    }
    
    // Inner class representing a high score entry
    public static class HighScore {
        private final String name;
        private final int score;

        public HighScore(String name, int score) {
            this.name = name;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }
    }

    class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            handleSinglePlayerControls(e);
            
            if (extendMode) {
                handleTwoPlayerControls(e);
            }

            handleGameStatusControls(e);
        }
    }
}
