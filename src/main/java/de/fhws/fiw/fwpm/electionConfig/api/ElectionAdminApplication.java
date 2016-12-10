package de.fhws.fiw.fwpm.electionConfig.api;

import de.fhws.fiw.fwpm.electionConfig.auth.AuthFilter;
import de.fhws.fiw.fwpm.electionConfig.database.Persistency;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.TableException;
import de.fhws.fiw.fwpm.electionConfig.mailService.ReminderDateMailService;
import de.fhws.fiw.fwpm.electionConfig.periodTrigger.PeriodTrigger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

import javax.ws.rs.ApplicationPath;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ElectionAdminApplication extends ResourceConfig {
	private ReminderDateMailService reminderDateMailService;
	private PeriodTrigger periodTrigger;

	public ElectionAdminApplication() {
		super();
		reminderDateMailService = new ReminderDateMailService();
		periodTrigger = new PeriodTrigger();

		registerClasses(getResourceClasses());
		register(RolesAllowedDynamicFeature.class);
		register(new AuthFilter());
		register(new CorsFilter());
		register(new ContainerLifecycleListener() {
			@Override
			public void onStartup(Container container) {
				reminderDateMailService.startReminderDateMailService();
				periodTrigger.startPeriodTrigger();
			}

			@Override
			public void onReload(Container container) {

			}

			@Override
			public void onShutdown(Container container) {
				reminderDateMailService.stopReminderDateMailService();
				periodTrigger.stopPeriodTrigger();
				AuthFilter.userCache.clear();
				try {
					Persistency.getInstance(false).closeConnectionPool();
				} catch (TableException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	public Set<Class<?>> getResourceClasses() {
		final Set<Class<?>> classes = new HashSet<>();
		classes.add(EntryResource.class);
		classes.add(ElectionPeriodResource.class);
		return classes;
	}
}