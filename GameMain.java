package a6fa21;

/*
 * NetId(s): TODO ses398, jim59
 * Time spent: 10 hours, 30 minutes
 * What I thought about this assignment:
 * The assignment was a very helpful practice on how to implement GUIs and
 * learning how to navigate Java API. Helped us catch a small glimpse of the
 * many libraries and functions that go into making an application or program
 */
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;

/** Main class for Click-a-Dot game. Creates window with game board, score label, start button, and
 * sliders for target size and speed. */
public class GameMain {
    /** Start the application. */
    public static void main(String[] args) {
        // Creation of window must occur on Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    /** Create application window.
     * <ul>
     * <li>Window title is "Click-a-Dot"
     * <li>Game board is in center of window, expands to fill window size
     * <li>Score label is at top; text is centered
     * <li>Start button is at bottom
     * <li>Size slider is at right
     * <li>Speed slider is at left
     * </ul>
     * Window should be disposed when closed, and all game tasks stopped. This should be sufficient
     * for application to shut down gracefully. */
    private static void createAndShowGUI() {
        // Create frame.
        JFrame frame= new JFrame("Click-a-Dot");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create and add game board.
        GameComponent game= new GameComponent();
        frame.add(game);

        // Create and add score label.
        JLabel scoreLabel= new JLabel("Score: " + game.getScore(),
            SwingConstants.CENTER);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(24.0f));
        // TODO 1: Add `scoreLabel` to top of frame.
        frame.add(scoreLabel, BorderLayout.NORTH);
        // Create and add start button.
        JButton startButton= new JButton("Start");
        startButton.setFont(startButton.getFont().deriveFont(20.0f));
        // TODO 2: Add `startButton` to bottom of frame.
        frame.add(startButton, BorderLayout.SOUTH);
        // Create and add vertical size slider.
        // Allowed target radii are 1..50 (query game board for initial radius).
        JSlider sizeSlider= new JSlider(JSlider.VERTICAL, 1, 50,
            game.getTargetRadius());
        addSliderLabels(sizeSlider, "Small", "Large");
        // Place slider in panel with label and padding.
        frame.add(makeSliderPanel(sizeSlider, "Size"), BorderLayout.WEST);

        // Create and add vertical speed slider.
        // Allowed target durations are 250..2000 ms (query game board for
        // initial duration).
        JSlider speedSlider= new JSlider(JSlider.VERTICAL, 250, 2000,
            game.getTargetTimeMillis());
        addSliderLabels(speedSlider, "Fast", "Slow");
        speedSlider.setInverted(true);
        // Place slider in panel with label and padding.
        frame.add(makeSliderPanel(speedSlider, "Speed"), BorderLayout.EAST);

        // Add menu bar
        JMenuItem saveItem= new JMenuItem("Save score");
        JMenuItem exitItem= new JMenuItem("Exit");
        // TODO 14: Add a menu bar with a "File" menu to the frame [1]. The
        // menu items `saveItem` and `exitItem` should be accessible under the
        // "File" menu.
        // [1] https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html

        JMenuBar menuBar= new JMenuBar();
        JMenu menu= new JMenu("File");
        menuBar.add(menu);

        menu.add(saveItem);

        menu.add(exitItem);

        frame.setJMenuBar(menuBar);

        ////////////////
        // Controller
        ////////////////

        // When start button is clicked, start a new game.
        // TODO 3: Add an ActionListener to `startButton` that starts a game.
        startButton.addActionListener((ActionEvent ae) -> game.startGame());
        // When game's score changes, update score label.
        // TODO 9: Add a ProrpertyChangeListener to `game` that updates
        // `scoreLabel`'s text whenever the "GameScore" property changes.
        // Label text should start with "Score: ", followed by the numerical
        // score.
        game.addPropertyChangeListener("GameScore",
            (PropertyChangeEvent e) -> scoreLabel
                .setText("Score: " + game.getScore()));

        // When size slider is adjusted, update target radius in game.
        // TODO 10: Add a ChangeListener to `sizeSlider` that sets the game's
        // target radius to the slider's current value.
        sizeSlider.addChangeListener(
            (ChangeEvent e) -> game.setTargetRadius(sizeSlider.getValue()));

        // When speed slider is adjusted, update target duration in game.
        // TODO 11: Add a ChangeListener to `speedSlider` that sets the game's
        // target duration to the slider's current value.
        speedSlider.addChangeListener((ChangeEvent e) -> game
            .setTargetTimeMillis(speedSlider.getValue()));

        // When "Save" menu item is activated, open file dialog and append score
        // to chosen file.
        saveItem.addActionListener(
            (ActionEvent ae) -> saveScore(frame, game.getScore()));

        // When "Exit" menu item is activated, dispose of the JFrame.
        exitItem.addActionListener((ActionEvent ae) -> frame.dispose());

        // Stop game when window is closed to ensure that game background tasks
        // do not hold up application shutdown.
        // Use an anonymous subclass of WindowAdapter to avoid having to handle
        // other window events.
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                game.stopGame();
            }
        });

        // Compute ideal window size and show window.
        frame.pack();
        frame.setVisible(true);
    }

    /** Label `slider`'s minimum value with `minLabel` and its maximum value with `maxLabel`. */
    private static void addSliderLabels(JSlider slider, String minLabel,
        String maxLabel) {
        Hashtable<Integer, JLabel> labels= new Hashtable<>();
        // TODO 12:
        // 1. Add a JLabel with the text in `minLabel` to the table
        // corresponding to the slider's minimum value.
        // 2. Add a JLabel with the text in `maxLabel` to the table
        // corresponding to the slider's maximum value.
        // 3. Show the labels on the slider [1].
        // [1] https://docs.oracle.com/javase/tutorial/uiswing/components/slider.html

        labels.put(slider.getMinimum(), new JLabel(minLabel));
        labels.put(slider.getMaximum(), new JLabel(maxLabel));
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
    }

    /** Place `slider` in a new padded panel with top label `title` and return the panel. */
    private static JComponent makeSliderPanel(JSlider slider, String title) {
        // TODO 13:
        // 1. Construct a new JPanel with an appropriate LayoutManager [1].
        // 2. Add an EmptyBorder around the panel to provide at least 4px of
        // padding on all sides [2].
        // 3. Add a label to the top of the panel with the text in `title`
        // (centered). Font size should be larger than default.
        // 4. Add `slider` to the panel to fill all remaining space below the
        // label.
        // 5. Return the panel.
        // [1] https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
        // [2] https://docs.oracle.com/javase/tutorial/uiswing/components/border.html

        JPanel panel= new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lb= new JLabel(title, SwingConstants.CENTER);
        Font f= lb.getFont();
        lb.setFont(new Font(f.getName(), f.getStyle(),
            f.getSize() * 2));

        panel.add(lb);

        panel.add(slider);

        return panel;  // Replace this line with one that returns your panel
    }

    /** Append a line containing `score` to a user-selected file, using `frame` as the parent of any
     * dialogs. Show an error dialog if a problem occurs when writing the file. */
    private static void saveScore(JFrame frame, int score) {
        // TODO 15:
        // * Show a "save file" dialog [1].
        // * If the user selects a file, write the value in `score` on a new
        // line of text at the end of the file, retaining its former contents
        // (see handout).
        // * If a problem occurs when opening or writing to the file, show an
        // error dialog with the class of the exception as its title and the
        // exception's message as its text [2].
        // [1] https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
        // [2] https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
        JFileChooser fc= new JFileChooser();
        fc.setDialogTitle("Save");

        int select= fc.showSaveDialog(frame);

        if (select == JFileChooser.APPROVE_OPTION) {
            File file= fc.getSelectedFile();
            try (PrintWriter out= new PrintWriter(
                new BufferedWriter(new FileWriter(file, true)))) {
                out.println(score);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, e.toString(),
                    e.getClass().getName(),
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
