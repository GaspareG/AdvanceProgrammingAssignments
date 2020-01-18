package gui;

import bean.Drone;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import utility.Point;

/**
 *
 * @author Gaspare Ferraro - 520549 - <ferraro@gaspa.re>
 */
public class DroneButton extends JLabel implements PropertyChangeListener {

    private final JPanel panel;
    private Drone drone;

    /**
     * fixed height of button
     */
    public static final int LABEL_WIDTH = 75;

    /**
     * fixed height of button
     */
    public static final int LABEL_HEIGHT = 75;

    /**
     * Default empty constructor
     */
    public DroneButton() {
        this(null);
    }

    /**
     * Initialize DroneButton and its drone
     *
     * @param panel
     */
    public DroneButton(JPanel panel) {

        // Graphical appearenace
        this.setVerticalAlignment(JLabel.CENTER);
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.randomForeground();

        // Initialize and take off drone
        this.panel = panel;
        this.drone = new Drone();
        this.drone.addPropertyChangeListener(this);
        this.drone.takeOff(new Point(0, 0));

        // Handle click event
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {

                // Onclick, if flying then land
                if (drone.getFlying()) {
                    drone.land();
                } // else (if landed) then take off
                else {
                    drone.takeOff(drone.getLocation());
                }

                updateText();
            }
        });
    }

    /**
     * Call correct update function
     *
     * @param evt Event
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {
            case "location":
            case "flying":
                updateLocation();
                updateText();
                break;
            default:
                break;
        }

    }

    private void randomForeground() {
        Random rand = new Random();

        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        // color = (rand(0, 255), rand(0, 255), rand(0, 255))
        this.setForeground(new Color(r, g, b));
    }

    private void updateLocation() {
        Point location = this.drone.getLocation();

        // Scale to label size (othersize coordinate from -10 to 10 are too small)
        int x = location.getX() * LABEL_WIDTH;
        int y = location.getY() * LABEL_HEIGHT;

        // If starting and ending points are both in panel, then move label
        if (this.panel.contains(x, y) && this.panel.contains(x + LABEL_WIDTH, y + LABEL_HEIGHT)) {
            this.setBounds(x, y, LABEL_WIDTH, LABEL_HEIGHT);
        }
    }

    private void updateText() {
        this.setText(this.drone.toString());
    }

}
