// StateMod_DelayTable - Contains information read from the delay table file.

/* NoticeStart

CDSS Models Java Library
CDSS Models Java Library is a part of Colorado's Decision Support Systems (CDSS)
Copyright (C) 1994-2019 Colorado Department of Natural Resources

CDSS Models Java Library is free software:  you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    CDSS Models Java Library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CDSS Models Java Library.  If not, see <https://www.gnu.org/licenses/>.

NoticeEnd */

//------------------------------------------------------------------------------
// StateMod_DelayTable - Contains information read from the delay table file.
//------------------------------------------------------------------------------
// Copyright:	See the COPYRIGHT file.
//------------------------------------------------------------------------------
// History:
// 
// 03 Sep 1997	Catherine E.		Created initial version of class.
//		Nutting-Lane, RTi
// 24 Mar 1998	CEN, RTi		Added setRet_val.
// 21 Dec 1998	CEN, RTi		Added throws IOException to read/write
//					routines.
// 24 Jan 2000	CEN, RTi		Modified to accommodate Ray's new open
//					format(not necessarily 12 entries per
//					line).
// 14 Mar 2000	CEN, RTi		Extends from SMData now to utilize
//					search abilities in GUI(need to use ID
//					field).
// 17 Feb 2001	Steven A. Malers, RTi	Code review.  Change IO to IOUtil.  Add
//					finalize().  Handle nulls.  Set to null
//					when varialbles not used.  Update
//					javadoc.  Alphabetize methods.
// 13 Aug 2001	SAM, RTi		Add int to set the units as percent or
//					fraction.
// 2002-09-19	SAM, RTi		Use isDirty() instead of setDirty() to
//					indicate edits.
//------------------------------------------------------------------------------
// 2003-06-04	J. Thomas Sapienza, RTi	Renamed from SMDelayTbl to 
//					StateMod_DelayTable
// 2003-06-10	JTS, RTi		* Folded dumpDelayTableFile() into
//					  writeDelayTableFile()
// 					* Renamed parseDelayTableFile() to
//					  readDelayTableFile()
// 2003-06-23	JTS, RTi		Renamed writeDelayTableFile() to
//					writeStateModFile()
// 2003-06-26	JTS, RTi		Renamed readDelayTableFile() to
//					readStateModFile()
// 2003-07-07	SAM, RTi		* Javadoc data and parameters that were
//					  not documented.
//					* Remove MAX_DELAYS - not used anywhere.
//					* Remove _table_id since the base class
//					  _id can be used.
//					* Also set the base class name to the
//					  same as the ID.
//					* Check for null data set when reading
//					  data since when using with StateCU
//					  in StateDMI there is no StateMod data
//					  set.
// 2003-07-15	JTS, RTi		Changed code to use new dataset design.
// 2003-08-03	SAM, RTi		Change isDirty() back to setDirty().
// 2004-03-17	SAM, RTi		Add the scale() method to deal with
//					percent/fraction issues.
// 2004-07-14	JTS, RTi		* Added acceptChanges().
//					* Added changed().
//					* Added clone().
//					* Added compareTo().
//					* Added createBackup().
//					* Added restoreOriginal().
//					* Now implements Cloneable.
//					* Now implements Comparable.
//					* Clone status is checked via _isClone
//					  when the component is marked as dirty.
// 2005-04-18	JTS, RTi		Added writeListFile().
// 2007-04-12	Kurt Tometich, RTi		Added checkComponentData() and
//									getDataHeader() methods for check
//									file and data check support.
// 2007-03-01	SAM, RTi		Clean up code based on Eclipse feedback.
//------------------------------------------------------------------------------

package DWR.StateMod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Vector;

import RTi.Util.IO.IOUtil;
import RTi.Util.Message.Message;
import RTi.Util.String.StringUtil;

