/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import bean.Drone;
import event.OutOfRangeEvent;
import event.OutOfRangeListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import utility.Point;

/**
 *
 * @author gaspare
 */
public class DronePanel extends JPanel {

    protected EventListenerList listenerList;

    public DronePanel() {
        this.listenerList = new EventListenerList();

        // I've tried everythings, that's the only "beatiful" thing that works
        this.addContainerListener(new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent ce) {
                if (ce.getChild().getClass().equals(DroneButton.class)) {
                    DroneButton droneButton = (DroneButton) ce.getChild();
                    Drone drone = droneButton.getDrone();

                    drone.addPropertyChangeListener((PropertyChangeEvent pce) -> {
                        if (pce.getPropertyName().equals("location")) {
                            Point location = (Point) pce.getNewValue();
                            if (location.getX() < 0 || location.getY() < 0 || location.getX() > Drone.maxX || location.getY() > Drone.maxY) {
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

    public void addOutOfRangeListener(OutOfRangeListener listener) {
        listenerList.add(OutOfRangeListener.class, listener);
    }

    public void removeOutOfRangeListener(OutOfRangeListener listener) {
        listenerList.remove(OutOfRangeListener.class, listener);
    }

    private void fireEvents(OutOfRangeEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == OutOfRangeListener.class) {
                ((OutOfRangeListener) listeners[i + 1]).OutOfRange(evt);
            }
        }
    }

}
