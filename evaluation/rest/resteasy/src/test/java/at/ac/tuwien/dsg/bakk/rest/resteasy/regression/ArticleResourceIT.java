package at.ac.tuwien.dsg.bakk.rest.resteasy.regression;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.ArticlePage;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Basket;

public class ArticleResourceIT {
	private static final String BASE_URI = "http://localhost:8080";
	private static Client client;
	private static WebTarget articleTarget;
	private static WebTarget basketTarget;

	@BeforeClass
	public static void setUp() {
		client = ClientBuilder.newClient();
		WebTarget target = client.target(BASE_URI);
		Response response = target.request(MediaType.WILDCARD).header("Content-Type", MediaType.TEXT_PLAIN).get();
		Link articlesLink = response.getLink("tos:articles");
		Link basketsLink = response.getLink("tos:baskets");
		response.close();
		articleTarget = client.target(BASE_URI + articlesLink.getUri().toString());
		basketTarget = client.target(BASE_URI + basketsLink.getUri().toString());
	}

	@Test
	public void testSelfLink() {
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		Response response = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
		Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
		response.close();

		Article getResponse = client.target(response.getLocation()).request().get(Article.class);
		Assert.assertNotNull(getResponse.getLinks().getLinkForRel("self"));
		String selfLink = getResponse.getLinks().getLinkForRel("self").getHref();
		Article linkedArticle = client.target(selfLink).request().get(Article.class);
		Assert.assertTrue(linkedArticle.getName().equals(newArticle.getName()));
		Assert.assertTrue(linkedArticle.getPrice().equals(newArticle.getPrice()));

		Response delete = client.target(selfLink).request().delete();
		Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
	}

	@Test
	public void testCreate() {
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		Response response = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
		Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());
		response.close();

		Article createdArticle = client.target(response.getLocation()).request().get(Article.class);
		Assert.assertTrue(createdArticle.getName().equals(newArticle.getName()));
		Assert.assertTrue(createdArticle.getPrice().equals(newArticle.getPrice()));
		Assert.assertNotNull(createdArticle.getLinks());
		Assert.assertFalse(createdArticle.getLinks().isEmpty());

		Response delete = client.target(response.getLocation()).request().delete();
		Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
		delete.close();
	}

	@Test
	public void testDelete() {
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		Response response = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
		response.close();
		Assert.assertTrue(response.getStatus() == Status.CREATED.getStatusCode());

		Response delete = client.target(response.getLocation()).request().delete();
		delete.close();
		Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());

		Response testGet = client.target(response.getLocation()).request().get();
		testGet.close();
		Assert.assertTrue(testGet.getStatus() == Status.NOT_FOUND.getStatusCode());
	}

	@Test
	public void testPaging() {
		Collection<URI> created = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			Article newArticle = new Article("Created" + i, "created..." + i, new BigDecimal(i));
			Response response = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
			response.close();
			Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
			created.add(response.getLocation());
		}
		ArticlePage firstPage = articleTarget.request().get(new GenericType<ArticlePage>() {
		});
		Assert.assertNotNull(firstPage.getLinks().getLinkForRel("self"));
		Assert.assertNotNull(firstPage.getLinks().getLinkForRel("next"));
		Assert.assertNull(firstPage.getLinks().getLinkForRel("prev"));
		ArticlePage secondPage = client.target(firstPage.getLinks().getLinkForRel("next").getHref()).request()
				.get(new GenericType<ArticlePage>() {
				});
		Assert.assertNotNull(secondPage.getLinks().getLinkForRel("next"));
		Assert.assertNotNull(secondPage.getLinks().getLinkForRel("prev"));
		Assert.assertEquals(10, secondPage.getEntries().size());

		AtomLink next = secondPage.getLinks().getLinkForRel("next");
		ArticlePage nextResponse = null;
		while (next != null) {
			nextResponse = client.target(next.getHref()).request().get(new GenericType<ArticlePage>() {
			});
			next = nextResponse.getLinks().getLinkForRel("next");
		}

		Assert.assertEquals("Created99", nextResponse.getEntries().get(9).getName());

		for (URI uri : created) {
			Response delete = client.target(uri).request().delete();
			Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
			delete.close();
		}
	}

	@Test
	public void testAddToBasket() {
		Article newArticle = new Article("Article for basket", "f00", new BigDecimal(13));
		Response createResponse = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
		createResponse.close();
		Assert.assertTrue(createResponse.getStatus() == Status.CREATED.getStatusCode());

		Response createBasketResponse = basketTarget.request().post(null);
		createBasketResponse.close();
		Assert.assertEquals(Status.CREATED.getStatusCode(), createBasketResponse.getStatus());

		Article getResponse = client.target(createResponse.getLocation()).request().get(Article.class);

		// find the correct link
		AtomLink addToBasketLink = getResponse.getLinks().getLinkForRel("tos:addToBasket");
		Assert.assertNotNull("There is no link to add the article to the basket", addToBasketLink);

		Response addResponse = client.target(addToBasketLink.getHref()).request(MediaType.APPLICATION_JSON).post(null);
		addResponse.close();
		Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), addResponse.getStatus());

		Basket readBasket = client.target(addResponse.getLocation()).request().get(Basket.class);
		Assert.assertEquals("Article for basket", readBasket.getArticles().iterator().next().getArticle().getName());
		Assert.assertEquals(new Long(1L), readBasket.getArticles().iterator().next().getAmount());

		Response deleteBasket = client.target(createBasketResponse.getLocation()).request().delete();
		deleteBasket.close();
		Assert.assertTrue(deleteBasket.getStatus() == Status.OK.getStatusCode());

		Response delete = client.target(createResponse.getLocation()).request().delete();
		delete.close();
		Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
	}

	@Test
	public void testUpdateArticle() {
		// create the article
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		Response response = articleTarget.request().post(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
		response.close();
		Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());

		// change the values
		newArticle.setName("ChangedName");
		newArticle.setPrice(new BigDecimal(1337));
		newArticle.setDescription("ChangedDescription");

		// update the article
		Response updateResponse = client.target(response.getLocation()).request()
				.put(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
		updateResponse.close();
		Assert.assertEquals(Status.OK.getStatusCode(), updateResponse.getStatus());

		// get the article again
		Article updatedArticle = client.target(response.getLocation()).request().get(Article.class);
		Assert.assertEquals(newArticle.getName(), updatedArticle.getName());
		Assert.assertEquals(newArticle.getDescription(), updatedArticle.getDescription());
		Assert.assertEquals(newArticle.getPrice(), updatedArticle.getPrice());

		// delete the article
		Response delete = client.target(response.getLocation()).request().delete();
		delete.close();
		Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
	}
}
