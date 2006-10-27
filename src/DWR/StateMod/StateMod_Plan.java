//------------------------------------------------------------------------------
// StateMod_Plan - class derived from StateMod_Data.  Contains information
//			read from the plan file.
//------------------------------------------------------------------------------
// Copyright:	See the COPYRIGHT file.
//------------------------------------------------------------------------------
// History:
// 
// 2006-08-22	Steven A. Malers, RTi	Copy diversion class and update for
//					plans.
//------------------------------------------------------------------------------
// EndHeader

package DWR.StateMod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.lang.Double;
import java.lang.Integer;

import java.util.StringTokenizer;
import java.util.Vector;

import RTi.GIS.GeoView.GeoRecord;

import RTi.Util.IO.IOUtil;

import RTi.Util.Message.Message;

import RTi.Util.String.StringUtil;

import RTi.Util.Time.TimeUtil;

/**
Object used to store plan information.  All set routines set
the COMP_PLANS flag dirty.  A new object will have empty non-null
Vectors, null time series, and defaults for all other data.
*/
public class StateMod_Plan extends StateMod_Data
implements Cloneable, Comparable
{

// ID, name, river node (cgoto), and switch are in the base class.

/**
Plan type.
*/
protected int 		_iPlnTyp;

/**
Plan efficiency.
*/
protected double 	_Peff;

/**
Return flow table.
*/
protected int		_iPrf;

/**
Plan failure switch.
*/
protected int		_iPfail;

/**
Initial plan storage value (AF).
*/
protected double 	_Psto1;

/**
Source ID of structure where reuse water became available or a T&C condition
originated (for reporting only).
*/
protected String 	_Psource;

/**
Reference to spatial data for this plan -- currently NOT cloned.
*/
protected GeoRecord _georecord;

/**
Construct a new plan and assign data to reasonable defaults.
*/
public StateMod_Plan()
{	super();
	initialize ( true );
}

/**
Construct a new plan.
@param initialize_defaults If true, assign data to reasonable defaults.
If false, all data are set to missing.
*/
public StateMod_Plan ( boolean initialize_defaults )
{	super();
	initialize ( initialize_defaults );
}

/**
Copy constructor.
@param deep_copy If true, make a deep copy including secondary vectors of data.
Currently only false is recognized, in which primitive data are copied.  This is
suitable to allow the StateMod_Plan_JFrame class to know when changes have
been made to data on the main screen.
*/
public StateMod_Plan ( StateMod_Plan plan, boolean deep_copy )
{	this();
	// Base class...
	// REVISIT
	// Local data members...
	_iPlnTyp = plan._iPlnTyp;
	_Peff = plan._Peff;
	_iPrf = plan._iPrf;
	_iPfail = plan._iPfail;
	_Psto1 = plan._Psto1;
	_Psource = plan._Psource;
	_georecord = plan._georecord;
}

/**
Accepts any changes made inside of a GUI to this object.
*/
public void acceptChanges() {
	_isClone = false;
	_original = null;
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
	StateMod_Plan plan = (StateMod_Plan)super.clone();
	plan._isClone = true;
	return plan;
}

/**
Compares this object to another StateMod_Diversion object.
@param o the object to compare against.
@return 0 if they are the same, 1 if this object is greater than the other
object, or -1 if it is less.
*/
public int compareTo(Object o) {
	int res = super.compareTo(o);
	if (res != 0) {
		return res;
	}

	StateMod_Plan p = (StateMod_Plan)o;

/* REVISIT SAM 2006-08-22
Need to review
	if (_divcap < d._divcap) {
		return -1;
	}
	else if (_divcap > d._divcap) {
		return 1;
	}
	
	res = _username.compareTo(d._username);
	if (res != 0) {
		return res;
	}

	if (_idvcom < d._idvcom) {
		return -1;
	}
	else if (_idvcom > d._idvcom) {
		return 1;
	}

	if (_divefc < d._divefc) {
		return -1;
	}
	else if (_divefc > d._divefc) {
		return 1;
	}

	if (_area < d._area) {
		return -1;
	}
	else if (_area > d._area) {
		return 1;
	}

	if (_irturn < d._irturn) {
		return -1;
	}
	else if (_irturn > d._irturn) {
		return 1;
	}
	
	if (_demsrc < d._demsrc) {
		return -1;
	}
	else if (_demsrc > d._demsrc) {
		return 1;
	}

	if (_ireptype < d._ireptype) {
		return -1;
	}
	else if (_ireptype > d._ireptype) {
		return 1;
	}

	if (_awc < d._awc) {
		return -1;
	}
	else if (_awc > d._awc) {
		return 1;
	}

	if (_diveff == null && d._diveff == null) {
		return 0;
	}
	else if (_diveff == null && d._diveff != null) {
		return -1;
	}
	else if (_diveff != null && d._diveff == null) {
		return 1;
	}
	else {
		int size1 = _diveff.length;
		int size2 = d._diveff.length;
		if (size1 < size2) {
			return -1;
		}
		else if (size1 > size2) {
			return 1;
		}
		
		for (int i = 0; i < size1; i++) {
			if (_diveff[i] < d._diveff[i]) {
				return -1;
			}
			else if (_diveff[i] > d._diveff[i]) {
				return 1;
			}
		}
	}
*/

	return 0;
}

/**
Creates a copy of the object for later use in checking to see if it was 
changed in a GUI.
*/
public void createBackup() {
	_original = clone();
	((StateMod_Plan)_original)._isClone = false;
	_isClone = true;
}

/**
Clean up for garbage collection.
*/
protected void finalize()
throws Throwable {
	_Psource = null;
	_georecord = null;
	super.finalize();
}

/**
Get the geographical data associated with the diversion.
@return the GeoRecord for the diversion.
*/
public GeoRecord getGeoRecord() {
	return _georecord;
}

/**
Return the plan efficiency.
*/
public double getPeff() {
	return _Peff;
}

/**
Return the plan fail flag.
*/
public int getIPfail() {
	return _iPfail;
}

/**
Return a list of failure flag option strings, for use in GUIs.
The options are of the form "0" if include_notes is false and
"0 - Do not turn plan off if it fails", if include_notes is true.
@return a list of plan type switch option strings, for use in GUIs.
@param include_notes Indicate whether notes should be added after the parameter
values.
*/
public static Vector getIPfailChoices ( boolean include_notes )
{	Vector v = new Vector(2);
	v.addElement ( "0 - Do not turn plan off if it fails" );
	v.addElement ( "1 - Turn plan off if it fails" );
	if ( !include_notes ) {
		// Remove the trailing notes...
		int size = v.size();
		for ( int i = 0; i < size; i++ ) {
			v.setElementAt(StringUtil.getToken(
				(String)v.elementAt(i), " ", 0, 0), i );
		}
	}
	return v;
}

/**
Return the default failure switch choice.  This can be used by GUI code
to pick a default for a new diversion.
@return the default plan type choice.
*/
public static String getIPfailDefault ( boolean include_notes )
{	// Make this aggree with the above method...
	if ( include_notes ) {
		return "0 - Do not turn plan off if it fails";
	}
	else {	return "0";
	}
}

/**
Return the plan type.
*/
public int getIPlnTyp() {
	return _iPlnTyp;
}

/**
Return a list of plan type option strings, for use in GUIs.
The options are of the form "1" if include_notes is false and
"1 - Terms and Conditions (T&C)", if include_notes is true.
@return a list of plan type switch option strings, for use in GUIs.
@param include_notes Indicate whether notes should be added after the parameter
values.
*/
public static Vector getIPlnTypChoices ( boolean include_notes )
{	Vector v = new Vector(9);
	v.addElement ( "1 - Terms and Conditions (T&C)" );
	v.addElement ( "2 - Well Augmentation" );
	v.addElement ( "3 - Reuse to a Reservoir" );
	v.addElement ( "4 - Reuse to a Diversion" );
	v.addElement ( "5 - Reuse to a Reservoir from Trasnmountain" );
	v.addElement ( "6 - Reuse to a Diversion from Trasnmountain" );
	v.addElement ( "7 - Transmountain import" );
	v.addElement ( "8 - Recharge Plan" );
	v.addElement ( "9 - Out of Priority Diversion or Storage" );
	if ( !include_notes ) {
		// Remove the trailing notes...
		int size = v.size();
		for ( int i = 0; i < size; i++ ) {
			v.setElementAt(StringUtil.getToken(
				(String)v.elementAt(i), " ", 0, 0), i );
		}
	}
	return v;
}

/**
Return the default plan type choice.  This can be used by GUI code
to pick a default for a new diversion.
@return the default plan type choice.
*/
public static String getIPlnTypDefault ( boolean include_notes )
{	// Make this aggree with the above method...
	if ( include_notes ) {
		return ( "1 - Terms and Conditions (T&C)" );
	}
	else {	return "1";
	}
}

/**
Return a list of on/off switch option strings, for use in GUIs.
The options are of the form "0" if include_notes is false and
"0 - Off", if include_notes is true.
@return a list of on/off switch option strings, for use in GUIs.
@param include_notes Indicate whether notes should be added after the parameter
values.
*/
public static Vector getPonChoices ( boolean include_notes )
{	Vector v = new Vector(2);
	v.addElement ( "0 - Off" );	// Possible options are listed here.
	v.addElement ( "1 - On" );
	if ( !include_notes ) {
		// Remove the trailing notes...
		int size = v.size();
		for ( int i = 0; i < size; i++ ) {
			v.setElementAt(StringUtil.getToken(
				(String)v.elementAt(i), " ", 0, 0), i );
		}
	}
	return v;
}

/**
Return the default on/off switch choice.  This can be used by GUI code
to pick a default for a new diversion.
@return the default reservoir replacement choice.
*/
public static String getPonDefault ( boolean include_notes )
{	// Make this aggree with the above method...
	if ( include_notes ) {
		return ( "1 - On" );
	}
	else {	return "1";
	}
}

/**
Return the return flow table.
*/
public int getIPrf() {
	return _iPrf;
}

/**
Return the ID of the source structure.
*/
public String getPsource() {
	return _Psource;
}

/**
Return the plan initial storage.
*/
public double getPsto1() {
	return _Psto1;
}

/**
Initialize data.
Sets the smdata_type to _dataset.COMP_PLANS.
@param initialize_defaults If true, assign data to reasonable defaults.
If false, all data are set to missing.
*/
private void initialize ( boolean initialize_defaults )
{	_smdata_type = _dataset.COMP_PLANS;
	if ( initialize_defaults ) {
		_iPlnTyp = 1;
		_Peff = 999;
		_iPrf = 999;
		_iPfail = 0;
		_Psto1 = 0.0;
		_Psource = "";
	}
	else {	// Use missing data...
		_iPlnTyp = StateMod_Util.MISSING_INT;
		_Peff = StateMod_Util.MISSING_DOUBLE;
		_iPrf = StateMod_Util.MISSING_INT;
		_iPfail = StateMod_Util.MISSING_INT;
		_Psto1 = StateMod_Util.MISSING_DOUBLE;
		_Psource = StateMod_Util.MISSING_STRING;
	}
	_georecord = null;
}

/**
Indicate whether a file is a StateMod plan file.  Currently the only
check that is done is to see if the file name ends in "pln".
@param filename File name.
@return true if the file appears to be a diversion file, false if not.
*/
public static boolean isStateModPlanFile ( String filename )
{	if ( StringUtil.endsWithIgnoreCase(filename,".pln") ) {
		return true;
	}
	return false;
}

/**
Read plan information in and store in a java vector.
The new plans are added to the end of the previously stored plans.
@param filename filename containing plan information
@throws Exception if an error occurs
*/
public static Vector readStateModFile(String filename)
throws Exception
{	String routine = "StateMod_Plan.readStateModFile";
	String iline = null;
	Vector v = new Vector(9);
	Vector thePlans = new Vector();
	int i;
	int linecount = 0;
	String s = null;
	
	StateMod_Plan aPlan = null;
	BufferedReader in = null;

	Message.printStatus(2, routine, "Reading plan file: " + filename);
	int size = 0;
	try {	
		in = new BufferedReader(new FileReader(
			IOUtil.getPathUsingWorkingDir(filename)));
		while ((iline = in.readLine()) != null) {
			++linecount;
			// check for comments
			if (iline.startsWith("#") || iline.trim().length()==0) {
				continue;
			}

			// allocate new diversion node
			aPlan = new StateMod_Plan();

			if (Message.isDebugOn) {
				Message.printDebug(50, routine, 
				"line: " + iline);
			}
			// Break the line using whitespace...
			v = StringUtil.breakStringList (
				iline, " \t", StringUtil.DELIM_ALLOW_STRINGS|
				StringUtil.DELIM_SKIP_BLANKS );
			size = 0;
			if ( v != null ) {
				size = v.size();
			}
			if ( size < 10 ) {
				Message.printStatus ( 2, routine,
				"Ignoring line " + linecount +
				" not enough data values.  Have " + size +
				" expecting " + 10 );
				continue;
			}
			// Uncomment if testing...
			//Message.printStatus ( 2, routine, "" + v );
			aPlan.setID(((String)v.elementAt(0)).trim()); 
			aPlan.setName(((String)v.elementAt(1)).trim()); 
			aPlan.setCgoto(((String)v.elementAt(2)).trim());
			aPlan.setSwitch(((String)v.elementAt(3)).trim());
			aPlan.setIPlnTyp(((String)v.elementAt(4)).trim());
			aPlan.setPeff(((String)v.elementAt(5)).trim());
			aPlan.setIPrf(((String)v.elementAt(6)).trim());
			aPlan.setIPfail(((String)v.elementAt(7)).trim());
			aPlan.setPsto1(((String)v.elementAt(8)).trim());
			aPlan.setPsource(((String)v.elementAt(9)).trim());

			// Set the diversion to not dirty because it was just
			// initialized...

			aPlan.setDirty ( false );

			// add the diversion to the vector of diversions
			thePlans.addElement(aPlan);
		}
	} 
	catch (Exception e) {
		routine = null;
		v = null;
		s = null;
		aPlan = null;
		if (in != null) {
			in.close();
		}
		in = null;
		Message.printWarning(2, routine, "Error reading line " +
			linecount + " \"" + iline + "\"" );
		Message.printWarning(2, routine, e);
		throw e;
	}

	routine = null;
	iline = null;
	v = null;
	s = null;
	aPlan = null;
	if (in != null) {
		in.close();
	}
	in = null;
	return thePlans;
}

/**
Cancels any changes made to this object within a GUI since createBackup()
was caled and sets _original to null.
*/
public void restoreOriginal() {
	StateMod_Plan plan = (StateMod_Plan)_original;
	super.restoreOriginal();

	_iPlnTyp = plan._iPlnTyp;
	_Peff = plan._Peff;
	_iPrf = plan._iPrf;
	_iPfail = plan._iPfail;
	_Psto1 = plan._Psto1;
	_Psource = plan._Psource;
	_isClone = false;
	_original = null;
}

/**
Set the plan failure flag.
@param iPfail Plan failure flag.
*/
public void setIPfail(int iPfail) {
	if (iPfail != _iPfail) {
		_iPfail = iPfail;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty( _dataset.COMP_PLANS, true);
		}
	}
}

