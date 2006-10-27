// ----------------------------------------------------------------------------
// StateMod_ReservoirClimate_TableModel - table model for displaying reservoir 
//	climate station assignment data
// ----------------------------------------------------------------------------
// Copyright:   See the COPYRIGHT file
// ----------------------------------------------------------------------------
// History:
// 2003-06-09	J. Thomas Sapienza, RTi	Initial version.
// 2003-06-11	JTS, RTi		Revised so that it displays real data
//					instead of dummy data for the main
//					Reservoir display.
// 2003-06-13	JTS, RTi		* Created code for displaying the
//					  area cap and climate data
//					* Added code to handle editing
//					  data
// 2003-06-16	JTS, RTi		* Added code for the reservoir account
//					  data
//					* Added code for the reservoir right
//					  data
// 2003-06-17	JTS, RTi		Revised javadocs.
// 2003-07-17	JTS, RTi		Constructor now takes a editable flag
//					to specify whether the data should be
//					editable.
// 2003-07-29	JTS, RTi		JWorksheet_RowTableModel changed to
//					JWorksheet_AbstractRowTableModel.
// 2003-08-16	Steven A. Malers, RTi	Update because of changes in the
//					StateMod_ReservoirClimate class.
// 2003-08-22	JTS, RTi		* Changed headings for the rights table.
//					* Changed headings for the owner account
//					  table.
//					* Added code to accomodate tables which
//					  are now using comboboxes for entering
//					  data.
// 2003-08-25	JTS, RTi		Added partner table code for saving
//					data in the reservoir climate gui.
// 2003-08-28	SAM, RTi		* Change setRightsVector() call to
//					  setRights().
//					* Update for changes in
//					  StateMod_Reservoir.
// 2003-09-18	JTS, RTi		Added ID column for reservoir
//					accounts.
// 2003-10-10	JTS, RTi		* Removed reference to parent reservoir.
//					* Added getColumnToolTips().
// 2004-01-21	JTS, RTi		Removed the row count column and 
//					changed all the other column numbers.
// 2004-10-28	SAM, RTi		Split code out of
//					StateMod_Reservoir_TableModel.
//					Change setValueAt() to support sort.
//					Add tool tips.
// 2005-01-21	JTS, RTi		Added ability to display data for either
//					one or many reservoirs.
// ----------------------------------------------------------------------------
// EndHeader

package DWR.StateMod;

import java.util.Vector;

import RTi.Util.GUI.JWorksheet_AbstractRowTableModel;

import RTi.Util.String.StringUtil;

