package bean;

import event.OutOfRangeEvent;
import event.OutOfRangeListener;
import gui.DroneButton;
import java.beans.*;
import java.io.Serializable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import utility.Point;

/**
 * Java bean Drone
 *
 * @author Gaspare Ferraro - 520549 - <ferraro@gaspa.re>
 */
public class Drone implements Serializable, OutOfRangeListener {

    // Drone attributes
    private Timer timer;
    private final PropertyChangeSupport propertySupport;
    private boolean flying;
    private Point loc;

    // Grid constraint
    public static int minX = -10;
    public static int minY = -10;
    public static int maxX = +10;
    public static int maxY = +10;

    /**
     * Default empty constructor Create a non-flying drone located in (0,0)
     */
    public Drone() {
        this.propertySupport = new PropertyChangeSupport(this);
        this.setFlying(false);
        this.setLocation(new Point(0, 0));
    }

    /**
     * Start drone
     *
     * @param initLoc Initial drone location
     */
    public void takeOff(Point initLoc) {
        this.timer = new Timer();
        this.setFlying(true);
        this.setLocation(initLoc);
        this.timer.schedule(new DroneTask(this), 0, 100); // Faster is Funnier
    }

    /**
     * Stop drone and cancel timer
     */
    public void land() {
        timer.cancel();
        this.setFlying(false);
    }

    private void setLocation(Point loc) {
        // Fire evento "location" (old position, new position)
        Point oldLocation = this.getLocation();
        this.loc = loc;
        this.propertySupport.firePropertyChange("location", oldLocation, this.getLocation());
    }

    /**
     * Getter method for drone location
     *
     * @return Point object, current location of the drone
     */
    public Point getLocation() {
        return this.loc;
    }

    private void setFlying(boolean flying) {
        // Fire evento "flying" (old status, new status)
        this.propertySupport.firePropertyChange("flying", this.getFlying(), flying);
        this.flying = flying;
    }

    /**
     * Getter method for flying
     *
     * @return Boolean, current flying status of the drone
     */
    public boolean getFlying() {
        return this.flying;
    }

    /**
     * Add listener to the property support
     *
     * @param listener PropertyChangeListener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove listener to the property support
     *
     * @param listener PropertyChangeListener to remove
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.propertySupport.removePropertyChangeListener(listener);
    }

    private void setRandomLocation() {
        Random rand = new Random();
        // Generate 
        int deltaX = 0;
        int deltaY = 0;

        switch (rand.nextInt() % 4) {
            case 0:
                deltaX = 1;
                break;
            case 1:
                deltaX = -1;
                break;
            case 2:
                deltaY = 1;
                break;
            case 3:
            default:
                deltaY = -1;
                break;
        }

        int x = this.getLocation().getX() + deltaX;
        int y = this.getLocation().getY() + deltaY;

        this.setLocation(new Point(x, y));
    }

    // Timer task, at every run generate a new location
    private static class DroneTask extends TimerTask {

        private final Drone drone;

        public DroneTask(Drone drone) {
            this.drone = drone;
        }

        @Override
        public void run() {
            drone.setRandomLocation();
        }
    }

    /**
     * String representation of the drone
     *
     * @return Return ">x,y<" if the drone is flying, and "<x,y>" otherwise
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(this.getFlying() ? ">" : "<");
        builder.append(this.getLocation().getX());
        builder.append(",");
        builder.append(this.getLocation().getY());
        builder.append(this.getFlying() ? "<" : ">");
        builder.append(" ");

        return builder.toString();
    }

    @Override
    public void OutOfRange(OutOfRangeEvent evt) {
        int x = evt.getX();
        int y = evt.getY();

        System.out.println("OUT OF RANGE " + x + " " + y);

        if (x < 0) {
            x = Drone.maxX;
        }
        if (y < 0) {
            y = Drone.maxY;
        }
        if (x > Drone.maxX) {
            x = 0;
        }
        if (y > Drone.maxY) {
            y = 0;
        }

        this.setLocation(new Point(x, y));

        DroneButton droneButton = (DroneButton) evt.getSource();
        droneButton.updateLocation();

    }

}
