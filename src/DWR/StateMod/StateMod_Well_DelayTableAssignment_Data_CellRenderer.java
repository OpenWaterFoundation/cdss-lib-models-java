// ----------------------------------------------------------------------------
// StateMod_Well_DelayTableAssignment_Data_CellRenderer - Class for 
//	rendering cells for delay table assignment tables
// ----------------------------------------------------------------------------
// Copyright:   See the COPYRIGHT file
// ----------------------------------------------------------------------------
// History:
//
// 2005-04-04	JTS, RTi		Initial version.
// ----------------------------------------------------------------------------

package DWR.StateMod;

import RTi.Util.GUI.JWorksheet_AbstractExcelCellRenderer;

/**
This class is used to render delay table assignment cells.
*/
@SuppressWarnings("serial")
public class StateMod_Well_DelayTableAssignment_Data_CellRenderer
extends JWorksheet_AbstractExcelCellRenderer {

/**
The table this will render cells for.
*/
private StateMod_Well_DelayTableAssignment_Data_TableModel __tableModel;

/**
Constructor.  
@param tableModel the table model for which to render cells
*/
public StateMod_Well_DelayTableAssignment_Data_CellRenderer(
StateMod_Well_DelayTableAssignment_Data_TableModel tableModel) {
	__tableModel = tableModel;
}

/**
Returns the format for a given column.
@param column the colum for which to return the format.
@return the format (as used by StringUtil.format) for a column.
*/
public String getFormat(int column) {
	return __tableModel.getFormat(column);
}

/**
Returns the widths of the columns in the table.
@return an integer array of the widths of the columns in the table.
*/
public int[] getColumnWidths() {
	return __tableModel.getColumnWidths();
}

}
