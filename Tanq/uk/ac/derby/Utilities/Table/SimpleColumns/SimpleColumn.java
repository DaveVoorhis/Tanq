/*
 * SimpleColumn.java
 *
 * Created on March 27, 2003, 9:24 PM
 */

package uk.ac.derby.Utilities.Table.SimpleColumns;

import java.awt.*;

/**
 * A JTable column definition for use with SimpleColumns.
 *
 * @author  Dave Voorhis
 */
public abstract class SimpleColumn {
    
    private int columnNumber = -1;
    
    /** Creates a new instance of SimpleColumn */
    public SimpleColumn() {
    }
    
    /** Given a row object at a specified row number, return the data that should be displayed. */
    public abstract Object getCellData(Object row, int rowNumber);
    
    /** Given a row and a data object returned by getDataForCell(), update the row data.
     * Return true if updated, return false if not updated. */
    public abstract void setCellData(Object row, int rowNumber, Object dataForCell);
    
    /** Set the column number.   This is automatically invoked by SimpleColumns. */
    public final void setColumnNumber(int n) {
        columnNumber = n;
    }
    
    /** Get the column number.  This is not used internally, but is available for those
     * mechanisms that require it. */
    public final int getColumnNumber() {
        return columnNumber;
    }

    /** Get the column heading. */
    public String getName() {
        return "";
    }
    
    /** Return true if this row is editable. */
    public boolean isEditable(Object row, int rowNumber) {
        return false;
    }
    
    /** Get foreground color.  Null to use default. */
    public Color getForeground(Object row, int rowNumber) {
        return null;
    }
    
    /** Get background color.  Null to use default. */
    public Color getBackground(Object row, int rowNumber) {
        return null;
    }
    
    /** Return a component that will be used to render a given cell object.  Return null to use JTable's
     * default renderer on the cell data's class. */
    public Component getRenderer(Object row, int rowNumber, boolean isSelected, boolean hasFocus) {
        return null;
    }
    
    /** Initialise and return a component that will be used to edit a given cell object.  Return null to use JTable's
     * default editor on the cell data's class. */
    public Component getEditor(Object row, int rowNumber, boolean isSelected) {
        return null;
    }
    
    /** Obtain the edited data from given edit component.  Should be overridden if getEditor() is overridden. */
    public Object getEditorData(Component c) {
        return null;
    }
    
    /** Return this column's data class.   This only needs to be implemented if getCellData() will be returning
     * null values. */
    public Class<?> getColumnClass() {
        return null;
    }
    
}
