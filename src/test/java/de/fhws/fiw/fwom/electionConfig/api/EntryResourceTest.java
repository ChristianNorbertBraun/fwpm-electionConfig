package de.fhws.fiw.fwom.electionConfig.api;

import de.fhws.fiw.fwpm.electionConfig.api.EntryResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import static org.junit.Assert.*;

public class EntryResourceTest {

	private EntryResource entryResource;
	private UriInfo uriInfo;

	@Before
	public void init() {
		entryResource = new EntryResource();
		uriInfo = Mockito.mock(UriInfo.class);
		Mockito.when(uriInfo.getAbsolutePathBuilder()).thenReturn(UriBuilder.fromUri("http://localhost:8080/test"));
	}

	@Test
	public void getLinksTest() {
		Response response = entryResource.getLinks(uriInfo);
		String linkHeader = response.getHeaderString("link");

		assertEquals(200, response.getStatus());
		assertFalse(linkHeader.isEmpty());
	}
}
