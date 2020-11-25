/**
 * Copyright (c) 2012, WonderBuilders, Inc., All Rights Reserved
 */

package org.jdesktop.wonderland.modules.appframe.client;

import com.jme.math.Vector3f;
import java.awt.Component;
import java.awt.Font;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellEditChannelConnection;
import org.jdesktop.wonderland.client.cell.registry.CellRegistry;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.client.cell.utils.CellCreationException;
import org.jdesktop.wonderland.client.cell.utils.CellSelectionRegistry;
import org.jdesktop.wonderland.client.cell.utils.spi.CellSelectionSPI;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.cell.CellEditConnectionType;
import org.jdesktop.wonderland.common.cell.messages.CellCreateMessage;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.modules.appframe.common.AppFrameApp;
import org.jdesktop.wonderland.modules.appframe.common.AppFrameConstants;
import org.jdesktop.wonderland.modules.appframe.common.AppFramePinToMenu;

import org.jdesktop.wonderland.modules.appframe.common.AppFrameProp;
import org.jdesktop.wonderland.modules.sharedstate.client.SharedMapCli;
import org.jdesktop.wonderland.modules.sharedstate.client.SharedStateComponent;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedData;

/**
 *
 * @author nilang
 */
public class AppFrameHistory extends javax.swing.JPanel {

    /** Creates new form AppFrameHistory */
    public JFrame parent;
    public SharedMapCli historyMap;
    public SharedMapCli propertyMap;
    public AppFrame parentCell;
    public int offsetX, offsetY;
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/jme/content/Bundle");
    private static final Logger LOGGER =
            Logger.getLogger(DropTargetListener.class.getName());

    public AppFrameHistory(SharedMapCli historyMap, JFrame parent, SharedMapCli propertyMap, Cell parentCell) {
        initComponents();

        this.historyMap = historyMap;
        this.propertyMap = propertyMap;
        this.parent = parent;
        this.parentCell = (AppFrame) parentCell;
        populate(historyMap, propertyMap);
        jLabel4.setVisible(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(350, 225));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setText("History of documents added to App Frame");

        jLabel2.setText("Items in italic are pinned to App Frame Menu");

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Name", "Data Last Used"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setEditingColumn(0);
        jTable1.setEditingRow(0);
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 18));
        jButton1.setText("-");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Display Now");
        jButton3.setName("Display Now"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel4.setText("jLabel4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(87, 87, 87)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton3))
                            .addComponent(jLabel3)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel2))
                            .addComponent(jLabel1))))
                .addGap(40, 40, 40))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel4))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jButton3)))
                .addGap(23, 23, 23))
        );
    }// </editor-fold>//GEN-END:initComponents
