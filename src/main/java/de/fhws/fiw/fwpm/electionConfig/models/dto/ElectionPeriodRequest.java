package de.fhws.fiw.fwpm.electionConfig.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.owlike.genson.annotation.JsonIgnore;
import com.webcohesion.enunciate.metadata.DocumentationExample;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;
import de.fhws.fiw.fwpm.electionConfig.models.ParticipatedFwpm;
import de.fhws.fiw.fwpm.electionConfig.models.ReminderDate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This Data Transfer Object (DTO) of an ElectionPeriod, contains all necessary attributes, which are needed to create, or update a ElectionPeriod.
 */
public class ElectionPeriodRequest {
	@JsonIgnore
	private static final ZoneId TIME_ZONE = ZoneId.of("Europe/Berlin");
	@JsonIgnore
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm");

	/**
	 * Start-date, indicates when the period begins. Pattern for this date <b>dd.MM.yyyy kk:mm</b>.
	 */
	@DocumentationExample("01.01.2016 00:00")
	@JsonProperty
	private String startDate;

	/**
	 * End-date, indicates when the period ends. Pattern for this date <b>dd.MM.yyyy kk:mm</b>.
	 */
	@DocumentationExample("01.02.2016 00:00")
	@JsonProperty
	private String endDate;

	/**
	 * List of dates, each of it indicates when the service should remind remind students. Pattern for a date <b>dd.MM.yyyy kk:mm</b>.
	 */
	@DocumentationExample(value = "19.01.2016 00:00", value2 = "25.01.2016 00:00")
	@JsonProperty
	private List<String> reminderDates;

	/**
	 * List of FWPM ids, each of it represent a FWPM which participates this period.
	 */
	@DocumentationExample(value = "1", value2 = "2")
	@JsonProperty
	private List<Long> fwpms;

	/**
	 * Title of the current period.
	 */
	@DocumentationExample("FWPM-Wahl SS16")
	@JsonProperty
	private String title;

	public ElectionPeriodRequest() {
	}

	public ElectionPeriodRequest(String startDate, String endDate, List<String> reminderDates, List<Long> fwpms, String title) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.reminderDates = reminderDates;
		this.fwpms = fwpms;
		this.title = title;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<String> getReminderDates() {
		return reminderDates;
	}

	public void setReminderDates(List<String> reminderDates) {
		this.reminderDates = reminderDates;
	}

	public List<Long> getFwpms() {
		return fwpms;
	}

	public void setFwpms(List<Long> fwpms) {
		this.fwpms = fwpms;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public static ElectionPeriodRequest toDto(ElectionPeriod entity) {
		ElectionPeriodRequest dto = new ElectionPeriodRequest();
		dto.setStartDate(entity.getStartDate().format(FORMATTER));
		dto.setEndDate(entity.getEndDate().format(FORMATTER));
		dto.setReminderDates(getReminderDates(entity.getReminderDates()));
		dto.setFwpms(getFwpms(entity.getFwpms()));
		dto.setTitle(entity.getTitle());

		return dto;
	}

	public static ElectionPeriod toEntity(ElectionPeriodRequest dto, long periodId) {
		ElectionPeriod electionPeriod = new ElectionPeriod();
		electionPeriod.setPeriodId(periodId);
		electionPeriod.setStartDate(ZonedDateTime.of(LocalDateTime.parse(dto.getStartDate(), FORMATTER), TIME_ZONE));
		electionPeriod.setEndDate(ZonedDateTime.of(LocalDateTime.parse(dto.getEndDate(), FORMATTER), TIME_ZONE));
		electionPeriod.setReminderDates(dto.getReminderDates().stream()
				.map(reminderDate -> new ReminderDate(periodId, ZonedDateTime.of(LocalDateTime.parse(reminderDate, FORMATTER), TIME_ZONE)))
				.collect(Collectors.toList()));
		electionPeriod.setFwpms(dto.getFwpms().stream()
				.map(fwpmId -> new ParticipatedFwpm(periodId, String.valueOf(fwpmId))).collect(Collectors.toList()));
		electionPeriod.setTitle(dto.getTitle());

		return electionPeriod;
	}

	private static List<String> getReminderDates(List<ReminderDate> reminderDates) {
		return reminderDates.stream().map(currentReminderDate -> currentReminderDate.getDate().format(FORMATTER))
				.collect(Collectors.toList());
	}
	private static List<Long> getFwpms(List<ParticipatedFwpm> participatedFwpms) {
		return participatedFwpms.stream().map(participatedFwpm -> Long.valueOf(participatedFwpm.getFwpmId()))
				.collect(Collectors.toList());
	}


}
