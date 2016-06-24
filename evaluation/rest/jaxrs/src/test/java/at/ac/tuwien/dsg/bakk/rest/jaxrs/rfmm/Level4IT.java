package at.ac.tuwien.dsg.bakk.rest.jaxrs.rfmm;

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

import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.BaseBean;
import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.ClientArticle;
import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.ClientBasket;

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
			ClientArticle newArticle = new ClientArticle("Article" + i, "created with love...", new BigDecimal(i),
					null);
			Response createResponse = client.target(links.get(REL_ARTICLES)).request()
					.post(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
			Assert.assertTrue(createResponse.getStatus() == Status.CREATED.getStatusCode());
		}

		// create a basket
		Response createBasketResponse = client.target(links.get(REL_BASKETS)).request()
				.post(Entity.entity("Newly introduced name", MediaType.APPLICATION_JSON));
		Assert.assertEquals(Status.CREATED.getStatusCode(), createBasketResponse.getStatus());
	}

	@After
	public void tearDownTest() {
		client = ClientBuilder.newClient().register(jackson_json_provider);
		for (Link link : links.values()) {
			Response page = client.target(link).request().get();
			Collection<? extends BaseBean> entities = page
					.readEntity(new GenericType<Collection<? extends BaseBean>>() {
					});
			do {
				if (entities == null) {
					break;
				}
				for (BaseBean entity : entities) {
					client.target(Link.fromLink(entity.getLink(REL_SELF)).baseUri(BASE_URI).build()).request().delete();
				}
				if (page.getLink(REL_NEXT) != null) {
					page = page.getLink(REL_NEXT) != null ? client.target(page.getLink(REL_NEXT)).request().get()
							: null;
					entities = page.readEntity(new GenericType<Collection<? extends BaseBean>>() {
					});
				} else {
					page = null;
				}
			} while (page != null);
		}
	}

	@Test
	public void testLevel4_1_TolerantToAddedProperties() {
		// get the first basket page
		Response page = client.target(links.get(REL_BASKETS)).request().get();
		Collection<ClientBasket> entities = page.readEntity(new GenericType<Collection<ClientBasket>>() {
		});
		// select the basket and read the details
		ClientBasket basket = entities.iterator().next();
		assertNotNull(basket);
	}

	@Test
	public void testLevel4_2_TolerantToRemovedProperties() {
		// get the first article page
		Response page = client.target(links.get(REL_ARTICLES)).request().get();
		Collection<ClientArticle> entities = page.readEntity(new GenericType<Collection<ClientArticle>>() {
		});
		// select the article and read the details
		ClientArticle article = entities.iterator().next();
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
