import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class TetrisBoard extends JPanel implements ActionListener {

    private int BOARD_WIDTH;
    private int BOARD_HEIGHT;
    private int INITIAL_DELAY;
    public Timer timer;
    private boolean isFallingFinished = false;
    private boolean isStarted = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int score = 0;
    private int curX = 0;
    private int curY = 0;
    private Shape curPiece;
    private Shape.Tetrominoes[] board;
    private TetrisGame parentFrame;
    private int gameLevel;
    private boolean musicOn;
    private boolean soundEffectOn;
    private boolean aiPlay;
    private boolean extendMode;
    private List<TetrisGame.HighScore> highScores;

    private JButton playPauseButton;
    private JButton backButton;
    private JPanel buttonPanel;
    private JLabel scoreLabel; // label to display the score

    public TetrisBoard(TetrisGame parentFrame, int width, int height, int level, boolean music, boolean soundEffect,
                       boolean ai, boolean extend) {
        this.parentFrame = parentFrame;
        this.BOARD_WIDTH = width;
        this.BOARD_HEIGHT = height - 2;  // Reduce the height by 2 rows to accommodate buttons
        this.gameLevel = level;
        this.musicOn = music;
        this.soundEffectOn = soundEffect;
        this.aiPlay = ai;
        this.extendMode = extend;
        this.INITIAL_DELAY = 400 - (level - 1) * 40;
        this.highScores = parentFrame.getHighScores();

        this.scoreLabel = new JLabel("Score: 0"); // init score label

        
        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(INITIAL_DELAY, this);
        board = new Shape.Tetrominoes[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        
        // Initialize and add the buttons
        
        playPauseButton = new JButton(parentFrame.createIcon("Image/Buttons/pause.png"));
        backButton = new JButton(parentFrame.createIcon("Image/Buttons/back.png"));
        
        playPauseButton.setFocusable(false);
        backButton.setFocusable(false);
        
        playPauseButton.addActionListener(e -> togglePlayPause());
        backButton.addActionListener(e -> backToMainMenu());

        setLayout(new BorderLayout());
        
        add(scoreLabel, BorderLayout.NORTH); // add the score label on top of the window
        
        // Adjust layout to ensure the buttons are visible
        buttonPanel = new JPanel(new GridLayout(1, 2));  // Use GridLayout for even distribution
        
        if (!parentFrame.isExtendMode()) {
            
            buttonPanel.setPreferredSize(new Dimension(getWidth(), 50));  // Adjust height for buttons
            buttonPanel.add(backButton);
            buttonPanel.add(playPauseButton);

            // Ensure the button panel is added to the correct position in the layout
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }

    private void togglePlayPause() {
        if (isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    public void pauseGame() {
        if (!isStarted) return;
        isPaused = true;
        timer.stop();
        playPauseButton.setIcon(parentFrame.createIcon("Image/Buttons/play.png"));  // Change icon to play
    }

    public void resumeGame() {
        if (!isStarted || !isPaused) return;
        isPaused = false;
        timer.start();
        playPauseButton.setIcon(parentFrame.createIcon("Image/Buttons/pause.png"));  // Change icon to pause
    }

    private void backToMainMenu() {
        timer.stop();
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to return to the main menu?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            parentFrame.showMainMenu();
        } else {
            if (!isPaused) {
                timer.start();
            }
        }
    }


    public int getScore() {
        return score;
    }

    public void updateSettings(int width, int height, int level, boolean music, boolean soundEffect, boolean ai,
                               boolean extend) {
        this.BOARD_WIDTH = width;
        this.BOARD_HEIGHT = height - 2;  // Adjust height if needed
        this.gameLevel = level;
        this.musicOn = music;
        this.soundEffectOn = soundEffect;
        this.aiPlay = ai;
        this.extendMode = extend;
        this.INITIAL_DELAY = 400 - (level - 1) * 40;
        clearBoard();
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    public void pause() {
        if (!isStarted) {
            return;
        }
        isPaused = !isPaused;
        if (isPaused) {
            pauseGame();
        } else {
            resumeGame();
        }
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    private int squareWidth() {
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    private int squareHeight() {
        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    private Shape.Tetrominoes shapeAt(int x, int y) {
        return board[(y * BOARD_WIDTH) + x];
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {
            board[i] = Shape.Tetrominoes.NoShape;
        }
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) {
                break;
            }
            newY--;
        }
        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) {
            pieceDropped();
        }
    }

    public void startGame() {
        isStarted = true;
        isPaused = false;
        numLinesRemoved = 0;
        score = 0;
        clearBoard();
        newPiece();
        timer.start();
        requestFocusInWindow();
        updateScore();
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished) {
            newPiece();
        }
    }

    private void newPiece() {
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Shape.Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;

            if (!parentFrame.isExtendMode()) {
                showGameOverDialog();
            } else {
                parentFrame.showGameOverDialog(extendMode);
            }
        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
                return false;
            }
            if (shapeAt(x, y) != Shape.Tetrominoes.NoShape) {
                return false;
            }
        }
        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;

            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (shapeAt(j, i) == Shape.Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            score += numFullLines * 10;
            updateScore();
            isFallingFinished = true;
            curPiece.setShape(Shape.Tetrominoes.NoShape);
            repaint();
        }
    }

    private void updateScore() {
        
        scoreLabel.setText("Score: " + score);
    }


    private void showGameOverDialog() {
        // Ask the player for their name when the game is over
        String playerName = JOptionPane.showInputDialog(this, "Game Over\nYour Score: " + score + "\nEnter your name:", "Enter Name", JOptionPane.PLAIN_MESSAGE);

        if (playerName != null && !playerName.trim().isEmpty()) {
            // Save the high score
            parentFrame.addHighScore(playerName, score);
        }

        int option = JOptionPane.showOptionDialog(this, "Would you like to play again?", "Game Over",
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                new String[]{"Restart", "View High Scores", "Main Menu"}, "Restart");

        if (option == JOptionPane.YES_OPTION) {
            parentFrame.restartGame();
        } else if (option == 1) {
            parentFrame.showHighScores();  // Show the high scores panel
        } else {
            parentFrame.showMainMenu();
        }
    }

    private void updateHighScores(String playerName, int playerScore) {
        highScores.add(new TetrisGame.HighScore(playerName, playerScore));
        parentFrame.saveHighScores(highScores);  // Save updated high scores to persistent storage
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight() - buttonPanel.getHeight(); // Adjusted for button height

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Shape.Tetrominoes shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Shape.Tetrominoes.NoShape) {
                    drawSquare(g, j * squareWidth(), boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (curPiece.getShape() != Shape.Tetrominoes.NoShape) {
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }

        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent black
            g.fillRect(0, 0, getWidth(), getHeight()); // Cover the entire board with a transulsent overlay

            // message
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String pauseMessage = "Game Paused";
            String resumeMessage = "Press 'P' to resume";
            int pauseMessageWidth = g.getFontMetrics().stringWidth(pauseMessage);
            int resumeMessageWidth = g.getFontMetrics().stringWidth(resumeMessage);
            g.drawString(pauseMessage, (getWidth() - pauseMessageWidth) / 2, getHeight()
                    / 2 - 20);
            g.drawString(resumeMessage, (getWidth() - resumeMessageWidth) / 2,
                    getHeight() / 2 + 10);
        }
        
    }

    private void drawSquare(Graphics g, int x, int y, Shape.Tetrominoes shape) {
        Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
                new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)};

        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    public void moveLeft () {
        tryMove(curPiece, curX - 1, curY);
    }

    public void moveRight () {
        tryMove(curPiece, curX + 1, curY);
    }

    public void drop () {
        dropDown();
    }

    public void rotate () {
        tryMove(curPiece.rotateRight(), curX, curY);
    }
    
    public void toggleMusic () {
        musicOn = !musicOn;
    }
}
