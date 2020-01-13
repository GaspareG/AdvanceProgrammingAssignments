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
    
    private final PropertyChangeSupport propertySupport;
    private final VetoableChangeSupport vetos;
    private boolean flying;
    private Point loc;
    private Timer timer;
    
    // Grid constraint
    private final int scale = 25;
    private final int minX = -10;
    private final int minY = -10;
    private final int maxX = +10;
    private final int maxY = +10;
    
    public Drone()
    {
        this.propertySupport = new PropertyChangeSupport(this);
        this.vetos = new VetoableChangeSupport(this);
        this.setFlying(false);
        this.setLocation(new Point(0,0));
    }

    public void takeOff(Point initLoc)
    {
        this.timer = new Timer();
        this.setFlying(true);
        try
        {
            this.vetos.fireVetoableChange("location", this.getLocation(), initLoc);
            this.setLocation(initLoc);
        } catch (PropertyVetoException ex) {
            this.setLocation(new Point(0,0));
        }
        this.timer.schedule(new DroneTask(this), 0, 1000);
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
    
    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
        this.vetos.addVetoableChangeListener(listener);
    }
    
    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
        this.vetos.removeVetoableChangeListener(listener);
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
        int x = rand.nextInt(maxX-minX+1)+minX;
        int y = rand.nextInt(maxY-minY+1)+minY;

        x *= scale;
        y *= scale;
        
        Point newLocation = new Point(x, y);
        try {
            this.vetos.fireVetoableChange("location", this.getLocation(), newLocation);
            this.setLocation(newLocation);
        } catch (PropertyVetoException ex) {  
            
        }
        
    }

    private static class DroneTask extends TimerTask {

        private final Drone drone;
        
        public DroneTask(Drone drone) {
            this.drone = drone;
        }
        
        @Override
        public void run(){
            drone.setRandomLocation();
        }
    }
    
    public String toString()
    {
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
