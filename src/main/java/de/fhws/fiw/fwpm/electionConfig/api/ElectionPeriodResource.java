package de.fhws.fiw.fwpm.electionConfig.api;

import com.google.common.base.Optional;
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import de.fhws.fiw.fwpm.electionConfig.auth.Roles;
import de.fhws.fiw.fwpm.electionConfig.database.CrudFacade;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.CRUDException;
import de.fhws.fiw.fwpm.electionConfig.exceptionHandling.NotFoundException;
import de.fhws.fiw.fwpm.electionConfig.models.ElectionPeriod;
import de.fhws.fiw.fwpm.electionConfig.models.dto.ElectionPeriodRequest;
import de.fhws.fiw.fwpm.electionConfig.models.dto.ElectionPeriodResponse;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.DateTimeException;
import java.util.stream.Collectors;

/**
 * This resource contains all necessary endpoints to manage a period.
 */
@Path("electionConfig")
public class ElectionPeriodResource {

	private static final String LINK = "link";
	private static final String JSON = "json";

	@Context
	private UriInfo uriInfo;

	@Context
	SecurityContext context;

	private CrudFacade crudFacade;

	@PostConstruct
	private void init() {
		try {
			crudFacade = CrudFacade.getInstance();
		} catch (Exception ex) {
			throwInternalServerError();
		}
	}

	/**
	 * This Endpoint will return all saved election periods.
	 *
	 * @ResponseHeader Link <b>relTypes</b>: template, latestPeriod <br>
	 * <tt>template:</tt>		Returns template uri for getting a election period (<tt>{PERIODID}</tt>).<br>
	 * <tt>latestPeriod:</tt>	Returns uri for getting the latest period. This relType will just be returned if a latest period exists<br>
	 * @HTTP 500 For database error
	 * @HTTP 403 If you haven't enough permissions.
	 * @HTTP 200 For successful Request
	 */
	@GET
	@RolesAllowed({ Roles.API_KEY_USER, Roles.EMPLOYEE, Roles.STUDENT })
	@Produces(MediaType.APPLICATION_JSON)
	@TypeHint(ElectionPeriodResponse[].class)
	public Response listAllPeriods() {
		Response.ResponseBuilder response;
		try {
			response = Response.ok(crudFacade.readAll().stream().map(ElectionPeriodResponse::toDto).collect(Collectors.toList()));
		} catch (NotFoundException ex) {
			response = Response.ok();
		}

		response.header(LINK, linkHeader(createTemplateUri(), "template", JSON));
		Optional<String> latestPeriodUri = createLatestPeriodURI();
		if (latestPeriodUri.isPresent()) {
			response.header(LINK, linkHeader(latestPeriodUri.get(), "latestPeriod", JSON));
		}
		return response.build();
	}

