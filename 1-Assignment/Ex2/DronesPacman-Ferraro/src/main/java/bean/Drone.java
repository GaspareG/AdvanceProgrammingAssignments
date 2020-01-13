package bean;

import java.beans.*;
import java.io.Serializable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import utility.Point;

/**
 *
 * @author Gaspare Ferraro - 520549 - <ferraro@gaspa.re>
 */
public class Drone implements Serializable {
    
    private Timer timer;
    private final PropertyChangeSupport propertySupport;
    private boolean flying;
    private Point loc;
    
    // Grid constraint
    private final int minX = 0;
    private final int minY = 0;
    private final int maxX = 10;
    private final int maxY = 10;

    public Drone()
    {
        this.propertySupport = new PropertyChangeSupport(this);
        this.setFlying(false);
        this.setLocation(new Point(0,0));
    }

    public void takeOff(Point initLoc)
    {
        this.timer = new Timer();
        this.setFlying(true);
        this.setLocation(initLoc);
        this.timer.schedule(new DroneTask(this), 0, 250);
    }
    
    public void land()
    {
        timer.cancel();
        this.setFlying(false);
    }
    
    private void setLocation(Point loc)
    {
        this.propertySupport.firePropertyChange("location", this.getLocation(), loc);
        this.loc = loc;
    }
    
    public Point getLocation()
    {
        return this.loc;
    }
    
    private void setFlying(boolean flying)
    {
        this.propertySupport.firePropertyChange("flying", this.getFlying(), flying);
        this.flying = flying;
    }
    
    public boolean getFlying()
    {
        return this.flying;
    }
    
    
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        this.propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        this.propertySupport.removePropertyChangeListener(listener);
    }

    private void setRandomLocation() {
        Random rand = new Random();
        int x = this.loc.getX() + (rand.nextInt() % 2 == 0 ? 1 : -1);
        int y = this.loc.getY() + (rand.nextInt() % 2 == 0 ? 1 : -1);
        
        
        this.setLocation(new Point(x, y));
    }

    private static class DroneTask extends TimerTask {

        private final Drone drone;
        
        public DroneTask(Drone drone) {
            this.drone = drone;
        }
        
        public void run(){
            drone.setRandomLocation();
        }
    }
    
}
