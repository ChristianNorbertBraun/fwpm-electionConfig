package de.fhws.fiw.fwpm.electionConfig.api;

import com.google.common.base.Optional;
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import de.fhws.fiw.fwpm.electionConfig.database.CrudFacade;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * This resource is meant to be the entry point of this service.
 */
@Path("/")
public class EntryResource {

	private static final String LINK = "link";
	private static final String JSON = "json";

	private UriInfo uriInfo;

	private CrudFacade crudFacade;

	@PostConstruct
	private void init() {
		try {
			crudFacade = CrudFacade.getInstance();
		} catch (Exception ex) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	/**
	 * This Endpoint will return all possible endpoints in its link header.
	 *
	 * @ResponseHeader Link <b>relTypes</b>: template, allPeriods, latestPeriod <br>
	 * <tt>template:</tt>		Returns template uri for getting a election period (<tt>{PERIODID}</tt>).<br>
	 * <tt>allPeriods:</tt>		Returns uri for getting all saved periods.<br>
	 * <tt>latestPeriod:</tt>	Returns uri for getting the latest period. This relType will just be returned if a latest period exists<br>
	 * @HTTP 500 For database error
	 * @HTTP 200 For successful Request
	 */
	@GET
	@TypeHint(void.class)
	public Response getLinks(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
		Response.ResponseBuilder response;
		response = Response.ok();
		response.header(LINK, linkHeader(createTemplateUri(), "template", JSON));
		response.header(LINK, linkHeader(createAllUri(), "allPeriods", JSON));
		Optional<String> latestPeriodUri = createLatestPeriodURI();
		if (latestPeriodUri.isPresent()) {
			response.header(LINK, linkHeader(latestPeriodUri.get(), "latestPeriod", JSON));
		}
		return response.build();
	}

	private String createTemplateUri() {
		return this.uriInfo.getAbsolutePathBuilder().path(ElectionPeriodResource.class).build().toString() + "/{PERIODID}";
	}

	private String createAllUri() {
		return this.uriInfo.getAbsolutePathBuilder().path(ElectionPeriodResource.class).build().toString();
	}

	private Optional<String> createLatestPeriodURI() {
		Optional<String> returnValue;
		Optional<ElectionPeriod> electionPeriodOptional;

		try {
			electionPeriodOptional = crudFacade.getLatestPeriod();
			if (electionPeriodOptional.isPresent()) {
				long periodId = electionPeriodOptional.get().getPeriodId();
				URI uri = this.uriInfo.getAbsolutePathBuilder().path(ElectionPeriodResource.class).path("/" + periodId).build();
				returnValue = Optional.fromNullable(uri.toString());
			} else {
				returnValue = Optional.fromNullable(null);
			}

		} catch (Exception ex) {
			returnValue = Optional.fromNullable(null);
		}

		return returnValue;
	}

	private static String linkHeader(final String uri, final String rel, final String mediaType) {
		final StringBuilder sb = new StringBuilder();

		sb.append('<').append(uri).append(">;");
		sb.append("rel").append("=\"").append(rel).append("\"");

		if (mediaType != null && !mediaType.isEmpty()) {
			sb.append(";");
			sb.append("type").append("=\"").append(mediaType).append("\"");
		}

		return sb.toString();
	}
}