/**
Set the plan failure flag.
@param iPfail plan failure flag.
*/
public void setIPfail(Integer iPfail) {
	setIPfail(iPfail.intValue());
}

/**
Set the plan failure flag.
@param iPfail plan failure flag.
*/
public void setIPfail(String iPfail) {
	if (iPfail == null) {
		return;
	}
	setIPfail(StringUtil.atoi(iPfail.trim()));
}

/**
Set the plan return flow table.
@param iPrf Plan return flow table.
*/
public void setIPrf(int iPrf) {
	if (iPrf != _iPrf) {
		_iPrf = iPrf;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty( _dataset.COMP_PLANS, true);
		}
	}
}

/**
Set the plan return flow table.
@param iPrf plan return flow table.
*/
public void setIPrf(Integer iPrf) {
	setIPrf(iPrf.intValue());
}

/**
Set the plan return flow table.
@param iPrf plan return flow table.
*/
public void setIPrf(String iPrf) {
	if (iPrf == null) {
		return;
	}
	setIPrf(StringUtil.atoi(iPrf.trim()));
}

/**
Set the geographic information object associated with the diversion.
@param georecord Geographic record associated with the diversion.
*/
public void setGeoRecord ( GeoRecord georecord )
{	_georecord = georecord;
}

/**
Set the plan type.
@param iPlnTyp Plan type.
*/
public void setIPlnTyp(int iPlnTyp) {
	if (iPlnTyp != _iPlnTyp) {
		_iPlnTyp = iPlnTyp;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty( _dataset.COMP_PLANS, true);
		}
	}
}

