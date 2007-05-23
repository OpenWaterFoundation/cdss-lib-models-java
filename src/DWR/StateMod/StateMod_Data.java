//------------------------------------------------------------------------------
// StateMod_Data - super class for many of the StateModLib classes
//------------------------------------------------------------------------------
// Copyright:	See the COPYRIGHT file.
//------------------------------------------------------------------------------
// Notes:	(1)This class is abstract and cannot be directly 
//		instantiated.
//		(2)Derived classes MUST override the toString()function.
//------------------------------------------------------------------------------
// History:
// 
// 19 Aug 1997	Catherine E.		Created initial version of class.
//		Nutting-Lane, RTi
// 07 Jan 1998	CEN, RTi		Adding operational rights type.
// 11 Feb 1998	CEN, RTi		Adding SMFileData.setDirty to all set
//					routines.
// 06 Apr 1998	CEN, RTi		Adding java documentation style
//					comments.
// 17 Feb 2001	Steven A. Malers, RTi	Review code as part of upgrades.  Add
//					finalize.  Add some javadoc.  Set unused
//					variables to null.  Get rid of debugs
//					that are no longer necessary.
//					Alphabetize methods.  Handle null
//					arguments.  Deprecated some methods that
//					are now in SMUtil.
// 2002-09-09	SAM, RTi		Add a comment about the GeoRecord
//					reference in derived classes to allow
//					two-way connections between spatial and
//					StateMod data.
// 2002-09-19	SAM, RTi		Use isDirty()instead of setDirty()to
//					indicate edits.
//------------------------------------------------------------------------------
// 2003-06-05	J. Thomas Sapienza 	Initial StateMod_ version.
// 2003-06-12	JTS, RTi		Added MISSING_* data
// 2003-07-07	SAM, RTi		Handle null data set for cases where the
//					code is used outside a full StateMod
//					data set.	
// 2003-07-16	JTS, RTi		Added indexOf and indexOfName
// 2003-08-03	SAM, RTi		* Changed isDirty() back to setDirty().
//					* Remove isMissing(), indexOf(),
//					  lookup*() methods - they are now in
//					  StateMod_Util.
// 2003-10-09	JTS, RTi		* Now implements Cloneable.
//					* Added clone().
//					* Added equals().
//					* Added rudimentary toString().
//					* Now implements Comparable.
//					* Added compareTo().
// 2003-10-15	JTS, RTi		Revised the clone code.
// 2004-07-14	JTS, RTi		* Added _isClone.
//					* Added _original.
//					* Added acceptChanges().
//					* Added changed().
//					* Added setDataSet().
// 2005-04-13	JTS, RTi		Added writeToListFile(), which is used
//					by subclasses.
// 2007-04-27	Kurt Tometich, RTi		Fixed some warnings.
// 2007-03-01	SAM, RTi		Clean up code based on Eclipse feedback.
// 2007-05-17	SAM, RTi		Add comment as data member to help with modeling
//					procedure development.
//------------------------------------------------------------------------------

package DWR.StateMod;

import RTi.Util.String.StringUtil;
import RTi.Util.Time.DateTime;

