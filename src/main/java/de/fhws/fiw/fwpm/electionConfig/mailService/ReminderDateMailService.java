package de.fhws.fiw.fwpm.electionConfig.mailService;

import com.google.common.base.Optional;
import de.fhws.fiw.fwpm.electionConfig.PropertySingleton;
import de.fhws.fiw.fwpm.electionConfig.database.CrudFacade;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;
import de.fhws.fiw.fwpm.electionConfig.models.ReminderDate;
import de.fhws.fiw.mail.SendMail;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderDateMailService {

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm");
	private final ZoneId timeZone = ZoneId.of("Europe/Berlin");

	private CrudFacade crudFacade;
	private ScheduledExecutorService executorService;

	public ReminderDateMailService() {
		try {
			crudFacade = CrudFacade.getInstance();
		} catch (Exception ex) {
			throw new WebApplicationException("Could not start Timer");
		}
	}

	public void startReminderDateMailService() {

		executorService = Executors.newSingleThreadScheduledExecutor();
		Runnable periodicTask = () -> {
			try {
				Optional<ElectionPeriod> electionPeriod = crudFacade.getLatestPeriod();
				checkIfMailHasToBeSent(electionPeriod.get());
			} catch (Exception ex) {
				//System.out.println("No latest Period found");
			}
		};

		executorService.scheduleAtFixedRate(periodicTask, 0, 10, TimeUnit.SECONDS);
	}

	public void stopReminderDateMailService() {
		executorService.shutdown();
	}

	private void checkIfMailHasToBeSent(ElectionPeriod electionPeriod) throws Exception {
		for (int i = 0; i < electionPeriod.getReminderDates().size(); ++i) {
			ReminderDate reminderDate = electionPeriod.getReminderDates().get(i);
			if (!reminderDate.isMailSent() && isInPast(reminderDate.getDate())) {
				sendMail(electionPeriod);
				reminderDate.setMailSent(true);
				crudFacade.updateReminderDate(reminderDate);
			}
		}
	}

	private void sendMail(ElectionPeriod electionPeriod) {
		if ("local".equals(getEnvironment())) {
			System.out.println("Send Mail:");
			System.out.println(getReminderMail(electionPeriod));
		} else {
			SendMail sendMail = new SendMail();
			try {
				sendMail.prepareMailer();
				sendMail.setRecipients(getRecipients());
				sendMail.setCcRecipients(getCCRecipients());
				sendMail.setMailSubject(getReminderMailSubject(electionPeriod));
				sendMail.setMailBody(getReminderMail(electionPeriod));
				sendMail.sendMail();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private String getEnvironment() {
		String environment = "";
		try {
			Properties properties = PropertySingleton.getInstance();
			environment = properties.getProperty("ENVIRONMENT");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return environment;
	}

	public String getReminderMail(ElectionPeriod electionPeriod) {
		String mailText = readProperty("REMINDER_MAIL");
		mailText = mailText.replace("{STARTDATE}", electionPeriod.getStartDate().format(formatter));
		mailText = mailText.replace("{ENDDATE}", electionPeriod.getEndDate().format(formatter));
		mailText = mailText.replace("{URL}", readProperty("REMINDER_MAIL_URL"));

		return mailText;
	}

	private String getReminderMailSubject(ElectionPeriod electionPeriod) {
		return readProperty("REMINDER_MAIL_SUBJECT").replace("{TITLE}", electionPeriod.getTitle());
	}

	private Address[] getRecipients() {
		return getAddresses("REMINDER_MAIL_RECIPIENTS");
	}

	private Address[] getCCRecipients() {
		return getAddresses("REMINDER_MAIL_CC_RECIPIENTS");
	}

	private Address[] getAddresses(String recipients) {
		String[] addressesAsStringArray = readProperty(recipients).split(",");
		Address[] returnValue = new Address[addressesAsStringArray.length];
		try {
			for (int i = 0; i < addressesAsStringArray.length; i++) {
				returnValue[i] = new InternetAddress(addressesAsStringArray[i]);
			}
		} catch (AddressException ex) {
			throw new WebApplicationException("couldn't parse mail address");
		}

		return returnValue;
	}

	private String readProperty(String property) {
		try {
			Properties properties = MailPropertyReader.getInstance();
			return properties.getProperty(property);
		} catch (IOException ex) {
			throw new WebApplicationException("Couldn't find Mail property");
		}
	}

	private boolean isInPast(ZonedDateTime dateTime) {

		return dateTime.isBefore(ZonedDateTime.now(timeZone));
	}
}