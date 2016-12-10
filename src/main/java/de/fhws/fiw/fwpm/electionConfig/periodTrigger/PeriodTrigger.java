package de.fhws.fiw.fwpm.electionConfig.periodTrigger;

import com.google.common.base.Optional;
import de.fhws.fiw.fwpm.electionConfig.PropertySingleton;
import de.fhws.fiw.fwpm.electionConfig.database.CrudFacade;
import de.fhws.fiw.fwpm.electionConfig.mailService.MailPropertyReader;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;
import de.fhws.fiw.mail.SendMail;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PeriodTrigger {

	private ScheduledExecutorService executorService;
	private CrudFacade crudFacade;

	public PeriodTrigger() {
		try {
			crudFacade = CrudFacade.getInstance();
		} catch (Exception ex) {
			throw new WebApplicationException("Could not start PeriodTrigger");
		}
	}

	public void startPeriodTrigger() {
		executorService = Executors.newSingleThreadScheduledExecutor();
		Runnable triggerTask = () -> {
			try {
				Optional<ElectionPeriod> electionPeriod = crudFacade.getLatestPeriod();
				System.out.println("Current ElectionPeriod id: " + electionPeriod.get().getPeriodId());
				if (electionPeriod.isPresent()) {
					checkIfAlgorithmHasToBeenTriggered(electionPeriod.get());
				}
			} catch (Exception ex) {
				System.out.println("No latest Period found");
			}
		};

		executorService.scheduleAtFixedRate(triggerTask, 0, 10, TimeUnit.SECONDS);
	}

	public void stopPeriodTrigger() {
		executorService.shutdown();
	}

	private void checkIfAlgorithmHasToBeenTriggered(ElectionPeriod electionPeriod) throws Exception {
		if (!electionPeriod.isElectionTriggered() && isInPast(electionPeriod.getEndDate())) {
			triggerElection(electionPeriod.getPeriodId());
			sendMail();
			electionPeriod.setIsElectionTriggered(true);
			crudFacade.update(electionPeriod);
		}
	}

	private void triggerElection(long periodId) {
		String assignmentsUrl;
		String assignmentApi;
		try {
			assignmentsUrl = PropertySingleton.getInstance().getProperty("ASSIGNMENTS_URL") + periodId;
			assignmentApi =  PropertySingleton.getInstance().getProperty("ASSIGNMENT");
		} catch (IOException ex) {
			throw new WebApplicationException("Could not read property");
		}
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(assignmentsUrl);

		Invocation.Builder builder = target.request();
		builder.header("Authorization", "api " + assignmentApi);
		Response response = builder.post(null);

		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			System.out.println("Triggering failed.");
			throw new WebApplicationException("Triggering failed.", Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	private void sendMail() {
		if ("local".equals(getEnvironment())) {
			System.out.println("Send Mail");
			System.out.println(getMail());
		} else {
			SendMail sendMail = new SendMail();
			try {
				sendMail.prepareMailer();
				sendMail.setRecipients(getRecipient());
				sendMail.setMailSubject(getMailSubject());
				sendMail.setMailBody(getMail());
				sendMail.sendMail();
			} catch (MessagingException ex) {
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

	private boolean isInPast(ZonedDateTime dateTime) {
		return dateTime.isBefore(ZonedDateTime.now(ZoneId.of("Europe/Berlin")));
	}

	private String readProperty(String property) {
		try {
			Properties properties = MailPropertyReader.getInstance();
			return properties.getProperty(property);
		} catch (IOException ex) {
			throw new WebApplicationException("Couldn't find Mail property");
		}
	}

	private Address[] getRecipient() {
		try {
			return new Address[] { new InternetAddress(readProperty("DEAN_MAIL")) };
		} catch (AddressException ex) {
			throw new WebApplicationException("couldn't parse mailaddress");
		}
	}

	private String getMailSubject() {
		return readProperty("TRIGGER_MAIL_SUBJECT");
	}

	private String getMail() {
		return readProperty("TRIGGER_MAIL");
	}

}