/**
Abstract object from which all other StateMod objects are derived.  
Each object can be identified by setting the smdata_type member.
Possible values for this member come from the SMFileData class 
(RES_FILE, DIV_FILE, etc.)
*/
public class StateMod_Data 
implements Cloneable, Comparable {


public static DateTime MISSING_DATE = 		null;
public static double MISSING_DOUBLE = 		-999.0;
public static float MISSING_FLOAT = 		(float)-999.0;
public static int MISSING_INT = 		-999;
public static long MISSING_LONG = 		-999;
public static String MISSING_STRING = 		"";

/**
Reference to the _dataset into which all the StateMod_* data objects are 
being placed.  It is used statically because this way every object that extends
StateMod_Data will have a reference to the same dataset for using the 
setDirty() method.
*/
protected static StateMod_DataSet _dataset;

/**
Whether the data is dirty or not.
*/
protected boolean _isDirty = false;

/**
Whether this object is a clone (ie, data that can be cancelled out of).
*/
protected boolean _isClone = false;

/**
Specific type of data.  This should be set by each derived class in its
constructor.  The types agree with the StateMod_DataSet component types.
*/
protected int _smdata_type = -999;

/**
Station id.
*/
protected String _id;

/**
Station name.
*/
protected String _name;

/**
Comment for data object.
*/
protected String _comment;

/**
For stations, the river node where station is located.  For water rights, the
station identifier where the right is located.
*/
protected String _cgoto;

/**
Switch on or off
*/
protected int _switch;

/**
UTM should be written to gis file.
*/
protected int _new_utm;

/**
For graphing.
*/
protected double _utm_x;

/**
For graphing
*/
protected double _utm_y;

/**
Label used when display on map.
*/
protected String _mapLabel;
protected boolean _mapLabelDisplayID;
protected boolean _mapLabelDisplayName;

/**
For screens that can cancel changes, this stores the original values.
*/
protected Object _original;

/**
Each GRShape has a pointer to the StateMod_Data which is its associated object.
This variable whether this object's location was found.  We could
have pointed back to the GRShape, but I was trying to avoid including GR in
this package.
Add a GeoRecord _georecord; object to derived classes that really have 
locational information.  Adding it here would bloat the code since 
StateMod_Data is the base class for most other classes.
*/
public boolean _shape_found;


/**
Constructor.
*/
public StateMod_Data() {
	super();
	initialize();
}

/**
Clones this object.  The _dataset object is not cloned -- the reference is 
kept pointing to the same dataset.  
@return a clone of this object.
*/
public Object clone() {
	StateMod_Data data = null;
	try {
		data = (StateMod_Data)super.clone();
	}
	catch (CloneNotSupportedException e) {
		// should never happen.
	}

	// dataset is not cloned -- the same reference is used.
	StateMod_Data._dataset = _dataset;
	data._isClone = true;

	return data;
}

/**
Compares this object to another StateMod_Data object based on _id, _name,
_cgoto, _switch, _utm_x, _utm_y, in that order.  The comment is not compared.
@param o the object to compare against.
@return 0 if they are the same, 1 if this object is greater than the other
object, or -1 if it is less.
*/
public int compareTo(Object o) {
	StateMod_Data data = (StateMod_Data)o;
	
	String name = data.getName();
	String id = data.getID();
	String cgoto = data.getCgoto();
	int swhich = data.getSwitch();
	double utm_x = data.getUTMx();
	double utm_y = data.getUTMy();

	int res = _id.compareTo(id);
	if (res != 0) {
		return res;
	}
	
	res = _name.compareTo(name);
	if (res != 0) {
		return res;
	}
	
	res = _cgoto.compareTo(cgoto);
	if (res != 0) {
		return res;
	}

	if (_switch < swhich) {
		return -1;
	}
	else if (_switch > swhich) {
		return 1;
	}

	if (_utm_x < utm_x) {	
		return -1;
	}
	else if (_utm_x > utm_x) {
		return 1;
	}

	if (_utm_y < utm_y) {
		return -1;
	}
	else if (_utm_y > utm_y) {
		return 1;
	}

	return 0;
}

/**
Checks to see if two StateMod_Data objects are equal.  The objects are equal
if all the boolean, double and int variables are the same, the Strings match
with case-sensitivity, and they both have are in the same _dataset object.
The comment is not compared.
@return true if they are equal, false if not.
*/
public boolean equals(StateMod_Data data) {
	if (	data._isDirty == _isDirty 
		&& data._utm_x == _utm_x
		&& data._utm_y == _utm_y
		&& data._switch == _switch
		&& data._id.equals(_id)
		&& data._name.equals(_name)
		&& data._cgoto.equals(_cgoto)
		&& data._smdata_type == _smdata_type
		&& data._mapLabel.equals(_mapLabel)
		&& data._mapLabelDisplayID == _mapLabelDisplayID
		&& data._mapLabelDisplayName == _mapLabelDisplayName
		&& StateMod_Data._dataset == _dataset) {
		return true;
	}
	return false;
}

/**
Clean up before garbage collection.
*/
protected void finalize()
throws Throwable {
	_id = null;
	_name = null;
	_comment = null;
	_cgoto = null;
	_mapLabel = null;
}

/**
Return the Cgoto.
*/
public String getCgoto() {
	return _cgoto;
}


/**
Return the comment.
*/
public String getComment() {
	return _comment;
}

/**
Return the ID.
*/
public String getID() {
	return _id;
}

public String getMapLabel() {
	return _mapLabel;
}

/**
Return the name.
*/
public String getName() {
	return _name;
}

/**
Return the new_utm flag.
*/
public int getNewUTM() {
	return _new_utm;
}

/**
Return the StateMod_DataType.
*/
public int getStateMod_DataType() {
	return(_smdata_type);
}

/**
Return the switch.
*/
public int getSwitch() {
	return _switch;
}

/**
Return the UTM x coordinate.
*/
public double getUTMx() {
	return _utm_x;
}

/**
Retrieve the UTM y coordinate.
*/
public double getUTMy() {
	return _utm_y;
}

/**
Initialize data members.
*/
private void initialize() {
	_id = "";
	_name = "";
	_comment = "";
	_cgoto = "";
	_mapLabel = "";
	_mapLabelDisplayID = false;
	_mapLabelDisplayName = false;
	_shape_found = false;
	_switch = 1;
	_new_utm = 0;
	_utm_x = -999;
	_utm_y = -999;
}

/**
Returns whether the data is dirty or not.
@return whether the data is dirty or not.
*/
public boolean isDirty() {
	return _isDirty;
}

/**
Resets the map label booleans to both false.
*/
public void resetMapLabelBooleans() {
	_mapLabelDisplayID = false;	
	_mapLabelDisplayName = false;	
}

/**
Restores the values from the _original object into the current object and 
sets _original to null.
*/
public void restoreOriginal() {
	StateMod_Data d = (StateMod_Data)_original;
	_utm_x = d._utm_x;
	_utm_y = d._utm_y;
	_new_utm = d._new_utm;
	_switch = d._switch;
	_id = d._id;
	_name = d._name;
	_comment = d._comment;
	_cgoto = d._cgoto;
	_smdata_type = d._smdata_type;
	_mapLabel = d._mapLabel;
	_mapLabelDisplayID = d._mapLabelDisplayID;
	_mapLabelDisplayName = d._mapLabelDisplayName;
}

/**
Set the Cgoto.
@param s the new Cgoto.
*/
public void setCgoto(String s) {
	if (s == null) {
		return;
	}
	if (!s.equals(_cgoto)) {
		if ( !_isClone && !_isClone && _dataset != null ) {
			_dataset.setDirty(_smdata_type, true);
		}
		_cgoto = s;
	}
}

/**
Set the Comment.
@param s the new comment.
*/
public void setComment (String s) {
	if (s == null) {
		return;
	}
	if (!s.equals(_comment)) {
		if ( !_isClone && !_isClone && _dataset != null ) {
			_dataset.setDirty(_smdata_type, true);
		}
		_comment = s;
	}
}

/**
Sets the dataset that all StateMod_Data objects will share.
*/
public static void setDataSet(StateMod_DataSet dataset) {
	_dataset = dataset;
}

/**
Sets whether the data is dirty or not.
@return whether the data is dirty or not.
*/
public void setDirty(boolean dirty) {
	_isDirty = dirty;
}

/**
Set the ID.
@param s the new ID.
*/
public void setID(String s) {
	if ((s != null)&&(!s.equals(_id))) {
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(_smdata_type, true);
		}
		_id = s;
	}
}

