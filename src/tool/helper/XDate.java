package tool.helper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class XDate {
	
	public String getNow() {return System.currentTimeMillis()+"";}
	
	public String printDate(String timeString, String pattern) { return new SimpleDateFormat(pattern).format(new Date(Long.parseLong(timeString))); }
	
	public static LocalDate parseStringToLocalDate(String dateString, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH);
		LocalDate localDate = LocalDate.parse(dateString, formatter);
		return localDate;
	}
	
	public String getYearMonthDay(int year, int month, int day) {
		return getYearMonthDayHourMinuteSecond(year, month, day, 0, 0, 0);
	}
	
	public String getYearMonthDayHourMinuteSecond(int year, int month, int day, int hour, int minute, int second) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(year, month-1, day, hour, minute, second);
		return gc.getTime().getTime()+"";
	}
	
	public String getDifference(String thisTimeS, String thatTimeS) {
		long thisTime = Long.parseLong(thisTimeS);
		long thatTime = Long.parseLong(thatTimeS);
		
		if(thatTime > thisTime) return "-"+getDifference(thatTimeS, thisTimeS);
		long diff = thisTime - thatTime;
		diff += 500; diff /= 1000; // rounding milliseconds
		long s = diff % 60; diff /= 60;
		long m = diff % 60; diff /= 60;
		long h = diff % 030; diff /= 030;
		long d = diff;
		
		DecimalFormat NN = new DecimalFormat("00");
		
		return (d>0?(d + "d "):"") + NN.format(h) + ":" + NN.format(m) + ":" + NN.format(s);
	}
	
	public Integer age(String time) {
		try {
			long thisTime = Long.parseLong(time);
			long now = System.currentTimeMillis();
			long diff = now - thisTime;
			long YEAR = 31_556_952_000l;
			long years = diff / YEAR;
			int yearsI = (int) years;
			return yearsI;
		} catch (Exception any) {
			return null;
		}
	}
	
	private Integer get(String timeString, int FIELD) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(new Date(Long.parseLong(timeString)));
		return gc.get(FIELD);
	}
	
	public Integer getYear(String timeString) { return get(timeString, GregorianCalendar.YEAR); }
	public Integer getMonth(String timeString) { return get(timeString, GregorianCalendar.MONTH); }
	public Integer getDay(String timeString) { return get(timeString, GregorianCalendar.DAY_OF_MONTH); }
	public Integer getHour(String timeString) { return get(timeString, GregorianCalendar.HOUR_OF_DAY); }
	public Integer getMinute(String timeString) { return get(timeString, GregorianCalendar.MINUTE); }
	public Integer getSecond(String timeString) { return get(timeString, GregorianCalendar.SECOND); }
}
