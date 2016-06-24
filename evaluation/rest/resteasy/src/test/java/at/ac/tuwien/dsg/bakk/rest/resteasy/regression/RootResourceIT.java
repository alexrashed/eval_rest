package at.ac.tuwien.dsg.bakk.rest.resteasy.regression;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

public class RootResourceIT {

	private static final String BASE_URI = "http://localhost:8080";

	@Test
	public void testRootResourceLinks() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(BASE_URI);
		// XXX NPE in EclipseLink if accept- and content-type-header are not set
		Response response = target.request(MediaType.WILDCARD).header("Content-Type", MediaType.TEXT_PLAIN).get();
		Link articlesLink = response.getLink("tos:articles");
		Link basketsLink = response.getLink("tos:baskets");
		Link billsLink = response.getLink("tos:bills");
		Assert.assertNotNull(articlesLink);
		Assert.assertNotNull(basketsLink);
		Assert.assertNotNull(billsLink);
		response.close();
		Response articleResponse = client.target(BASE_URI + articlesLink.getUri().toString()).request().get();
		Assert.assertEquals(Status.OK.getStatusCode(), articleResponse.getStatus());
		articleResponse.close();
		Response basketResponse = client.target(BASE_URI + basketsLink.getUri().toString()).request().get();
		Assert.assertEquals(Status.OK.getStatusCode(), basketResponse.getStatus());
		basketResponse.close();
		Response billResponse = client.target(BASE_URI + billsLink.getUri().toString()).request().get();
		Assert.assertEquals(Status.OK.getStatusCode(), billResponse.getStatus());
		billResponse.close();
	}

}
