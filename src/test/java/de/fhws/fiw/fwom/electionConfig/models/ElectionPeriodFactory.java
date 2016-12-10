package de.fhws.fiw.fwom.electionConfig.models;

import com.google.common.collect.ImmutableList;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by marcelgross on 23.08.16.
 */
public class ElectionPeriodFactory {

	private static final ZoneId TIME_ZONE = ZoneId.of("Europe/Berlin");


	public static ElectionPeriod getElectionPeriod(long periodId) {
		ElectionPeriod electionPeriod = new ElectionPeriod();
		electionPeriod.setPeriodId(periodId);
		electionPeriod.setStartDate(ZonedDateTime.now(TIME_ZONE).minusDays(1));
		electionPeriod.setEndDate(ZonedDateTime.now(TIME_ZONE).plusDays(1));
		electionPeriod.setReminderDates(ImmutableList.of(ReminderDateFactory.getReminderDate(periodId)));
		electionPeriod.setFwpms(ImmutableList.of(ParticipatedFwpmFactory.getParticipatedFwpm(periodId, "1"), ParticipatedFwpmFactory.getParticipatedFwpm(periodId, "2")));
		electionPeriod.setTitle("FWPM-TEST-WAHL SS16");
		electionPeriod.setIsElectionTriggered(false);

		return electionPeriod;
	}

	public static ElectionPeriod getElectionPeriod() {
		return getElectionPeriod(9999999999999l);
	}
}
