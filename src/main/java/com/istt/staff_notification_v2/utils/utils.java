package com.istt.staff_notification_v2.utils;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.istt.staff_notification_v2.entity.Attendance;
import com.istt.staff_notification_v2.entity.BusinessDays;

@Service
public class utils {
	public static class DateRange {
		private Date startDate;
		private Date endDate;
		private float duration;

		public DateRange(Date startDate, Date endDate, float duration) {
			this.startDate = startDate;
			this.endDate = endDate;
			this.duration = duration;
		}
		public DateRange() {
		}

		public Date getStartDate() {
			return startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public float getDuration() {
			return duration;
		}

		@Override
		public String toString() {
			return "DateRange{" + "startDate=" + startDate + ", endDate=" + endDate + ", duration=" + duration + " days"
					+ '}';
		}
	}
	
	public static String toStringDate(Date date){

        // Create a SimpleDateFormat object and define the desired date format
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        // Format the date
        String formattedDate = formatter.format(date);

        return formattedDate;
    }

	public static Date calculatorEndDate(Date startDate, float duration) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			long durationInMillis = (long) (duration * 24 * 3600 * 1000);

			calendar.setTimeInMillis(calendar.getTimeInMillis() + durationInMillis);

			return calendar.getTime();

		} catch (Exception e) {
			System.out.println("Exception :" + e);
			return null;
		}
	}
	
	public static DateRange getCurrentWeek() {
		Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        Date sevenDaysBefore = calendar.getTime();

		return new DateRange(today, sevenDaysBefore,7);
	}

	public static Date resetStartDate(Date startDate) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime();

		} catch (Exception e) {
			System.out.println("Exception :" + e);
			return null;
		}
	}

	public static List<DateRange> splitDateRange(Date startDate, float totalDuration) {
		List<DateRange> dateRanges = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);

		while (totalDuration > 0) {
			Date rangeStartDate = calendar.getTime();

			float currentDuration = Math.min(totalDuration, 1.0f);
			if (totalDuration < 1 && totalDuration > 0) {
				currentDuration = totalDuration;
			}

			Calendar tempCal = (Calendar) calendar.clone();
			
			int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY||dayOfWeek == Calendar.SUNDAY) {
                //System.err.println(calendar.getTime()+ "is SATURDAY, SUNDAY");
                calendar.add(Calendar.HOUR_OF_DAY, 12);
                continue;
            }
			
