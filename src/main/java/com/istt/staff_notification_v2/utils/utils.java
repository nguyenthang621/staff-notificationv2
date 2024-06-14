package com.istt.staff_notification_v2.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.istt.staff_notification_v2.entity.Attendance;

@Service
public class utils {

	public static class DateRange {
		private Date startDate;
		private Date endDate;
		private float duration; // duration in days

		public DateRange(Date startDate, Date endDate) {
			this.startDate = startDate;
			this.endDate = endDate;
			this.duration = calculateDuration(startDate, endDate);
		}

		private float calculateDuration(Date startDate, Date endDate) {
			long diffInMillies = endDate.getTime() - startDate.getTime();
			float diffInHours = (float) diffInMillies / (1000 * 60 * 60);
			float diffInDays = diffInHours / 24;

			return (float) (Math.round(diffInDays * 2) / 2.0);
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

			System.out.println("calendar: " + calendar);
			System.out.println("calendar.getTime(): " + calendar.getTime());
			return calendar.getTime();

		} catch (Exception e) {
			System.out.println("Exception :" + e);
			return null;
		}
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

	public static List<DateRange> splitDates(Date start, Date end) {
		List<DateRange> dateRanges = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();

		// Set the start date
		calendar.setTime(start);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date currentEnd = calendar.getTime();

		while (currentEnd.before(end)) {
			dateRanges.add(new DateRange(start, currentEnd));
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			start = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			currentEnd = calendar.getTime();
		}

		dateRanges.add(new DateRange(start, end));

		return dateRanges;
	}

	public static List<Attendance> handleSplitAttendence(Attendance attendance) {
		try {
			List<DateRange> splitDates = splitDates(attendance.getStartDate(), attendance.getEndDate());
			System.out.println("split date: " + splitDates.toString());
			List<Attendance> attendanceSplits = new ArrayList<>();

			if (splitDates.size() > 1) {
				for (DateRange splitDate : splitDates) {

					Attendance attendanceSplit = new Attendance();
					attendanceSplit.setAttendanceId(UUID.randomUUID().toString().replaceAll("-", ""));
					attendanceSplit.setApprovedBy(attendance.getApprovedBy());
					attendanceSplit.setCreateAt(attendance.getCreateAt());
					attendanceSplit.setEmployee(attendance.getEmployee());
					attendanceSplit.setApprovedBy(attendance.getApprovedBy());

					attendanceSplit.setStartDate(splitDate.getStartDate());
					attendanceSplit.setEndDate(splitDate.getEndDate());
					attendanceSplit.setDuration(splitDate.getDuration());
					attendanceSplits.add(attendanceSplit);
				}
			} else {
				attendanceSplits.add(attendance);
			}
			return attendanceSplits;

		} catch (Exception e) {
			System.out.println("Exception :" + e);
			return null;
		}
	}

}
