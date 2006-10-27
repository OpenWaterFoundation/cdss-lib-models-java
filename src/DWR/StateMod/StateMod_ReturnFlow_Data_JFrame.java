// ----------------------------------------------------------------------------
// StateMod_ReturnFlow_Data_JFrame - This is a JFrame that displays ReturnFlow
//	data in a tabular format.
// ----------------------------------------------------------------------------
// Copyright:   See the COPYRIGHT file
// ----------------------------------------------------------------------------
// History:
// 
// 2005-01-17	J. Thomas Sapienza, RTi	Initial version.
// 2005-01-20	JTS, RTi		Following review:
//					* Improved some loop performance.
//					* Removed getDataType().
//					* Title string is now passed to the
//					  super constructor.
//					* Editability of data in the worksheet
//					  is now passed in via the constructor.
// ----------------------------------------------------------------------------

package DWR.StateMod;

import java.util.Vector;

import RTi.Util.GUI.JScrollWorksheet;

import RTi.Util.Message.Message;

/**
This class is a JFrame for displaying a Vector of StateMod_ReturnFlow data in
a worksheet.  The worksheet data can be exported to a file or printed.
*/
public class StateMod_ReturnFlow_Data_JFrame 
extends StateMod_Data_JFrame {

/**
Whether the data being shown are return flows (true) or depletions (false).
*/
private boolean __isReturn = true;

/**
Constructor. 
@param data the data to display in the worksheet.  Can be null or empty, in
which case an empty worksheet is shown.
@param titleString the String to display as the GUI title.
@param isReturn if true, the data being shown are return flows.  If false,
the data are depletions.
@param editable whether the data in the JFrame can be edited or not.  If true
the data can be edited, if false they can not.
@throws Exception if there is an error building the worksheet.
*/
public StateMod_ReturnFlow_Data_JFrame(Vector data, String titleString, 
boolean isReturn, boolean editable)
throws Exception {
	super(data, titleString, editable);

	__isReturn = isReturn;
}

/**
Called when the Apply button is pressed. This commits any changes to the data
objects.
*/
protected void apply() {
	StateMod_ReturnFlow rf = null;
	int size = _data.size();
	for (int i = 0; i < size; i++) {
		rf = (StateMod_ReturnFlow)_data.elementAt(i);
		rf.createBackup();
	}
}

/**
Creates a JScrollWorksheet for the current data and returns it.
@return a JScrollWorksheet containing the data Vector passed in to the 
constructor.
*/
protected JScrollWorksheet buildJScrollWorksheet() 
throws Exception {
	StateMod_ReturnFlow_Data_TableModel tableModel 
		= new StateMod_ReturnFlow_Data_TableModel(_data, _editable, 
		__isReturn);
	StateMod_ReturnFlow_Data_CellRenderer cellRenderer 
		= new StateMod_ReturnFlow_Data_CellRenderer(tableModel);

	// _props is defined in the super class
	return new JScrollWorksheet(cellRenderer, tableModel, _props);
}

/**
Called when the cancel button is pressed.  This discards any changes made to 
the data objects.
*/
protected void cancel() {
	StateMod_ReturnFlow rf = null;
	int size = _data.size();
	for (int i = 0; i < size; i++) {
		rf = (StateMod_ReturnFlow)_data.elementAt(i);
		rf.restoreOriginal();
	}
}

/**
Creates backups of all the data objects in the Vector so that changes can 
later be cancelled if necessary.
*/
protected void createDataBackup() {
	StateMod_ReturnFlow rf = null;
	int size = _data.size();
	for (int i = 0; i < size; i++) {
		rf = (StateMod_ReturnFlow)_data.elementAt(i);
		rf.createBackup();
	}
}

}