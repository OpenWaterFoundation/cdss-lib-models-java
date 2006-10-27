//------------------------------------------------------------------------------
// StateMod_Diversion - class derived from StateMod_Data.  Contains information
//			read from the diversion file.
//------------------------------------------------------------------------------
// Copyright:	See the COPYRIGHT file.
//------------------------------------------------------------------------------
// History:
// 
// 19 Aug 1997	Catherine E.
//		Nutting-Lane, RTi	Created initial version of class
// 27 Mar 1998	CEN, RTi		Added pointers to TS.
// 06 Apr 1998	CEN, RTi		Added java style documentation.
// 21 Dec 1998	CEN, RTi		Added throws IOException to read/write
//					routines.
// 25 Oct 1999	CEN, RTi		Added daily diversion id.
//
// 01 Dec 1999	Steven A. Malers, RTi	Change so that connectAllTS is
//					overloaded to work the old way(no daily
//					time series)and with daily time series.
// 15 Feb 2001	SAM, RTi		Add use_daily_data flag to write methods
//					to allow writing old format files.
//					Change IO to IOUtil.  Add finalize();
//					Alphabetize methods.  Set unused
//					variables to null.  Add more checks for
//					null.  Update file output header.
//					In dumpDiversionsFile, reuse the vector
//					for output, optimize output formats
//					(don't need to format blank strings),
//					and remove debug statements.
// 2001-12-27	SAM, RTi		Update to use new fixedRead()to
//					improve performance.
// 2002-09-09	SAM, RTi		Add GeoRecord reference to allow 2-way
//					connection between spatial and StateMod
//					data.
// 2002-09-19	SAM, RTi		Use isDirty()instead of setDirty()to
//					indicate edits.
//					dds file.
//------------------------------------------------------------------------------
// 2003-06-04	J. Thomas Sapienza, RTi	Renamed from SMDiversion to 
//					StateMod_Diversion
// 2003-06-10	JTS, RTi		* Folded dumpDiversionsFile() into
//					  writeDiversionsFile()
//					* Renamed parseDiversionsFile() to
//					  readDiversionsFile()
// 2003-06-23	JTS, RTi		Renamed writeDiversionsFile() to
//					writeStateModFile()
// 2003-06-26	JTS, RTi		Renamed readDiversionsFile() to
//					readStateModFile()
// 2003-08-03	SAM, RTi		Change isDirty() to setDirty().
// 2003-08-14	SAM, RTi		Change GeoRecordNoSwing to GeoRecord.
// 2003-08-27	SAM, RTi		* Change default for cdividy to "0".
//					* Rework time series data members and
//					  methods to have better names,
//					  consistent with the data set
//					  components.
//					* Change so water rights are stored in
//					  a Vector, not an internally-maintained
//					  linked-list.
//					* Add all diversion data for the current
//					  StateMod design so that nothing is
//					  left out.
//					* Change connectDivRights() to
//					  connectRights().
//					* Change connectAllDivRights() to
//					  connectAllRights().
//					* Allow case-independent searches for
//					  time series identifiers.
//					* In addition to calling setDirty() on
//					  the data set component, do so on the
//					  individual objects.
//					* Clean up Javadoc.
//					* Remove data members for size of
//					  Vectors - the size can be determined
//					  from the Vectors.  Nduser is no longer
//					  used so don't need in any case.  Still
//					  output to allow file comparisons.
// 2003-09-30	SAM, RTi		Pass component type to
//					StateMod_ReturnFlow constructor.
// 2003-10-07	SAM, RTi		* As per Ray Bennett, default the demand
//					  source to 0, Unknown.
//					* Similarly, default efficiency is 60.
// 2003-10-10	SAM, RTi		Add disconnectRights().
// 2003-10-14	SAM, RTi		* Add a copy constructor for use by the
//					  StateMod_Diversion_JFrame to track
//					  edits.
//					* Change IWR to CWR (irrigation to
//					  consumptive) as per Ray Bennett
//					  feedback.
//					* Set the diversion dirty to false after
//					  read or construction - it may have
//					  been marked dirty with set() methods.
// 2003-10-21	SAM, RTi		Change demand override average monthly
//					to demand average monthly - more
//					consistent with documentation.
// 2004-02-25	SAM, RTi		Add isStateModDiversionFile().
// 2004-03-31	SAM, RTi		Print the line number and line when an
//					error occurs reading the file.
// 2004-04-12	SAM, RTi		* Change so read and write methods
//					  convert file paths using working
//					  directory.
// 2004-06-05	SAM, RTi		* Add methods to handle collections,
//					  similar to StateCU locations.
//					* Define static values here, that are
//					  possible values for some data members.
//					  These values were previously defined
//					  in StateMod_Diversion_JFrame.
//					* Add methods to retrieve the option
//					  strings.
// 2004-06-14	SAM, RTi		* Define public final int's for
//					  important demsrc values to help with
//					  StateDMI.
//					* Overload the constructor to allow
//					  initialization as completely missing
//					  or with reasonable defaults.  The
//					  former is better for StateDMI, the
//					  latter for StateMod GUI.
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
// 2004-08-16	SAM, RTi		* Output old Nduser as 1 instead of 0
//					  since that is what old files have.
// 2004-09-01	SAM, RTi		* Add the following for use with
//					  StateDMI only - no need to check for
//					  dirty - only set/gets on the entire
//					  array are enabled:
//						__cwr_monthly
//						__ddh_monthly
//						__calculated_efficiencies
//						__calculated_efficiency_stddevs
//						__model_efficiecies
// 2004-09-06	SAM, RTi		* Add "MultiStruct" to the types of
//					  collection.
// 2005-03-30	JTS, RTi		* Added getCollectionPartType().
//					* Added getCollectionYears().
// 2005-04-14	JTS, RTi		Added writeListFile().
// 2005-04-19	JTS, RTi		Added writeCollectionListFile().
// 2005-15-16	SAM, RTi		Overload setDiveff() to accept a
//					parameter indicating the year type of
//					the diversion stations file, to simplify
//					adjustments for water year, etc.
// 2006-04-09	SAM, RTi		Add _parcels_Vector data member and
//					associated methods, to help with
//					StateDMI error handling.
//------------------------------------------------------------------------------
// EndHeader
// REVISIT SAM 2006-04-09
// The _parcel_Vector has minimal support and is not yet considered in
// copy, clone, equals, etc.

package DWR.StateMod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.lang.Double;
import java.lang.Integer;

import java.util.StringTokenizer;
import java.util.Vector;

import DWR.StateCU.StateCU_IrrigationPracticeTS;

import RTi.GIS.GeoView.GeoRecord;

import RTi.TS.DayTS;
import RTi.TS.MonthTS;

import RTi.Util.IO.IOUtil;

import RTi.Util.Message.Message;

import RTi.Util.String.StringUtil;

import RTi.Util.Time.TimeUtil;

