package de.fhws.fiw.fwpm.electionConfig.models;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ElectionPeriod {

	private long periodId;
	private ZonedDateTime startDate;
	private ZonedDateTime endDate;
	private List<ReminderDate> reminderDates;
	private List<ParticipatedFwpm> fwpms;
	private String title;
	private boolean isElectionTriggered;

	public ElectionPeriod() {
		fwpms = new ArrayList<>();
	}

	public long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(long periodId) {
		this.periodId = periodId;
	}

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public ZonedDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(ZonedDateTime endDate) {
		this.endDate = endDate;
	}

	public List<ParticipatedFwpm> getFwpms() {
		return fwpms;
	}

	public void setFwpms(List<ParticipatedFwpm> fwpms) {
		this.fwpms = fwpms;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<ReminderDate> getReminderDates() {
		return reminderDates;
	}

	public void setReminderDates(List<ReminderDate> reminderDates) {
		this.reminderDates = reminderDates;
	}

	public boolean isElectionTriggered() {
		return isElectionTriggered;
	}

	public ElectionPeriod setIsElectionTriggered(boolean isElectionTriggered) {
		this.isElectionTriggered = isElectionTriggered;
		return this;
	}

	public java.sql.Timestamp getSqlEndDate() {
		return new java.sql.Timestamp(endDate.toInstant().getEpochSecond() * 1000L);
	}

	public java.sql.Timestamp getSqlStartDate() {
		return new java.sql.Timestamp(startDate.toInstant().getEpochSecond() * 1000L);
	}

	public interface Fields {
		String PERIOD_ID = "periodId";
		String START_DATE = "startDate";
		String END_DATE = "endDate";
		String TITLE = "title";
		String TRIGGERED = "electionTriggered";
	}

	@Override
	public String toString() {
		return "ElectionPeriod{" +
				"periodId=" + periodId +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", fwpms=" + fwpms +
				", title='" + title + '\'' +
				", triggered=" + isElectionTriggered +
				'}';
	}
}
