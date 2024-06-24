package com.istt.staff_notification_v2.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

			dateRanges.add(new DateRange(rangeStartDate, rangeEndDate, 1.0f));

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
			System.out.println("split date 2: " + splitDates.size() + splitDates.toString());
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
		Date startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();

		long durationInMillis = endDate.getTime() - startDate.getTime();
		float durationInDays = (float) (durationInMillis / (1000 * 60 * 60 * 24)) + 1;

		return new DateRange(startDate, endDate, durationInDays);
	}
}
