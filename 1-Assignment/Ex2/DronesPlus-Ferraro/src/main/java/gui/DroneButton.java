/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import bean.Drone;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import utility.Point;

/**
 *
 * @author gaspare
 */
public class DroneButton extends JLabel implements PropertyChangeListener {

    private final JPanel panel;
    private Drone drone;
    
    public DroneButton(JPanel panel) {
                
        this.setAlignmentX(SwingConstants.CENTER);
        this.setAlignmentY(SwingConstants.CENTER);
        
        this.panel = panel;
        this.drone = new Drone();
        this.drone.addPropertyChangeListener(this);
        
        this.drone.takeOff(new Point(0,0));
        
        this.randomForeground();
        
        this.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent evt){
                
                if(drone.getFlying())
                {
                    drone.land();
                }
                else
                {
                    drone.takeOff(drone.getLocation());
                }
                
                updateText();
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
         
        switch(evt.getPropertyName())
        {
            case "location":
                updateLocation();
                updateText();
                break;
            case "flying":
                updateText();
                break;
            default:
                break;
        }
        
    }

    public Drone getDrone()
    {
        return this.drone;
    }
    
    private void randomForeground() {
        Random rand = new Random();
        
        int r = rand.nextInt(256);
        int g = rand.nextInt(256);
        int b = rand.nextInt(256);
        
        this.setForeground(new Color(r, g, b));
    }

    private void updateLocation()
    {
        Point location = this.drone.getLocation();
        
        int x = location.getX();
        int y = location.getY();

        if(this.panel.contains(x, y))
            this.setBounds(x, y, 100, 25);
    }
    
    private void updateText() {
        this.setText(this.drone.toString());
    }
    
}
