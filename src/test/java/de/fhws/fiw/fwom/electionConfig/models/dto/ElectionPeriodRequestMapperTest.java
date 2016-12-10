package de.fhws.fiw.fwom.electionConfig.models.dto;

import de.fhws.fiw.fwom.electionConfig.models.ElectionPeriodFactory;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;
import de.fhws.fiw.fwpm.electionConfig.models.dto.ElectionPeriodRequest;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by marcelgross on 23.08.16.
 */
public class ElectionPeriodRequestMapperTest {

	@Test
	public void mapperTest() {
		ElectionPeriod electionPeriod = ElectionPeriodFactory.getElectionPeriod();

		ElectionPeriodRequest electionPeriodRequest = ElectionPeriodRequest.toDto(electionPeriod);
		ElectionPeriod entity = ElectionPeriodRequest.toEntity(electionPeriodRequest, electionPeriod.getPeriodId());

		assertTrue(comparePeriods(electionPeriod, entity));
	}

	private boolean comparePeriods(ElectionPeriod expected, ElectionPeriod given) {
		return expected.getPeriodId() == given.getPeriodId() && expected.getTitle().equals(given.getTitle()) && expected.getReminderDates().size() == given.getReminderDates().size() && expected.getFwpms().size() == given.getFwpms().size() && expected.isElectionTriggered() == given.isElectionTriggered();
	}
}
