package a6fa21;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

/** GUI component for Click-a-Dot game board. Maintains and visualizes game state and responds to
 * mouse inputs. Allows programmatic control of game settings and property access to game state. */
public class GameComponent extends JPanel implements MouseListener {
    /** Number of targets to show during one game. */
    private int maxTargets= 10;

    /** Duration each target is shown [ms]. */
    private int targetTimeMillis= 1500;

    /** Encapsulates state and controls for current target. */
    private Target target= new Target();

    /** Timer to trigger changing targets. */
    private Timer timer;

    /** Whether a game is currently being played. */
    private boolean isActive= false;

    /** Number of targets that have been shown during current game (including the current target).
     * If a game is not currently being played, is the number of targets shown during the previous
     * game (0 if no games have been played). */
    private int targetCount= 0;

    /** Number of targets successfully hit by player during the current game. If a game is not
     * currently being played, is the number of targets hit during the previous game (0 if no games
     * have been played). */
    private int score= 0;

    /** Construct a new GameComponent with default settings. */
    public GameComponent() {
        // Set up timer with initial delay of 0 (so target is shown as soon as
        // game starts). Coalescing queued actions ensures targets are not
        // skipped due to stalls or leftover events after a game is restarted.
        super();
        timer= new Timer(targetTimeMillis, (ActionEvent e) -> timeout());
        timer.setInitialDelay(0);
        timer.setCoalesce(true);

        // This component reacts to mouse events.
        addMouseListener(this);

        // Set a recommended size for the game board (prevents it from
        // disappearing when frame is packed).
        setPreferredSize(new Dimension(480, 360));
    }

    /** Start a new game using current settings. Progress from any previous or ongoing game is
     * reset. */
    public void startGame() {
        targetCount= 0;
        setScore(0);
        isActive= true;
        timer.restart();
        repaint();
    }

    /** Stop current game. Takes effect immediately (triggers a repaint request). All background
     * tasks are cancelled when game is stopped. */
    public void stopGame() {
        timer.stop();
        isActive= false;
        repaint();
    }

    /** Handle timer actions by changing targets. If the last target for the current game has not
     * yet been shown, respawn the target within the current component area (updating the target
     * count and requesting a repaint). Otherwise, stop the game. If no game is currently active, do
     * nothing. */
    private void timeout() {
        // TODO 5: Implement this method according to its specification.
        if (isActive == true && targetCount < maxTargets) {
            target.respawn(getWidth(), getHeight());
            targetCount= targetCount + 1;
            repaint();
        } else stopGame();

    }

    /** Query the current score. */
    public int getScore() {
        return score;
    }

    /** Change the current score to `newScore` and notify observers. */
    private void setScore(int newScore) {
        int oldScore= score;
        score= newScore;
        firePropertyChange("GameScore", oldScore, newScore);
    }

    /** Query the radius af the target dot [px]. */
    public int getTargetRadius() {
        return target.radius;
    }

    /** Change the radius of the target dot to `r` px. Takes effect immediately (triggers a repaint
     * request).
     * <p>
     * Precondition: &gt; 0 */
    public void setTargetRadius(int r) {
        target.radius= r;
        repaint();
    }

    /** Query the duration each target is shown [ms]. */
    public int getTargetTimeMillis() {
        return targetTimeMillis;
    }

    /** Change the duration each target is shown to `t` ms. Does not affect the currently-shown
     * target (if any).
     * <p>
     * Precondition: &ge; 0 */
    public void setTargetTimeMillis(int t) {
        targetTimeMillis= t;
        timer.setDelay(targetTimeMillis);
    }

    /** Visualize the current game state by painting on `g`. If game is inactive, fill component
     * area with black. If game is active, draw target at current location on top of default JPanel
     * background. */
    @Override
    public void paintComponent(Graphics g) {
        // TODO 4: Implement this method according to its specification.
        // Start by deferring to superclass, which will provide a default
        // background.
        super.paintComponent(g);
        if (isActive == false) {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        } else target.paintDot(g);

    }

    /** Handle a mouse button being pressed (details in `e`) by checking for a hit on the target if
     * a game is active. If click corresponds to a new hit, increment score by 1 and notify
     * observers. Requests a repaint, since target state may have changed. */
    @Override
    public void mousePressed(MouseEvent e) {
        // TODO 8: Implement this method according to its specifications.
        // Do not modify the `score` field directly; make use of an existing
        // method to perform the modification while notifying observers.
        if (isActive == true && target.checkHit(e.getX(), e.getY()) == true) {
            setScore(getScore() + 1);
            repaint();
        }
    }

    // Remaining MouseListener event handlers are unused.

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    /** Represents a dot to be clicked. Maintains current position and size and whether it has
     * already been "hit" in its current location. Able to check clicks for hits, move to a new
     * location, and paint itself. */
    private static class Target {
        /** x-coordinate of current center position [px]. */
        int x;

        /** y-coordinate of current center position [px]. */
        int y;

        /** Radius of circular dot representing the target [px]. */
        int radius= 15;

        /** Whether this has been "hit" in its current location. */
        private boolean isHit;

        /** Generate random numbers to use when choosing new location. By using our own generator,
         * could potentially set seed to enable deterministic testing. */
        private Random rng= new Random();

        /** Paint dot on provided Graphics `g`. Dot is a circle with our current radius, centered at
         * our current position (in local Graphics coordinates). Circle is filled red if we have
         * been hit, otherwise blue. */
        void paintDot(Graphics g) {
            // TODO 6: Implement this method according to its specifications.

            if (isHit == true) g.setColor(Color.red);
            else g.setColor(Color.blue);
            g.fillOval(x, y, radius, radius);
        }

        /** Move target to a random location and reset "hit" state. xMax and yMax are the
         * (inclusive) upper bounds of the x- and y-coordinates of the new position [px]; lower
         * bound is 0 (inclusive). */
        void respawn(int xMax, int yMax) {
            x= rng.nextInt(xMax + 1);
            y= rng.nextInt(yMax + 1);
            isHit= false;
        }

        /** Return true and set state to "hit" if coordinates (cx,cy) are within this target's
         * circular area and it was not already "hit"; return false otherwise. */
        boolean checkHit(int cx, int cy) {
            // TODO 7: Implement this method according to its specifications
            // (replacing the `throw` statement).
            if (cx < x + radius && cx > x - radius && cy < y + radius &&
                cy > y - radius && isHit == false) {
                isHit= true;
                return true;
            } else return false;

        }
    }
}
