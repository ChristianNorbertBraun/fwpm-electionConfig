package de.fhws.fiw.fwpm.electionConfig.auth;

import de.fhws.fiw.fwpm.electionConfig.PropertySingleton;
import net.jodah.expiringmap.ExpiringEntryLoader;
import net.jodah.expiringmap.ExpiringMap;
import net.jodah.expiringmap.ExpiringValue;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Provider
@PreMatching
public class AuthFilter implements ContainerRequestFilter, Roles {

	private static String authUrl;
	private static String selfApiKey;

	// Client wurde aus Objektbezug entfernt

	// das ist alles, was für den User Cache nötig ist
	public static Map<AuthenticationToken, User> userCache = ExpiringMap.builder()
			.expiringEntryLoader(new ExpiringEntryLoader<AuthenticationToken, User>() {
				@Override
				public ExpiringValue<User> load(AuthenticationToken authenticationToken) {
					long currentTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime());
					long timeToLife = authenticationToken.getExpiresAt() - currentTimeInSeconds;

					return new ExpiringValue<User>(
							getUserFromWeb(authenticationToken.getToken()),
							timeToLife,
							TimeUnit.SECONDS);
				}
			})
			.build();

	public AuthFilter() {
		try {
			loadProperties();
		} catch (IOException e) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void filter(ContainerRequestContext containerRequestContext) throws IOException {
		User user = null;
		String method = containerRequestContext.getMethod();
		String path = containerRequestContext.getUriInfo().getPath(true);

		String auth = containerRequestContext.getHeaderString("authorization");

		if (auth == null) {
			user = createAnonymousUser();

		} else {
			AuthenticationToken token = AuthDecoder.decode(auth);
			user = findUserForTokenType(token);
		}

		String scheme = containerRequestContext.getUriInfo().getRequestUri().getScheme();
		containerRequestContext.setSecurityContext(new CustomSecurityContext(user, scheme));
	}


	private void loadProperties() throws IOException {

		Properties prop = PropertySingleton.getInstance();
		authUrl = prop.getProperty("AUTH_URL");
		selfApiKey = prop.getProperty("SELF");
	}

	private User findUserForTokenType(AuthenticationToken token) {
		User user = null;
		if (token.isType(AuthenticationToken.TokenType.API)) {
			if (token.sameToken(selfApiKey)) {
				user = createApiKeyUser(token);
			} else {
				throw new WebApplicationException(Response.Status.UNAUTHORIZED);
			}

		} else if (token.isType(AuthenticationToken.TokenType.BEARER)) {
			// Hier muss dann direkt vom Cache der User geholt werden
			user = userCache.get(token);
		}

		return user;
	}

	// getUser wurde in getUserFromWeb umbenannt
	private static User getUserFromWeb(String token) {
		User user;
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(authUrl);

		Invocation.Builder builder = target.request();
		builder.header("X-fhws-jwt-token", token);

		Response response = builder.get();

		if (response.getStatus() == 200) {
			user = response.readEntity(User.class);
			user.setToken(response.getHeaderString("X-fhws-jwt-token"));
		} else {
			user = createAnonymousUser();
		}

		return user;
	}

	private static User createAnonymousUser() {
		User user = new User();
		user.setRole(ANONYMOUS);
		return user;
	}

	private static User createApiKeyUser(AuthenticationToken token) {
		User user = new User();
		user.setRole(API_KEY_USER);
		user.setToken(token.getToken());

		return user;
	}
}