//this is used to display any document from the history and also it will updat the lastused date of that document
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        try {
            CellEditChannelConnection connection = (CellEditChannelConnection) parentCell.getSession().getConnection(CellEditConnectionType.CLIENT_TYPE);
            if (jTable1.getValueAt(jTable1.getSelectedRow(), 1).equals("pinned")) {
                AppFrameProp afp = (AppFrameProp) propertyMap.get("afp");
                DropTargetListener dtl = new DropTargetListener(parentCell);
                parentCell.savePrevious();
                // parentCell.pinToMenuMap.get
                AppFramePinToMenu afPin = (AppFramePinToMenu) parentCell.pinToMenuMap.get(jTable1.getValueAt(jTable1.getSelectedRow(), 0));
                if (parentCell.getFileExtension(afPin.getFileName()) == null) {
                    Set<CellFactorySPI> cfs = CellRegistry.getCellRegistry().getAllCellFactories();
                    CellServerState serverState = null;
                    for (CellFactorySPI cfspi : cfs) {

                        if (cfspi.getDisplayName() != null) {
                            if (cfspi.getDisplayName().equals(afPin.getFileName())) {
                                serverState = cfspi.getDefaultCellServerState(null);
                            }
                        }
                    }
                    PositionComponentServerState pcss = (PositionComponentServerState) serverState.getComponentServerState(PositionComponentServerState.class);
                    if (pcss == null) {
                        pcss = new PositionComponentServerState();
                        serverState.addComponentServerState(pcss);
                    }
                    pcss.setTranslation(new Vector3f(0f, 0f, 0.02f));
                    ServerSessionManager manager = LoginManager.getPrimary();
                    WonderlandSession session = manager.getPrimarySession();
                    try {
                        if (parentCell.historyMap.get(serverState.getName()) != null) {
                            AppFrameApp afa = (AppFrameApp) parentCell.historyMap.get(serverState.getName());
                            String sss = parentCell.encodeState(serverState);
                            afa.setState(sss);
                            afa.setContentURI(parentCell.getContentURI(sss));
                            afa.setLastUsed(new Date());
                            parentCell.historyMap.put(serverState.getName(), afa);
                        } else {
                            if (parentCell.historyMap.size() >= 20) {
                                parentCell.dropItem();
                            } else {
                            }
                            String sss = parentCell.encodeState(serverState);
                            parentCell.historyMap.put(serverState.getName(), new AppFrameApp(sss, new Date()
                                    , session.getUserID().getUsername(), new Date()
                                    ,parentCell.getContentURI(sss)));
                        }
                    } catch (IOException ex) {
                        //Logger.getLogger(ClickListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    CellCreateMessage msg = new CellCreateMessage(parentCell.getCellID(), serverState);
                    connection.send(msg);
                }
                CellServerState css = dtl.createCell(afPin.getFileURL());
                if (css != null) {
                    SharedStateComponent ssc = parentCell.getComponent(SharedStateComponent.class);
                    historyMap = ssc.get(AppFrameConstants.History_MAP);
                    try {
                        if (historyMap.get(css.getName()) != null) {

                            AppFrameApp afa = (AppFrameApp) historyMap.get(css.getName());
                            String sss = parentCell.encodeState(css);
                            afa.setState(sss);
                            afa.setContentURI(parentCell.getContentURI(sss));
                            afa.setLastUsed(new Date());
                            historyMap.put(css.getName(), afa);
                        } else {
                            String sss = parentCell.encodeState(css);
                            historyMap.put(css.getName(), new AppFrameApp(sss, new Date()
                                    , parentCell.getSession().getUserID().getUsername(), new Date()
                                    ,parentCell.getContentURI(sss)));
                        }
                    } catch (IOException ex) {
                        //   Logger.getLogger(DropTargetListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else {
                AppFrameApp afa = (AppFrameApp) historyMap.get(jTable1.getValueAt(jTable1.getSelectedRow(), 0));
                CellServerState css = parentCell.decodeState(afa.getState());
                parentCell.savePrevious();
                if (css != null) {
                    SharedStateComponent ssc = parentCell.getComponent(SharedStateComponent.class);
                    historyMap = ssc.get(AppFrameConstants.History_MAP);
                    try {
                        String sss = parentCell.encodeState(css);
                        afa.setState(sss);
                        afa.setContentURI(parentCell.getContentURI(sss));
                        afa.setLastUsed(new Date());
                        historyMap.put(css.getName(), afa);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                CellCreateMessage msg = new CellCreateMessage(parentCell.getCellID(), css);
                connection.send(msg);
                populate(historyMap, propertyMap);
            }
            parent.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

//this method is used to remove the entry of recent document from history
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try {
            int n = JOptionPane.showConfirmDialog(parent, "Do you really want to delete this from history?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                if (jTable1.getValueAt(jTable1.getSelectedRow(), 1).equals("pinned")) {
                    parentCell.pinToMenuMap.remove(jTable1.getValueAt(jTable1.getSelectedRow(), 0));
                    populate(historyMap, propertyMap);

                } else {
                    historyMap.remove(jTable1.getValueAt(jTable1.getSelectedRow(), 0));
                    populate(historyMap, propertyMap);
                }
            } else {
            }
            jLabel4.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        parent.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        try {
            if (jTable1.getValueAt(jTable1.getSelectedRow(), 1).equals("pinned")) {
                jLabel4.setVisible(false);
            } else {
                String data = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 0);
                AppFrameApp app = (AppFrameApp) historyMap.get(data);



                jLabel4.setText("Added By  " + app.getCreatedBy() + " " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(app.getCreated()));
                jLabel4.setVisible(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jTable1MouseClicked
    public void populate(SharedMapCli historyMap, SharedMapCli propertyMap) {
        try {
            Set<Map.Entry<String, SharedData>> histry = historyMap.entrySet();
            Vector<Vector> Rows = new Vector<Vector>();
            Vector<String> columnNames = new Vector<String>();
            columnNames.addElement("Name");
            columnNames.addElement("Date Last Used");
            SimpleDateFormat myDateFormat = new SimpleDateFormat("EEE d MMM ,yyyy");
            AppFrameProp afp = (AppFrameProp) propertyMap.get("afp");
            for (String pinned : parentCell.pinToMenuMap.keySet()) {
                Vector<String> RowData = new Vector<String>();
                RowData.addElement(pinned);
                RowData.addElement("pinned");
                Rows.add(RowData);
            }
            HashMap<Date, String> myMap = new HashMap<Date, String>();
            ArrayList<Date> history = new ArrayList<Date>();
            for (Map.Entry<String, SharedData> list : histry) {
                String name1 = list.getKey();
                AppFrameApp afa1 = (AppFrameApp) list.getValue();
                Date date1 = afa1.getLastUsed();
                history.add(date1);
                myMap.put(date1, name1);
            }
            Date[] dates = new Date[historyMap.size() + 1];
            history.toArray(dates);
            //   Arrays.sort(dates);
            for (int im = 0; dates[im] != null; im++) {
                for (int im2 = im + 1; dates[im2] != null; im2++) {
                    if (dates[im].compareTo(dates[im2]) < 0) {
                        Date temp = dates[im];
                        dates[im] = dates[im2];
                        dates[im2] = temp;
                    }
                }
            }
            for (int im = 0; dates[im] != null; im++) {
                Vector<String> RowData = new Vector<String>();
                RowData.addElement(myMap.get(dates[im]));
                RowData.addElement(myDateFormat.format(dates[im]));
                Rows.add(RowData);
            }
            TableModel tm = new MyTableModel(Rows, columnNames);
            jTable1.setModel(tm);
            javax.swing.table.TableColumn column1 = jTable1.getColumnModel().getColumn(0);
            javax.swing.table.TableColumn column2 = jTable1.getColumnModel().getColumn(1);
            column1.setCellRenderer(new YourTableCellRenderer());
            column2.setCellRenderer(new YourTableCellRenderer());
            TableRowSorter sorter = new TableRowSorter(jTable1.getModel());
            jTable1.setRowSorter(sorter);
            Comparator comparator = new Comparator() {

                public int compare(Object o1, Object o2) {
                    int j = 0;
                    SimpleDateFormat myDateFormat = new SimpleDateFormat("EEE d MMM ,yyyy");
                    try {
                        String s1 = (String) o1;
                        String s2 = (String) o2;
                        if (s1.equalsIgnoreCase("pinned")) {
                            return 1;
                        } else if (s2.equalsIgnoreCase("pinned")) {
                            return -1;
                        } else {
                            Date date1 = myDateFormat.parse((String) o1);
                            Date date2 = myDateFormat.parse((String) o2);
                            j = date1.compareTo(date2);
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(AppFrameHistory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return j;
                }
            };
            sorter.setComparator(1, comparator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class YourTableCellRenderer extends DefaultTableCellRenderer {

        public YourTableCellRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {
            try {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus,
                        row, column);

                // Only for specific cell
                if (value.equals("pinned")) {
                    Font f = new Font("Tahoma", Font.ITALIC, 11);
                    c.setFont(f);
                    // you may want to address isSelected here too
                }
                if (jTable1.getValueAt(row, 1).equals("pinned")) {
                    Font f = new Font("Tahoma", Font.ITALIC, 11);
                    c.setFont(f);
                }
                return c;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class MyTableModel extends DefaultTableModel {

        public MyTableModel(Vector<Vector> Rows, Vector<String> Coloumn) {
            super(Rows, Coloumn);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
    
    public CellServerState createCell(String uri) {
        // Figure out what the file extension is from the uri, looking for
        // the final '.'.
        try {
            String extension = getFileExtension(uri);

            if (extension == null) {
                //  LOGGER.warning("Could not find extension for " + uri);
                return null;
            }

            // First look for the SPI that tells us which Cell to use. If there
            // is none, then it is a fairly big error. (There should be at least
            // one registered in the system).
            CellSelectionSPI spi = CellSelectionRegistry.getCellSelectionSPI();
            if (spi == null) {
                final JFrame frame = JmeClientMain.getFrame().getFrame();
                //  LOGGER.warning("Could not find the CellSelectionSPI factory");
                String message = BUNDLE.getString("Launch_Failed_Message");
                message = MessageFormat.format(message, uri);
                JOptionPane.showMessageDialog(frame, message,
                        BUNDLE.getString("Launch_Failed"),
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // Next look for a cell type that handles content with this file
            // extension and create a new cell with it.
            CellFactorySPI factory = null;
            try {
                factory = spi.getCellSelection(extension);
                
            } catch (CellCreationException excp) {
                final JFrame frame = JmeClientMain.getFrame().getFrame();
                LOGGER.log(Level.WARNING,
                        "Could not find cell factory for " + extension);
                String message = BUNDLE.getString("Launch_Failed_Message");
                message = MessageFormat.format(message, uri);
                JOptionPane.showMessageDialog(frame, message,
                        BUNDLE.getString("Launch_Failed"),
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            // If the returned factory is null, it means that the user has cancelled
            // the action, so we just return
            if (factory == null) {
                return null;
            }

            // Get the cell server state, injecting the content URI into it via
            // the properties
            Properties props = new Properties();
            props.put("content-uri", uri);
            CellServerState state = factory.getDefaultCellServerState(props);
            state.setName(getFileName(uri));
            // Create the new cell at a distance away from the avatar
            try {
                // the parent, so only a small offset in the Z dimension is needed
                PositionComponentServerState pcss = (PositionComponentServerState) state.getComponentServerState(PositionComponentServerState.class);
                if (pcss == null) {
                    pcss = new PositionComponentServerState();
                    state.addComponentServerState(pcss);
                }
                pcss.setTranslation(new Vector3f(0f, 0f, 0.02f));
                WonderlandSession session = parentCell.getSession();
                CellEditChannelConnection connection = (CellEditChannelConnection) session.getConnection(CellEditConnectionType.CLIENT_TYPE);
                CellCreateMessage msg = new CellCreateMessage(parentCell.getCellID(), state);
                connection.send(msg);
                return state;
            } catch (Exception excp) {
                LOGGER.log(Level.WARNING, "Unable to create cell for uri " + uri, excp);
            }
          
        } catch (Exception ei) {
            ei.printStackTrace();
        }
          return null;
    }
    private static String getFileExtension(String uri) {
        // Figure out what the file extension is from the uri, looking for
        // the final '.'.
        int index = uri.lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        return uri.substring(index + 1);
    }    
    public static String getFileName(String uri) { // Check to see if there is a final '/'. We always use a forward-slash
        // regardless of platform, because it is typically a wlcontent URI.
        int index = uri.lastIndexOf("/");
        if (index == -1) {
            return uri;
        }
        return uri.substring(index + 1);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
