/**
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
 * specific language governing rights and limitations under the License.
 *
 * The Original Code is "CommonTSTest.java".  Description:
 * "Unit test class for ca.uhn.hl7v2.model.primitive.CommonTS"
 *
 * The Initial Developer of the Original Code is Leslie Mann. Copyright (C)
 * 2002.  All Rights Reserved.
 *
 * Contributor(s): ______________________________________.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * GNU General Public License (the  �GPL�), in which case the provisions of the GPL are
 * applicable instead of those above.  If you wish to allow use of your version of this
 * file only under the terms of the GPL and not to allow others to use your version
 * of this file under the MPL, indicate your decision by deleting  the provisions above
 * and replace  them with the notice and other provisions required by the GPL License.
 * If you do not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the GPL.
 *
 */
package ca.uhn.hl7v2.model.primitive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.DataTypeUtil;

/**
 * Unit test class for ca.uhn.hl7v2.model.primitive.CommonTS
 * 
 * @author Leslie Mann
 */
public class CommonTSTest extends TestCase {
	String offsetStr;
	String timeStamp;
	String baseTime;
	CommonTS commonTS;
	int offset;
	int year;
	int month;
	int day;
	int hour;
	int minute;
	int second;
	float fractionalSecond;
	
	/**
	 * Constructor for CommonTSTest.
	 * @param testName
	 */
	public CommonTSTest(String testName) {
		super(testName);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(CommonTSTest.class);
	}

	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		year = 2002;
		month = 11;
		day = 30;
		hour = 11;
		minute = 23;
		second = 15;
		fractionalSecond = .1234F;
		