public class StateMod_DelayTable 
extends StateMod_Data 
implements Cloneable, Comparable<StateMod_Data>, StateMod_ComponentValidator {

/**
Number of return values.
*/
protected int _ndly;

/**
Double return values.
*/
protected List<Double> _ret_val;

/**
Units for the data, as determined at read time.
*/
protected String _units;

/**
Indicate whether the delay table is for monthly or daily data.
*/
protected boolean _isMonthly;
	
/**
Constructor.
@param isMonthly If true, the delay table contains monthly data.  If false, the
delay table contains daily data.
*/
public StateMod_DelayTable ( boolean isMonthly )
{	super();
	initialize();
	_isMonthly = isMonthly;
}

/**
Accepts any changes made inside of a GUI to this object.
*/
public void acceptChanges() {
	_isClone = false;
	_original = null;
}

/**
Add a delay.
*/
public void addRet_val(double d) {
	addRet_val(new Double(d));
}

/**
Add a delay
*/
public void addRet_val(Double D) {
	_ret_val.add(D);
	setNdly(_ret_val.size());
	if ( !_isClone && _dataset != null ) {
		_dataset.setDirty(StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY, true);
	}
}

/**
Add a delay
*/
public void addRet_val(String str) {
	if (str != null) {
		addRet_val(StringUtil.atod(str.trim()));
	}
}

/**
Compares this object with its original value (generated by createBackup() upon
entering a GUI) to see if it has changed.
*/
public boolean changed() {
	if (_original == null) {
		return true;
	}
	if (compareTo(_original) == 0) {
		return false;
	}

	return true;
}

/**
Clones the data object.
@return a cloned object.
*/
public Object clone() {
	StateMod_DelayTable d = (StateMod_DelayTable)super.clone();

	if (_ret_val == null) {
		d._ret_val = null;
	}
	else {
		d._ret_val = new Vector<Double>();
		int size = _ret_val.size();
		for ( int i = 0; i < size; i++ ) {
			d._ret_val.add( new Double(_ret_val.get(i).doubleValue()) );
		}
	}

	return d;
}

/**
Compares this object to another StateMod_DelayTable object.
@param data the object to compare against.
@return 0 if they are the same, 1 if this object is greater than the other object, or -1 if it is less.
*/
public int compareTo(StateMod_Data data) {
	int res = super.compareTo(data);
	if (res != 0) {
		return res;
	}

/* FIXME SAM 2009-01-11 Determine what to do with this code
	StateMod_DelayTable dt = (StateMod_DelayTable)_original;
	
	Message.printStatus(1, "", "'" + _ndly + "'  '" + dt._ndly + "'");
	Message.printStatus(1, "", "'" + _units + "'  '" + dt._units + "'");
	Message.printStatus(1, "", "'" + _is_monthly + "'   '" 
		+ dt._is_monthly + "'");

	if (_ret_val == null && dt._ret_val == null) {
		Message.printStatus(1, "", "Both are null");
	}
	else if (_ret_val == null) {
		Message.printStatus(1, "", "Mine is null");
	}
	else if (dt._ret_val == null) {
		Message.printStatus(1, "", "Hers is null");
	}
	else {
		int size1 = _ret_val.size();
		int size2 = dt._ret_val.size();
		Message.printStatus(1, "", "" + size1 + "   " + size2);
		if (size1 == size2) {
			for (int i = 0; i < size1; i++) {
				Message.printStatus(1, "", " " + (i + 1) 
					+ ") " + _ret_val.elementAt(i)
					+ "  " + dt._ret_val.elementAt(i));
			}
		}
	}
*/

	StateMod_DelayTable d = (StateMod_DelayTable)data;

	if (_ndly < d._ndly) {
		return -1;
	}
	else if (_ndly > d._ndly) {
		return 1;
	}
	
	res = _units.compareTo(d._units);
	if (res != 0) {
		return res;
	}

	if (_isMonthly != d._isMonthly) {
		return -1;
	}

	if (_ret_val == null && d._ret_val == null) {
		// ok
	}
	else if (_ret_val == null) {
		return -1;
	}
	else if (d._ret_val == null) {
		return 1;
	}
	else {
		double d1 = 0;
		double d2 = 0;
		Double D1 = null;
		Double D2 = null;

		int size1 = _ret_val.size();
		int size2 = d._ret_val.size();
		if (size1 < size2) {	
			return -1;
		}
		else if (size1 > size2) {
			return 1;
		}

		for (int i = 0; i < size1; i++) {
			D1 = (Double)_ret_val.get(i);
			d1 = D1.doubleValue();
			D2 = (Double)d._ret_val.get(i);
			d2 = D2.doubleValue();

			if (d1 < d2) {
				return -1;
			}
			else if (d1 > d2) {	
				return 1;
			}
		}
	}
	
	return 0;
}

/**
Creates a copy of the object for later use in checking to see if it was changed in a GUI.
*/
public void createBackup() {
	_original = (StateMod_DelayTable)clone();
	((StateMod_DelayTable)_original)._isClone = false;
	_isClone = true;
}

/**
Clean up before garbage collection.
*/
protected void finalize()
throws Throwable {
	_ret_val = null;
	_units = null;
	super.finalize();
}

/**
Returns the data column header for the specifically checked data.
@return Data column header.
 */
public static String[] getDataHeader()
{
	// TODO KAT 2007-04-16 When specific checks are added to checkComponentData
	// return the header for that data here
	return new String[] {};
}

/**
Get the number of return values.
*/
public int getNdly() {
	return _ndly;
}

/**
Get a delay corresponding to a particular index.
*/
public double getRet_val(int index) {
	return(((Double)_ret_val.get(index)).doubleValue());
}

/**
Get a entire list of delays.
*/
public List<Double> getRet_val() {
	return _ret_val;
}

/**
Return the delay table identifier.
@return the delay table identifier.
*/
public String getTableID() {
	return _id;
}

/**
Return the delay table units.
@return Units of delay table, consistent with time series data units, etc.
*/
public String getUnits() {
	return _units;
}

/**
Initialize data members.
*/
private void initialize() {
	_smdata_type = StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY;
	_ndly = 0;
	_units = "PCT";
	_ret_val = new Vector<Double>(1);
}

/**
Insert a delay - same as add but the index of where to insert can be given
*/
public void insertRet_val(double d, int index) {
	Double D = new Double(d);
	insertRet_val(D, index);
}

/**
Insert a delay - same as add but the index of where to insert can be given
*/
public void insertRet_val(Double D, int index) {
	_ret_val.add(index, D);
	setNdly(_ret_val.size());
	if ( !_isClone && _dataset != null ) {
		_dataset.setDirty(StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY, true);
	}
}

/**
Insert a delay - same as add but the index of where to insert can be given
*/
public void insertRet_val(String str, int index ) {
	if (str != null) {
		insertRet_val(StringUtil.atod(str.trim()), index);
	}
}

/**
Indicate whether the delay table contains monthly or daily data.
@return true if the delay table contains monthly data, false if daily.
*/
public boolean isMonthly ()
{	return _isMonthly;
}

/**
Remove a delay.
@param index the index of the delay to remove.
*/
public void removeRet_val(int index) {
	_ret_val.remove(index);
	setNdly(_ret_val.size());
	if ( !_isClone && _dataset != null ) {
		_dataset.setDirty(StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY, true);
	}
}

/**
Cancels any changes made to this object within a GUI since createBackup()
was caled and sets _original to null.
*/
public void restoreOriginal() {
	StateMod_DelayTable d = (StateMod_DelayTable)_original;
	super.restoreOriginal();

	_ndly = d._ndly;
	_ret_val = d._ret_val;
	_units = d._units;
	_isMonthly = d._isMonthly;

	_isClone = false;
	_original = null;
}

/**
Set the number of return values.
*/
public void setNdly(int i) {
	if (i != _ndly) {
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY,
			true);
		}
		_ndly = i;
	}
}

