/*
 * SimpleColumns.java
 *
 * Created on March 27, 2003, 10:36 PM
 */

package uk.ac.derby.Utilities.Table.SimpleColumns;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;

/**
 * Mechanism for configuring a JTable from a given set of SimpleColumnS.
 *
 * <p>This is ideal when:<ul>
 *    <li>Each item in a collection is represented by a single object, whose properties
 *       you wish to display and edit as columns in a table of collection items.
 *    <li>Any or all of the columns require custom editors and/or renderers.</ul>
 *
 * <p>To create a simple JTable with editable columns and custom controls,
 * create a derivative of SimpleColumns that implements #see getRowCount()
 * and #see getValueAt(), eg.:
 *
 * <p><blockquote><code><pre>
 *   public class MyColumns extends SimpleColumns {
 *         public int getRowCount() {return someVector.size();}
 *         public object getValueAt(int i) {return someVector.get(i);}
 *   }</pre></code></blockquote>
 *
 * <p>Then create the individual columns, and add them to the
 * SimpleColumns derivative.
 *
 * <p><blockquote><code><pre>
 *   MyColumns columns = new SimpleColumns();
 *   columns.add(new SimpleColumn() {
 *     public Object getCellData(Object row, int rowNumber) {((MyData)row).getValue();}
 *     public void setCellData(Object row, int rowNumber, Object dataForCell) 
 *                        {((MyData)row).setValue(dataForCell);}
 *   });
 *   columns.add(new SimpleColumn() {
 *       ....
 *   });</pre></code></blockquote>
 *
 * <p>Now create a table, and use the columns definition to
 *  initialize it.
 *
 * <p><blockquote><code><pre>
 *   JTable myTable = new JTable();
 *   columns.configureTable(myTable);</pre></code></blockquote>
 *   
 * @author  Dave Voorhis
 */
public abstract class SimpleColumns {
    
    private Vector<SimpleColumn> columns = new Vector<SimpleColumn>();
    private boolean readOnly = false;
    private ColumnTableModel model;
    private JTable table;
    
    /** Creates a new instance of SimpleColumns */
    public SimpleColumns() {
    }
    
    /** Get the number of row objects. */
    public abstract int getRowCount();
    
    /** Get the ith row object. */
    public abstract Object getValueAt(int i);
    
    /** Add a column. */
    public void add(SimpleColumn column) {
        columns.add(column);
    }
    
    /** Set ReadOnly mode. */
    public void setReadOnly(boolean flag) {
        readOnly = flag;
    }
    
    /** True if ReadOnly. */
    public boolean isReadOnly() {
        return readOnly;
    }
    
    /** Override to return whether or not a given column is ReadOnly. */
    public boolean isReadOnlyColumn(int column) {
        return false;
    }
    
    /** Override to return whether or not a given row is ReadOnly. */
    public boolean isReadOnlyRow(int row) {
        return false;
    }
    
    /** Return true if a cell is ReadOnly.  Invokes isReadOnly, isReadOnlyColumn(int) and isReadOnlyRow(int) */
    public boolean isReadOnly(int row, int column) {
        return (isReadOnly() || isReadOnlyColumn(column) || isReadOnlyRow(row));
    }
    
    /** Get the number of columns. */
    public int getColumnCount() {
        return columns.size();
    }
    
    /** Get the ith column. */
    public SimpleColumn getColumn(int i) {
        return columns.get(i);
    }
    
    /** Set up a given JTable to use the specified columns. */
    public void configureTable(JTable t) {
    	table = t;
    	model = new ColumnTableModel();
        table.setModel(model);
        for (int i=0; i<getColumnCount(); i++) {
            SimpleColumn column = getColumn(i);
            column.setColumnNumber(i);
            table.setDefaultRenderer(column.getClass(), new ColumnTableRenderer());
            table.setDefaultEditor(column.getClass(), new ColumnTableEditor());
        }
    }
    
    /** Refresh the table. */
    public void refresh() {
    	table.clearSelection();
    	model.fireTableModelListener(new TableModelEvent(model));
    }
    