		offset = DataTypeUtil.getLocalGMTOffset();
  	    offsetStr = DataTypeUtil.preAppendZeroes(Math.abs(offset),4);
        if (offset>=0){
		    offsetStr = "+" + offsetStr;
		}
		else{
		    offsetStr = "-" + offsetStr;
		}
		timeStamp = Integer.toString(year) +
					Integer.toString(month) +
					Integer.toString(day) +
					Integer.toString(hour) +
					Integer.toString(minute) +
					Integer.toString(second) + 
					Float.toString(fractionalSecond).substring(1) +
				 	offsetStr;
		baseTime = Integer.toString(year) +
				   Integer.toString(month) +
				   Integer.toString(day) +
				   Integer.toString(hour) +
				   Integer.toString(minute) +
				   Integer.toString(second) + 
				   Float.toString(fractionalSecond).substring(1);
		commonTS = new CommonTS();
	}

	/**
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	    offsetStr = null;
	}

	/*
	 ********************************************************** 
	 * Test Cases
	 ********************************************************** 
	 */
	 
	/**
	 * Test for http://sourceforge.net/support/tracker.php?aid=3410095
	 */
	public void testSetCalendarUsingHighValueTimeZoneOffset() throws ParseException, DataTypeException {
		Calendar c = Calendar.getInstance();
		c.setTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss Z").parse("20110102 12:00:00 -0000"));
		
		System.out.println(c.getTime());
		
		c.set(Calendar.ZONE_OFFSET, 12 * 60 * 60 * 1000);
		System.out.println(c.getTime());
		
		commonTS.setValue(c);
		
		String val = commonTS.getValue();
		assertEquals("20110102070000+1200", val);
		
	}
	
	/**
	 * Test for default constructor
	 */
	public void testConstructor() {
		assertNotNull("Should have a valid CommonTS object", commonTS);
	}

	/**
	 * Test for string constructor
	 */
	public void testStringConstructor() throws DataTypeException {
		commonTS = new CommonTS(timeStamp);
		assertNotNull("Should have a valid CommonTS object", commonTS);
	}
    
    /**
     * Testing string constructor with delete value "".
     */
    public void testStringConstructor2() throws DataTypeException {
        commonTS = new CommonTS("\"\"");
        assertNotNull("Should have a valid CommonTS object", commonTS);
    }

    public void testNativeJavaAccessorsAndMutators() throws DataTypeException, ParseException {
        
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date date = format.parse("20100609 12:40:05");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        commonTS = new CommonTS();
        commonTS.setValueToMinute(cal);
        assertEquals("201006091240", commonTS.getValue());

        commonTS = new CommonTS();
        commonTS.setValueToMinute(date);
        assertEquals("201006091240", commonTS.getValue());

        commonTS = new CommonTS();
        commonTS.setValueToSecond(cal);
        assertEquals("20100609124005", commonTS.getValue());

        commonTS = new CommonTS();
        commonTS.setValueToSecond(date);
        assertEquals("20100609124005", commonTS.getValue());
        
        commonTS = new CommonTS();
        cal.set(Calendar.MILLISECOND, 250);
        cal.set(Calendar.ZONE_OFFSET, -4 * 1000 * 60 * 60);
        commonTS.setValue(cal);
        String value = commonTS.getValue();
        assertEquals("20100609124005.25-0400", value);
        
        commonTS = new CommonTS();
        commonTS.setValue("201006091240");
        assertEquals("201006091240", commonTS.getValue());
        String formatted = format.format(commonTS.getValueAsDate());
        assertEquals("20100609 12:40:00", formatted);
        
        commonTS = new CommonTS();
        commonTS.setValue("201006091240");
        assertEquals("20100609 12:40:00", format.format(commonTS.getValueAsCalendar().getTime()));
     
        // Check millis and offset
        commonTS = new CommonTS();
        commonTS.setValue("20100609124005.25-0400");
        cal = commonTS.getValueAsCalendar();
        assertEquals(250, cal.get(Calendar.MILLISECOND));
        assertEquals(-4 * 1000 * 60 * 60, cal.get(Calendar.ZONE_OFFSET));
        
    }
    
    
	/**
	 * Test set/getValue with various inputs
	 */
	public void testSetGetValue() {
		class TestSpec {
			String value;
			Object outcome;
			Exception exception = null;
			
			TestSpec(String value, Object outcome) {
				this.value = value;
				this.outcome = outcome;
			}
			
			public String toString() {
				return "[ " + (value != null ? value : "null") + " : "
					+ (outcome != null ? outcome : "null")
 			    	+ (exception != null ? " [ " + exception.toString() + " ]":" ]");
			}
			
			public boolean executeTest() {
				CommonTS ts = new CommonTS();
				try {
					ts.setValue(value);
					String retval = ts.getValue();
					if (retval != null) {
						return retval.equals(outcome);
					} else {
						return outcome == null;
					}
				} catch (Exception e) {
					if (e.getClass().equals(outcome)) {
						return true;
					} else {
						this.exception = e;
						return (e.getClass().equals(outcome));
					}
				}
			}
		}//inner class
    		
		TestSpec [] tests = {
			new TestSpec(null, null),
			new TestSpec("", ""),
            new TestSpec("\"\"","\"\""),
			//year
			new TestSpec("0", DataTypeException.class),
			new TestSpec("1", DataTypeException.class),
			new TestSpec("19", DataTypeException.class),
			new TestSpec("198", DataTypeException.class),
			new TestSpec("1984", "1984"),
			//year & time zone
			new TestSpec("1984-1", DataTypeException.class),
			new TestSpec("1984-11", DataTypeException.class),
			new TestSpec("1984-111", DataTypeException.class),
			new TestSpec("1984-0060", DataTypeException.class),
			new TestSpec("1984-0059", "1984-0059"),
			new TestSpec("1984-0001", "1984-0001"),
			new TestSpec("1984-0000", "1984+0000"),
			new TestSpec("1984+0000", "1984+0000"),
			new TestSpec("1984+0001", "1984+0001"),
			new TestSpec("1984+0059", "1984+0059"),
			new TestSpec("1984+0060", DataTypeException.class),
			new TestSpec("1984+11111", DataTypeException.class),
			//month
			new TestSpec("19840", DataTypeException.class),
			new TestSpec("198400", DataTypeException.class),
			new TestSpec("198401", "198401"),
			new TestSpec("198412", "198412" ),
			new TestSpec("198413", DataTypeException.class),
			//month & time zone
			new TestSpec("198401-1", DataTypeException.class),
			new TestSpec("198401-11", DataTypeException.class),
			new TestSpec("198401-111", DataTypeException.class),
			new TestSpec("198401-0060", DataTypeException.class),
			new TestSpec("198401-0059", "198401-0059"),
			new TestSpec("198401-0001", "198401-0001"),
			new TestSpec("198401-0000", "198401+0000"),
			new TestSpec("198401+0000", "198401+0000"),
			new TestSpec("198401+0001", "198401+0001"),
			new TestSpec("198401+0059", "198401+0059"),
			new TestSpec("198401+0060", DataTypeException.class),
			new TestSpec("198401+11111", DataTypeException.class),
			//day
			new TestSpec("1984010", DataTypeException.class),
			new TestSpec("19840100", DataTypeException.class),
			new TestSpec("19840101", "19840101"),
			new TestSpec("19840131", "19840131"),
			new TestSpec("19840132", DataTypeException.class),
			//day & time zone
			new TestSpec("19840101-1", DataTypeException.class),
			new TestSpec("19840101-11", DataTypeException.class),
			new TestSpec("19840101-111", DataTypeException.class),
			new TestSpec("19840101-0060", DataTypeException.class),
			new TestSpec("19840101-0059", "19840101-0059"),
			new TestSpec("19840101-0001", "19840101-0001"),
			new TestSpec("19840101-0000", "19840101+0000"),
			new TestSpec("19840101+0000", "19840101+0000"),
			new TestSpec("19840101+0001", "19840101+0001"),
			new TestSpec("19840101+0059", "19840101+0059"),
			new TestSpec("19840101+0060", DataTypeException.class),
			new TestSpec("19840101+11111", DataTypeException.class),
			//hour
			new TestSpec("198401011", DataTypeException.class),
			new TestSpec("1984010111", DataTypeException.class),
			new TestSpec("19840101111", DataTypeException.class),
			new TestSpec("198401010000", "198401010000"),
			new TestSpec("198401010100", "198401010100"),
			new TestSpec("198401012300", "198401012300"),
			new TestSpec("198401012400", DataTypeException.class),
			new TestSpec("1984010123001", DataTypeException.class),
			//minute
			new TestSpec("19840101000", DataTypeException.class),
			new TestSpec("198401010000", "198401010000"),
			new TestSpec("198401010001", "198401010001"),
			new TestSpec("198401010059", "198401010059"),
			new TestSpec("198401010060", DataTypeException.class),
			//hour/minute & time zone
			new TestSpec("198401010000-1", DataTypeException.class),
			new TestSpec("198401010000-11", DataTypeException.class),
			new TestSpec("198401010000-111", DataTypeException.class),
			new TestSpec("198401010000-0060", DataTypeException.class),
			new TestSpec("198401010000-0059", "198401010000-0059"),
			new TestSpec("198401010000-0001", "198401010000-0001"),
			new TestSpec("198401010000-0000", "198401010000+0000"),
			new TestSpec("198401010000+0000", "198401010000+0000"),
			new TestSpec("198401010000+0001", "198401010000+0001"),
			new TestSpec("198401010000+0059", "198401010000+0059"),
			new TestSpec("198401010000+0060", DataTypeException.class),
			new TestSpec("198401010000+11111", DataTypeException.class),
			//seconds
			new TestSpec("1984010100000", DataTypeException.class),
			new TestSpec("19840101000000", "19840101000000"),
			new TestSpec("19840101000001", "19840101000001"),
			new TestSpec("19840101000059", "19840101000059"),
			new TestSpec("19840101000060", DataTypeException.class),
			//seconds & time zone
			new TestSpec("19840101000000-1", DataTypeException.class),
			new TestSpec("19840101000000-11", DataTypeException.class),
			new TestSpec("19840101000000-111", DataTypeException.class),
			new TestSpec("19840101000000-0060", DataTypeException.class),
			new TestSpec("19840101000000-0059", "19840101000000-0059"),
			new TestSpec("19840101000000-0001", "19840101000000-0001"),
			new TestSpec("19840101000000-0000", "19840101000000+0000"),
			new TestSpec("19840101000000+0000", "19840101000000+0000"),
			new TestSpec("19840101000000+0001", "19840101000000+0001"),
			new TestSpec("19840101000000+0059", "19840101000000+0059"),
			new TestSpec("19840101000000+0060", DataTypeException.class),
			new TestSpec("19840101000000+11111", DataTypeException.class),
			//milliseconds
			new TestSpec("19840101000000.", DataTypeException.class),
			new TestSpec("19840101000000.0", "19840101000000.0"),
			new TestSpec("19840101000000.00", "19840101000000.00"),
			new TestSpec("19840101000000.000", "19840101000000.000"),
			new TestSpec("19840101000000.0000", "19840101000000.0000"),
			new TestSpec("19840101000000.0001", "19840101000000.0001"),
			new TestSpec("19840101000000.9999", "19840101000000.9999"),
			new TestSpec("19840101000000.00000", DataTypeException.class),
			//milliseconds & time zone
			new TestSpec("19840101000000.0-1", DataTypeException.class),
			new TestSpec("19840101000000.0-11", DataTypeException.class),
			new TestSpec("19840101000000.0-111", DataTypeException.class),
			new TestSpec("19840101000000.0-0060", DataTypeException.class),
			new TestSpec("19840101000000.0-0059", "19840101000000.0-0059"),
			new TestSpec("19840101000000.0-0001", "19840101000000.0-0001"),
			new TestSpec("19840101000000.0-0000", "19840101000000.0+0000"),
			new TestSpec("19840101000000.0+0000", "19840101000000.0+0000"),
			new TestSpec("19840101000000.00+0000", "19840101000000.00+0000"),
			new TestSpec("19840101000000.000+0000", "19840101000000.000+0000"),
			new TestSpec("19840101000000.0000+0000", "19840101000000.0000+0000"),
			new TestSpec("19840101000000.0+0001", "19840101000000.0+0001"),
			new TestSpec("19840101000000.0+0059", "19840101000000.0+0059"),
			new TestSpec("19840101000000.0+0060", DataTypeException.class),
			new TestSpec("19840101000000.0+11111", DataTypeException.class),
		};
		
		ArrayList failedTests = new ArrayList();

		for (int i = 0; i < tests.length ; i++) {
			if ( ! tests[ i ].executeTest() ) 
         		failedTests.add( tests[ i ] );
		}

   		assertEquals("Failures: " + failedTests, 0, failedTests.size()); 
		
	}

	public void testSetDatePrecision() {
		class TestSpec {
			int year;
			int month;
			int day;
			Object outcome;
			Exception exception = null;
			
			TestSpec(int year, int month, int day, Object outcome) {
				this.year = year;
				this.month = month;
				this.day = day;
				this.outcome = outcome;
			}
			
			public String toString() {
				return "[ " + Integer.toString(year) + " " + Integer.toString(month) + " : "
					+ Integer.toString(day) + " : "
					+ (outcome != null ? outcome : "null")
 			    	+ (exception != null ? " [ " + exception.toString() + " ]":" ]");
			}
			
			public boolean executeTest() {
				CommonTS ts = new CommonTS();
				try {
					ts.setDatePrecision(year, month, day);
					String retval = ts.getValue();
					if (retval != null) {
						return retval.equals(outcome);
					} else {
						return outcome == null;
					}
				} catch (Exception e) {
					if (e.getClass().equals(outcome)) {
						return true;
					} else {
						this.exception = e;
						return (e.getClass().equals(outcome));
					}
				}
			}
		}//inner class
    	
		TestSpec [] tests = {
			new TestSpec(-2001, 1, 1, DataTypeException.class),
			new TestSpec(2001, -1, -1, DataTypeException.class),
			new TestSpec(2001, 1, -1, DataTypeException.class),
			new TestSpec(9, 1, 1, DataTypeException.class),
			new TestSpec(99, 1, 1, DataTypeException.class),
			new TestSpec(999, 1, 1, DataTypeException.class),
			new TestSpec(2001, 0, 1, DataTypeException.class),
			new TestSpec(2001, 1, 0, DataTypeException.class),
			new TestSpec(2001, 1, 1, "20010101"),
			new TestSpec(1000, 1, 1, "10000101"),
			new TestSpec(1987, 1, 1, "19870101"),
			new TestSpec(2001, 1, 1, "20010101"),
			new TestSpec(2001, 1, 7, "20010107"),
			new TestSpec(2001, 1, 12, "20010112"),
			new TestSpec(2001, 1, 25, "20010125"),
			new TestSpec(2001, 1, 31, "20010131"),
			new TestSpec(2001, 1, 32, DataTypeException.class),
			new TestSpec(9999, 1, 1, "99990101"),
			new TestSpec(10000, 1, 1, DataTypeException.class),
			new TestSpec(2001, 1, 1, "20010101"),
			new TestSpec(2001, -1, 21, DataTypeException.class),
		};
		
		ArrayList failedTests = new ArrayList();

		for (int i = 0; i < tests.length ; i++) {
			if ( ! tests[ i ].executeTest() ) 
         		failedTests.add( tests[ i ] );
		}

   		assertEquals("Failures: " + failedTests, 0, failedTests.size()); 
	}

	public void testSetDateMinutePrecision() {
		class TestSpec {
			int year;
			int month;
			int day;
			int hour;
			int minute;
			Object outcome;
			Exception exception = null;
			
			TestSpec(int year, int month, int day, int hour, int minute, Object outcome) {
				this.year = year;
				this.month = month;
				this.day = day;
				this.hour = hour;
				this.minute = minute;
				this.outcome = outcome;
			}
			
			public String toString() {
				return "[ " + Integer.toString(year) + " "  + Integer.toString(month) + " "
					+ Integer.toString(day) + " " + Integer.toString(hour) + " " +Integer.toString(minute) 
					+ " : "
					+ (outcome != null ? outcome : "null")
 			    	+ (exception != null ? " [ " + exception.toString() + " ]":" ]");
			}
			
			public boolean executeTest() {
				CommonTS ts = new CommonTS();
				try {
					ts.setDateMinutePrecision(year, month, day, hour, minute);
					String retval = ts.getValue();
					if (retval != null) {
						return retval.equals(outcome);
					} else {
						return outcome == null;
					}
				} catch (Exception e) {
					if (e.getClass().equals(outcome)) {
						return true;
					} else {
						this.exception = e;
						return (e.getClass().equals(outcome));
					}
				}
			}
		}//inner class
    	
		TestSpec [] tests = {
			new TestSpec(-2001, 1, 1, 1, 1, DataTypeException.class),
			new TestSpec(2001, -1, 1, 1, 1, DataTypeException.class),
			new TestSpec(2001, 1, -1, 1, 1, DataTypeException.class),
			new TestSpec(2001, 1, 1, -1, 1, DataTypeException.class),
			new TestSpec(2001, 1, 1, 1, -1, DataTypeException.class),
			new TestSpec(9, 1, 1, 1, 1, DataTypeException.class),
			new TestSpec(99, 1, 1, 1, 1, DataTypeException.class),
			new TestSpec(999, 1, 1, 1, 1, DataTypeException.class),
			new TestSpec(1000, 1, 1, 1, 1, "100001010101"),
			new TestSpec(1987, 1, 1, 0, 30, "198701010030" ),
			new TestSpec(2001, 0, 1, 1, 1, DataTypeException.class),
			new TestSpec(2001, 1, 0, 1, 1, DataTypeException.class),
			new TestSpec(2001, 1, 1, 1, 1, "200101010101"),
			new TestSpec(2001, 1, 1, 0, 0, "200101010000"),
			new TestSpec(2001, 1, 1, 8, 15, "200101010815" ),
			new TestSpec(2001, 1, 7, 13, 27, "200101071327" ),
			new TestSpec(2001, 1, 12, 23, 59, "200101122359" ),
			new TestSpec(2001, 1, 12, 23, 59, "200101122359"),
			new TestSpec(2001, 1, 12, 23, 60, DataTypeException.class),
			new TestSpec(2001, 1, 12, 24, 59, DataTypeException.class),
			new TestSpec(2001, 1, 12, 24, 60, DataTypeException.class),
			new TestSpec(2001, 1, 25, 1, 1, "200101250101"),
			new TestSpec(2001, 1, 31, 1, 1, "200101310101"),
			new TestSpec(2001, 1, 32, 1, 1, DataTypeException.class),
			new TestSpec(9999, 1, 1, 1, 1, "999901010101"),
			new TestSpec(10000, 1, 1, 1, 1,DataTypeException.class),
			new TestSpec(2001, 1, 1, 1, 1, "200101010101"),
			new TestSpec(2001, -1, 21, 1, 1, DataTypeException.class),
		};
		
		ArrayList failedTests = new ArrayList();

		for (int i = 0; i < tests.length ; i++) {
			if ( ! tests[ i ].executeTest() ) 
         		failedTests.add( tests[ i ] );
		}

   		assertEquals("Failures: " + failedTests, 0, failedTests.size()); 
	}

	public void testSetDateSecondPrecision() {
		class TestSpec {
			int year;
			int month;
			int day;
			int hour;
			int minute;
			float second;
			Object outcome;
			Exception exception = null;
                        String retval;
			
			TestSpec(int year, int month, int day, int hour, int minute, float second, Object outcome) {
				this.year = year;
				this.month = month;
				this.day = day;
				this.hour = hour;
				this.minute = minute;
				this.second = second;
				this.outcome = outcome;
			}
			
			public String toString() {
				return "[ " + Integer.toString(year) + " "  + Integer.toString(month) + " "
					+ Integer.toString(day) + " " + Integer.toString(hour) + " " +Integer.toString(minute) + " "
					+ Float.toString(second) + " : "
					+ (outcome != null ? outcome : "null") + " : "
					+ (retval != null ? retval : "null")
 			    	+ (exception != null ? " [ " + exception.toString() + " ]":" ]");
			}
			
			public boolean executeTest() {
				CommonTS ts = new CommonTS();
				try {
					ts.setDateSecondPrecision(year, month, day, hour, minute, second);
					retval = ts.getValue();
		  			if (retval != null) {
						return retval.equals(outcome);
					} else {
						return outcome == null;
					}
				} catch (Exception e) {
					if (e.getClass().equals(outcome)) {
						return true;
					} else {
						this.exception = e;
						return (e.getClass().equals(outcome));
					}
				}
			}
		}//inner class
    	
		TestSpec [] tests = {
			new TestSpec(2001, -1, 1, 1, 1, 1.0F, DataTypeException.class),
			new TestSpec(2001, 1, -1, 1, 1, 1.0F, DataTypeException.class),
			new TestSpec(2001, 1, 1, -1, 1, 1.0F, DataTypeException.class),
			new TestSpec(2001, 1, 1, 1, -1, 1.0F, DataTypeException.class),
			new TestSpec(2001, 1, 1, 1, 1, -1.0F, DataTypeException.class),
			new TestSpec(9, 1, 1, 1, 1, 1.0F, DataTypeException.class),
			new TestSpec(99, 1, 1, 1, 1, 1.0F, DataTypeException.class),
			new TestSpec(999, 1, 1, 1, 1, 1.0F, DataTypeException.class),
			new TestSpec(1000, 1, 1, 1, 1, 1.0F, "10000101010101"),
			new TestSpec(1987, 1, 1, 0, 30, 1.1234F, "19870101003001.1234" ),
			new TestSpec(2001, 0, 1, 1, 1, 1.0F, DataTypeException.class),
			new TestSpec(2001, 1, 0, 1, 1, 1.0F, DataTypeException.class),
			new TestSpec(2001, 1, 1, 1, 1, 0.0F, "20010101010100"),
			new TestSpec(2001, 1, 1, 1, 1, 1.12F, "20010101010101.12"),
			new TestSpec(2001, 1, 1, 1, 1, 12.12F, "20010101010112.12"),
			new TestSpec(2001, 1, 1, 1, 1, 123.12F, DataTypeException.class),
			new TestSpec(2001, 1, 1, 1, 1, 12.123F, "20010101010112.123"),
			new TestSpec(2001, 1, 1, 1, 1, 12.1234F, "20010101010112.1234"),
			new TestSpec(2001, 1, 1, 1, 1, 12.12345F, "20010101010112.1235"),
			new TestSpec(2001, 1, 1, 1, 1, 59.1234F, "20010101010159.1234"),
			new TestSpec(2001, 1, 1, 1, 1, 59.9999F, "20010101010159.9999"),
			new TestSpec(2001, 1, 1, 1, 1, 59.99999F, DataTypeException.class),
			new TestSpec(2001, 1, 1, 1, 1, 60.0F, DataTypeException.class),
			new TestSpec(2001, 1, 1, 1, 1, 1.12F, "20010101010101.12"),
			new TestSpec(2001, 1, 1, 0, 0, 23.456F, "20010101000023.456"),
			new TestSpec(2001, 1, 1, 8, 15, 1.0F, "20010101081501" ),
			new TestSpec(2001, 1, 7, 13, 27, 1.0F, "20010107132701" ),
			new TestSpec(2001, 1, 12, 23, 59, 1.0F, "20010112235901" ),
			new TestSpec(2001, 1, 12, 23, 59, 1.0F, "20010112235901"),
			new TestSpec(2001, 1, 12, 23, 60, 1.0F, DataTypeException.class),
			new TestSpec(2001, 1, 12, 24, 59, 1.0F, DataTypeException.class),
			new TestSpec(2001, 1, 12, 24, 60, 1.0F, DataTypeException.class),
			new TestSpec(2001, 1, 25, 1, 1, 1.0F, "20010125010101"),
			new TestSpec(2001, 1, 31, 1, 1, 1.0F, "20010131010101"),
			new TestSpec(2001, 1, 32, 1, 1, 1.0F, DataTypeException.class),
			new TestSpec(9999, 1, 1, 1, 1, 1.0F, "99990101010101"),
			new TestSpec(10000, 1, 1, 1, 1, 1.0F, DataTypeException.class),
			new TestSpec(2001, 1, 1, 1, 1, 1.0F, "20010101010101"),
			new TestSpec(2001, -1, 21, 1, 1, 1.0F, DataTypeException.class),
		};
		
		ArrayList failedTests = new ArrayList();

		for (int i = 0; i < tests.length ; i++) {
			if ( ! tests[ i ].executeTest() ) 
         		failedTests.add( tests[ i ] );
		}

   		assertEquals("Failures: " + failedTests, 0, failedTests.size()); 
	}

	/**
	 * Test set/getOffset.  Testspec constructor sets up a value
	 * of "154638" so we can get a value back
	 */
	public void testSetGetOffset() {
		
		class TestSpec {
			int offset;
			Object outcome;
			Exception exception = null;
			
			TestSpec(int offset, Object outcome) {
				this.offset = offset;
				this.outcome = outcome;
			}
			
			public String toString() {
				return "[ " + Integer.toString(offset) + " : "
					+ (outcome != null ? outcome : "null")
 			    	+ (exception != null ? " [ " + exception.toString() + " ]":" ]");
			}
			
			public boolean executeTest() {
				try {
					CommonTS ts = new CommonTS(timeStamp);
					ts.setOffset(offset);
					String retval = ts.getValue();
					if (retval != null) {
						return retval.equals(outcome);
					} else {
						return outcome == null;
					}
				} catch (Exception e) {
					if (e.getClass().equals(outcome)) {
						return true;
					} else {
						this.exception = e;
						return (e.getClass().equals(outcome));
					}
				}
			}
		}//inner class
    		
		TestSpec [] tests = {
			new TestSpec(-0000, baseTime + "+0000"),
			new TestSpec(-1160, DataTypeException.class),
			new TestSpec(-1159, baseTime + "-1159"),
			new TestSpec(2106, baseTime + "+2106"),
			new TestSpec(21064, DataTypeException.class),
			new TestSpec(-24, baseTime + "-0024"), 
			new TestSpec(800, baseTime + "+0800"),
			new TestSpec(1300, baseTime + "+1300"),
			//new TestSpec(24, DataTypeException.class),
			new TestSpec(2300, baseTime + "+2300"),
		};
		
		ArrayList failedTests = new ArrayList();

		for (int i = 0; i < tests.length ; i++) {
			if ( ! tests[ i ].executeTest() ) 
         		failedTests.add( tests[ i ] );
		}

   		assertEquals("Failures: " + failedTests, 0, failedTests.size()); 
	}

	/**
	 * Testing ability to retrieve year value
	 */
    public void testGetYear() throws DataTypeException {
		commonTS = new CommonTS(timeStamp);
		assertEquals("Should get year back", year, commonTS.getYear());
    }
    
	/**
	 * Testing ability to retrieve month value
	 */
    public void testGetMonth() throws DataTypeException {
		commonTS = new CommonTS(timeStamp);
		assertEquals("Should get month back", month, commonTS.getMonth());
    }
    
	/**
	 * Testing ability to retrieve day value
	 */
    public void testGetDay() throws DataTypeException {
		commonTS = new CommonTS(timeStamp);
		assertEquals("Should get day back", day, commonTS.getDay());
    }

	/**
	 * Testing ability to retrieve hour value
	 */
	public void testGetHour() throws DataTypeException {
		commonTS = new CommonTS(timeStamp);
		assertEquals("Should get hour back", hour, commonTS.getHour());
	}

	/**
	 * Testing ability to retrieve minute value
	 */
	public void testGetMinute() throws DataTypeException {
		commonTS = new CommonTS(timeStamp);
		assertEquals("Should get minute back", minute, commonTS.getMinute());
	}

	/**
	 * Testing ability to retrieve second value
	 */
	public void testGetSecond() throws DataTypeException {
		commonTS = new CommonTS(timeStamp);
		assertEquals("Should get seconds back", second, commonTS.getSecond());
	}

	/**
	 * Testing ability to retrieve fractional second value
	 */
	public void testGetFractSecond() throws DataTypeException {
		commonTS = new CommonTS(timeStamp);
		assertEquals("Should get fractional seconds back", fractionalSecond, commonTS.getFractSecond(), 0.0001);
	}

	/**
	 * Testing ability to retrieve GMT offset value
	 */
	public void testGetGMTOffset() throws DataTypeException {
		commonTS = new CommonTS(timeStamp);
		assertEquals("Should get GMT offset back", offset, commonTS.getGMTOffset());
        commonTS = new CommonTS("19990909+0500");
        assertEquals(500, commonTS.getGMTOffset());
	}

	public void testToHl7TMFormat() throws DataTypeException {
		GregorianCalendar cal = new GregorianCalendar();
        cal.clear();
        cal.setLenient(false);
	    cal.set(Calendar.HOUR_OF_DAY, 20);
    	cal.set(Calendar.MINUTE, 6);
	    cal.set(Calendar.SECOND, 24);
	    cal.set(Calendar.MILLISECOND, 528);
		String convertedDate = CommonTM.toHl7TMFormat(cal);
		assertEquals("Should get a HL7 formatted date back", "200624.528-0500", convertedDate);
	}
}
