package bean;

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
public class Drone implements Serializable {

    // Drone attributes
    private Timer timer;
    private final PropertyChangeSupport propertySupport;
    private boolean flying;
    private Point loc;

    // Grid constraint
    private final int minX = -10;
    private final int minY = -10;
    private final int maxX = +10;
    private final int maxY = +10;

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
        this.timer.schedule(new DroneTask(this), 0, 1000);
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
        this.propertySupport.firePropertyChange("location", this.getLocation(), loc);
        this.loc = loc;
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
        // Generate a x coordinate in [minX, maxX]
        int x = rand.nextInt(maxX - minX + 1) + minX;
        // Generate a y coordinate in [minY, maxY]
        int y = rand.nextInt(maxY - minY + 1) + minY;
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

}