/**
Set the plan type.
@param iPlnTyp plan type.
*/
public void setIPlnTyp(Integer iPlnTyp) {
	setIPlnTyp(iPlnTyp.intValue());
}

/**
Set the plan type.
@param iPlnTyp plan type.
*/
public void setIPlnTyp(String iPlnTyp) {
	if (iPlnTyp == null) {
		return;
	}
	setIPlnTyp(StringUtil.atoi(iPlnTyp.trim()));
}

/**
Set the plan efficiency.
@param peff plan efficiency.
*/
public void setPeff(double Peff) {
	if (_Peff != Peff) {
		_Peff = Peff;
		setDirty(true);
		// REVISIT SAM 2006-08-22
		// Take out after initial troubleshooting is complete
		Message.printStatus ( 2, "", "Setting object dirty = true" );
		String s = "not null";
		if ( _dataset == null ) {
			s = "null";
		}
		Message.printStatus ( 2, "", "_isClone=" + _isClone +
			" _dataset="+s );
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty( _dataset.COMP_PLANS, true);
			Message.printStatus ( 2, "", "Is data set dirt?"  +
			_dataset.isDirty() );
		}
	}
}

/**
Set the plan efficiency.
@param Peff plan efficiency.
*/
public void setPeff (Double Peff) {
	setPeff (Peff.doubleValue());
}

