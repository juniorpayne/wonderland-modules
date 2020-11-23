/**
 * iSocial Project
 * http://isocial.missouri.edu
 *
 * Copyright (c) 2011, University of Missouri iSocial Project, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The iSocial project designates this particular file as
 * subject to the "Classpath" exception as provided by the iSocial
 * project in the License file that accompanied this code.
 */
package org.jdesktop.wonderland.modules.isocial.generic.client;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jdesktop.wonderland.modules.isocial.generic.common.GenericAnswer;
import org.jdesktop.wonderland.modules.isocial.generic.common.GenericQuestion;
import org.jdesktop.wonderland.modules.isocial.generic.common.MultipleChoiceQuestion;

/**
 * Creates the answer review panel for students. This gives an ability to student
 * to check and confirm all the answers before submitting.
 *
 * @author Kaustubh
 */
public class ReviewPanel extends javax.swing.JPanel {

    /** Creates new form ReviewPanel */
    public ReviewPanel(List<GenericQuestion> questions) {
        initComponents();
        addColumnContents(questions);
        jTable1.setCellSelectionEnabled(false);
        jTable1.setDefaultRenderer(Object.class, new CustomTableRenderer());
        setTableRenderers();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane1.setColumnHeaderView(null);
        jScrollPane1.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(0, 0));

        jTable1.setFont(new java.awt.Font("Lucida Grande", 0, 14));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {}
            },
            new String [] {

            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setEnabled(false);
        jTable1.setRowHeight(50);
        jTable1.setRowMargin(3);
        jTable1.setShowGrid(true);
        jScrollPane1.setViewportView(jTable1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    /**
     * This method creates the empty columns with the questions as the column
     * header and the student answers are then added each time the student
     * presses back / next button.
     * @param questions
     */
    private void addColumnContents(List<GenericQuestion> questions) {
        for (Iterator<GenericQuestion> it = questions.iterator(); it.hasNext();) {
            GenericQuestion question = it.next();
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.addColumn(((MultipleChoiceQuestion) question).getValue());
        }
    }

    /**
     * This method is called every time the back / next button is pressed on the
     * question wizard. The answer selected by user on the corresponding page is
     * updated in this table.
     * @param answer
     * @param column
     */
    public void updateAnswer(String answer, int column) {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setValueAt(answer, 0, column);
        String columnName = model.getColumnName(column);
        int stringWidth = getFontMetrics(jTable1.getFont()).stringWidth(columnName);
        TableColumn tColumn = jTable1.getColumnModel().getColumn(column);
        //tColumn.setMinWidth(stringWidth);
        //tColumn.setWidth(stringWidth);
        //tColumn.setPreferredWidth(stringWidth);
        //tColumn.setHeaderRenderer(new CustomTableRenderer());
        int answerWidth = getFontMetrics(getFont()).stringWidth(answer);
        int rowHeight = jTable1.getRowHeight();
        if ((answerWidth / 2) > rowHeight) {
            jTable1.setRowHeight(rowHeight + 20);
        }
        jTable1.setSize(getPreferredSize().width + stringWidth, getPreferredSize().height);
        jTable1.repaint();
        doLayout();
        validate();
        repaint();
    }

    /**
     * This method returns the set of answer strings entered by student. It
     * retrieves this list from the first row of table which contains all the
     * answers and the question titles are retrieved from the column headers.
     * @return
     */
    public List<GenericAnswer> getAnswerList() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        List<GenericAnswer> answers = new ArrayList<GenericAnswer>();
        int columns = model.getColumnCount();
        for (int i = 0; i < columns; i++) {
            String answer = (String) model.getValueAt(0, i);
            GenericAnswer genericAnswer = new GenericAnswer();
            genericAnswer.setValue(answer);
            String questionTitle = model.getColumnName(i);
            genericAnswer.setQuestionTitle(questionTitle);
            answers.add(genericAnswer);
        }
        return answers;
    }

    private void setTableRenderers() {
        Enumeration<TableColumn> columns = jTable1.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn column = columns.nextElement();
            String header = (String) column.getIdentifier();
            CustomTableRenderer renderer = new CustomTableRenderer();
            int headerWidth = 0;
            if (renderer instanceof JTextArea) {
                JTextArea area = renderer;
                headerWidth = area.getFontMetrics(area.getFont()).stringWidth(header);
                if (headerWidth > 100) {
                    headerWidth = headerWidth / 2;
                    
                }
                area.setPreferredSize(new Dimension(headerWidth, 80));
            }
            column.setHeaderRenderer(renderer);
            column.setPreferredWidth(headerWidth);
        }
    }
}