/**
Set the number of return values.
*/
public void setNdly(Integer i) {
	setNdly(i.intValue());
}

/**
Set the number of return values.
*/
public void setNdly(String str) {
	if (str != null) {
		setNdly(StringUtil.atoi(str.trim()));
	}
}

public void setRet_val(List<Double> v) {
	_ret_val = new Vector<Double>(v);
	_ndly = _ret_val.size();
}

public void setRet_val(int index, String str) {
	setRet_val(index, new Double(str.trim()));
}

public void setRet_val(int index, double d) {
	setRet_val(index, new Double(d));
}
	
public void setRet_val(int index, Double d) {
	if (d != null) {
		if (getNdly() > index) {
			_ret_val.set(index, d);
			if ( !_isClone && _dataset != null ) {
				_dataset.setDirty( StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY, true);
			}
		}
		else {	
			addRet_val(d);
		}
	}
}

/**
Set the id.
*/
public void setTableID(String str) {
	if (!str.equals(_id)) {
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY, true);
		}
		_id = str;
		// Set the name to the same as the ID...
		_name = str;
	}
}

/**
Set the delay table units.
@param units Units of delay table, consistent with time series data units, etc.
*/
public void setUnits(String units) {
	if (!units.equals(_units)) {
		if (units != null) {
			if (!_isClone && _dataset != null) {
				_dataset.setDirty(StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY, true);
			}
			_units = units;
		}
	}
}

