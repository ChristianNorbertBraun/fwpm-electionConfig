package de.fhws.fiw.fwom.electionConfig.database;

import de.fhws.fiw.fwom.electionConfig.models.ElectionPeriodFactory;
import de.fhws.fiw.fwpm.electionConfig.database.CrudFacade;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.TableException;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;
import de.fhws.fiw.fwpm.electionConfig.models.ReminderDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;
import com.google.common.base.Optional;

import static org.junit.Assert.*;

public class CrudFacadeTest {

	private final long periodId = 999999999999l;

	private CrudFacade crudFacade;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void init() throws Exception {
		crudFacade = CrudFacade.getInstance();
	}

	@Test
	public void getInstanceTest() throws TableException {
		CrudFacade crudFacade = CrudFacade.getInstance();

		assertNotNull(crudFacade);
	}

	@Test
	public void readAllTest() throws Exception {
		Optional<ElectionPeriod> savedPeriod = crudFacade.save(ElectionPeriodFactory.getElectionPeriod(periodId));

		List<ElectionPeriod> electionPeriods = crudFacade.readAll();

		assertTrue(electionPeriods.size() > 0);

		clean(savedPeriod.get().getPeriodId());
	}

	@Test
	public void readByIdTest() throws Exception {
		ElectionPeriod electionPeriod = ElectionPeriodFactory.getElectionPeriod(periodId);

		Optional<ElectionPeriod> savedPeriod = crudFacade.save(electionPeriod);

		Optional<ElectionPeriod> readPeriod = crudFacade.readById(savedPeriod.get().getPeriodId());

		assertTrue(readPeriod.isPresent());
		assertTrue(comparePeriods(electionPeriod, readPeriod.get()));

		clean(savedPeriod.get().getPeriodId());
	}

	@Test
	public void getLatestPeriodTest() throws Exception {
		ElectionPeriod electionPeriod = ElectionPeriodFactory.getElectionPeriod(periodId);

		Optional<ElectionPeriod> savedPeriod = crudFacade.save(electionPeriod);

		Optional<ElectionPeriod> latestPeriod = crudFacade.getLatestPeriod();

		assertTrue(latestPeriod.isPresent());

		List<ElectionPeriod> electionPeriods = crudFacade.readAll();

		assertTrue(isLatest(electionPeriods, latestPeriod.get()));

		clean(savedPeriod.get().getPeriodId());
	}

	@Test
	public void updateTest() throws Exception {
		String title = "New Title";
		ElectionPeriod electionPeriod = ElectionPeriodFactory.getElectionPeriod(periodId);

		Optional<ElectionPeriod> savedPeriod = crudFacade.save(electionPeriod);
		savedPeriod.get().setTitle(title);

		Optional<ElectionPeriod> updatedPeriod = crudFacade.update(savedPeriod.get());

		assertEquals(updatedPeriod.get().getTitle(), title);

		clean(savedPeriod.get().getPeriodId());
	}

	@Test
	public void updateReminderDateTest() throws Exception {
		ElectionPeriod electionPeriod = ElectionPeriodFactory.getElectionPeriod(periodId);

		Optional<ElectionPeriod> savedPeriod = crudFacade.save(electionPeriod);

		ReminderDate reminderDate = savedPeriod.get().getReminderDates().get(0);
		reminderDate.setMailSent(true);

		Optional<ReminderDate> reminderDateOptional = crudFacade.updateReminderDate(reminderDate);

		assertTrue(reminderDateOptional.get().isMailSent());

		clean(savedPeriod.get().getPeriodId());

	}

	private boolean isLatest(List<ElectionPeriod> electionPeriods, ElectionPeriod latest) {
		for (ElectionPeriod electionPeriod : electionPeriods) {
			if (electionPeriod.getPeriodId() != latest.getPeriodId()) {
				if (latest.getEndDate().isBefore(electionPeriod.getEndDate()))
					return false;
			}
		}
		return true;
	}

	private void clean(long periodId) throws Exception {
		crudFacade.delete(periodId);
	}

	private boolean comparePeriods(ElectionPeriod expected, ElectionPeriod given) {
		return expected.getPeriodId() == given.getPeriodId() && expected.getTitle().equals(given.getTitle()) && expected.getReminderDates().size() == given.getReminderDates().size() && expected.getFwpms().size() == given.getFwpms().size() && expected.isElectionTriggered() == given.isElectionTriggered();
	}
}