	/**
	 * This Endpoint will save the given period into the database.
	 *
	 * @ResponseHeader Link <b>relTypes</b>: template, location <br>
	 * <tt>template:</tt>	Returns template uri for getting a election period (<tt>{PERIODID}</tt>).<br>
	 * <tt>location:</tt>	Returns uri for getting the currently saved period.<br>
	 * @HTTP 500 For database error
	 * @HTTP 409 For a constraint error. If unique constraint for start- and/or end-date is violated
	 * @HTTP 403 If you haven't enough permissions.
	 * @HTTP 201 For successful Request
	 */
	@POST
	@RolesAllowed(Roles.EMPLOYEE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@TypeHint(ElectionPeriodResponse.class)
	public Response createPeriod(final ElectionPeriodRequest electionPeriodRequest) {
		try {
			Optional<ElectionPeriod> savedInstance = crudFacade.save(ElectionPeriodRequest.toEntity(electionPeriodRequest, -1L));

			return Response.created(createContentLocationUri(savedInstance)).header(LINK, linkHeader(createTemplateUri(), "template", JSON))
					.entity(ElectionPeriodResponse.toDto(savedInstance.get())).build();
		} catch (DateTimeException ex) {
			throw new WebApplicationException(Response.Status.CONFLICT);
		} catch (CRUDException ex) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}

	}
	/**
	 * This Endpoint will return the requested election period.
	 *
	 * @ResponseHeader Link <b>relTypes</b>: template <br>
	 * <tt>template:</tt>		Returns template uri for getting a election period (<tt>{PERIODID}</tt>).<br>
	 * @HTTP 500 For database error
	 * @HTTP 404 If the requested election period does not exists
	 * @HTTP 403 If you haven't enough permissions.
	 * @HTTP 200 For successful Request
	 */
	@GET
	@RolesAllowed({ Roles.API_KEY_USER, Roles.EMPLOYEE, Roles.STUDENT })
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{periodId}")
	@TypeHint(ElectionPeriodResponse.class)
	public Response getPeriod(@PathParam("periodId") final long periodId) {
		try {
			Optional<ElectionPeriod> electionPeriodOptional = crudFacade.readById(periodId);
			if (!electionPeriodOptional.isPresent()) {
				return Response.status(Response.Status.NOT_FOUND).header(LINK, linkHeader(createTemplateUri(), "template", JSON)).build();
			}
			return Response.ok().entity(ElectionPeriodResponse.toDto(electionPeriodOptional.get()))
					.header(LINK, linkHeader(createTemplateUri(), "template", JSON)).build();
		} catch (NotFoundException ex) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	/**
	 * This Endpoint will return the latest period if it exists.
	 *
	 * @ResponseHeader Link <b>relTypes</b>: template<br>
	 * <tt>template:</tt>	Returns template uri for getting a election period (<tt>{PERIODID}</tt>).<br>
	 * @HTTP 500 For database error
	 * @HTTP 404 If no latest period exists
	 * @HTTP 403 If you haven't enough permissions.
	 * @HTTP 200 For successful Request
	 */
	@GET
	@RolesAllowed({ Roles.API_KEY_USER, Roles.EMPLOYEE, Roles.STUDENT })
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/latest")
	@TypeHint(ElectionPeriodResponse.class)
	public Response getLatestPeriod() {
		try {
			Optional<ElectionPeriod> electionPeriodOptional = crudFacade.getLatestPeriod();
			if (!electionPeriodOptional.isPresent()) {
				return Response.status(Response.Status.NOT_FOUND).header(LINK, linkHeader(createTemplateUri(), "template", JSON)).build();
			}
			return Response.ok().entity(ElectionPeriodResponse.toDto(electionPeriodOptional.get()))
					.header(LINK, linkHeader(createTemplateUri(), "template", JSON)).build();
		} catch (NotFoundException ex) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This Endpoint will delete the current period.
	 *
	 * @HTTP 500 For database error
	 * @HTTP 403 If you haven't enough permissions.
	 * @HTTP 204 For successful Request
	 */
	@DELETE
	@RolesAllowed(Roles.EMPLOYEE)
	@Path("/{periodId}")
	@TypeHint(void.class)
	public Response deletePeriod(@PathParam("periodId") final long periodId) {
		try {
			crudFacade.delete(periodId);
			return Response.noContent().build();
		} catch (CRUDException ex) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This Endpoint will update the current period.
	 *
	 * @ResponseHeader Link <b>relTypes</b>: template, location <br>
	 * <tt>template:</tt>	Returns template uri for getting a election period (<tt>{PERIODID}</tt>).<br>
	 * <tt>location:</tt>	Returns uri for getting the currently saved period.<br>
	 * @HTTP 500 For database error
	 * @HTTP 409 For a constraint error. If unique constraint for start- and/or end-date is violated
	 * @HTTP 403 If you haven't enough permissions.
	 * @HTTP 204 For successful Request
	 */
	@PUT
	@RolesAllowed(Roles.EMPLOYEE)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{periodId}")
	@TypeHint(void.class)
	public Response updatePeriod(@PathParam("periodId") final long periodId, final ElectionPeriodRequest electionPeriod) {
		try {
			Optional<ElectionPeriod> updatedResource = crudFacade.update(ElectionPeriodRequest.toEntity(electionPeriod, periodId));
			return Response.noContent().contentLocation(createContentLocationUri(updatedResource))
					.header(LINK, linkHeader(createTemplateUri(), "template", JSON)).build();
		} catch (Exception ex) {
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	private String createTemplateUri() {
		return this.uriInfo.getAbsolutePathBuilder().build().toString() + "/{PERIODID}";
	}

	private URI createContentLocationUri(Optional<ElectionPeriod> electionPeriodOptional) {
		return this.uriInfo.getAbsolutePathBuilder().path("/" + electionPeriodOptional.get().getPeriodId()).build();
	}

	private Optional<String> createLatestPeriodURI() {
		Optional<String> returnValue;
		Optional<ElectionPeriod> electionPeriodOptional;

		try {
			electionPeriodOptional = crudFacade.getLatestPeriod();
			if (electionPeriodOptional.isPresent()) {
				long periodId = electionPeriodOptional.get().getPeriodId();
				URI uri = this.uriInfo.getAbsolutePathBuilder().path("/" + periodId).build();
				returnValue = Optional.fromNullable(uri.toString());
			} else {
				returnValue = Optional.fromNullable(null);
			}

		} catch (Exception ex) {
			returnValue = Optional.fromNullable(null);
		}

		return returnValue;
	}

	private void throwInternalServerError() {
		throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
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