/**
Returns the value for nrtn to be compared against the interv in the control file.  Either a value
is returned (if every nrtn is the same) or a -1 is returned (variable values for nrtn).
*/
/* TODO SAM 2007-03-01 Evaluate use
private int checkDelayInterv(Vector delaysVector) {
	int ndly = -999;
	if (delaysVector == null) {
		return ndly;
	}

	int ndelay = delaysVector.size();
	StateMod_DelayTable delayTable;

	for (int i = 0; i < ndelay; i++) {
		delayTable =(StateMod_DelayTable)delaysVector.elementAt(i);
		if (ndly == -999) {
			ndly = delayTable.getNdly();
		}
		if (ndly != delayTable.getNdly()) {
			delayTable = null;
			return(-1);
		}
	}
	delayTable = null;
	return ndly;
}
*/

/** 
Read delay information in and store in a java vector.  The new delay entries are
added to the end of the previously stored delays.  Returns the delay table information.
@param filename the filename to read from.
@param isMonthly Set to true if the delay table contains monthly data, false if it contains daily data.
@param interv The control file interv parameter.  +n indicates the number of
values in each delay pattern.  -1 indicates variable number of values with
values as percent (0-100).  -100 indicates variable number of values with values as fraction (0-1).
*/
public static List<StateMod_DelayTable> readStateModFile ( String filename, boolean isMonthly, int interv )
throws Exception {
	String routine = "StateMod_DelayTable.readStateModFile";
	String iline;
	List<StateMod_DelayTable> theDelays = new Vector<StateMod_DelayTable>(1);
	StateMod_DelayTable aDelay = new StateMod_DelayTable ( isMonthly );
	int numRead=0, totalNumToRead=0;
	boolean reading=false;
	BufferedReader in = null;
	StringTokenizer split = null;

	if (Message.isDebugOn) {
		Message.printDebug(10, routine, "in readStateModFile reading file: " + filename);
	}
	try {	
		in = new BufferedReader(new FileReader(filename));
		while ((iline = in.readLine()) != null) {
			// check for comments
			iline = iline.trim();
			if (iline.startsWith("#") || iline.length()==0) {
				continue;
			}

			split = new StringTokenizer(iline);
			if ((split == null) || (split.countTokens()== 0)) {
				continue;
			}

			if (!reading) {
				// allocate new delay node
				aDelay = new StateMod_DelayTable ( isMonthly );
				numRead = 0;
				reading = true;
				theDelays.add(aDelay);
				aDelay.setTableID(split.nextToken());

				if (interv < 0) {
					aDelay.setNdly(split.nextToken());
				}
				else {	
					aDelay.setNdly(interv);
				}
				totalNumToRead = aDelay.getNdly();
				// Set the delay table units(default is percent)...
				aDelay.setUnits("PCT");
				if (interv == -100) {
					aDelay.setUnits("FRACTION");
				} 
			}

			while (split.hasMoreTokens()) {
				aDelay.addRet_val(split.nextToken());
				numRead++;
			}
			if (numRead >= totalNumToRead) {
				reading = false;
			}
		}
	} catch (Exception e) {
		Message.printWarning(3, routine, e);
		throw e;
	}
	finally {
		if (in != null) {
			in.close();
		}
		in = null;
	}
	return theDelays;
}