//            System.err.println(calendar.getTime());
			
			
			if (currentDuration == 0.5f) {
				tempCal.add(Calendar.HOUR_OF_DAY, 12);
			} else {
				tempCal.add(Calendar.DAY_OF_MONTH, (int) currentDuration - 1);
				if (currentDuration % 1 != 0) {
					tempCal.add(Calendar.HOUR_OF_DAY, 12);
				}
			}
			Date rangeEndDate = tempCal.getTime();

			dateRanges.add(new DateRange(rangeStartDate, rangeEndDate, currentDuration));

			calendar.setTime(rangeEndDate);
			if (currentDuration == 0.5f) {
				calendar.add(Calendar.HOUR_OF_DAY, 12);
			} else {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			}

			totalDuration -= currentDuration;
		}

		return dateRanges;
	}

	public static List<DateRange> splitDateByRange(Date startDate, Date endDate) {
		List<DateRange> dateRanges = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		while (!calendar.getTime().after(endDate)) {
			Date rangeStartDate = calendar.getTime();

			// Move to the end of the current day
			Calendar tempCal = (Calendar) calendar.clone();
			tempCal.add(Calendar.DAY_OF_MONTH, 1);
			tempCal.add(Calendar.MILLISECOND, -1);
			
			

			Date rangeEndDate = tempCal.getTime();
			if (rangeEndDate.after(endDate)) {
				rangeEndDate = endDate;
			}
			
			int dayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
            	dateRanges.add(new DateRange(rangeStartDate, rangeEndDate, 1.0f));
            }

			// Move to the next day
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}

		return dateRanges;
	}

	public static List<Attendance> handleSplitAttendence(Attendance attendance) {
		try {
			List<DateRange> splitDates = splitDateRange(attendance.getStartDate(), attendance.getDuration());
//			System.out.println("split date: " + splitDates.toString());
			List<Attendance> attendanceSplits = new ArrayList<>();

			if (splitDates.size() > 1) {
				for (DateRange splitDate : splitDates) {

					Attendance attendanceSplit = new Attendance();
					attendanceSplit.setAttendanceId(UUID.randomUUID().toString().replaceAll("-", ""));
					attendanceSplit.setApprovedBy(attendance.getApprovedBy());
					attendanceSplit.setCreateAt(attendance.getCreateAt());
					attendanceSplit.setEmployee(attendance.getEmployee());
					attendanceSplit.setUpdateAt(attendance.getUpdateAt());
					attendanceSplit.setUpdateBy(attendance.getUpdateBy());
					attendanceSplit.setLeaveRequest(attendance.getLeaveRequest());
					attendanceSplit.setLeaveType(attendance.getLeaveType());
					attendanceSplit.setStartDate(splitDate.getStartDate());
					attendanceSplit.setEndDate(splitDate.getEndDate());
					attendanceSplit.setDuration(splitDate.getDuration());
					attendanceSplit.setNote(attendance.getNote());
//					handle fill index
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(splitDate.getStartDate());
					attendanceSplit.setYear(Long.valueOf(calendar.get(Calendar.YEAR)));
					attendanceSplit.setMonth(Long.valueOf(calendar.get(Calendar.MONTH) + 1));
					attendanceSplit.setDay(Long.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

					attendanceSplits.add(attendanceSplit);

				}
			} else {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(attendance.getStartDate());
				attendance.setYear(Long.valueOf(calendar.get(Calendar.YEAR)));
				attendance.setMonth(Long.valueOf(calendar.get(Calendar.MONTH) + 1));
				attendance.setDay(Long.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
				attendanceSplits.add(attendance);
			}
			return attendanceSplits;

		} catch (Exception e) {
			System.out.println("Exception :" + e);
			return null;
		}
	}

	public static List<BusinessDays> handleSplitBusinessDays(BusinessDays businessDays) {
		try {
			List<DateRange> splitDates = splitDateByRange(businessDays.getStartdate(), businessDays.getEnddate());
//			System.out.println("split date 2: " + splitDates.size() + splitDates.toString());
			List<BusinessDays> businessDaysSplits = new ArrayList<>();
			if (splitDates.size() > 1) {
				for (DateRange splitDate : splitDates) {
					BusinessDays businessDaysSplit = new BusinessDays();
					businessDaysSplit.setBussinessdaysId(UUID.randomUUID().toString().replaceAll("-", ""));
					businessDaysSplit.setStartdate(splitDate.getStartDate());
					businessDaysSplit.setEnddate(splitDate.getEndDate());
					businessDaysSplit.setType(businessDays.getType());
					businessDaysSplit.setDescription(businessDays.getDescription());

					businessDaysSplits.add(businessDaysSplit);

				}
			} else {
				businessDaysSplits.add(businessDays);
			}
			return businessDaysSplits;

		} catch (Exception e) {
			System.out.println("Exception :" + e);
			return null;
		}
	}

	public static DateRange getCurrentMonth() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date endDate = calendar.getTime();

		long durationInMillis = endDate.getTime() - startDate.getTime();
		float durationInDays = (float) (durationInMillis / (1000 * 60 * 60 * 24)) + 1;

		return new DateRange(startDate, endDate, durationInDays);
	}
	public static String changeName(String input) {
        // Normalize the input string to decompose accents
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Remove the accents using a regular expression
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String result = pattern.matcher(normalized).replaceAll("");
        // Replace special Vietnamese characters
        result = result.replaceAll("Đ", "D").replaceAll("đ", "d");
        int lastSpaceIndex = input.lastIndexOf(' ');
        if (lastSpaceIndex == -1) {
            return result; // No space found, return the whole string
        }
        String lastWord = result.substring(lastSpaceIndex + 1);
        String remainingString = result.substring(0, lastSpaceIndex);
        return lastWord + " " + remainingString;
    }
	
	 public static Date format(String dateString){
	        String dateFormat = "dd/MM/yyyy";

	        Date currentDate = new Date();

	        // Create a SimpleDateFormat object
	        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

	        try {
	            // Parse the string into a Date object
	            currentDate = formatter.parse(dateString);

	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        return currentDate;
	    }
	
	public static DateRange getDate(Date date) {
		DateRange dateRange = new DateRange();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		dateRange.startDate= calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 0);
		dateRange.endDate= calendar.getTime();
		return dateRange;
	}
	
	
}
