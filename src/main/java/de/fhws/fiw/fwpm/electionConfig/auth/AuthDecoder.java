package de.fhws.fiw.fwpm.electionConfig.auth;

import org.json.JSONObject;

import java.util.Base64;

public final class AuthDecoder {

	private AuthDecoder() {}

	public static AuthenticationToken decode(String auth) {

		AuthenticationToken token = new AuthenticationToken();

		String[] typeAndToken = auth.split(" ");
		if (typeAndToken.length < 2) {
			throw new IllegalArgumentException("No type in auth");
		}

		token.setType(typeAndToken[0]);
		token.setToken(typeAndToken[1]);

		//    Parse expirationDate from token
		if (token.isType(AuthenticationToken.TokenType.BEARER)) {
			String[] jsonWebtoken = token.getToken().split("\\.");

			if (jsonWebtoken.length < 3) {
				throw new IllegalArgumentException("No metadata in token");
			}
			byte[] tokenContent =  Base64.getDecoder().decode(jsonWebtoken[1]);

			JSONObject object = new JSONObject(new String(tokenContent));
			token.setExpiresAt(Long.valueOf(object.getLong("exp")));
		}

		return token;
	}
}