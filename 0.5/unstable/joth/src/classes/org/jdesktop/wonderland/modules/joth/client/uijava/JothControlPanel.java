/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * JothControlPanel.java
 *
 * Created on Jun 8, 2009, 5:56:14 PM
 */

package org.jdesktop.wonderland.modules.joth.client.uijava;

/**
 *
 * @author dj
 */
public class JothControlPanel extends javax.swing.JPanel {

    public interface ControlPanelContainer {
        // TODO: not yet used
        public void newGame();
    }

    /** The game that contains this control panel. */
    private ControlPanelContainer container;

    /** Creates new form JothControlPanel */
    public JothControlPanel() {
        initComponents();
    }

    public void setContainer (ControlPanelContainer container) {
        this.container = container;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        turnMessage = new javax.swing.JLabel();
        whiteCountMessage = new javax.swing.JLabel();
        blackCountMessage = new javax.swing.JLabel();
        instructionMessage = new javax.swing.JLabel();

        turnMessage.setText("It is white's turn");

        whiteCountMessage.setText("White Pieces: 0");

        blackCountMessage.setText("Black Pieces: 0");

        instructionMessage.setText("Click mouse left on a square to place a piece.");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(turnMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                        .add(188, 188, 188))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, blackCountMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, whiteCountMessage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(instructionMessage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 327, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(16, 16, 16)
                .add(instructionMessage)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(turnMessage)
                .add(19, 19, 19)
                .add(whiteCountMessage)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(blackCountMessage)
                .addContainerGap(185, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel blackCountMessage;
    private javax.swing.JLabel instructionMessage;
    private javax.swing.JLabel turnMessage;
    private javax.swing.JLabel whiteCountMessage;
    // End of variables declaration//GEN-END:variables

    public void clearError () {
        instructionMessage.setText("Click left on a square to place a piece.");
    }

    public void error (String message) {
        instructionMessage.setText("Error: " + message);
    }

    public void displayCounts (int whiteCount, int blackCount) {
        whiteCountMessage.setText("White Pieces: " + whiteCount);
        blackCountMessage.setText("Black Pieces: " + blackCount);
    }

    public void setTurn (String whoseTurn) {
        turnMessage.setText("It is " + whoseTurn + "'s turn.");
    }

    public void notifyGameOver (String msg) {
        instructionMessage.setText(msg);
        turnMessage.setText("");
    }
}
