import javax.swing.*;
import java.awt.*;
public class SplashScreen extends JWindow {
    static int width,height;
    private int duration;

    public SplashScreen(int duration,final int width,final int height) {
        this.duration = duration;
        this.width=width;
        this.height=height;
    }

    public void showSplash() {
        JPanel content = (JPanel) getContentPane();
        content.setBackground(Color.white);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);
        // Build the splash screen
        JLabel label = new JLabel(new
                ImageIcon("Image/Splash/imageNew.jpg"));
        JLabel copyrt = new JLabel("Copyright 2024, Group 19",
                JLabel.CENTER);
        copyrt.setFont(new Font("Sans-Serif", Font.BOLD, 12));
        content.add(label, BorderLayout.CENTER);
        content.add(copyrt, BorderLayout.SOUTH);
        Color oraRed = new Color(156, 20, 20, 255);
        content.setBorder(BorderFactory.createLineBorder(oraRed, 5));
        // Display it
        setVisible(true);
        // Wait a little while, maybe showing a progress bar
        try {
            Thread.sleep(duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setVisible(false);
    }
    public void showSplashAndExit() {
        showSplash();
        System.exit(0);
    }
    public static void main(String[] args) {
        // Show the splash screen
        SplashScreen splash = new SplashScreen(5000,width,height);
        splash.showSplashAndExit();
    }
}