/**
Set the ID 
@param d the new ID
*/
public void setID(double d) {
	Double D = new Double(d);
	setID(D.toString());
}

/**
Sets the map ID according to contents of aResponse.  However, because one 
layer may turn the ID on and the next may turn it back off, a check is first 
done to see if _mapLabel has already been set.  Therefore, before calling 
setMapLabel in a loop, all _mapLabel members should be set to ""
*/
/*
REVISIT(JTS - 2003-06-04)
whenever SMGISResponse is converted ...
public void setMapLabel(SMGISResponse aResponse) {
	String id = "";

	// ID - this seems like a waste to put into 2 steps, but previous 
	// calls to setMapLabel may have set to true
	if (aResponse.getMapDisplayID()) {
		_mapLabelDisplayID = true;
	}
	if (_mapLabelDisplayID) {
		id += _id;
	}

	if (aResponse.getMapDisplayName()) {
		_mapLabelDisplayName = true;
	}
	if (_mapLabelDisplayName) {
		id += " " + _name;
	}

	if (Message.isDebugOn) {
		Message.printDebug(50, "StateMod_Data.setMapLabel",
			"Setting map label for " + _id + " to " + id);
	}
	_mapLabel = id;
	id = null;
}
*/

/**
Set the StateMod_DataType 
@param type type of node - uses types in SMFileData.
*/
public void setStateMod_DataType(int type) {
	_smdata_type = type;
}