/**
Object used to store diversion information.  All set routines set
the COMP_DIVERSION_STATIONS flag dirty.  A new object will have empty non-null
Vectors, null time series, and defaults for all other data.
*/
public class StateMod_Diversion extends StateMod_Data
implements Cloneable, Comparable
{

/**
Demand source values used by other software.  Most interaction is expected to
occur through GUIs.
*/
public static final int DEMSRC_UNKNOWN = 0;
public static final int DEMSRC_GIS = 1;
public static final int DEMSRC_TIA = 2;
public static final int DEMSRC_GIS_PRIMARY = 3;
public static final int DEMSRC_TIA_PRIMARY = 4;
public static final int DEMSRC_GIS_SECONDARY = 5;
public static final int DEMSRC_MI_TRANSBASIN = 6;
public static final int DEMSRC_CARRIER = 7;
public static final int DEMSRC_USER = 8;

/**
Daily diversion ID.
*/
protected String	_cdividy;

/**
Diversion capacity
*/
protected double	_divcap;

/**
User name
*/
protected String 	_username;

/**
data type switch
*/
protected int 		_idvcom;

/**
System efficiency switch
*/
protected double 	_divefc;

/**
Efficiency % by month.  The efficiencies are in the order of the calendar for
the data set.  Therefore, for proper display, the calendar type must be known.
*/
protected double	_diveff[];

// The following are used only by StateDMI and do not needed to be handled in
// comparison, initialization, etc.
private double []	__calculated_efficiencies = null;
private double []	__calculated_efficiency_stddevs = null;
private double []	__model_efficiencies = null;

/**
irrigated acreage, future
*/
protected double	_area;

/**
use type
*/
protected int		_irturn;

/**
river nodes receiving return flow
*/
protected Vector	_rivret;

/**
Direct diversions rights
*/
protected Vector	_rights;

/**
Acreage source
*/
protected int		_demsrc;

/**
Replacement code
*/
protected int		_ireptype;

/**
Pointer to monthly demand ts.
*/
protected MonthTS 	_demand_MonthTS;

/**
Pointer to monthly demand override ts.
*/
protected MonthTS 	_demand_override_MonthTS;

/**
Pointer to average monthly demand override ts.
*/
protected MonthTS 	_demand_average_MonthTS;

/**
Pointer to daily demand ts.
*/
protected DayTS 	_demand_DayTS;

/**
Pointer to historical monthly diversion ts
*/
protected MonthTS 	_diversion_MonthTS;
private double []	__ddh_monthly = null;	// 12 monthly and annual average
						// over period, used by StateDMI

/**
Pointer to historical daily diversion ts
*/
protected DayTS 	_diversion_DayTS;

/**
Pointer to monthly consumptive water requirement ts.
*/
protected MonthTS 	_cwr_MonthTS;
private double []	__cwr_monthly = null;	// 12 monthly and annual average
						// over period, used by StateDMI

/**
Pointer to daily consumptive water requirement ts.
*/
protected DayTS 	_cwr_DayTS;

/**
Pointer to the StateCU_IrrigationPracticeTS.  This object actually contains
other time series, which can be retrieved for displays.
*/
protected StateCU_IrrigationPracticeTS _ipy_YearTS;

/**
Soil available water content, from StateCU file.
*/
protected double _awc;

/**
Reference to spatial data for this diversion -- currently NOT cloned.
*/
protected GeoRecord _georecord;

/**
Vector of parcel data, in particular to allow StateDMI to detect when a
diverion had no data.
*/
protected Vector _parcel_Vector = new Vector();

// Collections are set up to be specified by year, although currently for
// diversions collections are always the same for the full period.

/**
Types of collections.  An aggregate merges the water rights whereas
a system keeps all the water rights but just has one ID.  See email from Erin
Wilson 2004-09-01, to reiterate current modeling procedures:
<pre>
1)  Multistructure should be used to represent two or more structures
that divert from DIFFERENT TRIBUTARIES to serve the same demand
(irrigated acreage or M&I demand).  In the Historic model used to
estimate Baseflows, the historic diversions need to be represented on
the correct tributary, so all structures are in the network.  Average
efficiencies need to be set for these structures, since IWR has been
assigned to only one structure.  In Baseline and Calculated mode, the
multistruct(x,x) command will assign all demand to the primary structure
and zero out the demand for the secondary structures.  Water rights will
continue to be assigned to each individual structure, and operating
rules need to be included to allow the model to divert from the
secondary structure location (under their water right) to meet the
primary structure demand.

2)  Divsystems should be used to represents two or more structures with
intermingled lands and/or diversions that divert from the SAME
TRIBUTARY.  Only the primary structure should be included in the
network.  The Divsystem(x,x) command will combine historic diversions,
capacities, and acreages for use in the Historic model and to create
Baseflows.  Water rights for all structures will be assigned explicitely
to the primary structure.  No operating rules or set efficiency commands
are required.

3)  Aggregates.  The only difference between Divsystems and Aggregates
is that the water rights are not necessarily assigned explicitely, but
are generally grouped into water rights classes.
</pre>
*/
public static String COLLECTION_TYPE_AGGREGATE = "Aggregate";
public static String COLLECTION_TYPE_SYSTEM = "System";
public static String COLLECTION_TYPE_MULTISTRUCT = "MultiStruct";

private String __collection_type = StateMod_Util.MISSING_STRING;

private String __collection_part_type = "Ditch";
					// Used by DMI software - currently no
					// options.
private Vector __collection_Vector = null;
					// The identifiers for data that are
					// collected - null if not a collection
					// location.  This is actually a Vector
					// of Vector's where the
					// __collection_year is the first
					// dimension.  This is ugly but need to
					// use the code to see if it can be
					// made cleaner.

private int [] __collection_year = null;
					// An array of years that correspond to
					// the aggregate/system.  Ditches
					// currently only have one year.

/**
Construct a new diversion and assign data to reasonable defaults.
*/
public StateMod_Diversion()
{	super();
	initialize ( true );
}

/**
Construct a new diversion.
@param initialize_defaults If true, assign data to reasonable defaults.
If false, all data are set to missing.
*/
public StateMod_Diversion ( boolean initialize_defaults )
{	super();
	initialize ( initialize_defaults );
}

/**
Copy constructor.
@param deep_copy If true, make a deep copy including secondary vectors of data.
Currently only false is recognized, in which primitive data are copied.  This is
suitable to allow the StateMod_Diversion_JFrame class to know when changes have
been made to data on the main screen.
*/
public StateMod_Diversion ( StateMod_Diversion div, boolean deep_copy )
{	this();
	// Base class...
	// REVISIT
	// Local data members...
	_cdividy = div._cdividy;
	_divcap = div._divcap;
	_username = div._username;
	_idvcom = div._idvcom;
	_divefc = div._divefc;
	for ( int i = 0; i < 12; i++ ) {
		_diveff[i] = div._diveff[i];
	}
	_area = div._area;
	_irturn = div._irturn;
	_rivret = div._rivret;
	_rights = div._rights;
	_demsrc = div._demsrc;
	_ireptype = div._ireptype;
	// For time series, the references are pointed to the original but
	// data are not copied.
	_demand_MonthTS = div._demand_MonthTS;
	_demand_override_MonthTS = div._demand_override_MonthTS;
	_demand_average_MonthTS = div._demand_average_MonthTS;
	_demand_DayTS = div._demand_DayTS;
	_diversion_MonthTS = div._diversion_MonthTS;
	_diversion_DayTS = div._diversion_DayTS;
	_cwr_MonthTS = div._cwr_MonthTS;
	_cwr_DayTS = div._cwr_DayTS;
	_ipy_YearTS = div._ipy_YearTS;
	_awc = div._awc;
	_georecord = div._georecord;
}

/**
Accepts any changes made inside of a GUI to this object.
*/
public void acceptChanges() {
	_isClone = false;
	_original = null;
}

/**
Add return flow node to the vector of return flow nodes.
@param rivret return flow
*/
public void addReturnFlow(StateMod_ReturnFlow rivret)
{	if (rivret == null) {
		return;
	}
	_rivret.addElement(rivret);
	setDirty(true);
	if ( !_isClone && _dataset != null ) {
		_dataset.setDirty(_dataset.COMP_DIVERSION_STATIONS, true);
	}
}

/**
Adds a right for the diversion.
*/
public void addRight ( StateMod_DiversionRight right )
{	if ( right != null ) {
		_rights.addElement ( right );
	}
	// No need to set dirty because right is not saved in station file.
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
	StateMod_Diversion d = (StateMod_Diversion)super.clone();
	d._isClone = true;

	// The following are not cloned because there is no need to.  
	// The cloned values are only used for comparing between the 
	// values that can be changed in a single GUI.  The following
	// Vectors' data have their changes committed in other GUIs.	
	d._rivret = _rivret;
	d._rights = _rights;

	if (_diveff == null) {
		d._diveff = null;
	}
	else {
		d._diveff = (double[])_diveff.clone();	
	}
	return d;
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

	StateMod_Diversion d = (StateMod_Diversion)o;
	boolean emptyCdividy = false;
	boolean emptyDCdividy = false;
	if (_cdividy.equals("")) {
		emptyCdividy = true;
	}
	if (d._cdividy.equals("")) {
		emptyDCdividy = true;
	}

	if (emptyCdividy && emptyDCdividy) {}
	else if (emptyCdividy) {
		res = d._cdividy.compareTo("0");
		if (res != 0) {
			return res;
		}
	}
	else if (emptyDCdividy) {
		res = _cdividy.compareTo("0");
		if (res != 0) {
			return res;
		}
	}		
	else {
		res = _cdividy.compareTo(d._cdividy);
		if (res != 0) {
			return res;
		}
	}

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

	return 0;
}

/**
Given a vector containing all the diversions and another vector containing
all the rights, creates a system of pointers to link the diversions to their
associated rights.
This routines doesn't add an element to an array.  The array
already exists, we are just connecting next and previous pointers.
@param diversions all diversions
@param rights all rights
*/
public static void connectAllRights ( Vector diversions, Vector rights )
{	if ((diversions == null)||(rights == null)) {
		return;
	}
	int num_divs = diversions.size();
	
	StateMod_Diversion div;
	for (int i = 0; i < num_divs; i++) {
		div = (StateMod_Diversion)diversions.elementAt(i);
		if (div == null) {
			continue;
		}
		div.connectRights(rights);
	}
	div = null;
}

/**
Given a vector containing all the diversion objects and lists of time series
objects, sets pointers from the diversions to the associated time series.
@param diversions All diversions.
@param diversion_MonthTS Vector of monthly historical diversion time series,
or null.
@param diversion_DayTS Vector of daily historical diversion time series, or
null.
@param demand_MonthTS Vector of monthly demand time series, or null.
@param demand_override_MonthTS Vector of monthly override demand time series,
or null.
@param demand_average_MonthTS Vector of average monthly override
demand time series, or null.
@param ipy_YearTS Vector of yearly irrigation practice objects, containing 
time series of efficiencies, etc. (StateCU_IrrigationPracticeTS), or null
@param cwr_MonthTS Vector of monthly consumptive water requirement time series,
or null.
@param cwr_DayTS Vector of daily consumptive water requirement time series,
or null.
*/
public static void connectAllTS (	Vector diversions,
					Vector diversion_MonthTS,
					Vector diversion_DayTS,
					Vector demand_MonthTS, 
					Vector demand_override_MonthTS, 
					Vector demand_average_MonthTS, 
					Vector demand_DayTS,
					Vector ipy_YearTS,
					Vector cwr_MonthTS,
					Vector cwr_DayTS )
{	if (diversions == null) {
		return;
	}
	int i;
	int num_divs = diversions.size();
	
	StateMod_Diversion div;
	for (i = 0; i < num_divs; i++) {
		div = (StateMod_Diversion)diversions.elementAt(i);
		if (div == null) {
			continue;
		}
		if ( diversion_MonthTS != null ) {
			div.connectDiversionMonthTS ( diversion_MonthTS );
		}
		if ( diversion_DayTS != null ) {
			div.connectDiversionDayTS ( diversion_DayTS );
		}
		if ( demand_MonthTS != null) {
			div.connectDemandMonthTS ( demand_MonthTS );
		}
		if ( demand_override_MonthTS != null) {
			div.connectDemandOverrideMonthTS (
				demand_override_MonthTS );
		}
		if ( demand_average_MonthTS != null) {
			div.connectDemandAverageMonthTS(demand_average_MonthTS);
		}
		if ( demand_DayTS != null ) {
			div.connectDemandDayTS ( demand_DayTS );
		}
		if ( ipy_YearTS != null ) {
			div.connectIrrigationPracticeYearTS ( ipy_YearTS );
		}
		if ( cwr_MonthTS != null ) {
			div.connectCWRMonthTS ( cwr_MonthTS );
		}
		if ( cwr_DayTS != null ) {
			div.connectCWRDayTS ( cwr_DayTS );
		}
	}
	div = null;
}

/**
Connect daily CWR series pointer.  The connection is made using the
value of "cdividy" for the diversion.
@param tslist demand time series
*/
public void connectCWRDayTS ( Vector tslist )
{	if ( tslist == null) {
		return;
	}
	_cwr_DayTS = null;
	int num_TS = tslist.size();

	DayTS ts;
	for (int i = 0; i < num_TS; i++) {
		ts = (DayTS)tslist.elementAt(i);
		if ( ts == null ) {
			return;
		}
		if (_cdividy.equalsIgnoreCase(ts.getLocation())) {
			_cwr_DayTS = ts;
			ts.setDescription(getName());
			break;
		}
	}
}

/**
Connect monthly CWR time series pointer.  The time series name is set to
that of the diversion.
@param tslist Time series list.
*/
public void connectCWRMonthTS ( Vector tslist )
{	if ( tslist == null ) {
		return;
	}
	int num_TS = tslist.size();

	MonthTS ts;
	_cwr_MonthTS = null;
	for ( int i = 0; i < num_TS; i++ ) {
		ts = (MonthTS)tslist.elementAt(i);
		if ( ts == null ) {
			continue;
		}
		if ( _id.equalsIgnoreCase(ts.getLocation()) ) {
			_cwr_MonthTS = ts;
			ts.setDescription ( getName() );
			break;
		}
	}
}

/**
Connect average monthly demand time series pointer.  The time series
name is set to that of the diversion.
@param tslist Time series list.
*/
public void connectDemandAverageMonthTS ( Vector tslist )
{	if ( tslist == null ) {
		return;
	}
	int num_TS = tslist.size();

	MonthTS ts;
	_demand_average_MonthTS = null;
	for ( int i = 0; i < num_TS; i++ ) {
		ts = (MonthTS)tslist.elementAt(i);
		if ( ts == null ) {
			continue;
		}
		if ( _id.equalsIgnoreCase(ts.getLocation()) ) {
			_demand_average_MonthTS = ts;
			ts.setDescription ( getName() );
			break;
		}
	}
}

/**
Connect daily demand time series pointer.  The connection is made using the
value of "cdividy" for the diversion.
@param tslist demand time series
*/
public void connectDemandDayTS ( Vector tslist )
{	if ( tslist == null) {
		return;
	}
	_demand_DayTS = null;
	int num_TS = tslist.size();

	DayTS ts;
	for (int i = 0; i < num_TS; i++) {
		ts = (DayTS)tslist.elementAt(i);
		if ( ts == null ) {
			return;
		}
		if (_cdividy.equalsIgnoreCase(ts.getLocation())) {
			_demand_DayTS = ts;
			ts.setDescription(getName());
			break;
		}
	}
}

/**
Connect monthly demand time series pointer.  The time series name is set to
that of the diversion.
@param tslist Time series list.
*/
public void connectDemandMonthTS ( Vector tslist )
{	if ( tslist == null ) {
		return;
	}
	int num_TS = tslist.size();

	MonthTS ts;
	_demand_MonthTS = null;
	for ( int i = 0; i < num_TS; i++ ) {
		ts = (MonthTS)tslist.elementAt(i);
		if ( ts == null ) {
			continue;
		}
		if ( _id.equalsIgnoreCase(ts.getLocation()) ) {
			_demand_MonthTS = ts;
			ts.setDescription ( getName() );
			break;
		}
	}
}

/**
Connect monthly demand override time series pointer.  The time series name is
set to that of the diversion.
@param tslist Time series list.
*/
public void connectDemandOverrideMonthTS ( Vector tslist )
{	if ( tslist == null ) {
		return;
	}
	int num_TS = tslist.size();

	MonthTS ts;
	_demand_override_MonthTS = null;
	for ( int i = 0; i < num_TS; i++ ) {
		ts = (MonthTS)tslist.elementAt(i);
		if ( ts == null ) {
			continue;
		}
		if ( _id.equalsIgnoreCase(ts.getLocation()) ) {
			_demand_override_MonthTS = ts;
			ts.setDescription ( getName() );
			break;
		}
	}
}

/**
Connect historical daily diversion time series pointer.  
@param tslist Vector of historical daily diversion time series.
*/
public void connectDiversionDayTS ( Vector tslist )
{	if ( tslist == null ) {
		return;
	}
	int num_TS = tslist.size();

	DayTS ts;
	_diversion_DayTS = null;
	for ( int i = 0; i < num_TS; i++ ) {
		ts = (DayTS)tslist.elementAt(i);
		if ( ts == null ) {
			continue;
		}
		if ( _id.equalsIgnoreCase(ts.getLocation())) {
			_diversion_DayTS = ts;
			ts.setDescription(getName());
			break;
		}
	}
}

/**
Connect historical monthly time series pointer.  
@param tslist Vector of historical monthly time series.
*/
public void connectDiversionMonthTS ( Vector tslist )
{	if ( tslist == null ) {
		return;
	}
	int num_TS = tslist.size();

	MonthTS ts;
	_diversion_MonthTS = null;
	for ( int i = 0; i < num_TS; i++ ) {
		ts = (MonthTS)tslist.elementAt(i);
		if ( ts == null ) {
			continue;
		}
		if ( _id.equalsIgnoreCase(ts.getLocation())) {
			_diversion_MonthTS = ts;
			ts.setDescription(getName());
			break;
		}
	}
}

/**
Connect the irrigation practice TS object.
@param tslist Time series list.
*/
public void connectIrrigationPracticeYearTS ( Vector tslist )
{	if ( tslist == null ) {
		return;
	}
	int num_TS = tslist.size();

	_ipy_YearTS = null;
	StateCU_IrrigationPracticeTS ipy_YearTS;
	for ( int i = 0; i < num_TS; i++ ) {
		ipy_YearTS = (StateCU_IrrigationPracticeTS)tslist.elementAt(i);
		if ( ipy_YearTS == null ) {
			continue;
		}
		if ( _id.equalsIgnoreCase(ipy_YearTS.getID()) ) {
			_ipy_YearTS = ipy_YearTS;
			break;
		}
	}
}

/**
Connect the diversion rights with this diversion, comparing the "cgoto" for the
right to the diversion identifier.  Multiple rights may be associated with a
diversion.
@param rights all rights.
*/
public void connectRights ( Vector rights )
{	if (rights == null) {
		return;
	}
	int num_rights = rights.size();

	StateMod_DiversionRight right;
	for ( int i = 0; i < num_rights; i++) {
		right = (StateMod_DiversionRight)rights.elementAt(i);
		if (right == null) {
			continue;
		}
		if ( _id.equalsIgnoreCase(right.getCgoto()) ) {
			_rights.addElement ( right );
		}
	}
	right = null;
}

/**
Creates a copy of the object for later use in checking to see if it was 
changed in a GUI.
*/
public void createBackup() {
	_original = clone();
	((StateMod_Diversion)_original)._isClone = false;
	_isClone = true;
}

/**
Delete return flow node at a specified index.
@param index index desired to delete
*/
public void deleteReturnFlowAt(int index)
{	_rivret.removeElementAt(index);
	setDirty(true);
	if ( !_isClone && _dataset != null ) {
		_dataset.setDirty(_dataset.COMP_DIVERSION_STATIONS, true);
	}
}

// REVISIT - in the GUI need to decide if the right is actually removed from
// the main list
/**
Remove right from list.  A comparison on the ID is made.
@param right Right to remove.  Note that the right is only removed from the
list for this diversion and must also be removed from the main diversion right
list.
*/
public void disconnectRight ( StateMod_DiversionRight right )
{	if (right == null) {
		return;
	}
	int size = _rights.size();
	StateMod_DiversionRight right2;
	// Assume that more than on instance can exist, even though this is
	// not allowed...
	for ( int i = 0; i < size; i++ ) {
		right2 = (StateMod_DiversionRight)_rights.elementAt(i);
		if ( right2.getID().equalsIgnoreCase(right.getID()) ) {
			_rights.removeElementAt(i);
		}
	}
}

/**
Disconnect all rights.
*/
public void disconnectRights ()
{	_rights.removeAllElements();
}

/**
Clean up for garbage collection.
*/
protected void finalize()
throws Throwable {
	_cdividy = null;
	_username = null;
	_rivret = null;
	_rights = null;
	_demand_DayTS = null;
	_diversion_MonthTS = null;
	_demand_MonthTS = null;
	_georecord = null;
	super.finalize();
}

/**
Return the irrigated acreage.
*/
public double getArea() {
	return _area;
}

/**
Return the average monthly CWR (12 monthly values + annual average), for the
data set calendar type.  This is ONLY used by StateDMI and does not need
to be considered in comparison code.
*/
public double [] getAverageMonthlyCWR ()
{	return __cwr_monthly;
}

/**
Return the average monthly historical diversion (12 monthly values + annual
average), for the data set calendar type.  This is ONLY used by StateDMI and
does not need to be considered in comparison code.
*/
public double [] getAverageMonthlyHistoricalDiversions ()
{	return __ddh_monthly;
}

/**
Return the AWC (available water capacity).
*/
public double getAWC() {
	return _awc;
}

/**
Return the average monthly efficiencies calculated from CWR and historical
diversions (12 monthly values + annual average), for the
data set calendar type.  This is ONLY used by StateDMI and does not need
to be considered in comparison code.
*/
public double [] getCalculatedEfficiencies()
{	return __calculated_efficiencies;
}

/**
Return the standard deviation of monthly efficiencies calculated from CWR and
historical diversions (12 monthly values + annual average), for the
data set calendar type.  This is ONLY used by StateDMI and does not need
to be considered in comparison code.
*/
public double [] getCalculatedEfficiencyStddevs()
{	return __calculated_efficiency_stddevs;
}

/**
Return the daily id.
*/
public String getCdividy() {
	return _cdividy;
}

/**
Return the collection part ID list for the specific year.  For ditches, only one
aggregate/system list is currently supported so the same information is returned
regardless of the year value.
@return the list of collection part IDS, or null if not defined.
*/
public Vector getCollectionPartIDs ( int year )
{	if ( __collection_Vector.size() == 0 ) {
		return null;
	}
	//if ( __collection_part_type.equalsIgnoreCase("Ditch") ) {
		// The list of part IDs will be the first and only list...
		return (Vector)__collection_Vector.elementAt(0);
	//}
	/* Not supported
	else if ( __collection_part_type.equalsIgnoreCase("Parcel") ) {
		// The list of part IDs needs to match the year.
		for ( int i = 0; i < __collection_year.length; i++ ) {
			if ( year == __collection_year[i] ) {
				return (Vector)__collection_Vector.elementAt(i);
			}
		}
	}
	return null;
	*/
}

/**
Returns the collection part type ("Ditch").
@return the collection part type ("Ditch").
*/
public String getCollectionPartType() {
	return __collection_part_type;
}

/**
Return the collection type, "Aggregate", "System", or "MultiStruct".
@return the collection type, "Aggregate", "System", or "MultiStruct".
*/
public String getCollectionType()
{	return __collection_type;
}

/**
Returns the collection years.
@return the collection years.
*/
public int[] getCollectionYears() {
	return __collection_year;
}

/**
Get daily consumptive water requirement time series.
*/
public DayTS getConsumptiveWaterRequirementDayTS() {
	return _cwr_DayTS;
}

/**
Get monthly consumptive water requirement time series.
*/
public MonthTS getConsumptiveWaterRequirementMonthTS() {
	return _cwr_MonthTS;
}

/**
Get average monthly demand time series.
*/
public MonthTS getDemandAverageMonthTS() {
	return _demand_average_MonthTS;
}

/**
Get daily demand time series.
*/
public DayTS getDemandDayTS() {
	return _demand_DayTS;
}

/**
Get monthly demand time series.
*/
public MonthTS getDemandMonthTS() {
	return _demand_MonthTS;
}

/**
Get monthly demand override time series.
*/
public MonthTS getDemandOverrideMonthTS() {
	return _demand_override_MonthTS;
}

/**
Return the demand source.
*/
public int getDemsrc() {
	return _demsrc;
}

/**
Return a list of demand source option strings, for use in GUIs.
The options are of the form "1" if include_notes is false and
"1 - Irrigated acres from GIS", if include_notes is true.
@return a list of demand source option strings, for use in GUIs.
@param include_notes Indicate whether notes should be included.
*/
public static Vector getDemsrcChoices ( boolean include_notes )
{	Vector v = new Vector(8);
	v.addElement ( "0 - Irrigated acres source unknown" );
	v.addElement ( "1 - Irrigated acres from GIS" );
	v.addElement ( "2 - Irrigated acres from structure file (tia)" );
	v.addElement (
	"3 - Irr. acr. from GIS, primary comp. served by mult. structs" );
	v.addElement ( "4 - Same as 3 but data from struct. file (tia)" );
	v.addElement (
	"5 - Irr. acr. from GIS, secondary comp. served by mult. structs" );
	v.addElement ( "6 - Municipal, industrial, or transmountain structure");
	v.addElement ( "7 - Carrier structure (no irrigated acres)" );
	v.addElement ( "8 - Irrigated acres provided by user" );
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
Return the default demand source choice.  This can be used by GUI code
to pick a default for a new diversion.
@return the default demand source choice.
*/
public static String getDemsrcDefault ( boolean include_notes )
{	if ( include_notes ) {
		return "0 - Irrigated acres source unknown";
	}
	else {	return "0";
	}
}

/**
Return the diversion capacity.
*/
public double getDivcap() {
	return _divcap;
}

/**
Return the system efficiency switch.
*/
public double getDivefc() {
	return _divefc;
}

/**
Return the system efficiency for the specified month index.
The efficiencies are stored in the order of the year for the data set.  For
example, if water years are used, the first efficiency will be for October.  For
calendar year, the first efficiency will be for January.
@param index 0 based monthly index
*/
public double getDiveff(int index) {
	return _diveff[index];
}

/**
Return the system efficiency for the specified month index, where the month
is always for calendar year (0=January).
@param index 0-based monthly index (0=January).
@param yeartype The yeartype for the diversion stations file (consistent with
the control file for a full data set).  Recognized values are:
<ol>
<li>	"Calendar", "CYR" (Jan - Dec).</li>
<li>	"Irrigation", "IYR" (Oct - Sep).</li>
<li>	"Water", "WYR" (Nov - Oct).</li>
</ol>
*/
public double getDiveff ( int index, String yeartype )
{	// Adjust the index if necessary based on the year type...
	if ( yeartype == null ) {
		// Assume calendar.
	}
	else if ( yeartype.equalsIgnoreCase("Water") ||
		yeartype.equalsIgnoreCase("WYR") ) {
		index = TimeUtil.convertCalendarMonthToCustomMonth (
				(index + 1), 10 ) - 1;
	}
	else if ( yeartype.equalsIgnoreCase("Irrigation") ||
		yeartype.equalsIgnoreCase("IYR") ) {
		index = TimeUtil.convertCalendarMonthToCustomMonth (
				(index + 1), 11 ) - 1;
	}
	return _diveff[index];
}

/**
Get daily historical diversion time series.
*/
public DayTS getDiversionDayTS() {
	return _diversion_DayTS;
}

/**
Get monthly historical diversion time series.
*/
public MonthTS getDiversionMonthTS() {
	return _diversion_MonthTS;
}

/**
Get the geographical data associated with the diversion.
@return the GeoRecord for the diversion.
*/
public GeoRecord getGeoRecord() {
	return _georecord;
}

/**
Return a list of on/off switch option strings, for use in GUIs.
The options are of the form "0" if include_notes is false and
"0 - Off", if include_notes is true.
@return a list of on/off switch option strings, for use in GUIs.
@param include_notes Indicate whether notes should be added after the parameter
values.
*/
public static Vector getIdivswChoices ( boolean include_notes )
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
public static String getIdivswDefault ( boolean include_notes )
{	// Make this aggree with the above method...
	if ( include_notes ) {
		return ( "1 - On" );
	}
	else {	return "1";
	}
}

/**
Return the data type switch.
*/
public int getIdvcom() {
	return _idvcom;
}

/**
Return a list of monthly demand type option strings, for use in GUIs.
The options are of the form "1" if include_notes is false and
"1 - Monthly total demand", if include_notes is true.
@return a list of monthly demand type option strings, for use in GUIs.
@param include_notes Indicate whether notes should be included.
*/
public static Vector getIdvcomChoices ( boolean include_notes )
{	Vector v = new Vector(5);
	v.addElement ( "1 - Monthly total demand" );
	v.addElement ( "2 - Annual total demand" );
	v.addElement ( "3 - Monthly irrigation water requirement" );
	v.addElement ( "4 - Annual irrigation water requirement" );
	v.addElement ( "5 - Estimate to be zero" );
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
Return the default monthly demand type choice.  This can be used by GUI code
to pick a default for a new diversion.
@return the default monthly demand type choice.
*/
public static String getIdvcomDefault ( boolean include_notes )
{	if ( include_notes ) {
		return "1 - Monthly total demand";
	}
	else {	return "1";
	}
}

/**
Return the replacement code.
*/
public int getIreptype() {
	return _ireptype;
}

/**
Return a list of reservoir replacement option strings, for use in GUIs.
The options are of the form "0" if include_notes is false and
"0 - Do not provide replacement res. benefits", if include_notes is true.
@return a list of reservoir replacement option strings, for use in GUIs.
@param include_notes Indicate whether notes should be included.
*/
public static Vector getIreptypeChoices ( boolean include_notes )
{	Vector v = new Vector(3);
	v.addElement ( "0 - Do not provide replacement res. benefits" );
	v.addElement ( "1 - Provide 100% replacement" );
	v.addElement ( "-1 - Provide depletion replacement" );
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
Return the default reservoir replacement choice.  This can be used by GUI code
to pick a default for a new diversion.
@return the default reservoir replacement choice.
*/
public static String getIreptypeDefault ( boolean include_notes )
{	if ( include_notes ) {
		return "-1 - Provide depletion replacement";
	}
	else {	return "-1";
	}
}

/**
Get yearly irrigation practice time series.
*/
public StateCU_IrrigationPracticeTS getIrrigationPracticeYearTS() {
	return _ipy_YearTS;
}

/**
Return the use type.
*/
public int getIrturn() {
	return _irturn;
}

/**
Return a list of use type option strings, for use in GUIs.
The options are of the form "1" if include_notes is false and
"1 - Irrigation", if include_notes is true.
@return a list of use type option strings, for use in GUIs.
@param include_notes Indicate whether notes should be included.
*/
public static Vector getIrturnChoices ( boolean include_notes )
{	Vector v = new Vector(6);
	v.addElement ( "0 - Storage" );
	v.addElement ( "1 - Irrigation" );
	v.addElement ( "2 - Municipal" );
	v.addElement ( "3 - N/A" );
	v.addElement ( "4 - Transmountain" );
	v.addElement ( "5 - Other" );
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
Return the default use type choice.  This can be used by GUI code
to pick a default for a new diversion.
@return the default use type choice.
*/
public static String getIrturnDefault ( boolean include_notes )
{	if ( include_notes ) {
		return "1 - Irrigation";
	}
	else {	return "1";
	}
}

/**
Get the last right associated with diversion.
*/
public StateMod_DiversionRight getLastRight()
{	if ( (_rights == null) || (_rights.size() == 0) ) {
		return null;
	}
	return (StateMod_DiversionRight)_rights.elementAt(_rights.size() - 1);
}

/**
Return the average monthly efficiencies to be used for modeling (
12 monthly values + annual average), for the
data set calendar type.  This is ONLY used by StateDMI and does not need
to be considered in comparison code.
*/
public double [] getModelEfficiencies()
{	return __model_efficiencies;
}

/**
Return the number of return flow locations.
*/
public int getNrtn() {
	return _rivret.size();
}

/**
Return the Vector of parcels.
@return the Vector of parcels.
*/
public Vector getParcels()
{	return _parcel_Vector;
}

/**
Return the return flow at a particular index.
@param index index desired to retrieve.
*/
public StateMod_ReturnFlow getReturnFlow(int index)
{	return ((StateMod_ReturnFlow)_rivret.elementAt(index));
}

/**
Retrieve the return flow vector.
*/
public Vector getReturnFlows()
{	return _rivret;
}

/**
Return the right associated with the given index.  If index
number of rights don't exist, null will be returned.
@param index desired right index
*/
public StateMod_DiversionRight getRight(int index)
{	if ( (index < 0) || (index >= _rights.size()) ) {
		return null;
	}
	else {	return (StateMod_DiversionRight)_rights.elementAt(index);
	}
}

/**
Return the rights Vector.
@return the rights Vector.
*/
public Vector getRights()
{	return _rights;
}

/**
Return the user name.
*/
public String getUsername() {
	return _username;
}

/**
Initialize data.
Sets the smdata_type to _dataset.COMP_DIVERSION_STATIONS.
@param initialize_defaults If true, assign data to reasonable defaults.
If false, all data are set to missing.
*/
private void initialize ( boolean initialize_defaults )
{	_smdata_type = _dataset.COMP_DIVERSION_STATIONS;
	if ( initialize_defaults ) {
		_divefc	= -60.0;	// Ray Bennett, Ray Alvarado,
					// 2003-10-07 progress mtg.
		_diveff = new double[12];
		for ( int i = 0; i < 12; i++ ) {
			_diveff[i] = 60.0;	// See above
		}
		_username = "";
		_cdividy = "0";		// Use the average monthly TS for
					// daily TS
		_divcap = 0;
		_idvcom = 1;
		_area	= 0;
		_irturn	= 1;
		_demsrc	= DEMSRC_UNKNOWN;
		_ireptype = -1;	// Provide depletion replacement
	}
	else {	_divefc	= StateMod_Util.MISSING_DOUBLE;
		_diveff = new double[12];
		for ( int i = 0; i < 12; i++ ) {
			_diveff[i] = StateMod_Util.MISSING_DOUBLE;
		}
		_username = StateMod_Util.MISSING_STRING;
		_cdividy = StateMod_Util.MISSING_STRING;
		_divcap = StateMod_Util.MISSING_INT;
		_idvcom = StateMod_Util.MISSING_INT;
		_area	= StateMod_Util.MISSING_DOUBLE;
		_irturn	= StateMod_Util.MISSING_INT;
		_demsrc	= StateMod_Util.MISSING_INT;
		_ireptype = StateMod_Util.MISSING_INT;
	}
	_rivret = new Vector();
	_rights = new Vector();
	_diversion_MonthTS = null;
	_diversion_DayTS = null;
	_demand_MonthTS = null;
	_demand_override_MonthTS = null;
	_demand_average_MonthTS = null;
	_demand_DayTS = null;
	_ipy_YearTS = null;
	_cwr_MonthTS = null;
	_cwr_DayTS = null;
	_georecord = null;
}

/**
Indicate whether the diversion is a collection (an aggregate or system).
@return true if the diversion is an aggregate or system.
*/
public boolean isCollection()
{	if ( __collection_Vector == null ) {
		return false;
	}
	else {	return true;
	}
}

/**
Indicate whether a file is a StateMod diversion file.  Currently the only
check that is done is to see if the file name ends in "dds".
@param filename File name.
@return true if the file appears to be a diversion file, false if not.
*/
public static boolean isStateModDiversionFile ( String filename )
{	if ( StringUtil.endsWithIgnoreCase(filename,".dds") ) {
		return true;
	}
	return false;
}

/**
Read diversion information in and store in a java vector.
The new diversions are added to the end of the previously stored diversions.
@param filename filename containing diversion information
@throws Exception if an error occurs
*/
public static Vector readStateModFile(String filename)
throws Exception
{	String routine = "StateMod_Diversion.readStateModFile";
	String iline = null;
	Vector v = new Vector(9);
	Vector theDiversions = new Vector();
	int i;
	int linecount = 0;
	String s = null;
	
	int format_0[] = {	StringUtil.TYPE_STRING,
				StringUtil.TYPE_STRING,
				StringUtil.TYPE_STRING,
				StringUtil.TYPE_INTEGER,
				StringUtil.TYPE_DOUBLE,
				StringUtil.TYPE_INTEGER,
				StringUtil.TYPE_INTEGER,
				StringUtil.TYPE_STRING,
				StringUtil.TYPE_STRING };
	int format_0w[] = {	12,
				24,
				12,
				8,
				8,
				8,
				8,
				1,
				12 };
	int format_1[] = {	StringUtil.TYPE_STRING,
				StringUtil.TYPE_STRING,
				StringUtil.TYPE_STRING,
				StringUtil.TYPE_INTEGER,
				StringUtil.TYPE_INTEGER,
				StringUtil.TYPE_DOUBLE,
				StringUtil.TYPE_DOUBLE,
				StringUtil.TYPE_INTEGER,
				StringUtil.TYPE_INTEGER };
	int format_1w[] = {	12,
				24,
				12,
				8,
				8,
				8,
				8,
				8,
				8 };
	int format_2[] = {	StringUtil.TYPE_STRING,
				StringUtil.TYPE_STRING,
				StringUtil.TYPE_DOUBLE,
				StringUtil.TYPE_INTEGER };
	int format_2w[] = {	36,
				12,
				8,
				8 };

	StateMod_Diversion aDiversion = null;
	StateMod_ReturnFlow aReturnNode = null;
	BufferedReader in = null;

	Message.printStatus(1, routine, "Reading diversion file: " + filename);
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
			aDiversion = new StateMod_Diversion();

			// line 1
			if (Message.isDebugOn) {
				Message.printDebug(50, routine, 
				"line 1: " + iline);
			}
			StringUtil.fixedRead(iline, format_0, format_0w, v);
			aDiversion.setID(((String)v.elementAt(0)).trim()); 
			aDiversion.setName(((String)v.elementAt(1)).trim()); 
			aDiversion.setCgoto(((String)v.elementAt(2)).trim());
			aDiversion.setSwitch((Integer)v.elementAt(3));
			aDiversion.setDivcap((Double)v.elementAt(4));
			aDiversion.setIreptype(((Integer)v.elementAt(6)));
			aDiversion.setCdividy(((String)v.elementAt(8)).trim());

			// line 2
			iline = in.readLine();
			++linecount;
			if (Message.isDebugOn) {
				Message.printDebug(50, routine, 
				"line 2: " + iline);
			}
			StringUtil.fixedRead(iline, format_1, format_1w, v);
			aDiversion.setUsername(((String)v.elementAt(1)).trim());
			aDiversion.setIdvcom(((Integer)v.elementAt(3)));
			int nrtn =((Integer)v.elementAt(4)).intValue();
			aDiversion.setDivefc(((Double)v.elementAt(5)));
			aDiversion.setArea(((Double)v.elementAt(6)));
			aDiversion.setIrturn(((Integer)v.elementAt(7)));
			aDiversion.setDemsrc(((Integer)v.elementAt(8)));

			// get the efficiency information
			if (aDiversion.getDivefc() < 0) {
				// Negative value indicates monthly efficiencies
				// will follow...
				iline = in.readLine();
				++linecount;
				// Free format...
				StringTokenizer split = 
					new StringTokenizer(iline);
				if (split != null && 
				     split.countTokens ()== 12) { 
					for (i = 0; i < 12; i++) {
						aDiversion.setDiveff(i, 
							split.nextToken());
					}
				}
			}
			else {	// Annual efficiency so set monthly efficiencies
				// to the annual...
				aDiversion.setDiveff(0,aDiversion.getDivefc());
				aDiversion.setDiveff(1,aDiversion.getDivefc());
				aDiversion.setDiveff(2,aDiversion.getDivefc());
				aDiversion.setDiveff(3,aDiversion.getDivefc());
				aDiversion.setDiveff(4,aDiversion.getDivefc());
				aDiversion.setDiveff(5,aDiversion.getDivefc());
				aDiversion.setDiveff(6,aDiversion.getDivefc());
				aDiversion.setDiveff(7,aDiversion.getDivefc());
				aDiversion.setDiveff(8,aDiversion.getDivefc());
				aDiversion.setDiveff(9,aDiversion.getDivefc());
				aDiversion.setDiveff(10,aDiversion.getDivefc());
				aDiversion.setDiveff(11,aDiversion.getDivefc());
			}

			// get the return information
			for (i = 0; i < nrtn; i++) {
				iline = in.readLine();
				++linecount;
				StringUtil.fixedRead(iline, format_2,
					format_2w, v);
				aReturnNode = new StateMod_ReturnFlow(
				StateMod_DataSet.COMP_DIVERSION_STATIONS);
				s = ((String)v.elementAt(1)).trim();
				if (s.length() <= 0) {
					aReturnNode.setCrtnid(
					((String)v.elementAt(0)).trim());
					Message.printWarning(2, routine, 
						"Return node for structure \""
						+ aDiversion.getID()
						+ "\" is blank. ");
				}
				else {	
					aReturnNode.setCrtnid(s);
				}

				aReturnNode.setPcttot(
					((Double)v.elementAt(2)));
				aReturnNode.setIrtndl(
					((Integer)v.elementAt(3)));
				aDiversion.addReturnFlow(aReturnNode);
			}

			// Set the diversion to not dirty because it was just
			// initialized...

			aDiversion.setDirty ( false );

			// add the diversion to the vector of diversions
			theDiversions.addElement(aDiversion);
		}
	} 
	catch (Exception e) {
		routine = null;
		v = null;
		s = null;
		format_0 = null;
		format_0w = null;
		format_1 = null;
		format_1w = null;
		format_2 = null;
		format_2w = null;
		aDiversion = null;
		aReturnNode = null;
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
	format_0 = null;
	format_0w = null;
	format_1 = null;
	format_1w = null;
	format_2 = null;
	format_2w = null;
	aDiversion = null;
	aReturnNode = null;
	if (in != null) {
		in.close();
	}
	in = null;
	return theDiversions;
}

/**
Set the irrigated acreage.
@param area acreage.
*/
public void setArea(double area) {
	if (_area != area) {
		_area = area;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Set the irrigated acreage.
@param area acreage.
*/
public void setArea(Double area) {
	setArea(area.doubleValue());
}

/**
Set the irrigated acreage.
@param area acreage.
*/
public void setArea(String area) {
	if (area == null) {
		return;
	}
	setArea(StringUtil.atod(area.trim()));
}

/**
Set the average monthly CWR (12 monthly values + annual average), for the
data set calendar type.  This is ONLY used by StateDMI and does not need
to be considered in comparison code.
*/
public void setAverageMonthlyCWR ( double [] cwr_monthly )
{	__cwr_monthly = cwr_monthly;
}

/**
Set the average monthly historical diversions (12 monthly values + annual
average), for the data set calendar type.  This is ONLY used by StateDMI and
does not need to be considered in comparison code.
*/
public void setAverageMonthlyHistoricalDiversions ( double [] ddh_monthly )
{	__ddh_monthly = ddh_monthly;
}

/**
Set the available water capacity.
@param awc available water capacity.
*/
public void setAWC(double awc) {
	if (_awc != awc) {
		_awc = awc;
		setDirty(true);
		if (!_isClone && _dataset != null) {
			_dataset.setDirty(_dataset.COMP_DIVERSION_STATIONS, 
				true);
		}
	}
}

/**
Set the available water capacity.
@param awc available water capacity.
*/
public void setAWC(Double awc) {
	setAWC(awc.doubleValue());
}

/**
Set the available water capacity.
@param awc available water capacity.
*/
public void setAWC(String awc) {
	if (awc == null) {
		return;
	}
	setAWC(StringUtil.atod(awc.trim()));
}

/**
Set the average monthly efficiencies calculated from CWR and historical
diversions (12 monthly values + annual average), for the
data set calendar type.  This is ONLY used by StateDMI and does not need
to be considered in comparison code.
*/
public void setCalculatedEfficiencies ( double [] calculated_efficiencies )
{	__calculated_efficiencies = calculated_efficiencies;
}

/**
Set the standard deviation of monthly efficiencies calculated from CWR and
historical diversions (12 monthly values + annual average), for the
data set calendar type.  This is ONLY used by StateDMI and does not need
to be considered in comparison code.
*/
public void setCalculatedEfficiencyStddevs (
	double [] calculated_efficiency_stddevs )
{	__calculated_efficiency_stddevs = calculated_efficiency_stddevs;
}

/**
Set the daily id.
@param cdividy daily id.
*/
public void setCdividy(String cdividy) {
	if (cdividy == null) {
		return;
	}
	if (!cdividy.equals(_cdividy)) {
		_cdividy = cdividy;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Return a list of daily ID option strings, for use in GUIs.
The options are of the form "3" if include_notes is false and
"3 - Daily time series are supplied", if include_notes is true.
@return a list of daily ID option strings, for use in GUIs.
@param include_notes Indicate whether notes should be included.
*/
public static Vector getCdividyChoices ( boolean include_notes )
{	Vector v = new Vector(8);
	v.addElement (
		"0 - Use monthly time series to get average daily values" );
	v.addElement (
		"3 - Daily time series are supplied" );
	v.addElement (
	"4 - Daily time series interpolated from midpoints of monthly data" );
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
Return the default daily ID choice.  This can be used by GUI code
to pick a default for a new diversion.
@return the default daily ID choice.
*/
public static String getCdividyDefault ( boolean include_notes )
{	if ( include_notes ) {
		return
		"0 - Use monthly time series to get average daily values";
	}
	else {	return "0";
	}
}

/**
Cancels any changes made to this object within a GUI since createBackup()
was caled and sets _original to null.
*/
public void restoreOriginal() {
	StateMod_Diversion d = (StateMod_Diversion)_original;
	super.restoreOriginal();

	_diveff = d._diveff;
	_cdividy = d._cdividy;
	_divcap = d._divcap;
	_username = d._username;
	_idvcom = d._idvcom;
	_divefc = d._divefc;
	_area = d._area;
	_irturn = d._irturn;
	_demsrc = d._demsrc;
	_ireptype = d._ireptype;
	_awc = d._awc;
	_isClone = false;
	_original = null;
}

/**
Set the collection list for an aggregate/system.  It is assumed that the
collection applies to all years of data.
@param ids The identifiers indicating the locations to collection.
*/
public void setCollectionPartIDs ( Vector ids )
{	if ( __collection_Vector == null ) {
		__collection_Vector = new Vector ( 1 );
		__collection_year = new int[1];
	}
	else {	// Remove the previous contents...
		__collection_Vector.removeAllElements();
	}
	// Now assign...
	__collection_Vector.addElement ( ids );
	__collection_year[0] = 0;
}

/**
Set the collection type.
@param collection_type The collection type, either
COLLECTION_TYPE_AGGREGATE, COLLECTION_TYPE_SYSTEM, or
COLLECTION_TYPE_MULTISTRUCT.
*/
public void setCollectionType ( String collection_type )
{	__collection_type = collection_type;
}

/**
Set the consumptive water requirement daily time series for the diversion
structure.
*/
public void setConsumptiveWaterRequirementDayTS ( DayTS cwr_DayTS) {
	_cwr_DayTS = cwr_DayTS;
}

/**
Set the consumptive water requirement monthly time series for the diversion
structure.
*/
public void setConsumptiveWaterRequirementMonthTS ( MonthTS cwr_MonthTS) {
	_cwr_MonthTS = cwr_MonthTS;
}

/**
Set the average monthly demand time series for the diversion structure.
*/
public void setDemandAverageMonthTS(MonthTS demand_average_MonthTS) {
	_demand_average_MonthTS = demand_average_MonthTS;
}

/**
Set the daily demand time series for the diversion structure.
*/
public void setDemandDayTS(DayTS demand_DayTS) {
	_demand_DayTS = demand_DayTS;
}

/**
Set the monthly demand time series for the diversion structure.
*/
public void setDemandMonthTS(MonthTS demand_MonthTS) {
	_demand_MonthTS = demand_MonthTS;
}

/**
Set the monthly demand override time series for the diversion structure.
*/
public void setDemandOverrideMonthTS(MonthTS demand_override_MonthTS) {
	_demand_override_MonthTS = demand_override_MonthTS;
}

/**
Set the demand source.
@param demsrc acreage source.
*/
public void setDemsrc(int demsrc) {
	if (demsrc != _demsrc) {
		_demsrc = demsrc;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Set the demand source.
@param demsrc acreage source.
*/
public void setDemsrc(Integer demsrc) {
	setDemsrc(demsrc.intValue());
}

/**
Set the demand source.
@param demsrc demand source.
*/
public void setDemsrc(String demsrc) {
	if (demsrc == null) {
		return;
	}
	setDemsrc(StringUtil.atoi(demsrc.trim()));
}

/**
Set the diversion capacity.
@param divcap diversion capacity.
*/
public void setDivcap(double divcap) {
	if (divcap != _divcap) {
		_divcap = divcap;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Set the diversion capacity.
@param divcap diversion capacity.
*/
public void setDivcap(Double divcap) {
	setDivcap(divcap.doubleValue());
}

/**
Set the diversion capacity.
@param divcap diversion capacity.
*/
public void setDivcap(String divcap) {
	if (divcap != null) {
		setDivcap(StringUtil.atod(divcap.trim()));
	}
}

/**
Set the system efficiency switch.
@param divefc effeciency.
*/
public void setDivefc(double divefc) {
	if (divefc != _divefc) {
		_divefc = divefc;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Set the system efficiency switch.
@param divefc effeciency.
*/
public void setDivefc(Double divefc) {
	setDivefc(divefc.doubleValue());
}

/**
Set the system efficiency switch.
@param divefc effeciency.
*/
public void setDivefc(String divefc) {
	if (divefc == null) {
		return;
	}
	setDivefc(StringUtil.atod(divefc.trim()));
}

/**
Set the system efficiency for a particular month.
The efficiencies are stored in the order of the year for the data set.  For
example, if water years are used, the first efficiency will be for October.  For
calendar year, the first efficiency will be for January.
@param index month index
@param diveff monthly efficiency
*/
public void setDiveff(int index, double diveff) {
	if (_diveff[index] != diveff) {
		_diveff[index] = diveff;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Set the system efficiency for a particular month.
The efficiencies are specified with month 0 being January.
@param index month index (0=January).
@param diveff monthly efficiency
@param yeartype The yeartype for the diversion stations file (consistent with
the control file for a full data set).  Recognized values are:
<ol>
<li>	"Calendar", "CYR" (Jan - Dec).</li>
<li>	"Irrigation", "IYR" (Oct - Sep).</li>
<li>	"Water", "WYR" (Nov - Oct).</li>
</ol>
*/
public void setDiveff(int index, double diveff, String yeartype )
{	// Adjust the index if necessary based on the year type...
	if ( yeartype == null ) {
		// Assume calendar.
	}
	else if ( yeartype.equalsIgnoreCase("Water") ||
		yeartype.equalsIgnoreCase("WYR") ) {
		index = TimeUtil.convertCalendarMonthToCustomMonth (
				(index + 1), 10 ) - 1;
	}
	else if ( yeartype.equalsIgnoreCase("Irrigation") ||
		yeartype.equalsIgnoreCase("IYR") ) {
		index = TimeUtil.convertCalendarMonthToCustomMonth (
				(index + 1), 11 ) - 1;
	}
	if (_diveff[index] != diveff) {
		_diveff[index] = diveff;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Set the system efficiency for a particular month.
The efficiencies are stored in the order of the year for the data set.  For
example, if water years are used, the first efficiency will be for October.  For
calendar year, the first efficiency will be for January.
@param index month index
@param diveff monthly efficiency
*/
public void setDiveff(int index, Double diveff) {
	setDiveff(index, diveff.doubleValue());
}

/**
Set the system efficiency for a particular month.
The efficiencies are stored in the order of the year for the data set.  For
example, if water years are used, the first efficiency will be for October.  For
calendar year, the first efficiency will be for January.
@param index month index
@param diveff monthly efficiency
*/
public void setDiveff(int index, String diveff) {
	if (diveff == null) {
		return;
	}
	setDiveff(index, StringUtil.atod(diveff.trim()));
}

/**
Set the historical daily diversion time series.
*/
public void setDiversionDayTS(DayTS diversion_DayTS) {
	_diversion_DayTS = diversion_DayTS;
}

/**
Set the historical monthly diversion time series.
*/
public void setDiversionMonthTS(MonthTS diversion_MonthTS) {
	_diversion_MonthTS = diversion_MonthTS;
}

/**
Set the geographic information object associated with the diversion.
@param georecord Geographic record associated with the diversion.
*/
public void setGeoRecord ( GeoRecord georecord )
{	_georecord = georecord;
}

/**
Set the data type switch.
@param idvcom data type switch.
*/
public void setIdvcom(int idvcom) {
	if (idvcom != _idvcom) {
		_idvcom = idvcom;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Set the data type switch.
@param idvcom data type switch.
*/
public void setIdvcom(Integer idvcom) {
	setIdvcom(idvcom.intValue());
}

/**
Set the data type switch.
@param idvcom data type switch.
*/
public void setIdvcom(String idvcom) {
	if (idvcom == null) {
		return;
	}
	setIdvcom(StringUtil.atoi(idvcom.trim()));
}

/**
Set the replacement code.
@param ireptype replacement code.
*/
public void setIreptype(int ireptype) {
	if (ireptype != _ireptype) {
		_ireptype = ireptype;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Set the replacement code.
@param ireptype replacement code.
*/
public void setIreptype(Integer ireptype) {
	setIreptype(ireptype.intValue());
}

/**
Set the replacement code.
@param ireptype replacement code.
*/
public void setIreptype(String ireptype) {
	if (ireptype == null) {
		return;
	}
	setIreptype(StringUtil.atoi(ireptype.trim()));
}

/**
Set the use type.
@param irturn use type.
*/
public void setIrturn(int irturn) {
	if (irturn != _irturn) {
		_irturn = irturn;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Set the use type.
@param irturn use type.
*/
public void setIrturn(Integer irturn) {
	setIrturn(irturn.intValue());
}

/**
Set the use type.
@param irturn use type.
*/
public void setIrturn(String irturn) {
	if (irturn == null) {
		return;
	}
	setIrturn(StringUtil.atoi(irturn.trim()));
}

/**
Set the average monthly efficiencies to be used for modeling (
12 monthly values + annual average), for the
data set calendar type.  This is ONLY used by StateDMI and does not need
to be considered in comparison code.
*/
public void setModelEfficiencies ( double [] model_efficiencies )
{	__model_efficiencies = model_efficiencies;
}

/**
Set the parcel Vector.
@param parcel_Vector the Vector of StateMod_Parcel to set for parcel data.
*/
void setParcels ( Vector parcel_Vector )
{	_parcel_Vector = parcel_Vector;
}

/**
Sets the Return flow vector.
*/
public void setReturnFlow(Vector rivret) {
	_rivret = rivret;
}

/**
set the rights from a vector of rights.  The linked list will be set up, too, 
according to the order in the vector.
*/
public void setRightsVector(Vector rights)
{	_rights = rights;
}

/**
Set the user name.
@param username user name.
*/
public void setUsername(String username) {
	if (username == null) {
		return;
	}
	if (!username.equals(_username)) {
		_username = username;
		setDirty(true);
		if ( !_isClone && _dataset != null ) {
			_dataset.setDirty(
			_dataset.COMP_DIVERSION_STATIONS, true);
		}
	}
}

/**
Write diversion information to output.  History header information 
is also maintained by calling this routine.  Daily data fields are written.
@param instrfile input file from which previous history should be taken
@param outstrfile output file to which to write
@param theDiversions vector of diversions to write.
@param new_comments addition comments which should be included in history
@exception Exception if an error occurs.
*/
public static void writeStateModFile(String instrfile, String outstrfile,
Vector theDiversions, String[] new_comments)
throws Exception {
	writeStateModFile(instrfile, outstrfile, theDiversions, new_comments,
		true);
}

/**
Write diversion information to output.  History header information 
is also maintained by calling this routine.
@param instrfile input file from which previous history should be taken
@param outstrfile output file to which to write
@param theDiversions vector of diversions to write.
@param new_comments addition comments which should be included in history
@param use_daily_data Indicates whether daily data should be written.  The data
are only used if the control file indicates that a daily run is occurring.
@exception Exception if an error occurs.
*/
public static void writeStateModFile(String instrfile, String outstrfile,
Vector theDiversions, String[] new_comments, boolean use_daily_data)
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
		String routine = "StateMod_Diversion.writeStateModFile";
		String format_1 =	// With daily ID
			"%-12.12s%-24.24s%-12.12s%8d%#8.2F%8d%8d %-12.12s";
		String format_1A=	// Without daily ID
			"%-12.12s%-24.24s%-12.12s%8d%#8.2F%8d%8d";
		String format_2 =
		"            %-24.24s            %8d%8d%#8.0F%#8.2F%8d%8d";
		String format_3 = "%1.1s%#5.0F";
		String format_4 =
		"                                    %-12.12s%8.2F%8d";
		StateMod_Diversion div = null;
		StateMod_ReturnFlow ret = null;
		Vector v = new Vector(9);	// Reuse for all output lines.
		Vector v5 = null;		// For return flows.

		out.println(cmnt);
		out.println(cmnt
			+ "*************************************************");
		out.println(cmnt + "  Direct Diversion Station File");
		out.println(cmnt);
		out.println(cmnt
			+ "  Card 1 format (a12, a24, a12, i8, f8.2, 2i8, "
			+ "1x, a12)");
		out.println(cmnt);
		out.println(cmnt
			+ "  ID          cdivid:  Diversion station ID");
		out.println(cmnt
			+ "  Name        divnam:  Diversion name");
		out.println(cmnt
			+ "  Riv ID       cgoto:  River node for diversion");
		out.println(cmnt
			+ "  On/Off      idivsw:  Switch 0=off, 1=on");
		out.println(cmnt
			+ "  Capacity    divcap:  Diversion capacity (CFS)");
		out.println(cmnt
			+ "                dumx:  Not currently used");
		out.println(cmnt
			+ "  RepType    ireptyp:  Replacement reservoir option "
			+ "(see StateMod doc)");
		out.println(cmnt
			+ "  Daily ID   cdividy:  Daily diversion ID");
		out.println(cmnt);
		out.println(cmnt
			+ "  Card 2 format (12x, a24, 12x, 2i8, f8.2, f8.0, "
			+ "2i8)");
		out.println(cmnt);
		out.println(cmnt
			+ "  User Name  usernam:  User name.");
		out.println(cmnt
			+ "  DemType     idvcom:  Demand data type switch (see "
			+ "StateMod doc)");
		out.println(cmnt
			+ "  #-Ret         nrtn:  Number of return "
			+ "flow table ref");
		out.println(cmnt
			+ "  Eff         divefc:  Annual system efficiency");
		out.println(cmnt
			+ "  Area          area:  Irrigated acreage");
		out.println(cmnt
			+ "  UseType     irturn:  Use type (see StateMod doc)");
		out.println(cmnt
			+ "  Demsrc      demsrc:  Demand source (see "
			+ "StateMod doc)");
		out.println(cmnt);
		out.println(cmnt
			+ "  Card 3 format (free format)");
		out.println(cmnt);
		out.println(cmnt
			+ "     diveff (12):  System efficiency % by month");
		out.println(cmnt);
		out.println(cmnt
			+ "  Card 4 format (36x, a12, f8.2, i8)");
		out.println(cmnt);
		out.println(cmnt
			+ "  Ret ID      crtnid:  River node receiving "
			+ "return flow");
		out.println(cmnt
			+ "  Ret %       pcttot:  Percent of return flow to "
			+ "this river node");
		out.println(cmnt
			+ "  Table #     irtndl:  Delay (return flow) "
			+ "table for this return flow.");
		out.println(cmnt);

		out.println(cmnt
			+ " ID               Name             Riv ID     "
			+ "On/Off  Capacity        RepType   Daily ID");
		out.println(cmnt 
			+ "---------eb----------------------eb----------e"
			+ "b------eb------eb------eb------exb----------e");
		out.println(cmnt 
			+ "              User Name                       "
			+ "DemType   #-Ret   Eff %   Area  UseType DemSrc");
		out.println(cmnt 
			+ "xxxxxxxxxxb----------------------exxxxxxxxxxxx"
			+ "b------eb------eb------eb------eb------eb------e");
		out.println(cmnt 
			+ "          ... Monthly Efficiencies...");
		out.println(cmnt
			+ "b------------------------------------------------"
			+ "----------------------------e");
		out.println(cmnt 
			+ "                                   Ret ID     "
			+ "  Ret % Table #");
		out.println(cmnt
			+ "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxb----------e"
			+ "b------eb------e");
		out.println(cmnt + "EndHeader");

		int num = 0;
		if (theDiversions != null) {
			num = theDiversions.size();
		}
		for (i = 0; i < num; i++) {
			div = (StateMod_Diversion)theDiversions.elementAt(i);
			if (div == null) {
				continue;
			}

			// line 1
			v.removeAllElements();
			v.addElement(div.getID());
			v.addElement(div.getName());
			v.addElement(div.getCgoto());
			v.addElement(new Integer(div.getSwitch()));
			v.addElement(new Double (div.getDivcap()));
			v.addElement(new Integer(1));	// Old nduser not used
							// anymore.
			v.addElement(new Integer(div.getIreptype()));
			if (use_daily_data) {
				v.addElement(div.getCdividy());
				iline = StringUtil.formatString(v, format_1);
			}
			else {	
				iline = StringUtil.formatString(v, format_1A);
			}
			out.println(iline);

			// line 2
			v.removeAllElements();
			v.addElement(div.getUsername());
			v.addElement(new Integer(div.getIdvcom()));
			v.addElement(new Integer(div.getNrtn()));
			v.addElement(new Double (div.getDivefc()));
			v.addElement(new Double (div.getArea()));
			v.addElement(new Integer(div.getIrturn()));
			v.addElement(new Integer(div.getDemsrc()));
			iline = StringUtil.formatString(v, format_2);
			out.println(iline);

			// line 3 - diversion efficiency
			if ( div.getDivefc() < 0 ) {
				for (j=0; j<12; j++) {
					v.removeAllElements();
					v.addElement("");
					v.addElement(new Double(
						div.getDiveff(j)));
					iline = StringUtil.formatString(
						v, format_3);
					out.print(iline);
				}

				out.println();
			}

			// line 4 - return information
			int nrtn = div.getNrtn();
			v5 = div.getReturnFlows();
			for (j = 0; j < nrtn; j++) {
				v.removeAllElements();
				ret =(StateMod_ReturnFlow)v5.elementAt(j);
				v.addElement(ret.getCrtnid());
				v.addElement(new Double(ret.getPcttot()));
				v.addElement(new Integer(ret.getIrtndl()));
				iline = StringUtil.formatString(v, format_4);
				out.println(iline);
			}

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

/**
Writes a Vector of StateMod_Diversion objects to a list file.  A header is 
printed to the top of the file, containing the commands used to generate the 
file.  Any strings in the body of the file that contain the field delimiter 
will be wrapped in "...".  This method also prints Diversion Return Flows to
filename[without extension]_ReturnFlows[extension], so if this method is called
with a filename parameter of "diversions.txt", two files will be generated:
- diversions.txt
- diversions_ReturnFlows.txt
@param filename the name of the file to which the data will be written.
@param delimiter the delimiter to use for separating field values.
@param update whether to update an existing file, retaining the current 
header (true) or to create a new file with a new header.
@param data the Vector of objects to write.  
@throws Exception if an error occurs.
*/
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
	int comp = StateMod_DataSet.COMP_DIVERSION_STATIONS;
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

/**
Writes the collection data from a Vector of StateMod_Diversion objects to a 
list file.  A header is printed to the top of the file, containing the commands 
used to generate the file.  Any strings in the body of the file that contain 
the field delimiter will be wrapped in "...". 
@param filename the name of the file to which the data will be written.
@param delimiter the delimiter to use for separating field values.
@param update whether to update an existing file, retaining the current 
header (true) or to create a new file with a new header.
@param data the Vector of objects to write.  
@throws Exception if an error occurs.
*/
public static void writeCollectionListFile(String filename, 
String delimiter, boolean update, Vector data) 
throws Exception {
	int size = 0;
	if (data != null) {
		size = data.size();
	}
	
	Vector fields = new Vector();
	fields.add("LocationID");
	fields.add("Year");
	fields.add("CollectionType");
	fields.add("PartType");
	fields.add("PartID");
	int fieldCount = fields.size();

	String[] names = new String[fieldCount];
	String[] formats = new String[fieldCount]; 
	int comp = StateMod_DataSet.COMP_DIVERSION_STATION_COLLECTIONS;
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
	
	int[] years = null;
	int k = 0;
	int j = 0;
	int num = 0;
	PrintWriter out = null;
	StateMod_Diversion div = null;
	String[] commentString = { "#" };
	String[] ignoreCommentString = { "#>" };
	String[] line = new String[fieldCount];
	String[] newComments = null;
	String colType = null;
	String id = null;
	String partType = null;	
	StringBuffer buffer = new StringBuffer();
	Vector ids = null;

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
			id = div.getID();
			years = div.getCollectionYears();
			if (years == null) {
				num = 0;
			}
			else {
				num = years.length;
			}
			colType = div.getCollectionType();
			partType = div.getCollectionPartType();
			
			for (j = 0; j < num; j++) {
				ids = div.getCollectionPartIDs(years[j]);
				line[0] = StringUtil.formatString(id,
					formats[0]).trim();
				line[1] = StringUtil.formatString(years[j],
					formats[1]).trim();
				line[2] = StringUtil.formatString(colType,
					formats[2]).trim();
				line[3] = StringUtil.formatString(partType,
					formats[3]).trim();
				line[4] = StringUtil.formatString(
					((String)(ids.elementAt(k))),
					formats[4]).trim();

				buffer = new StringBuffer();	
				for (k = 0; k < fieldCount; k++) {
					if (line[k].indexOf(delimiter) > -1) {
						line[k] = "\"" + line[k] + "\"";
					}
					buffer.append(line[k]);
					if (k < (fieldCount - 1)) {
						buffer.append(delimiter);
					}
				}
	
				out.println(buffer.toString());
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
}

} // End StateMod_Diversion