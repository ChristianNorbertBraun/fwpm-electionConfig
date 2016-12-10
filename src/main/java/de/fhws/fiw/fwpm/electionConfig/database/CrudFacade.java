package de.fhws.fiw.fwpm.electionConfig.database;

import com.google.common.base.Optional;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.CRUDException;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.DAOException;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.NotFoundException;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.TableException;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;
import de.fhws.fiw.fwpm.electionConfig.models.ParticipatedFwpm;
import de.fhws.fiw.fwpm.electionConfig.models.ReminderDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class CrudFacade {

	private DAOFactory daoFactory;
	private static CrudFacade instance;

	public static CrudFacade getInstance() throws TableException {
		if (instance == null) {
			instance = new CrudFacade();
		}
		return instance;
	}

	private CrudFacade() throws TableException {
		daoFactory = DAOFactory.getInstance();
	}

	public List<ElectionPeriod> readAll() throws NotFoundException {
		try {
			return iterateAndAddDatesAndFwpms(daoFactory.createElectionPeriodDAO().listAll());
		} catch (Exception ex) {
			throw new NotFoundException(ex);
		}

	}

	public Optional<ElectionPeriod> readById(long periodId) throws NotFoundException {
		Optional<ElectionPeriod> returnValue;
		try {
			returnValue = daoFactory.createElectionPeriodDAO().findByPeriod(periodId);
			if (returnValue.isPresent()) {
				returnValue = Optional.fromNullable(addDatesAndFwpms(returnValue.get()));
			}
			return returnValue;
		} catch (Exception ex) {
			throw new NotFoundException(ex);
		}
	}

	public Optional<ElectionPeriod> getLatestPeriod() throws NotFoundException {
		Optional<ElectionPeriod> returnValue;
		try {
			returnValue = daoFactory.createElectionPeriodDAO().getLatest();
			if (returnValue.isPresent()) {
				returnValue = Optional.fromNullable(addDatesAndFwpms(returnValue.get()));
			}
		} catch (Exception ex) {
			throw new NotFoundException(ex);
		}

		return returnValue;
	}

	public Optional<ElectionPeriod> save(ElectionPeriod electionPeriod) throws CRUDException {
		Optional<ElectionPeriod> returnValue = Optional.fromNullable(electionPeriod);
		Optional<ElectionPeriod> savedInstance;
		try {
			savedInstance = daoFactory.createElectionPeriodDAO().create(electionPeriod);

			returnValue.get().setPeriodId(savedInstance.get().getPeriodId());
			saveReminderDates(returnValue.get());
			saveParticipatedFwpm(returnValue.get());

		} catch (SQLException | DAOException ex) {
			throw new CRUDException(ex);
		}

		return returnValue;
	}

	public Optional<ElectionPeriod> update(ElectionPeriod electionPeriod) throws CRUDException, NotFoundException {

		try {
			updateDates(electionPeriod);
			updateFwpms(electionPeriod);
			return daoFactory.createElectionPeriodDAO().update(electionPeriod);
		} catch (DAOException ex) {
			throw new CRUDException(ex);
		} catch (SQLException ex) {
			throw new NotFoundException(ex);
		}

	}

	public void delete(long periodId) throws CRUDException {
		try {
			daoFactory.createReminderDateDAO().deleteByPeriodId(periodId);
			daoFactory.createParticipatedFwpmDAO().deleteByPeriodId(periodId);
			daoFactory.createElectionPeriodDAO().delete(periodId);

		} catch (Exception ex) {
			throw new CRUDException(ex);
		}

	}

	public Optional<ReminderDate> updateReminderDate(ReminderDate reminderDate) throws CRUDException, NotFoundException {
		try {
			return daoFactory.createReminderDateDAO().update(reminderDate);
		} catch (DAOException ex) {
			throw new CRUDException(ex);
		} catch (SQLException ex) {
			throw new NotFoundException(ex);
		}
	}

	private void updateDates(ElectionPeriod electionPeriod) throws CRUDException {
		try {
			daoFactory.createReminderDateDAO().deleteByPeriodId(electionPeriod.getPeriodId());

			saveReminderDates(electionPeriod);

		} catch (Exception ex) {
			throw new CRUDException(ex);
		}
	}

	private void updateFwpms(ElectionPeriod electionPeriod) throws CRUDException {
		try {
			daoFactory.createParticipatedFwpmDAO().deleteByPeriodId(electionPeriod.getPeriodId());

			saveParticipatedFwpm(electionPeriod);
		} catch (Exception ex) {
			throw new CRUDException(ex);
		}

	}

	private void saveReminderDates(ElectionPeriod electionPeriod) throws CRUDException {
		for (ReminderDate date : electionPeriod.getReminderDates()) {
			try {
				date.setPeriodId(electionPeriod.getPeriodId());
				daoFactory.createReminderDateDAO().create(date);
			} catch (Exception ex) {
				throw new CRUDException(ex);
			}
		}
	}

	private void saveParticipatedFwpm(ElectionPeriod electionPeriod) throws CRUDException {
		for (ParticipatedFwpm fwpm : electionPeriod.getFwpms()) {
			try {
				fwpm.setPeriodId(electionPeriod.getPeriodId());
				daoFactory.createParticipatedFwpmDAO().create(fwpm);
			} catch (Exception ex) {
				throw new CRUDException(ex);
			}
		}
	}

	private ElectionPeriod addDatesAndFwpms(ElectionPeriod electionPeriod) throws NotFoundException {
		try {
			electionPeriod.setReminderDates(daoFactory.createReminderDateDAO().findByPeriod(electionPeriod.getPeriodId()));
			electionPeriod.setFwpms(daoFactory.createParticipatedFwpmDAO().findByPeriod(electionPeriod.getPeriodId()));
		} catch (Exception ex) {
			throw new NotFoundException(ex);
		}

		return electionPeriod;
	}

	private List<ElectionPeriod> iterateAndAddDatesAndFwpms(List<ElectionPeriod> electionPeriods) throws NotFoundException {
		List<ElectionPeriod> returnValue = new ArrayList<>();
		for (ElectionPeriod electionPeriod : electionPeriods) {
			returnValue.add(addDatesAndFwpms(electionPeriod));
		}
		return returnValue;
	}
}