/**
Set the plan efficiency.
@param Peff Plan efficiency.
*/
public void setPeff(String Peff) {
	if (Peff == null) {
		return;
	}
	setPeff(StringUtil.atod(Peff.trim()));
}

/**
Set the source id.
@param Psource source id.
*/
public void setPsource(String Psource) {
	if (Psource == null) {
		return;
	}
	if (!Psource.equals(_Psource)) {
		_Psource = Psource;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty( _dataset.COMP_PLANS, true);
		}
	}
}

/**
Set the plan initial storage.
@param peff plan initial storage.
*/
public void setPsto1(double Psto1) {
	if (_Psto1 != Psto1) {
		_Psto1 = Psto1;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty( _dataset.COMP_PLANS, true);
		}
	}
}

/**
Set the plan initial storage.
@param Psto1 plan initial storage.
*/
public void setPsto1 (Double Psto1) {
	setPeff (Psto1.doubleValue());
}

/**
Set the plan initial storage.
@param Peff Plan initial storage.
*/
public void setPsto1(String Psto1) {
	if (Psto1 == null) {
		return;
	}
	setPsto1(StringUtil.atod(Psto1.trim()));
}

/**
Write plan information to output.  History header information 
is also maintained by calling this routine.
@param instrfile input file from which previous history should be taken
@param outstrfile output file to which to write
@param thePlans vector of plans to write.
@param new_comments addition comments which should be included in history
@exception Exception if an error occurs.
*/
public static void writeStateModFile(String instrfile, String outstrfile,
Vector thePlans, String[] new_comments )
throws Exception {
	String [] comment_str = { "#" };
	String [] ignore_comment_str = { "#>" };
	PrintWriter out = null;
	try {	out = IOUtil.processFileHeaders(
			IOUtil.getPathUsingWorkingDir(instrfile),
			IOUtil.getPathUsingWorkingDir(outstrfile), 
			new_comments, comment_str, ignore_comment_str, 0);

		int i;
		int j;
		String iline;
		String cmnt = "#>";
		String routine = "StateMod_Plan.writeStateModFile";
		String format =
		"%-12.12s %-24.24s %-12.12s %8d %8d% #8.2F %8d %8d %8.2F %-12.12s";
		StateMod_Plan plan = null;
		Vector v = new Vector(10);	// Reuse for all output lines.

		out.println(cmnt);
		out.println(cmnt
			+ "*************************************************");
		out.println(cmnt +
			"  Plan (Augmentation and Terms and Conditions Data)");
		out.println(cmnt);
		out.println(cmnt
			+ "  Card 1 format (a12, a24, 1x, 3i8, 6(1x,a12)" );
		out.println(cmnt);
		out.println(cmnt
			+ "  Plan ID           Pid:  Plan ID");
		out.println(cmnt
			+ "  Plan Name       PName:  Plan name");
		out.println(cmnt
			+ "  Plan Location   iPsta:  River node for plan");
		out.println(cmnt
			+ "  Plan On/Off       Pon:  Switch 0=off, 1=on");
		out.println(cmnt
			+ "  Plan Type     iPlnTyp:  Plan type");
		out.println(cmnt
			+ "                          1=Terms and Conditions (T&C)");
		out.println(cmnt
			+ "                          2=Well Augmentation");
		out.println(cmnt
			+ "                          3=CU Reuse to a Reservoir");
		out.println(cmnt
			+ "                          4=CU Reuse to a Diversion");
		out.println(cmnt
			+ "                          5=CU Reuse to a Reservoir from Tmtn");
		out.println(cmnt
			+ "                          6=CU Reuse to a Diversion from Tmtn");
		out.println(cmnt
			+ "                          7=Tmtn Reuse");
		out.println(cmnt
			+ "                          8=Recharge Plan");
		out.println(cmnt
			+ "                          9=OOP Plan");
		out.println(cmnt
			+ "  Plan Eff.        Peff:  Plan efficiency (%)");
		out.println(cmnt
			+ "  Plan Rtn. Flow ID iPrf: Return flow table." );
		out.println(cmnt
			+ "                          999 to use source structure's" );
		out.println(cmnt
			+ "  Plan Failure Switch iPfail:  Failure switch" );
		out.println(cmnt
			+ "                          0=Do not stop for failure");
		out.println(cmnt
			+ "                          1=Stop for failure");
		out.println(cmnt
			+ "  Plan Init. Sto. Psto1:  Initial storage (AF)" );
		out.println(cmnt
			+ "  Plan source   PSource:  A reference (comment) typically used to describe" );
		out.println(cmnt
			+ "                          the source this plan is associated with.");
		out.println(cmnt
			+ "                          Note this is currently used only in reporting.");

		out.println(cmnt
			+ " ID               Name                 RivLoc   "
			+ "  On/Off  iPlnTyp    Peff    iPrf     iPfail "
			+ " Psto1       Psource");
		out.println(cmnt 
			+ "---------exb----------------------exb----------e"
			+ "xb------exb------exb------exb------exe------e"
			+ "xb------exb----------e");
		out.println(cmnt + "EndHeader");

		int num = 0;
		if (thePlans != null) {
			num = thePlans.size();
		}
		for (i = 0; i < num; i++) {
			plan = (StateMod_Plan)thePlans.elementAt(i);
			if (plan == null) {
				continue;
			}

			// line 1
			v.removeAllElements();
			v.addElement(plan.getID());
			v.addElement(plan.getName());
			v.addElement(plan.getCgoto());
			v.addElement(new Integer(plan.getSwitch()));
			v.addElement(new Integer(plan.getIPlnTyp()));
			v.addElement(new Double(plan.getPeff()));
			v.addElement(new Integer(plan.getIPrf()));
			v.addElement(new Integer(plan.getIPfail()));
			v.addElement(new Double(plan.getPsto1()));
			v.addElement(plan.getPsource());
			iline = StringUtil.formatString(v, format);
			out.println(iline);
		}
		
	out.flush();
	out.close();
	comment_str = null;
	ignore_comment_str = null;
	out = null;
	} 
	catch (Exception e) {
		comment_str = null;
		ignore_comment_str = null;
		if (out != null) {
			out.flush();
			out.close();
		}
		out = null;
		throw e;
	}
}

