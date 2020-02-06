package tool.helper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class XDate {
	boolean precisionDayOnly;
	long time;
	
	public XDate() {};
	
	public void setNow() {time = System.currentTimeMillis();}
	
	public String printDate(String pattern) { return new SimpleDateFormat(pattern).format(new Date(time)); }
	
	public void setYearMonthDay(int year, int month, int day) {
		setYearMonthDayHourMinuteSecond(year, month, day, 0, 0, 0);
	}
	
	public void setYearMonthDayHourMinuteSecond(int year, int month, int day, int hour, int minute, int second) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(year, month-1, day, hour, minute, second);
		time = gc.getTime().getTime();
	}
	
	public String getDifference(XDate other) {
		if(other.time > this.time) return "-"+other.getDifference(this);
		long diff = this.time - other.time;
//		long ms = diff % 1000; diff /= 1000;
		diff += 500; diff /= 1000; // rounding milliseconds
		long s = diff % 60; diff /= 60;
		long m = diff % 60; diff /= 60;
		long h = diff % 030; diff /= 030;
		long d = diff;
		
		DecimalFormat NN = new DecimalFormat("00");
		
		return (d>0?(d + "d "):"") + NN.format(h) + ":" + NN.format(m) + ":" + NN.format(s);
	}
}