/**
Scale the delay table by the given value.  This is used to convert between percent and fraction.
@param value Value by which to multiple the delay table values.
*/
public void scale ( double value )
{	for ( int i = 0; i < _ndly; i++ ) {
		setRet_val(i,getRet_val(i)*value);
	}
}

/**
@param count Number of components checked.
@param dataset StateMod dataset object.
@return validation results.
 */
public StateMod_ComponentValidation validateComponent ( StateMod_DataSet dataset ) 
{
	// TODO KAT 2007-04-16 add specific checks here
	return null;
}

/**
Write the new (updated) delay table file.  This routine writes the new delay table file.
If an original file is specified, then the original header is carried into the new file.
The writing of data is done by the dumpDelayFile routine which now does not mess with headers.
@param inputFile old file (used as input)
@param outputFile new file to create
@param dly list of delays
@param newcomments new comments to save with the header of the file
@param interv interv variable specified in control file (if negative, include the number
of entries at the start of each record).
@param precision number of digits after the decimal to write.  If negative, 2 is assumed.
@throws Exception if an error occurs
*/
public static void writeStateModFile(String inputFile, String outputFile,
		List<StateMod_DelayTable> dly, List<String> newcomments, int interv, int precision )
throws Exception {
	PrintWriter	out = null;
	List<String> commentIndicators = new Vector<String>(1);
	commentIndicators.add ( "#" );
	List<String> ignoredCommentIndicators = new Vector<String>(1);
	ignoredCommentIndicators.add ( "#>");
	String routine = "StateMod_DelayTable.writeStateModFile";
	if ( precision < 0 ) {
		precision = 2;
	}

	Message.printStatus(2, routine, "Writing delay tables to file \"" + outputFile 
		+ "\" using \"" + inputFile + "\" header...");

	try {	
		// Process the header from the old file...
		out = IOUtil.processFileHeaders(inputFile, outputFile,
			newcomments, commentIndicators, ignoredCommentIndicators, 0);
	
		// Now write the new data...
		String delayVal = null;
		String cmnt="#>";
		String idFormat = "%8.8s"; // Right justify because StateMod actually uses integers
		String delayValueFormat0 = "%8."; // no precision
		String delayValueFormat = "%8." + precision + "f";
		String countFormat = "%4d";
	
		out.println(cmnt);
		out.println(cmnt + " *******************************************************");
		out.println(cmnt + " StateMod Delay (Return flow) Table");
		out.println(cmnt);
		out.println(cmnt + "     Format (a8, i4, (12f8.2)");
		out.println(cmnt);
		out.println(cmnt + "   ID       idly: Delay table id");
		out.println(cmnt + "   Ndly     ndly: Number of entries in delay table idly.");
		out.println(cmnt + "                  Include only if \"interv\" in the");
		out.println(cmnt + "                  control file is equal to -1.");
		out.println(cmnt + "                  interv = -1 = Variable number of entries");
		out.println(cmnt + "                                as percent (0-100)");
		out.println(cmnt + "                  interv = -100 = Variable number of entries");
		out.println(cmnt + "                                as fraction (0-1)");
		out.println(cmnt + "   Ret  dlyrat(1-n,idl): Return for month n, station idl");
		out.println(cmnt);
		out.println(cmnt + " ID   Ndly  Ret1    Ret2    Ret3    Ret4    Ret5    Ret6    Ret7    Ret8    Ret9    Ret10   Ret11   Ret12");
		out.println(cmnt + "-----eb--eb------eb------eb------eb------eb------eb------eb------eb------eb------eb------eb------eb------e...next line" );
		out.println(cmnt + "EndHeader");
		out.println(cmnt);
	
		int ndly = 0;
		if (dly != null) {
			ndly = dly.size();
		}
		int numWritten, numToWrite;
		if (Message.isDebugOn) {
			Message.printDebug(3, routine, "Writing " + ndly + " delay table entries.");
		}
		StringBuffer iline = new StringBuffer();
		StateMod_DelayTable delay = null;
		for (int i = 0; i < ndly; i++) {
			delay = dly.get(i);
			// Clear out the string buffer for the new delay table
			iline.setLength(0);
			// Create one delay table entry with ID, Nvals, and 12 return values per line.
			if (interv < 0) {
				iline.append ( StringUtil.formatString(delay.getTableID(),idFormat));
				iline.append ( StringUtil.formatString(delay.getNdly(),countFormat));
			}
			else {
				iline.append ( StringUtil.formatString(delay.getTableID(),idFormat) );
			}
			
			numWritten = 0; // Number of values written for this delay table
			int numDelayVals = delay.getNdly();
			while (true) {
				if ( numWritten > 0 ) {
					// Clear lines 2+ before adding values
					iline.setLength(0);
					// Add spaces as per the record header info
					if (interv < 0) {
						iline.append ( "            " );
					}
					else {
						iline.append ( "        " );
					}
				}
				// Number of values remaining to write
				numToWrite = numDelayVals - numWritten;
	
				if (numToWrite > 12) {
					// Have more than 12 so only write 12 on this line
					numToWrite = 12;
				}
	
				for (int j = 0; j < numToWrite; j++) {
					delayVal = StringUtil.formatString(delay.getRet_val(numWritten + j),delayValueFormat);
					if ( delayVal.indexOf(' ') < 0 ) {
						// There are no spaces - this will be a problem because the file is free format.
						// Do a little more work here to reduce the precision until there is a leading
						// space.
						for ( int iprecision = precision -1; iprecision >= 0; iprecision-- ) {
							delayVal = StringUtil.formatString(delay.getRet_val(numWritten + j),
								delayValueFormat0 + iprecision + ".f");
							if ( delayVal.indexOf(' ') >= 0 ) {
								// Done
								break;
							}
						}
					}
					iline.append(delayVal);
				}
				// Now output the line:
				
				out.println(iline);
				numWritten += numToWrite;
				if ( numWritten == numDelayVals ) {
					// Done writing so break out
					break;
				}
			}	
		}
	} 
	catch (Exception e) {
		if (out != null) {
			out.close();
		}
		Message.printWarning(3, routine, e);
		throw e;
	}
	finally {
		if ( out != null ) {
			out.flush();
			out.close();
		}
	}
}

