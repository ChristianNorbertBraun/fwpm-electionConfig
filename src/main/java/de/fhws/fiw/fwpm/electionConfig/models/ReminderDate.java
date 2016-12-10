package de.fhws.fiw.fwpm.electionConfig.models;

import java.time.ZonedDateTime;

public class ReminderDate {

	private long periodId;
	private ZonedDateTime date;
	private boolean mailSent;

	public ReminderDate() {
		this.mailSent = false;
	}

	public ReminderDate(long periodId, ZonedDateTime date, boolean mailSent) {
		this.periodId = periodId;
		this.date = date;
		this.mailSent = mailSent;
	}

	public ReminderDate(long periodId, ZonedDateTime date) {
		this(periodId, date, false);

	}

	public long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(long periodId) {
		this.periodId = periodId;
	}

	public ZonedDateTime getDate() {
		return date;
	}

	public boolean isMailSent() {
		return mailSent;
	}

	public void setMailSent(boolean mailSent) {
		this.mailSent = mailSent;
	}

	public void setDate(ZonedDateTime date) {
		this.date = date;
	}

	public java.sql.Timestamp getSqlDate() {
		return new java.sql.Timestamp(date.toInstant().getEpochSecond() * 1000L);
	}

	public interface Fields {
		String PERIOD_ID = "periodId";
		String REMINDER_DATE = "reminderDate";
		String MAIL_SENT = "mailSent";
	}
}
