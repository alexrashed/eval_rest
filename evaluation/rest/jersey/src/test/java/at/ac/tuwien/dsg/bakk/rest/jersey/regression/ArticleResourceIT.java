package at.ac.tuwien.dsg.bakk.rest.jersey.regression;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import at.ac.tuwien.dsg.bakk.rest.jersey.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.ArticlePage;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.Basket;

public class ArticleResourceIT {
	private static Client client;
	private static WebTarget articleTarget;
	private static WebTarget basketTarget;
	private static JacksonJsonProvider jackson_json_provider = new JacksonJaxbJsonProvider()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@BeforeClass
	public static void setUp() {
		client = ClientBuilder.newClient().register(jackson_json_provider);
		WebTarget target = client.target("http://localhost:8080");
		Response rootResponse = target.request().get();
		articleTarget = client.target(rootResponse.getLink("tos:articles"));
		basketTarget = client.target(rootResponse.getLink("tos:baskets"));
	}

	@Test
	public void testSelfLink() {
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		Response response = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_XML));
		Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());

		Response getResponse = client.target(response.getLocation()).request().get();
		Assert.assertTrue(getResponse.hasLink("self"));
		Article linkedArticle = client.target(getResponse.getLink("self")).request().get(Article.class);
		Assert.assertTrue(linkedArticle.getName().equals(newArticle.getName()));
		Assert.assertTrue(linkedArticle.getPrice().equals(newArticle.getPrice()));

		Response delete = client.target(getResponse.getLink("self")).request().delete();
		Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
	}

	@Test
	public void testCreate() {
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		Response response = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_XML));
		Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());

		Article createdArticle = client.target(response.getLocation()).request().get(Article.class);
		Assert.assertTrue(createdArticle.getName().equals(newArticle.getName()));
		Assert.assertTrue(createdArticle.getPrice().equals(newArticle.getPrice()));
		Assert.assertNotNull(createdArticle.getLinks());
		Assert.assertFalse(createdArticle.getLinks().isEmpty());

		Response delete = client.target(response.getLocation()).request().delete();
		Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
	}

	@Test
	public void testDelete() {
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		Response response = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_XML));
		Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());

		Response delete = client.target(response.getLocation()).request().delete();
		Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());

		Response testGet = client.target(response.getLocation()).request().get();
		Assert.assertTrue(testGet.getStatus() == Status.NOT_FOUND.getStatusCode());
	}

	@Test
	public void testPaging() {
		Collection<URI> created = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			Article newArticle = new Article("Created" + i, "created..." + i, new BigDecimal(i));
			Response response = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_XML));
			Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
			created.add(response.getLocation());
		}
		Response firstPage = articleTarget.request().get();
		Assert.assertTrue(firstPage.hasLink("next"));
		Assert.assertFalse(firstPage.hasLink("prev"));
		Response secondPage = client.target(firstPage.getLink("next")).request().get();
		Assert.assertTrue(secondPage.hasLink("next"));
		Assert.assertTrue(secondPage.hasLink("prev"));
		ArticlePage page = secondPage.readEntity(ArticlePage.class);
		Assert.assertNotNull(page);
		Assert.assertEquals(10, page.getArticles().size());

		Link next = secondPage.getLink("next");
		Response nextResponse = null;
		while (next != null) {
			nextResponse = client.target(next.getUri()).request().get();
			next = nextResponse.getLink("next");
		}

		Assert.assertEquals("Created99", nextResponse.readEntity(ArticlePage.class).getArticles().get(9).getName());

		for (URI uri : created) {
			Response delete = client.target(uri).request().delete();
			Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
		}
	}

	@Test
	public void testAddToBasket() {
		Article newArticle = new Article("Article for basket", "f00", new BigDecimal(13));
		Response createResponse = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_XML));
		Assert.assertTrue(createResponse.getStatus() == Status.CREATED.getStatusCode());

		Response createBasketResponse = basketTarget.request().post(null);
		Assert.assertEquals(Status.CREATED.getStatusCode(), createBasketResponse.getStatus());

		Article getResponse = client.target(createResponse.getLocation()).request().get(Article.class);

		// find the correct link
		Link addToBasketLink = getResponse.getLink("tos:addToBasket");
		Assert.assertNotNull("There is no link to add the article to the basket", addToBasketLink);

		Response addResponse = client
				.target(Link.fromLink(addToBasketLink).baseUri(createResponse.getLocation()).build()).request()
				.post(null);
		Assert.assertEquals(Status.OK.getStatusCode(), addResponse.getStatus());

		Basket readBasket = addResponse.readEntity(Basket.class);
		Assert.assertEquals("Article for basket", readBasket.getArticles().iterator().next().getArticle().getName());
		Assert.assertEquals(new Long(1L), readBasket.getArticles().iterator().next().getAmount());

		Response deleteBasket = client.target(createBasketResponse.getLocation()).request().delete();
		Assert.assertTrue(deleteBasket.getStatus() == Status.OK.getStatusCode());

		Response delete = client.target(createResponse.getLocation()).request().delete();
		Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
	}

	@Test
	public void testUpdateArticle() {
		// create the article
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		Response response = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_XML));
		Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());

		// change the values
		newArticle.setName("ChangedName");
		newArticle.setPrice(new BigDecimal(1337));
		newArticle.setDescription("ChangedDescription");

		// update the article
		Response updateResponse = client.target(response.getLocation()).request()
				.put(Entity.entity(newArticle, MediaType.APPLICATION_XML));
		Assert.assertEquals(Status.OK.getStatusCode(), updateResponse.getStatus());

		// get the article again
		Article updatedArticle = client.target(response.getLocation()).request().get(Article.class);
		Assert.assertEquals(newArticle.getName(), updatedArticle.getName());
		Assert.assertEquals(newArticle.getDescription(), updatedArticle.getDescription());
		Assert.assertEquals(newArticle.getPrice(), updatedArticle.getPrice());

		// delete the article
		Response delete = client.target(response.getLocation()).request().delete();
		Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
	}
}
