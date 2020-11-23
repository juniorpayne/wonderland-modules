/**
 * Copyright (c) 2014, WonderBuilders, Inc., All Rights Reserved
 */
package org.jdesktop.wonderland.modules.fairbooth.client;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Nilang
 */
public class FairBoothPropertyPanel extends javax.swing.JPanel {

    public String boothName="Untitled Booth";
    public int colorTheme=0;
    public String infoText="Untitled";
    public int leftPanelFrames=1;
    public int rightPanelFrames=1;
    public boolean instructionShown;
    public boolean instructionShown1;
    
    
    public void setValues() { 
        
        boothName = jTextField1.getText()==null || 
                jTextField1.getText().equals("") || 
                jTextField1.getForeground().equals(Color.GRAY) 
                ?"Untitled Booth":jTextField1.getText();
        infoText = jTextArea1.getText()==null || 
                jTextArea1.getText().equals("") ||
                jTextArea1.getForeground().equals(Color.GRAY)
                ?"Untitled":jTextArea1.getText();
        colorTheme = jComboBox4.getSelectedIndex();
        leftPanelFrames = Integer.parseInt(String.valueOf(jComboBox2.getSelectedItem()));
        rightPanelFrames = Integer.parseInt(String.valueOf(jComboBox3.getSelectedItem()));
        
    }
    
