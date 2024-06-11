package com.istt.staff_notification_v2.utils;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class utils {

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

}