// REVISIT SAM 2006-08-22
// Enable when plan stations are enabled in StateDMI
/**
Writes a Vector of StateMod_Plan objects to a list file.  A header is 
printed to the top of the file, containing the commands used to generate the 
file.  Any strings in the body of the file that contain the field delimiter 
will be wrapped in "...".
@param filename the name of the file to which the data will be written.
@param delimiter the delimiter to use for separating field values.
@param update whether to update an existing file, retaining the current 
header (true) or to create a new file with a new header.
@param data the Vector of objects to write.  
@throws Exception if an error occurs.
*/
/*
public static void writeListFile(String filename, String delimiter,
boolean update, Vector data) 
throws Exception {
	int size = 0;
	if (data != null) {
		size = data.size();
	}
	
	Vector fields = new Vector();
	fields.add("ID");
	fields.add("Name");
	fields.add("RiverNodeID");
	fields.add("OnOff");
	fields.add("Capacity");
	fields.add("ReplaceResOption");
	fields.add("DailyID");
	fields.add("UserName");
	fields.add("DemandType");
	fields.add("EffAnnual");
	fields.add("IrrigatedAcres");
	fields.add("UseType");
	fields.add("DemandSource");
	fields.add("EffMonthly01");
	fields.add("EffMonthly02");
	fields.add("EffMonthly03");
	fields.add("EffMonthly04");
	fields.add("EffMonthly05");
	fields.add("EffMonthly06");
	fields.add("EffMonthly07");
	fields.add("EffMonthly08");
	fields.add("EffMonthly09");
	fields.add("EffMonthly10");
	fields.add("EffMonthly11");
	fields.add("EffMonthly12");
	int fieldCount = fields.size();

	String[] names = new String[fieldCount];
	String[] formats = new String[fieldCount]; 
	int comp = StateMod_DataSet.COMP_PLANS;
	String s = null;
	for (int i = 0; i < fieldCount; i++) {
		s = (String)fields.elementAt(i);
		names[i] = StateMod_Util.lookupPropValue(comp, "FieldName", s);
		formats[i] = StateMod_Util.lookupPropValue(comp, "Format", s);
	}

	String oldFile = null;	
	if (update) {
		oldFile = IOUtil.getPathUsingWorkingDir(filename);
	}
	
	int j = 0;
	int size2 = 0;
	PrintWriter out = null;
	String[] commentString = { "#" };
	String[] ignoreCommentString = { "#>" };
	String[] line = new String[fieldCount];
	String[] newComments = null;
	String id = null;
	StringBuffer buffer = new StringBuffer();
	StateMod_Diversion div = null;
	StateMod_ReturnFlow rf = null;
	Vector returnFlows = new Vector();
	Vector temp = null;
	
	try {	
		out = IOUtil.processFileHeaders(
			oldFile,
			IOUtil.getPathUsingWorkingDir(filename), 
			newComments, commentString, ignoreCommentString, 0);

		for (int i = 0; i < fieldCount; i++) {
			buffer.append("\"" + names[i] + "\"");
			if (i < (fieldCount - 1)) {
				buffer.append(delimiter);
			}
		}

		out.println(buffer.toString());
		
		for (int i = 0; i < size; i++) {
			div = (StateMod_Diversion)data.elementAt(i);
			
			line[0] = StringUtil.formatString(div.getID(), 
				formats[0]).trim();
			line[1] = StringUtil.formatString(div.getName(), 
				formats[1]).trim();
			line[2] = StringUtil.formatString(div.getCgoto(), 
				formats[2]).trim();
			line[3] = StringUtil.formatString(div.getSwitch(), 
				formats[3]).trim();
			line[4] = StringUtil.formatString(div.getDivcap(), 
				formats[4]).trim();
			line[5] = StringUtil.formatString(div.getIreptype(), 
				formats[5]).trim();
			line[6] = StringUtil.formatString(div.getCdividy(), 
				formats[6]).trim();
			line[7] = StringUtil.formatString(div.getUsername(), 
				formats[7]).trim();
			line[8] = StringUtil.formatString(div.getIdvcom(), 
				formats[8]).trim();
			line[9] = StringUtil.formatString(div.getArea(), 
				formats[9]).trim();
			line[10] = StringUtil.formatString(div.getIrturn(), 
				formats[10]).trim();
			line[11] = StringUtil.formatString(div.getDemsrc(), 
				formats[11]).trim();
			line[12] = StringUtil.formatString(div.getDivefc(), 
				formats[12]).trim();
			line[13] = StringUtil.formatString(div.getDiveff(0), 
				formats[13]).trim();
			line[14] = StringUtil.formatString(div.getDiveff(1), 
				formats[14]).trim();
			line[15] = StringUtil.formatString(div.getDiveff(2), 
				formats[15]).trim();
			line[16] = StringUtil.formatString(div.getDiveff(3), 
				formats[16]).trim();
			line[17] = StringUtil.formatString(div.getDiveff(4), 
				formats[17]).trim();
			line[18] = StringUtil.formatString(div.getDiveff(5), 
				formats[18]).trim();
			line[19] = StringUtil.formatString(div.getDiveff(6), 
				formats[19]).trim();
			line[20] = StringUtil.formatString(div.getDiveff(7), 
				formats[20]).trim();
			line[21] = StringUtil.formatString(div.getDiveff(8), 
				formats[21]).trim();
			line[22] = StringUtil.formatString(div.getDiveff(9), 
				formats[22]).trim();
			line[23] = StringUtil.formatString(div.getDiveff(10),
				formats[23]).trim();
			line[24] = StringUtil.formatString(div.getDiveff(11),
				formats[24]).trim();

			buffer = new StringBuffer();	
			for (j = 0; j < fieldCount; j++) {
				if (line[j].indexOf(delimiter) > -1) {
					line[j] = "\"" + line[j] + "\"";
				}
				buffer.append(line[j]);
				if (j < (fieldCount - 1)) {
					buffer.append(delimiter);
				}
			}

			out.println(buffer.toString());

			temp = div.getReturnFlows();
			size2 = temp.size();
			id = div.getID();
			for (j = 0; j < size2; j++) {
				rf = (StateMod_ReturnFlow)temp.elementAt(j);
				rf.setID(id);
				returnFlows.add(rf);
			}
		}
		out.flush();
		out.close();
		out = null;
	}
	catch (Exception e) {
		if (out != null) {
			out.flush();
			out.close();
		}
		out = null;
		throw e;
	}

	int lastIndex = filename.lastIndexOf(".");
	String front = filename.substring(0, lastIndex);
	String end = filename.substring((lastIndex + 1), filename.length());
	
	String returnFlowFilename = front + "_ReturnFlows." + end;
	StateMod_ReturnFlow.writeListFile(returnFlowFilename, delimiter,
		update, returnFlows,
		StateMod_DataSet.COMP_DIVERSION_STATION_DELAY_TABLES);

	String collectionFilename = front + "_Collections." + end;
	writeCollectionListFile(collectionFilename, delimiter,
		update, data);		
}
	*/

} // End StateMod_Plan