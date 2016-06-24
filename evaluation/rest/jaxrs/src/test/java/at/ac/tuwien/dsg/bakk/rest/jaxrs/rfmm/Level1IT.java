package at.ac.tuwien.dsg.bakk.rest.jaxrs.rfmm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.BaseBean;
import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.Basket;

/**
 * Integration test executing the tests for Level 1 of the REST Framework
 * Maturity Model.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class Level1IT {
	private static Client client;
	private static Map<String, Link> links = new HashMap<>();
	private static JacksonJsonProvider jackson_json_provider = new JacksonJaxbJsonProvider()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private static final String BASE_URI = "http://localhost:8080/";
	private static final String URI_SCHEME = "tos";
	private static final String REL_ARTICLES = URI_SCHEME + ":" + "articles";
	private static final String REL_BASKETS = URI_SCHEME + ":" + "baskets";
	private static final String REL_ADDTOBASKET = URI_SCHEME + ":" + "addToBasket";
	private static final String REL_PAYMENT = "payment";
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
		// create some articles
		for (int i = 0; i < 30; i++) {
			Article newArticle = new Article("Article" + i, "created with love...", new BigDecimal(i), null);
			Response createResponse = client.target(links.get(REL_ARTICLES)).request()
					.post(Entity.entity(newArticle, MediaType.APPLICATION_JSON));
			Assert.assertTrue(createResponse.getStatus() == Status.CREATED.getStatusCode());
		}

		// create a basket
		Response createBasketResponse = client.target(links.get(REL_BASKETS)).request().post(null);
		Assert.assertEquals(Status.CREATED.getStatusCode(), createBasketResponse.getStatus());
	}

	@After
	public void tearDownTest() {
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
	public void testLevel1_1_Linking() {
		// get the first article page
		Response page = client.target(links.get(REL_ARTICLES)).request().get();
		Collection<Article> entities = page.readEntity(new GenericType<Collection<Article>>() {
		});
		// select the first article
		Article article = entities.iterator().next();
		assertNotNull(article.getLink(REL_SELF));
	}

	@Test
	public void testLevel1_2_TypeSafety() {
		// get the first article page
		Response page = client.target(links.get(REL_ARTICLES)).request().get();
		Collection<Article> entities = page.readEntity(new GenericType<Collection<Article>>() {
		});
		// select the first article and read the details
		Article article = entities.iterator().next();
		article = client.target(Link.fromLink(article.getLink(REL_SELF)).baseUri(BASE_URI).build()).request()
				.get(Article.class);

		// add the article to the basket
		Response addResponse = client.target(Link.fromLink(article.getLink(REL_ADDTOBASKET)).baseUri(BASE_URI).build())
				.request().post(null);
		assertEquals(Status.OK.getStatusCode(), addResponse.getStatus());
		Basket basket = addResponse.readEntity(Basket.class);

		// pay the basket
		Response payResponse = client.target(Link.fromLink(basket.getLink(REL_PAYMENT)).baseUri(BASE_URI).build())
				.request().post(null);
		assertEquals(Status.CREATED.getStatusCode(), payResponse.getStatus());

		// ensure that the bill is available
		Response billResponse = client.target(payResponse.getLocation()).request().get();
		assertEquals(Status.OK.getStatusCode(), billResponse.getStatus());
	}

	@Test
	public void testLevel1_3_AnnotationBasedLinking() {
		Assert.fail("No annotation based linking possible.");
	}

	@Test
	public void testLevel1_4_IANALinkRelationUsage() {
		Assert.fail("IANA link relations are not used/defined at all.");
	}

}