    // Table model.
    private class ColumnTableModel implements javax.swing.table.TableModel {
        
        private Class<?> temporaryColumnClass = null;
        
        /** Set up temporary return value for getColumnClass() */
        void setTemporaryColumnClass(Class<?> c) {
            temporaryColumnClass = c;
        }
            
        /** Returns the most specific superclass for all the cell values in the column. */
        public Class<?> getColumnClass(int columnIndex) {
            if (temporaryColumnClass != null)
                return temporaryColumnClass;
            else
                return getColumn(columnIndex).getClass();
        }
        
        /** Returns the name of the column at columnIndex. */
        public String getColumnName(int columnIndex) {
            return getColumn(columnIndex).getName();
        }
        
        /** Returns the number of rows in the model. */
        public int getRowCount() {
            return SimpleColumns.this.getRowCount();
        }
        
        /** Returns the number of columns in the model. */
        public int getColumnCount() {
            return SimpleColumns.this.getColumnCount();
        }
        
        /** Returns the value for the cell at columnIndex and rowIndex. */
        public Object getValueAt(int rowIndex, int columnIndex) {
            return getColumn(columnIndex);
        }
        
        /** Returns true if the cell at rowIndex and columnIndex is editable. */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return getColumn(columnIndex).isEditable(SimpleColumns.this.getValueAt(rowIndex), rowIndex);
        }
        