/**
This table model displays reservoir climate station assignment data.  The model 
can display climate data for a single reservoir or for 1+ reservoirs.  The
difference is specified in the constructor and affects how many columns of 
data are shown.
*/
public class StateMod_ReservoirClimate_TableModel 
extends JWorksheet_AbstractRowTableModel {

/**
Number of columns in the table model.  For table models that display climate
data for a single reservoir, the tables only have 2 columns.  Table models that
display climate data for 1+ reservoirs have 3.  The variable is modified in the
constructor.
*/
private int __COLUMNS = 2;

/**
References to columns.
*/
public final static int
	COL_RESERVOIR_ID = 	-1,
	COL_STATION =		0,
	COL_PCT_WEIGHT = 	1;
	
/**
Whether the table data is editable.
*/
private boolean __editable = true;

/**
For the reservoir climate gui, the other table model of data that is displaying
station information.  Two table models are shown, one for precipitation
stations and one for evaporation stations.
REVISIT (JTS - 2005-01-25)
is this even needed anymore by stuff that uses this table model??
*/
private StateMod_ReservoirClimate_TableModel __partnerModel;

/**
Whether the table model should be set up for displaying the rights for only
a single reservoir (true) or for multiple reservoirs (false).  If true, then
the reservoir ID field will not be displayed.  If false, then it will be.
*/
private boolean __singleReservoir = true;

/**
Constructor.  
@param data the StateMod_ReservoirClimate data that will be displayed in the
table.
@param editable whether the table data can be modified.
@param singleReservoir if true, then the table model is set up to only display
a single reservoir's right data.  This means that the reservoir ID field will
not be shown.  If false then the reservoir right field will be included.
@throws Exception if an invalid data or dmi was passed in.
*/
public StateMod_ReservoirClimate_TableModel(Vector data, boolean editable,
boolean singleReservoir)
throws Exception {
	if (data == null) {
		throw new Exception ("Invalid data Vector passed to " 
			+ "StateMod_ReservoirClimate_TableModel constructor.");
	}
	_rows = data.size();
	_data = data;

	__editable = editable;
	__singleReservoir = singleReservoir;

	if (!__singleReservoir) {
		// this is done because if climate data for 1+ reservoirs are
		// shown in the worksheet the ID for the associated reservoirs
		// needs shown as well.  So instead of the usual 2 columns of 
		// data, an additional one must be shown.
		__COLUMNS++;
	}
}

/**
Returns the class of the data stored in a given column.
@param col the column for which to return the data class.
@return the class of the data stored in a given column.
*/
public Class getColumnClass (int col) {
	// necessary for table models that display climate data for 1+
	// reservoirs, so that the -1st column (ID) can also be displayed.  
	// By doing it this way, code can be shared between the two kinds of
	// table models and less maintenance is necessary.
	if (!__singleReservoir) {	
		col--;
	}
	
	switch (col) {
		case COL_RESERVOIR_ID:	return String.class;
		case COL_STATION:	return String.class;
		case COL_PCT_WEIGHT:	return Double.class;
		default:		return String.class;
	}
}

/**
Returns the number of columns of data.
@return the number of columns of data.
*/
public int getColumnCount() {
	return __COLUMNS;
}

/**
Returns the name of the column at the given position.
@param col the position of the column for which to return the name.
@return the name of the column at the given position.
*/
public String getColumnName(int col) {
	// necessary for table models that display climate data for 1+
	// reservoirs, so that the -1st column (ID) can also be displayed.  
	// By doing it this way, code can be shared between the two kinds of
	// table models and less maintenance is necessary.
	if (!__singleReservoir) {
		col--;
	}
	
	switch (col) {
		case COL_RESERVOIR_ID:	return "RESERVOIR ID";
		case COL_STATION:	return "STATION ID";
		case COL_PCT_WEIGHT:	return "WEIGHT (%)";		
		default:		return " ";
	}	
}

/**
Returns the text to be assigned to worksheet tooltips.
@return a String array of tool tips.
*/
public String[] getColumnToolTips() {
	String[] tips = new String[__COLUMNS];

	// the offset is used because in worksheets that have climate data 
	// for 1+ reservoirs the first column is numbered -1.  The offset
	// calculation allows this column number, which is usually invalid 
	// for a worksheet, to be used for those worksheets that need to 
	// display the -1st column.
	int offset = 0;
	if (!__singleReservoir) {
		offset = 1;
		tips[COL_RESERVOIR_ID + offset] = 
			"<html>The reservoir station ID of the reservoir to "
			+ "which<br>the climate data belong.</html>";
	}

	tips[COL_STATION + offset] = "Station identifier.";
	tips[COL_PCT_WEIGHT + offset] = "Weight for station's data (%).";
	return tips;
}

/**
Returns the format that the specified column should be displayed in when
the table is being displayed in the given table format. 
@param column column for which to return the format.
@return the format (as used by StringUtil.formatString() in which to display the
column.
*/
public String getFormat(int column) {
	// necessary for table models that display climate data for 1+
	// reservoirs, so that the -1st column (ID) can also be displayed.  
	// By doing it this way, code can be shared between the two kinds of
	// table models and less maintenance is necessary.
	if (!__singleReservoir) {
		column--;
	}

	switch (column) {
		case COL_RESERVOIR_ID:	return "%-12s";
		case COL_STATION:	return "%-12s";	
		case COL_PCT_WEIGHT:	return "%12.1f";
		default:		return "%-8s";
	}
}

/**
Returns the number of rows of data in the table.
@return the number of rows of data in the table.
*/
public int getRowCount() {
	return _rows;
}

/**
Returns the data that should be placed in the JTable
at the given row and column.
@param row the row for which to return data.
@param col the column for which to return data.
@return the data that should be placed in the JTable at the given row and col.
*/
public Object getValueAt(int row, int col) {
	if (_sortOrder != null) {
		row = _sortOrder[row];
	}

	StateMod_ReservoirClimate cl 
		= (StateMod_ReservoirClimate)_data.elementAt(row);

	// necessary for table models that display climate data for 1+
	// reservoirs, so that the -1st column (ID) can also be displayed.  
	// By doing it this way, code can be shared between the two kinds of
	// table models and less maintenance is necessary.
	if (!__singleReservoir) {
		col--;
	}
	
	switch (col) {
		case COL_RESERVOIR_ID:	return cl.getCgoto();
		case COL_STATION:	return cl.getID();
		case COL_PCT_WEIGHT:	return new Double(cl.getWeight());
		default:		return "";
	}
}

/**
Returns an array containing the widths (in number of characters) that the 
fields in the table should be sized to.
@return an integer array containing the widths for each field.
*/
public int[] getColumnWidths() {
	int[] widths = new int[__COLUMNS];

	// the offset is used because in worksheets that have climate data 
	// for 1+ reservoirs the first column is numbered -1.  The offset
	// calculation allows this column number, which is usually invalid 
	// for a worksheet, to be used for those worksheets that need to 
	// display the -1st column.
	int offset = 0;
	if (!__singleReservoir) {
		offset = 1;
		widths[COL_RESERVOIR_ID + offset] = 10;
	}

	widths[COL_STATION + offset] = 		8;
	widths[COL_PCT_WEIGHT + offset] = 	8;
	
	return widths;
}

/**
Returns whether the cell at the given position is editable.  In this
table model all columns are editable (unless the table is not editable).
@param rowIndex unused
@param col the index of the column to check for whether it is editable.
@return whether the cell at the given position is editable.
*/
public boolean isCellEditable(int rowIndex, int col) {
	// necessary for table models that display climate data for 1+
	// reservoirs, so that the -1st column (ID) can also be displayed.  
	// By doing it this way, code can be shared between the two kinds of
	// table models and less maintenance is necessary.
	if (!__singleReservoir) {
		col--;
	}

	if (!__editable) {
		return false;
	}

	return true;
}

/**
Sets the value at the specified position to the specified value.
@param value the value to set the cell to.
@param row the row of the cell for which to set the value.
@param col the col of the cell for which to set the value.
*/
public void setValueAt(Object value, int row, int col) {
	if (_sortOrder != null) {
		row = _sortOrder[row];
	}
	double dval;
	int ival;
	StateMod_ReservoirClimate cl 
		= (StateMod_ReservoirClimate)_data.elementAt(row);

	// necessary for table models that display climate data for 1+
	// reservoirs, so that the -1st column (ID) can also be displayed.  
	// By doing it this way, code can be shared between the two kinds of
	// table models and less maintenance is necessary.
	if (!__singleReservoir) {
		col--;
	}
			
	switch (col) {
		case COL_RESERVOIR_ID:
			cl.setCgoto((String)value);
			break;
		case COL_STATION:	
			String s = new String((String)value);
			int index = s.indexOf(" - ");
			if (index > -1) {
				s = s.substring(0, index);
			}
			cl.setID(s);
			break;
		case COL_PCT_WEIGHT:
			dval = ((Double)value).doubleValue();
			cl.setWeight(dval);
			break;
	}	
	
	if (!__singleReservoir) {
		col++;
	}

	super.setValueAt(value, row, col);
}

}