package uk.ac.derby.Tanq.Core;

import uk.ac.derby.Tanq.GUI;



/**
 * This is the main menubar for the application.
 */
public class Menu extends javax.swing.JMenuBar {
	private static final long serialVersionUID = 0;
    public Menu() {
        {
            javax.swing.JMenu menu = new javax.swing.JMenu("File");
            menu.setMnemonic( java.awt.event.KeyEvent.VK_F);
            add( menu);
            {
                javax.swing.JMenuItem menuItem = new javax.swing.JMenuItem("Exit");
                    menuItem.addActionListener( new java.awt.event.ActionListener() {
                            public void actionPerformed( java.awt.event.ActionEvent e) {
                                GUI.exit();
                            }
                        });
                menu.add( menuItem);       
            }
        }
/*        {
            javax.swing.JMenu menu = new javax.swing.JMenu("Tools");
            menu.setMnemonic( java.awt.event.KeyEvent.VK_T);
            add( menu);
            {
                javax.swing.JMenuItem menuItem = new javax.swing.JMenuItem("Options");
                    menuItem.addActionListener( new java.awt.event.ActionListener() {
                            public void actionPerformed( java.awt.event.ActionEvent e) {
                               optionsDialog.setVisible(true);
                            }
                        });
                menu.add( menuItem);
            }
        }
*/    }
}