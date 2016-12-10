package de.fhws.fiw.fwpm.electionConfig.mailService;

import de.fhws.fiw.fwpm.electionConfig.PropertySingleton;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class MailPropertyReader {

	private static Properties instance;
	private static InputStream inputStream;

	private MailPropertyReader() {
	}

	public static Properties getInstance(boolean createNew, String fileName) throws IOException {
		if (instance == null || createNew) {
			instance = new Properties();

			inputStream = PropertySingleton.class.getResourceAsStream("/" + fileName);
			instance.load(inputStream);
		}
		return instance;
	}

	public static Properties getInstance() throws IOException {
		return getInstance(false, "mail.properties");
	}
}
