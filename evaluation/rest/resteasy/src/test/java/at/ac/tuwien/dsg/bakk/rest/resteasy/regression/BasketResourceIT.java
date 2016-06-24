package at.ac.tuwien.dsg.bakk.rest.resteasy.regression;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.BasketPage;

public class BasketResourceIT {
	private static final String BASE_URI = "http://localhost:8080";
	private static ResteasyClient client;
	private static WebTarget basketTarget;
	private static WebTarget articleTarget;

	@BeforeClass
	public static void setUp() {
		client = (ResteasyClient) ClientBuilder.newClient();
		WebTarget target = client.target(BASE_URI);
		Response response = target.request(MediaType.WILDCARD).header("Content-Type", MediaType.TEXT_PLAIN).get();
		Link articlesLink = response.getLink("tos:articles");
		Link basketsLink = response.getLink("tos:baskets");
		response.close();
		articleTarget = client.target(BASE_URI + articlesLink.getUri().toString());
		basketTarget = client.target(BASE_URI + basketsLink.getUri().toString());
	}

	@Test
	public void testCreateSelfLinkDelete() {
		Response createdResponse = basketTarget.request().post(null);
		createdResponse.close();
		Assert.assertEquals(Status.CREATED.getStatusCode(), createdResponse.getStatus());

		Basket getResponse = client.target(createdResponse.getLocation()).request().get(Basket.class);
		Assert.assertNotNull(getResponse.getLinks());
		String link = getResponse.getLinks().getLinkForRel("self").getHref();
		Basket linkedBasket = client.target(link).request().get(Basket.class);
		Assert.assertNotNull(linkedBasket);

		Response delete = client.target(link).request().delete();
		delete.close();
		Assert.assertEquals(Status.OK.getStatusCode(), delete.getStatus());
	}

	@Test
	public void testDelete() {
		Response response = basketTarget.request().post(null);
		response.close();
		Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());

		Response delete = client.target(response.getLocation()).request().delete();
		delete.close();
		Assert.assertEquals(Status.OK.getStatusCode(), delete.getStatus());

		Response testGet = client.target(response.getLocation()).request().get();
		testGet.close();
		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), testGet.getStatus());
	}

	@Test
	public void testPayBasket() {
		// create a new article
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		Response createdArticleResponse = articleTarget.request()
				.post(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
		createdArticleResponse.close();
		Assert.assertEquals(Status.CREATED.getStatusCode(), createdArticleResponse.getStatus());

		// create a new basket
		Response createBasketResponse = basketTarget.request().post(null);
		createBasketResponse.close();
		Assert.assertEquals(Status.CREATED.getStatusCode(), createBasketResponse.getStatus());
		Basket getBasketResponse = client.target(createBasketResponse.getLocation()).request().get(Basket.class);
		Assert.assertNotNull(getBasketResponse.getLinks().getLinkForRel("self"));
		Assert.assertNotNull(getBasketResponse.getLinks().getLinkForRel("payment"));

		// get the article
		Article getArticleResponse = client.target(createdArticleResponse.getLocation()).request().get(Article.class);

		// find the correct link
		AtomLink addToBasketLink = getArticleResponse.getLinks().getLinkForRel("tos:addToBasket");
		Assert.assertNotNull("There is no link to add the article to the basket", addToBasketLink);

		// add an article to the basket
		Response addArticleToBasketResponse = client
				.target(Link.fromUri(addToBasketLink.getHref()).baseUri(createdArticleResponse.getLocation()).build())
				.request().post(null);
		addArticleToBasketResponse.close();
		Assert.assertEquals(Status.SEE_OTHER.getStatusCode(), addArticleToBasketResponse.getStatus());

		// pay the basket
		Response payResponse = client.target(getBasketResponse.getLinks().getLinkForRel("payment").getHref()).request()
				.post(null);
		payResponse.close();
		Assert.assertEquals(Status.CREATED.getStatusCode(), payResponse.getStatus());

		// find the bill
		Response getBillResponse = client.target(payResponse.getLocation()).request().get();
		getBillResponse.close();
		Assert.assertEquals(Status.OK.getStatusCode(), getBillResponse.getStatus());

		// delete bill, basket and article
		Response deleteBillResponse = client.target(payResponse.getLocation()).request().delete();
		deleteBillResponse.close();
		Assert.assertEquals(Status.OK.getStatusCode(), deleteBillResponse.getStatus());
		Response deleteBasketResponse = client.target(createBasketResponse.getLocation()).request().delete();
		deleteBasketResponse.close();
		Assert.assertEquals(Status.OK.getStatusCode(), deleteBasketResponse.getStatus());
		Response deleteArticleResponse = client.target(createdArticleResponse.getLocation()).request().delete();
		deleteArticleResponse.close();
		Assert.assertEquals(Status.OK.getStatusCode(), deleteArticleResponse.getStatus());
	}

	@Test
	public void testPaging() {
		Collection<URI> created = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			Response response = basketTarget.request().post(null);
			response.close();
			Assert.assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
			created.add(response.getLocation());
		}
		BasketPage firstPage = basketTarget.request().get(new GenericType<BasketPage>() {
		});
		Assert.assertNotNull(firstPage.getLinks().getLinkForRel("self"));
		Assert.assertNotNull(firstPage.getLinks().getLinkForRel("next"));
		Assert.assertNull(firstPage.getLinks().getLinkForRel("prev"));
		BasketPage secondPage = client.target(firstPage.getLinks().getLinkForRel("next").getHref()).request()
				.get(new GenericType<BasketPage>() {
				});
		Assert.assertNotNull(secondPage.getLinks().getLinkForRel("next"));
		Assert.assertNotNull(secondPage.getLinks().getLinkForRel("prev"));
		Assert.assertEquals(10, secondPage.getEntries().size());

		AtomLink next = secondPage.getLinks().getLinkForRel("next");
		BasketPage nextResponse = null;
		while (next != null) {
			nextResponse = client.target(next.getHref()).request().get(new GenericType<BasketPage>() {
			});
			next = nextResponse.getLinks().getLinkForRel("next");
		}

		Assert.assertNull(nextResponse.getLinks().getLinkForRel("next"));

		for (URI uri : created) {
			Response delete = client.target(uri).request().delete();
			Assert.assertTrue(delete.getStatus() == Status.OK.getStatusCode());
			delete.close();
		}
	}
}