        /** Sets the value in the cell at columnIndex and rowIndex to aValue. */
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (aValue==null) {
                System.out.println("SimpleColumns: aValue is null.");
                
            } else
                getColumn(columnIndex).setCellData(SimpleColumns.this.getValueAt(rowIndex), rowIndex, aValue);
        }
        
        private Vector<TableModelListener> listeners = new Vector<TableModelListener>();

        /** Fire event to table model listeners. */
        public void fireTableModelListener(javax.swing.event.TableModelEvent e) {
            for (int i=0; i<listeners.size(); i++)
                listeners.get(i).tableChanged(e);
        }

        /** Adds a listener to the list that is notified each time a change to the data model occurs. */
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }
        
        /** Removes a listener from the list that is notified each time a change to the data model occurs. */
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }
    };
    
    private class ColumnTableRenderer extends javax.swing.table.DefaultTableCellRenderer {
        
    	private static final long serialVersionUID = 0;
    	
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable jTable,
                                                       Object value,
                                                       boolean isSelected,
                                                       boolean hasFocus,
                                                       int row,
                                                       int column) {
            SimpleColumn columnDefinition = getColumn(column);
            Object rowObject = getValueAt(row);
            Component comp = columnDefinition.getRenderer(rowObject, row, isSelected, hasFocus);
            if (comp == null) {
                Object actualValue = columnDefinition.getCellData(rowObject, row);
                if (actualValue.getClass()==this.getClass())
                    throw new Error("SimpleColumns: A column's renderer is incorrectly configured and has become self-referrential.");
                TableCellRenderer renderer = jTable.getDefaultRenderer(actualValue.getClass());
                comp = renderer.getTableCellRendererComponent(jTable, actualValue, isSelected, hasFocus, row, column);
            }

            if (isSelected) {
               comp.setForeground(jTable.getSelectionForeground());
               comp.setBackground(jTable.getSelectionBackground());
            }
            else {
                Color unselectedForeground = columnDefinition.getForeground(rowObject, row);
                Color unselectedBackground = columnDefinition.getBackground(rowObject, row);            
                comp.setForeground((unselectedForeground != null) ? unselectedForeground 
                                                                   : jTable.getForeground());
                comp.setBackground((unselectedBackground != null) ? unselectedBackground 
                                                                   : jTable.getBackground());
            }
            
            comp.setFont(jTable.getFont());

            if (hasFocus) {
                if (comp instanceof JComponent)
                    ((JComponent)comp).setBorder( UIManager.getBorder("Table.focusCellHighlightBorder") );
                if (columnDefinition.isEditable(rowObject, row)) {
                    comp.setForeground( UIManager.getColor("Table.focusCellForeground") );
                    comp.setBackground( UIManager.getColor("Table.focusCellBackground") );
                }
            } else {
                if (comp instanceof JComponent)
                    ((JComponent)comp).setBorder(noFocusBorder);
            }
            if (comp instanceof JComponent)
                ((JComponent)comp).setOpaque(true);
            
            if (!isReadOnly(row, column))
                comp.setEnabled(columnDefinition.isEditable(rowObject, row));
            
            return comp;
        }
    }
    
    private class ColumnTableEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor {
        
    	private static final long serialVersionUID = 0;
    	
        private Component component = null;
        private int colm;
        private TableCellEditor editor;
        private Class<?> actualClass;
        
        public ColumnTableEditor() {
        }
        
        /** Returns the value contained in the editor.
         * @return the value contained in the editor
         *
         */
        public Object getCellEditorValue() {
            if (editor != null) {
                Object o = editor.getCellEditorValue();
                if (o == null && component instanceof JTextField)
                    try {                   // hackage to obtain value from JTable$GenericEditor
                        java.lang.reflect.Constructor<?> constructor = actualClass.getConstructor(new Class[]{String.class});
                        o = constructor.newInstance(new Object[]{((JTextField)component).getText()});
                    } catch (Exception e) {
                        o = null;
                    }
                return o;
            } else
                return getColumn(colm).getEditorData(component);
        }
        
        /**  Sets an initial <code>value</code> for the editor.  This will cause
         *  the editor to <code>stopEditing</code> and lose any partially
         *  edited value if the editor is editing when this method is called. <p>
         *
         *  Returns the component that should be added to the client's
         *  <code>Component</code> hierarchy.  Once installed in the client's
         *  hierarchy this component will then be able to draw and receive
         *  user input.
         *
         * @param	table		the <code>JTable</code> that is asking the
         * 				editor to edit; can be <code>null</code>
         * @param	value		the value of the cell to be edited; it is
         * 				up to the specific editor to interpret
         * 				and draw the value.  For example, if value is
         * 				the string "true", it could be rendered as a
         * 				string or it could be rendered as a check
         * 				box that is checked.  <code>null</code>
         * 				is a valid value
         * @param	isSelected	true if the cell is to be rendered with
         * 				highlighting
         * @param	row     	the row of the cell being edited
         * @param	column  	the column of the cell being edited
         * @return	the component for editing
         *
         */
        public java.awt.Component getTableCellEditorComponent(
                                                        javax.swing.JTable jtable,
                                                        Object value,
                                                        boolean isSelected,
                                                        int row, int column) {
            if (isReadOnly(row, column))
                return null;
            colm = column;                                                        
            SimpleColumn columnDefinition = getColumn(column);
            Object rowObject = getValueAt(row);
            component = columnDefinition.getEditor(rowObject, row, isSelected);
            if (component == null) {
                Object actualValue = columnDefinition.getCellData(rowObject, row);
                if (actualValue == null) {
                    actualClass = columnDefinition.getColumnClass();
                    if (actualClass == null)
                        actualClass = String.class;
                } else {
                    actualClass = actualValue.getClass();
                    if (actualClass==this.getClass())
                        throw new Error("SimpleColumns: A column's editor is incorrectly configured and has become self-referrential.");
                }
                editor = jtable.getDefaultEditor(actualClass);
                //
                // This is a hack.  Internally, getTableCellEditorComponent calls jtable.getModel().getColumnClass(column), 
                // which normally returns the SimpleColumn derivative class.  It would then try to create an instance 
                // of the SimpleColumn derivative class given a string parameter.  This breaks.  To get around this,
                // we temporarily return actualClass in the model's getColumnClass(int).
                //
                // Alternatively, we could simply create our own default editors, but this might cause unpleasant
                // (further) divergence from standard JTable behavior.
                //
                ((ColumnTableModel)jtable.getModel()).setTemporaryColumnClass(actualClass);
                //
                component = editor.getTableCellEditorComponent(jtable, actualValue, isSelected, row, column);
                //
                ((ColumnTableModel)jtable.getModel()).setTemporaryColumnClass(null);
                //
            } else {
                editor = null;
            }
                        
            return component;
        }
    }
    
}
