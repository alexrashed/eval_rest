package at.ac.tuwien.dsg.bakk.rest.jaxrs.regression;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.Test;

public class RootResourceIT {

	@Test
	public void testRootResourceLinks() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080");
		Response response = target.request().get();
		Assert.assertTrue(response.hasLink("tos:articles"));
		Assert.assertTrue(response.hasLink("tos:baskets"));
		Assert.assertTrue(response.hasLink("tos:bills"));

		Response articleResponse = client.target(response.getLink("tos:articles")).request().get();
		Assert.assertEquals(Status.OK.getStatusCode(), articleResponse.getStatus());
		Response basketResponse = client.target(response.getLink("tos:baskets")).request().get();
		Assert.assertEquals(Status.OK.getStatusCode(), basketResponse.getStatus());
		Response billResponse = client.target(response.getLink("tos:bills")).request().get();
		Assert.assertEquals(Status.OK.getStatusCode(), billResponse.getStatus());
	}

}