/**
Writes a list of StateMod_DelayTable objects to a list file.  A header is 
printed to the top of the file, containing the commands used to generate the 
file.  Any strings in the body of the file that contain the field delimiter will be wrapped in "...".  
@param filename the name of the file to which the data will be written.
@param delimiter the delimiter to use for separating field values.
@param update whether to update an existing file, retaining the current 
header (true) or to create a new file with a new header.
@param data the list of objects to write.
@param newComments the list of new comments to write to the header, or null if none.
@param comp the component type being written (to distinguish between daily and monthly delay tables),
either StateMod_DataSet.COMP_DELAY_TABLES_DAILY or StateMod_DataSet.COMP_DELAY_TABLES_MONTHLY.
@throws Exception if an error occurs.
*/
public static void writeListFile(String filename, String delimiter, boolean update, List<StateMod_DelayTable> data,
	List<String> newComments, int comp ) 
throws Exception {
	int size = 0;
	if (data != null) {
		size = data.size();
	}
	
	List<String> fields = new Vector<String>();
	fields.add("DelayTableID");
	fields.add("Date");
	fields.add("ReturnAmount");
	
	int fieldCount = fields.size();
	String[] names = new String[fieldCount];
	String[] formats = new String[fieldCount]; 
	String s = null;
	for (int i = 0; i < fieldCount; i++) {
		s = (String)fields.get(i);
		names[i] = StateMod_Util.lookupPropValue(comp, "FieldName", s);
		formats[i] = StateMod_Util.lookupPropValue(comp, "Format", s);
	}

	String oldFile = null;	
	if (update) {
		oldFile = IOUtil.getPathUsingWorkingDir(filename);
	}
	
	int j = 0;
	int k = 0;
	int num = 0;
	PrintWriter out = null;
	StateMod_DelayTable delay = null;
	List<String> commentIndicators = new Vector<String>(1);
	commentIndicators.add ( "#" );
	List<String> ignoredCommentIndicators = new Vector<String>(1);
	ignoredCommentIndicators.add ( "#>");
	String[] line = new String[fieldCount];	
	String id = null;
	StringBuffer buffer = new StringBuffer();
	
	try {
		// Add some basic comments at the top of the file.  Do this to a copy of the
		// incoming comments so that they are not modified in the calling code.
		List<String> newComments2 = null;
		if ( newComments == null ) {
			newComments2 = new Vector<String>();
		}
		else {
			newComments2 = new Vector<String>(newComments);
		}
		newComments2.add(0,"");
		if ( comp == StateMod_DataSet.COMP_DELAY_TABLES_DAILY ) {
			newComments2.add(1,"StateMod delay tables (daily) as a delimited list file.");
		}
		else {
			newComments2.add(1,"StateMod delay tables (monthly) as a delimited list file.");
		}
		newComments2.add(2,"");
		out = IOUtil.processFileHeaders( IOUtil.getPathUsingWorkingDir(oldFile),
			IOUtil.getPathUsingWorkingDir(filename), 
			newComments2, commentIndicators, ignoredCommentIndicators, 0);

		for (int i = 0; i < fieldCount; i++) {
			buffer.append("\"" + names[i] + "\"");
			if (i < (fieldCount - 1)) {
				buffer.append(delimiter);
			}
		}

		out.println(buffer.toString());
		
		for (int i = 0; i < size; i++) {
			delay = (StateMod_DelayTable)data.get(i);
			
			id = delay.getID();
			num = delay.getNdly();
			for (j = 0; j < num; j++) {			
				line[0] = StringUtil.formatString(id, formats[0]).trim();
				line[1] = StringUtil.formatString((j + 1), formats[1]).trim();
				line[2] = StringUtil.formatString( delay.getRet_val(j), formats[2]).trim();

				buffer = new StringBuffer();	
				for (k = 0; k < fieldCount; k++) {
					if (k > 0) {
						buffer.append(delimiter);
					}
					if (line[k].indexOf(delimiter) > -1) {
						line[k] = "\"" + line[k] + "\"";
					}
					buffer.append(line[k]);
				}
	
				out.println(buffer.toString());
			}
		}
		out.flush();
		out.close();
		out = null;
	}
	catch (Exception e) {
		// TODO SAM 2009-01-11 Log it?
		throw e;
	}
	finally {
		if (out != null) {
			out.flush();
			out.close();
		}
	}
}

}
