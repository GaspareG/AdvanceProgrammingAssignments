/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import static gui.DroneButton.LABEL_HEIGHT;
import static gui.DroneButton.LABEL_WIDTH;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import utility.Point;

/**
 *
 * @author gaspare
 */
public class DroneFrame extends javax.swing.JFrame implements VetoableChangeListener {

    /**
     * Creates new form DroneFrame
     */
    public DroneFrame() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlDrones = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DronesPlus-Ferraro");
        setMaximumSize(new java.awt.Dimension(900, 900));
        setMinimumSize(new java.awt.Dimension(900, 900));
        setPreferredSize(new java.awt.Dimension(900, 900));
        setResizable(false);

        pnlDrones.setMaximumSize(new java.awt.Dimension(850, 850));
        pnlDrones.setMinimumSize(new java.awt.Dimension(850, 850));
        pnlDrones.setPreferredSize(new java.awt.Dimension(850, 850));
        pnlDrones.setLayout(null);

        btnAdd.setText("New drone");
        btnAdd.setMargin(null);
        btnAdd.setMaximumSize(new java.awt.Dimension(150, 25));
        btnAdd.setMinimumSize(new java.awt.Dimension(150, 25));
        btnAdd.setPreferredSize(new java.awt.Dimension(150, 25));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        pnlDrones.add(btnAdd);
        btnAdd.setBounds(350, 825, 150, 25);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDrones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlDrones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        DroneButton droneButton = new DroneButton(pnlDrones);

        droneButton.getDrone().addVetoableChangeListener(this);

        pnlDrones.add(droneButton);
        pnlDrones.revalidate();
        pnlDrones.repaint();

    }//GEN-LAST:event_btnAddActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DroneFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DroneFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DroneFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DroneFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new DroneFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JPanel pnlDrones;
    // End of variables declaration//GEN-END:variables

    /**
     * Check if new location is in panel bound
     *
     * @param pce PropertyChangeEvent
     * @throws PropertyVetoException
     */
    @Override
    public void vetoableChange(PropertyChangeEvent pce) throws PropertyVetoException {
        if (pce.getPropertyName() != null && pce.getPropertyName().equals("location")) {
            Point location = (Point) pce.getNewValue();

            int x = location.getX() * LABEL_WIDTH;
            int y = location.getY() * LABEL_HEIGHT;

            // If starting or ending points are not both in panel, throws veto
            if (!pnlDrones.contains(x, y) || !pnlDrones.contains(x + LABEL_WIDTH, y + LABEL_HEIGHT)) {
                throw new PropertyVetoException("Location (" + location.getX() + " " + location.getY() + ") out of bounds", pce);
            }
        }
    }

}
