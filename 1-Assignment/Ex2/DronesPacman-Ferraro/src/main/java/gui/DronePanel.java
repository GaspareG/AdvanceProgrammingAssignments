/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import event.OutOfRangeEvent;
import event.OutOfRangeListener;
import java.awt.event.ComponentListener;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

/**
 *
 * @author gaspare
 */
public class DronePanel extends JPanel {

    protected EventListenerList listenerList;

    public DronePanel() {
        this.listenerList = new EventListenerList();

        this.addComponentListener((ComponentListener) this);
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
