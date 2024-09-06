import javax.swing.*;
import java.awt.*;

public class ConfigPanel extends JPanel {
    // ref parent tetris game frame
    private final TetrisGame parentFrame;

    // constructor to init config panel
    public ConfigPanel(TetrisGame parentFrame) {
        this.parentFrame = parentFrame; // store ref to perent frame
        setLayout(new GridBagLayout()); // set layout to grid bag layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // create and config field title label
        JLabel titleLabel = new JLabel("Options");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // create and config field width label
        JLabel fieldWidthLabel = new JLabel("Field Width:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(fieldWidthLabel, gbc);

        // create and config spinner for field width
        // Initialize with current setting
        JSpinner fieldWidthSpinner = new JSpinner(new SpinnerNumberModel(parentFrame.getFieldWidth(), 6, 15, 1));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(fieldWidthSpinner, gbc);

        // create and config field height label
        JLabel fieldHeightLabel = new JLabel("Field Height:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(fieldHeightLabel, gbc);

        // create and config field height spinner
        // Initialize with current setting
        JSpinner fieldHeightSpinner = new JSpinner(new SpinnerNumberModel(parentFrame.getFieldHeight(), 8, 30, 1));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(fieldHeightSpinner, gbc);

        // create and config game level label
        JLabel gameLevelLabel = new JLabel("Game Level:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(gameLevelLabel, gbc);

        // create and config game level spinner
        // Initialize with current setting
        JSpinner gameLevelSpinner = new JSpinner(new SpinnerNumberModel(parentFrame.getGameLevel(), 1, 10, 1));
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(gameLevelSpinner, gbc);

        // create and config music checkbox
        JCheckBox musicCheckBox = new JCheckBox("Music", parentFrame.isMusicOn());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(musicCheckBox, gbc);

        if(parentFrame.isMusicOn()){
            musicCheckBox.setSelected(true);
        }
        else{
            musicCheckBox.setSelected(false);
        }

        // create and config sound effect check box
        JCheckBox soundEffectCheckBox = new JCheckBox("Sound Effect", parentFrame.isSoundEffectOn());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(soundEffectCheckBox, gbc);

        // create and config ai play checkbox
        JCheckBox aiPlayCheckBox = new JCheckBox("AI Play", parentFrame.isAiPlay());
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(aiPlayCheckBox, gbc);

        // create and config extend mode check box
        JCheckBox extendModeCheckBox = new JCheckBox("Extend Mode", parentFrame.isExtendMode());
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(extendModeCheckBox, gbc);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            int width = (int) fieldWidthSpinner.getValue();
            int height = (int) fieldHeightSpinner.getValue();
            int level = (int) gameLevelSpinner.getValue();
            boolean music = musicCheckBox.isSelected();
            boolean soundEffect = soundEffectCheckBox.isSelected();
            boolean aiPlay = aiPlayCheckBox.isSelected();
            boolean extendMode = extendModeCheckBox.isSelected();

            // updates parent frame with new settings
            parentFrame.updateSettings(width, height, level, music, soundEffect, aiPlay, extendMode);
            parentFrame.showMainMenu();

            //Music Handler
            if(music){
                parentFrame.music.playAudio();
            }
            else{
                parentFrame.music.endAudio();
            }

        });
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        add(saveButton, gbc);

        // create config cancel btn
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> parentFrame.showMainMenu());
        gbc.gridx = 1;
        gbc.gridy = 8;
        add(cancelButton, gbc);
    }
}
