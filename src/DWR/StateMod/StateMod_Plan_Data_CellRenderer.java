package DWR.StateMod;

import RTi.Util.GUI.JWorksheet_AbstractExcelCellRenderer;

/**
This class renders cells for plan station tables.
*/
public class StateMod_Plan_Data_CellRenderer extends JWorksheet_AbstractExcelCellRenderer
{

/**
The table model for which this class renders the cells.
*/
private StateMod_Plan_Data_TableModel __tableModel;

/**
Constructor.  
@param tableModel the table model for which this class renders the cells.
*/
public StateMod_Plan_Data_CellRenderer(StateMod_Plan_Data_TableModel tableModel)
{
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