/**
Set the name.
@param s the new Name.
*/
public void setName(String s) {
	if (s == null) {
		return;
	}
	if (!s.equals(_name)) {
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(_smdata_type, true);
		}
		_name = s;
	}
}

/**
Set the new_utm flag.  If the flag is set to 1, the UTM value is new and
the GIS file should be rewritten.
@param i new utm flag
*/
public void setNewUTM(int i) {
		_new_utm = i;
}

/**
Set the switch.
@param i the new switch: 1 = on, 0 = off, or other values for some data types.
*/
public void setSwitch(int i) {
	if (i != _switch) {
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(_smdata_type, true);
		}
		_switch = i;
	}
}

/**
Set the switch.
@param i the new switch: 1 = on, 0 = off.
*/
public void setSwitch(Integer i) {
	setSwitch(i.intValue());
}

/**
Set the switch 
@param str the new switch: 1 = on, 0 = off
*/
public void setSwitch(String str) {
	if (str == null) {
		return;
	}
	setSwitch(StringUtil.atoi(str.trim()));
}

/**
Set the UTM x and y coordinate.
@param x new x UTM
@param y new y UTM
*/
public void setUTM(double x, double y) {
	if (_utm_x != x || _utm_y != y) {
		_utm_x = x;
		_utm_y = y;
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(StateMod_DataSet.COMP_GEOVIEW, true);
		}
	}
}

/**
Set the UTM x and y coordinate.
@param x new x UTM
@param y new y UTM
*/
public void setUTM(Double x, Double y) {
	setUTM(x.doubleValue(), y.doubleValue());
}

/**
Set the UTM x and y coordinate
@param sx_orig new x UTM
@param sy_orig new y UTM
*/
public void setUTM(String sx_orig, String sy_orig) {
	if ((sx_orig == null) || (sy_orig == null)) {
		return;
	}
	setUTM(StringUtil.atod(sx_orig.trim()),StringUtil.atod(sy_orig.trim()));
}

/**
Returns a String representation of this object.  Omit comment.
@return a String representation of this object.
*/
public String toString() {
	return _isDirty + ", " + _utm_x + ", " + _utm_y + ", " + _new_utm 
		+ ", " + _switch + ", " + _id + ", " + _name + ", " + _cgoto
		+ ", " + _smdata_type + ", " + _mapLabel + ", "
		+ _mapLabelDisplayID + ", " + _mapLabelDisplayName;
}

}
