package at.ac.tuwien.dsg.bakk.rest.jersey.rfmm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import at.ac.tuwien.dsg.bakk.rest.jersey.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.ArticlePage;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.BasketPage;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.ClientArticle;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.ClientArticlePage;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.ClientBasket;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.ClientBasketPage;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.ClientBill;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.ClientBillPage;

/**
 * Integration test executing the tests for Level 4 of the REST Framework
 * Maturity Model.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class Level4IT {
	private static Client client;
	private static Map<String, Link> links = new HashMap<>();
	private static JacksonJsonProvider jackson_json_provider = new JacksonJaxbJsonProvider()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private static final String BASE_URI = "http://localhost:8080/";
	private static final String URI_SCHEME = "tos";
	private static final String REL_ARTICLES = URI_SCHEME + ":" + "articles";
	private static final String REL_BASKETS = URI_SCHEME + ":" + "baskets";
	private static final String REL_BILLS = URI_SCHEME + ":" + "bills";
	private static final String REL_SELF = "self";
	private static final String REL_NEXT = "next";

	@BeforeClass
	public static void setUp() {
		client = ClientBuilder.newClient().register(jackson_json_provider);
		WebTarget target = client.target(BASE_URI);
		Response rootResponse = target.request().get();
		for (Link link : rootResponse.getLinks()) {
			links.put(link.getRel(), link);
		}
	}

	@Before
	public void setUpTest() {
		client = ClientBuilder.newClient().register(jackson_json_provider);
		// create some articles
		for (int i = 0; i < 30; i++) {
			Article newArticle = new Article("Article" + i, new BigDecimal(i));
			Response createResponse = client.target(links.get(REL_ARTICLES)).request()
					.post(Entity.entity(newArticle, MediaType.APPLICATION_XML));
			Assert.assertTrue(createResponse.getStatus() == Status.CREATED.getStatusCode());
		}

		// create a basket
		Response createBasketResponse = client.target(links.get(REL_BASKETS)).request()
				.post(Entity.entity("Newly introduced name", MediaType.APPLICATION_XML));
		Assert.assertEquals(Status.CREATED.getStatusCode(), createBasketResponse.getStatus());
	}

	@After
	public void tearDownTest() {
		Response page = client.target(links.get(REL_BILLS)).request().get();
		ClientBillPage bills = page.readEntity(ClientBillPage.class);
		do {
			if (bills == null || bills.getBills() == null) {
				break;
			}
			for (ClientBill entity : bills.getBills()) {
				client.target(Link.fromLink(entity.getLink(REL_SELF)).baseUri(BASE_URI).build()).request().delete();
			}
			if (page.getLink(REL_NEXT) != null) {
				page = page.getLink(REL_NEXT) != null ? client.target(page.getLink(REL_NEXT)).request().get() : null;
				bills = page.readEntity(ClientBillPage.class);
			} else {
				page = null;
			}
		} while (page != null);

		page = client.target(links.get(REL_BASKETS)).request().get();
		BasketPage baskets = page.readEntity(BasketPage.class);
		do {
			if (baskets == null || baskets.getBaskets() == null) {
				break;
			}
			for (Basket entity : baskets.getBaskets()) {
				client.target(Link.fromLink(entity.getLink(REL_SELF)).baseUri(BASE_URI).build()).request().delete();
			}
			if (page.getLink(REL_NEXT) != null) {
				page = page.getLink(REL_NEXT) != null ? client.target(page.getLink(REL_NEXT)).request().get() : null;
				baskets = page.readEntity(BasketPage.class);
			} else {
				page = null;
			}
		} while (page != null);

		page = client.target(links.get(REL_ARTICLES)).request().get();
		ArticlePage articles = page.readEntity(ArticlePage.class);
		do {
			if (articles == null || articles.getArticles() == null) {
				break;
			}
			for (Article entity : articles.getArticles()) {
				client.target(Link.fromLink(entity.getLink(REL_SELF)).baseUri(BASE_URI).build()).request().delete();
			}
			if (page.getLink(REL_NEXT) != null) {
				page = page.getLink(REL_NEXT) != null ? client.target(page.getLink(REL_NEXT)).request().get() : null;
				articles = page.readEntity(ArticlePage.class);
			} else {
				page = null;
			}
		} while (page != null);
	}

	@Test
	public void testLevel4_1_TolerantToAddedProperties() {
		// get the first basket page
		Response page = client.target(links.get(REL_BASKETS)).request().get();
		ClientBasketPage entities = page.readEntity(ClientBasketPage.class);
		// select the basket and read the details
		ClientBasket basket = entities.getBaskets().iterator().next();
		assertNotNull(basket);
	}

	@Test
	public void testLevel4_2_TolerantToRemovedProperties() {
		// get the first article page
		Response page = client.target(links.get(REL_ARTICLES)).request().get();
		ClientArticlePage entities = page.readEntity(ClientArticlePage.class);
		// select the article and read the details
		ClientArticle article = entities.getArticles().iterator().next();
		assertNotNull(article);
		assertNotNull(article.getName());
		assertNull(article.getDescription());
	}

	@Test
	public void testLevel4_3_StatusCodeHandling() {
		// get to the bills collection URI
		Response page = client.target(links.get(REL_BILLS)).request().get();

		// check if the client followed the redirect to the actual collection
		// URI
		assertNotNull(page.getLink(REL_SELF));
		assertEquals(Status.OK.getStatusCode(), page.getStatus());
	}

	@Test
	public void testLevel4_4_DefaultHandling() {
		client = ClientBuilder.newClient();

		// get the first basket page
		Response page = client.target(links.get(REL_BASKETS)).request().get();
		Collection<ClientBasket> basketEntries = page.readEntity(new GenericType<Collection<ClientBasket>>() {
		});
		// select the basket and read the details
		ClientBasket basket = basketEntries.iterator().next();
		assertNotNull(basket);

		// get the first article page
		page = client.target(links.get(REL_ARTICLES)).request().get();
		Collection<ClientArticle> articleEntries = page.readEntity(new GenericType<Collection<ClientArticle>>() {
		});
		// select the article and read the details
		ClientArticle article = articleEntries.iterator().next();
		assertNotNull(article);
		assertNotNull(article.getName());
		assertNull(article.getDescription());
	}

}
