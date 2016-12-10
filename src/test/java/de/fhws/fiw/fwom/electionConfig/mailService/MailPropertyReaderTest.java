package de.fhws.fiw.fwom.electionConfig.mailService;

import de.fhws.fiw.fwpm.electionConfig.mailService.MailPropertyReader;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.junit.Assert.*;

public class MailPropertyReaderTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void readMailPropertiesTest() throws IOException {
		MailPropertyReader.getInstance().getProperty("");
	}

	@Test
	public void readDeanMailTest() throws IOException {
		String deanMail = MailPropertyReader.getInstance().getProperty("DEAN_MAIL");

		assertFalse(deanMail.isEmpty());
	}

	@Test
	public void readAllReminderMailPropertiesTest() throws IOException {
		String subject = MailPropertyReader.getInstance().getProperty("REMINDER_MAIL_SUBJECT");
		String mailText = MailPropertyReader.getInstance().getProperty("REMINDER_MAIL");
		String url = MailPropertyReader.getInstance().getProperty("REMINDER_MAIL_URL");
		String recipients = MailPropertyReader.getInstance().getProperty("REMINDER_MAIL_RECIPIENTS");
		String recipientsCC = MailPropertyReader.getInstance().getProperty("REMINDER_MAIL_CC_RECIPIENTS");

		assertFalse(subject.isEmpty());
		assertFalse(mailText.isEmpty());
		assertFalse(url.isEmpty());
		assertFalse(recipients.isEmpty());
		assertFalse(recipientsCC.isEmpty());
	}

	@Test
	public void mailTextFormatTest() throws IOException {
		String mailText = MailPropertyReader.getInstance().getProperty("REMINDER_MAIL");

		assertTrue(mailText.contains("{STARTDATE}"));
		assertTrue(mailText.contains("{ENDDATE}"));
		assertTrue(mailText.contains("{URL}"));
	}

	@Test
	public void mailSubjectFormatTest() throws IOException {
		String subject = MailPropertyReader.getInstance().getProperty("REMINDER_MAIL_SUBJECT");

		assertTrue(subject.contains("{TITLE}"));
	}

	@Test
	public void readAllTriggerMailPropertiesTest() throws IOException {
		String subject = MailPropertyReader.getInstance().getProperty("TRIGGER_MAIL_SUBJECT");
		String mailText = MailPropertyReader.getInstance().getProperty("TRIGGER_MAIL");

		assertFalse(subject.isEmpty());
		assertFalse(mailText.isEmpty());
	}
}