    /**
     * Creates new form FairBoothPropertyPanel
     */
    public FairBoothPropertyPanel() {
        initComponents();
        instructionShown=true;
        instructionShown1=true;
        jTextField1.setForeground(Color.GRAY);
        jTextArea1.setForeground(Color.GRAY);
        
        Map map = getThemeMap(colorTheme);
        jPanel11.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("FFFFFF")),16)));
        jPanel12.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("CCCCCC")),16)));
        jPanel13.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("999999")),16)));
        jPanel14.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("666666")),16)));
        jPanel15.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("333333")),16)));
        jPanel16.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("000000")),16)));
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jComboBox2 = new javax.swing.JComboBox();
        jComboBox3 = new javax.swing.JComboBox();
        jComboBox4 = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 250));

        jLabel1.setText("Booth Name:");

        jLabel2.setText("Info Text:");

        jLabel3.setText("Color Theme:");

        jLabel4.setText("Left Panel Frames:");

        jLabel5.setText("Right Panel Frmaes:");

        jTextField1.setColumns(10);
        jTextField1.setText("Name");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextField1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });

        jTextArea1.setColumns(15);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(2);
        jTextArea1.setTabSize(0);
        jTextArea1.setText("Desciption or question");
        jTextArea1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextArea1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextArea1FocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "4" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Red", "Orange", "Yellow", "Green", "Blue", "Purple", "Cool Dark", "Cool Medium", "Cool Pastel", "Warm Dark", "Warm Light", "Pastel Mix", "Art Deco Dark", "Art Deco Medium", "Art Deco Pastel", "Elegant", "Fun", "Fun Pastel", "Neutral", "Earth Tones", "Bold", "Muted", "Primary Colors", "Print Colors" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jPanel2.setMinimumSize(new java.awt.Dimension(100, 30));
        jPanel2.setPreferredSize(new java.awt.Dimension(200, 26));

        jPanel15.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel15.setMaximumSize(new java.awt.Dimension(25, 25));
        jPanel15.setPreferredSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel14.setMaximumSize(new java.awt.Dimension(25, 25));
        jPanel14.setPreferredSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel12.setMaximumSize(new java.awt.Dimension(25, 25));
        jPanel12.setPreferredSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel13.setMaximumSize(new java.awt.Dimension(25, 25));
        jPanel13.setPreferredSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel16.setMaximumSize(new java.awt.Dimension(25, 25));
        jPanel16.setPreferredSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel11.setMaximumSize(new java.awt.Dimension(25, 25));
        jPanel11.setPreferredSize(new java.awt.Dimension(25, 25));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(53, 53, 53)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextArea1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextArea1FocusLost
        // TODO add your handling code here:
        if(jTextArea1.getText().length()==0) {
            jTextArea1.setText("Description or question");
            instructionShown1=true;
            jTextArea1.setForeground(Color.GRAY);
        } else {
            instructionShown1=false;
        }
        
    }//GEN-LAST:event_jTextArea1FocusLost

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
        
        Map map = getThemeMap(jComboBox4.getSelectedIndex());
        jPanel11.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("FFFFFF")),16)));
        jPanel12.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("CCCCCC")),16)));
        jPanel13.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("999999")),16)));
        jPanel14.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("666666")),16)));
        jPanel15.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("333333")),16)));
        jPanel16.setBackground(new Color(Integer.parseInt(String.valueOf(map.get("000000")),16)));
        
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusGained
        // TODO add your handling code here:
        if(instructionShown) {
            jTextField1.setText("");
            jTextField1.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_jTextField1FocusGained

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        // TODO add your handling code here:
        if(jTextField1.getText().length()==0) {
            jTextField1.setText("Name");
            instructionShown=true;
            jTextField1.setForeground(Color.GRAY);
        } else {
            instructionShown=false;
        }
    }//GEN-LAST:event_jTextField1FocusLost

    private void jTextArea1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextArea1FocusGained
        // TODO add your handling code here:
        if(instructionShown1) {
            jTextArea1.setText("");
            jTextArea1.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_jTextArea1FocusGained

    public Map<String, String> getThemeMap(int colorTheme) {
        Map<String, String> themeMap = new HashMap<String, String>();
        if(colorTheme==0) {
            //Red
            themeMap.put("FFFFFF", "F1B3B3");
            themeMap.put("CCCCCC", "E26E60");
            themeMap.put("999999", "DB4E4E");
            themeMap.put("666666", "DB1F2A");
            themeMap.put("333333", "CA002A");
            themeMap.put("000000", "A12830");
        } else if(colorTheme==1) {
            //Orange
            themeMap.put("FFFFFF", "F4D09D");
            themeMap.put("CCCCCC", "E8A36E");
            themeMap.put("999999", "B9293B");
            themeMap.put("666666", "DC7E00");
            themeMap.put("333333", "BF550D");
            themeMap.put("000000", "BF550D");
        } else if(colorTheme==2) {
            //Yellow
            themeMap.put("FFFFFF", "C6D6E2");
            themeMap.put("CCCCCC", "FDEC00");
            themeMap.put("999999", "F6D300");
            themeMap.put("666666", "E9A700");
            themeMap.put("333333", "DC7E00");
            themeMap.put("000000", "5E4E00");
        } else if(colorTheme==3) {
            //Green
            themeMap.put("FFFFFF", "D1E2A9");
            themeMap.put("CCCCCC", "A5C47D");
            themeMap.put("999999", "FCE220");
            themeMap.put("666666", "4EA32A");
            themeMap.put("333333", "007251");
            themeMap.put("000000", "216462");
        } else if(colorTheme==4) {
            //Blue
            themeMap.put("FFFFFF", "A6CBF0");
            themeMap.put("CCCCCC", "60BAE3");
            themeMap.put("999999", "4978A9");
            themeMap.put("666666", "2C4E86");
            themeMap.put("333333", "004B67");
            themeMap.put("000000", "2D3C4D");
        } else if(colorTheme==5) {
            //Purple
            themeMap.put("FFFFFF", "C8AFD1");
            themeMap.put("CCCCCC", "BA99C3");
            themeMap.put("999999", "9979AD");
            themeMap.put("666666", "7E5899");
            themeMap.put("333333", "6A3787");
            themeMap.put("000000", "462556");
        } else if(colorTheme==6) {
            //Cool Dark
            themeMap.put("FFFFFF", "45A989");
            themeMap.put("CCCCCC", "00ADCE");
            themeMap.put("999999", "0052a5");
            themeMap.put("666666", "8c65d3");
            themeMap.put("333333", "004159");
            themeMap.put("000000", "000000");
        } else if(colorTheme==7) {
            //Cool Medium
            themeMap.put("FFFFFF", "89C1A2");
            themeMap.put("CCCCCC", "7BC1D8");
            themeMap.put("999999", "67AADF");
            themeMap.put("666666", "8081B8");
            themeMap.put("333333", "65a8c4");
            themeMap.put("000000", "000000");
        } else if(colorTheme==8) {
            //Cool Pastel
            themeMap.put("FFFFFF", "FFFFFF");
            themeMap.put("CCCCCC", "9BCCB6");
            themeMap.put("999999", "aacee2");
            themeMap.put("666666", "60BAE3");
            themeMap.put("333333", "cab9f1");
            themeMap.put("000000", "AACEE2");
        } else if(colorTheme==9) {
            //Warm Dark
            themeMap.put("FFFFFF", "67545B");
            themeMap.put("CCCCCC", "575D40");
            themeMap.put("999999", "A59A45");
            themeMap.put("666666", "E29063");
            themeMap.put("333333", "5B3900");
            themeMap.put("000000", "000000");
        } else if(colorTheme==10) {
            //Warm Light
            themeMap.put("FFFFFF", "C6B9B3");
            themeMap.put("CCCCCC", "BCBA7F");
            themeMap.put("999999", "F3EBA3");
            themeMap.put("666666", "EEB794");
            themeMap.put("333333", "AF7D5C");
            themeMap.put("000000", "5B3900");
        } else if(colorTheme==11) {
            //Pastel Mix
            themeMap.put("FFFFFF", "FCEAAE");
            themeMap.put("CCCCCC", "F6D3D7");
            themeMap.put("999999", "A7AED7");
            themeMap.put("666666", "67B6B4");
            themeMap.put("333333", "C5B9A1");
            themeMap.put("000000", "94A7AE");
        } else if(colorTheme==12) {
            //Art Deco Dark
            themeMap.put("FFFFFF", "00A2AB");
            themeMap.put("CCCCCC", "606C72");
            themeMap.put("999999", "3E596F");
            themeMap.put("666666", "D2434E");
            themeMap.put("333333", "411D63");
            themeMap.put("000000", "000000");
        } else if(colorTheme==13) {
            //Art Deco Medium
            themeMap.put("FFFFFF", "A39994");
            themeMap.put("CCCCCC", "7CBFC0");
            themeMap.put("999999", "798EA2");
            themeMap.put("666666", "DD788A");
            themeMap.put("333333", "665A88");
            themeMap.put("000000", "000000");
        } else if(colorTheme==14) {
            //Art Deco Pastel
            themeMap.put("FFFFFF", "C6CACF");
            themeMap.put("CCCCCC", "95C590");
            themeMap.put("999999", "8EB3CB");
            themeMap.put("666666", "E59CA4");
            themeMap.put("333333", "A6A4D0");
            themeMap.put("000000", "000000");
        } else if(colorTheme==15) {
            //Elegant
            themeMap.put("FFFFFF", "606C77");
            themeMap.put("CCCCCC", "00372E");
            themeMap.put("999999", "172C51");
            themeMap.put("666666", "5F194D");
            themeMap.put("333333", "4C2B00");
            themeMap.put("000000", "391C00");
        } else if(colorTheme==16) {
            //Fun
            themeMap.put("FFFFFF", "E18876");
            themeMap.put("CCCCCC", "E49600");
            themeMap.put("999999", "00828E");
            themeMap.put("666666", "7D2880");
            themeMap.put("333333", "4C4C4C");
            themeMap.put("000000", "000000");
        } else if(colorTheme==17) {
            //Fun Pastel
            themeMap.put("FFFFFF", "C8C8C8");
            themeMap.put("CCCCCC", "C6A5CA");
            themeMap.put("999999", "E8A6B1");
            themeMap.put("666666", "F1C175");
            themeMap.put("333333", "86C5DA");
            themeMap.put("000000", "4C4C4C");
        } else if(colorTheme==18) {
            //Neutral
            themeMap.put("FFFFFF", "B2BBC3");
            themeMap.put("CCCCCC", "BBBC91");
            themeMap.put("999999", "D5C28A");
            themeMap.put("666666", "C6B9AB");
            themeMap.put("333333", "ADA3A4");
            themeMap.put("000000", "998693");
        } else if(colorTheme==19) {
            //Earth Tones
            themeMap.put("FFFFFF", "B69A71");
            themeMap.put("CCCCCC", "988F76");
            themeMap.put("999999", "333C1A");
            themeMap.put("666666", "6C3108");
            themeMap.put("333333", "482714");
            themeMap.put("000000", "35291F");
        } else if(colorTheme==20) {
            //Bold
            themeMap.put("FFFFFF", "FAE868");
            themeMap.put("CCCCCC", "FF9627");
            themeMap.put("999999", "FF6600");
            themeMap.put("666666", "C4CD20");
            themeMap.put("333333", "703F8A");
            themeMap.put("000000", "FF2761");
        } else if(colorTheme==21) {
            //Muted
            themeMap.put("FFFFFF", "D9DADE");
            themeMap.put("CCCCCC", "999891");
            themeMap.put("999999", "AD2F18");
            themeMap.put("666666", "CDC04B");
            themeMap.put("333333", "1E3072");
            themeMap.put("000000", "1D2220");
        } else if(colorTheme==22) {
            //Primary Colors
            themeMap.put("FFFFFF", "FFFFFF");
            themeMap.put("CCCCCC", "FAFA00");
            themeMap.put("999999", "00FA00");
            themeMap.put("666666", "FF0000");
            themeMap.put("333333", "0000FF");
            themeMap.put("000000", "000000");
        } else {
            //Print Colors
            themeMap.put("FFFFFF", "FFF200");
            themeMap.put("CCCCCC", "FAFA00");
            themeMap.put("999999", "00FA00");
            themeMap.put("666666", "FF0000");
            themeMap.put("333333", "0000FF");
            themeMap.put("000000", "000000");
        }
        return themeMap;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}


