package gui;

import bean.Drone;
import event.OutOfRangeEvent;
import event.OutOfRangeListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import utility.Point;

/**
 *
 * @author Gaspare Ferraro - 520549 - <ferraro@gaspa.re>
 */
public class DronePanel extends JPanel {

    /**
     * list of EventListener
     */
    protected EventListenerList listenerList;

    /**
     * Create a JPanel that keep tracks of DroneButton children
     */
    public DronePanel() {
        this.listenerList = new EventListenerList();

        // I've tried everythings, that's the only "beatiful" thing that works
        this.addContainerListener(new ContainerListener() {

            // Detect new component added to panel
            @Override
            public void componentAdded(ContainerEvent ce) {

                // If added new DroneButton
                if (ce.getChild().getClass().equals(DroneButton.class)) {
                    DroneButton droneButton = (DroneButton) ce.getChild();
                    Drone drone = droneButton.getDrone();

                    // Add property change listener to droneButton drone
                    drone.addPropertyChangeListener((PropertyChangeEvent pce) -> {
                        // If location change
                        if (pce.getPropertyName().equals("location")) {
                            Point location = (Point) pce.getNewValue();
                            // Check out of [0, 10]
                            if (location.getX() < 0 || location.getY() < 0 || location.getX() > Drone.maxX || location.getY() > Drone.maxY) {
                                // Create and fire event
                                OutOfRangeEvent evt = new OutOfRangeEvent(droneButton, location.getX(), location.getY());
                                fireEvents(evt);
                            }
                        }
                    });
                }
            }

            @Override
            public void componentRemoved(ContainerEvent ce) {

            }

        });

    }

    /**
     * Add new OutOfRangeListener
     *
     * @param listener
     */
    public void addOutOfRangeListener(OutOfRangeListener listener) {
        listenerList.add(OutOfRangeListener.class, listener);
    }

    /**
     * Remove a OutOfRangeListener
     *
     * @param listener
     */
    public void removeOutOfRangeListener(OutOfRangeListener listener) {
        listenerList.remove(OutOfRangeListener.class, listener);
    }

    // Fire OutOfRangeEvent to all the OutOfRangeListener
    private void fireEvents(OutOfRangeEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == OutOfRangeListener.class) {
                ((OutOfRangeListener) listeners[i + 1]).OutOfRange(evt);
            }
        }
    }

}
