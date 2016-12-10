package de.fhws.fiw.fwom.electionConfig.models.dto;

import de.fhws.fiw.fwom.electionConfig.models.ElectionPeriodFactory;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;
import de.fhws.fiw.fwpm.electionConfig.models.dto.ElectionPeriodResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class ElectionPeriodResponseMapperTest {

	@Test
	public void mapperTest() {
		ElectionPeriod electionPeriod = ElectionPeriodFactory.getElectionPeriod();

		ElectionPeriodResponse electionPeriodResponse = ElectionPeriodResponse.toDto(electionPeriod);
		ElectionPeriod entity = ElectionPeriodResponse.toEntity(electionPeriodResponse);

		assertTrue(comparePeriods(electionPeriod, entity));
	}

	private boolean comparePeriods(ElectionPeriod expected, ElectionPeriod given) {
		return expected.getPeriodId() == given.getPeriodId() && expected.getTitle().equals(given.getTitle()) && expected.getReminderDates().size() == given.getReminderDates().size() && expected.getFwpms().size() == given.getFwpms().size() && expected.isElectionTriggered() == given.isElectionTriggered();
	}
}
