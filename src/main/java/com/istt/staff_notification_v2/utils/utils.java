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
			System.out.println(startDate + "--------" + endDate);
			long diffInMillies = endDate.getTime() - startDate.getTime();
			float diffInHours = (float) diffInMillies / (1000 * 60 * 60);
			float diffInDays = diffInHours / 24;
			System.out.println("dureation: " + (float) (Math.round(diffInDays * 2) / 2.0));
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
		calendar.setTime(start);

		while (calendar.getTime().before(end)) {
			Date startDate = calendar.getTime();

			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = calendar.getTime();

			if (endDate.after(end)) {
				endDate = end;
			} else {
				Calendar tempCal = (Calendar) calendar.clone();
				tempCal.add(Calendar.DAY_OF_MONTH, 1);
				endDate = tempCal.getTime();
			}

			dateRanges.add(new DateRange(startDate, endDate));

			calendar.setTime(endDate);
			if (calendar.getTime().before(end)) {
				calendar.add(Calendar.DAY_OF_MONTH, 0);
			}
		}

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
					attendanceSplit.setUpdateAt(attendance.getUpdateAt());
					attendanceSplit.setUpdateBy(attendance.getUpdateBy());
					attendanceSplit.setLeaveRequest(attendance.getLeaveRequest());
					attendanceSplit.setLeavetype(attendance.getLeavetype());
					attendanceSplit.setStartDate(splitDate.getStartDate());
					attendanceSplit.setEndDate(splitDate.getEndDate());
					attendanceSplit.setDuration(splitDate.getDuration());
					attendanceSplit.setNote(attendance.getNote());
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
