package de.fhws.fiw.fwom.electionConfig.models;

import de.fhws.fiw.fwpm.electionConfig.models.ReminderDate;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ReminderDateFactory {

	private static final ZoneId TIME_ZONE = ZoneId.of("Europe/Berlin");

	public static ReminderDate getReminderDate(long periodId) {
		ReminderDate reminderDate = new ReminderDate();
		reminderDate.setPeriodId(periodId);
		reminderDate.setDate(ZonedDateTime.now(TIME_ZONE));
		return reminderDate;
	}

	public static ReminderDate getReminderDate() {
		return getReminderDate(9999999999999l);
	}
}
