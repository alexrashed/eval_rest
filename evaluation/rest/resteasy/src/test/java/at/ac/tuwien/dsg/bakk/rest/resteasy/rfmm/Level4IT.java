package at.ac.tuwien.dsg.bakk.rest.resteasy.rfmm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.ArticlePage;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.BasketPage;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Bill;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.BillPage;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.ClientArticle;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.ClientArticlePage;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.ClientBasket;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.ClientBasketPage;

/**
 * Integration test executing the tests for Level 4 of the REST Framework
 * Maturity Model.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class Level4IT {
	private static Client client;
	private static Map<String, Link> links = new HashMap<>();

	private static final String BASE_URI = "http://localhost:8080";
	private static final String URI_SCHEME = "tos";
	private static final String REL_ARTICLES = URI_SCHEME + ":" + "articles";
	private static final String REL_BASKETS = URI_SCHEME + ":" + "baskets";
	private static final String REL_BILLS = URI_SCHEME + ":" + "bills";
	private static final String REL_SELF = "self";
	private static final String REL_NEXT = "next";

	@BeforeClass
	public static void setUp() {
		client = ClientBuilder.newClient();
		WebTarget target = client.target(BASE_URI);
		Response rootResponse = target.request(MediaType.WILDCARD).header("Content-Type", MediaType.TEXT_PLAIN).get();
		for (Link link : rootResponse.getLinks()) {
			links.put(link.getRel(), link);
		}
		rootResponse.close();
	}

	@Before
	public void setUpTest() {
		// create some articles
		for (int i = 0; i < 30; i++) {
			Article newArticle = new Article("Article" + i, new BigDecimal(i));
			Response createResponse = client.target(BASE_URI + links.get(REL_ARTICLES).getUri()).request()
					.post(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
			Assert.assertTrue(createResponse.getStatus() == Status.CREATED.getStatusCode());
			createResponse.close();
		}

		// create a basket
		Response createBasketResponse = client.target(BASE_URI + links.get(REL_BASKETS).getUri()).request()
				.post(Entity.entity("Newly introduced name", MediaType.APPLICATION_JSON));
		Assert.assertEquals(Status.CREATED.getStatusCode(), createBasketResponse.getStatus());
		createBasketResponse.close();
	}

	@After
	public void tearDownTest() {
		BillPage bills = client.target(BASE_URI + links.get(REL_BILLS).getUri() + "/listing").request()
				.get(BillPage.class);
		do {
			if (bills == null || bills.getEntries() == null) {
				break;
			}
			for (Bill entity : bills.getEntries()) {
				Response delete = client.target(entity.getLinks().getLinkForRel(REL_SELF).getHref()).request().delete();
				delete.close();
			}
			bills = bills.getLinks().getLinkForRel(REL_NEXT) != null
					? client.target(bills.getLinks().getLinkForRel(REL_NEXT).getHref()).request().get(BillPage.class)
					: null;
		} while (bills != null);

		BasketPage baskets = client.target(BASE_URI + links.get(REL_BASKETS).getUri()).request().get(BasketPage.class);
		do {
			if (baskets == null || baskets.getEntries() == null) {
				break;
			}
			for (Basket entity : baskets.getEntries()) {
				Response delete = client.target(entity.getLinks().getLinkForRel(REL_SELF).getHref()).request().delete();
				delete.close();
			}
			baskets = baskets.getLinks().getLinkForRel(REL_NEXT) != null ? client
					.target(baskets.getLinks().getLinkForRel(REL_NEXT).getHref()).request().get(BasketPage.class)
					: null;
		} while (baskets != null);

		ArticlePage articles = client.target(BASE_URI + links.get(REL_ARTICLES).getUri()).request()
				.get(ArticlePage.class);
		do {
			if (articles == null || articles.getEntries() == null) {
				break;
			}
			for (Article entity : articles.getEntries()) {
				Response delete = client.target(entity.getLinks().getLinkForRel(REL_SELF).getHref()).request().delete();
				delete.close();
			}
			articles = articles.getLinks().getLinkForRel(REL_NEXT) != null ? client
					.target(articles.getLinks().getLinkForRel(REL_NEXT).getHref()).request().get(ArticlePage.class)
					: null;
		} while (articles != null);
	}

	@Test
	public void testLevel4_1_TolerantToAddedProperties() {
		// get the first basket page
		Response page = client.target(BASE_URI + links.get(REL_BASKETS).getUri()).request().get();
		ClientBasketPage entities = page.readEntity(ClientBasketPage.class);
		page.close();
		// select the basket and read the details
		ClientBasket basket = entities.getEntries().iterator().next();
		assertNotNull(basket);
	}

	@Test
	public void testLevel4_2_TolerantToRemovedProperties() {
		// get the first article page
		Response page = client.target(BASE_URI + links.get(REL_ARTICLES).getUri()).request().get();
		ClientArticlePage entities = page.readEntity(ClientArticlePage.class);
		page.close();
		// select the first article and read the details
		ClientArticle article = entities.getEntries().iterator().next();
		assertNotNull(article);
		assertNotNull(article.getName());
		assertNull(article.getDescription());
	}

	@Test
	public void testLevel4_3_StatusCodeHandling() {
		// get to the bills collection URI
		ClientBasketPage page = client.target(BASE_URI + links.get(REL_BILLS).getUri()).request()
				.get(ClientBasketPage.class);

		// check if the client followed the redirect to the actual collection
		// URI
		assertNotNull(page.getLinks().getLinkForRel(REL_SELF));
	}

	@Test
	public void testLevel4_4_DefaultHandling() {
		client = ClientBuilder.newClient();

		// get the first basket page
		Response page = client.target(BASE_URI + links.get(REL_BASKETS).getUri()).request().get();
		ClientBasketPage entities = page.readEntity(ClientBasketPage.class);
		page.close();
		// select the basket and read the details
		ClientBasket basket = entities.getEntries().iterator().next();
		assertNotNull(basket);

		// get the first article page
		page = client.target(BASE_URI + links.get(REL_ARTICLES).getUri()).request().get();
		ClientArticlePage articleEntities = page.readEntity(ClientArticlePage.class);
		page.close();
		// select the first article and read the details
		ClientArticle article = articleEntities.getEntries().iterator().next();
		assertNotNull(article);
		assertNotNull(article.getName());
		assertNull(article.getDescription());
	}

}
