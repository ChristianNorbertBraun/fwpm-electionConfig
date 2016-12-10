package de.fhws.fiw.fwom.electionConfig;

import com.google.common.base.Optional;
import de.fhws.fiw.fwom.electionConfig.models.ElectionPeriodFactory;
import de.fhws.fiw.fwpm.electionConfig.database.CrudFacade;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ElectionAdminIT {

	private CrudFacade crudFacade;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void init() throws Exception {
		crudFacade = CrudFacade.getInstance();
	}

	@Ignore
	@Test
	public void createTestData() throws Exception {
		ElectionPeriod electionPeriod = ElectionPeriodFactory.getElectionPeriod(1234567890);

		Optional<ElectionPeriod> savedPeriod = crudFacade.save(electionPeriod);
	}
}
