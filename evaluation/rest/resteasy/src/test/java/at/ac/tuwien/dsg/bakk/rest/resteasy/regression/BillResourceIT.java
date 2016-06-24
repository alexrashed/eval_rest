package at.ac.tuwien.dsg.bakk.rest.resteasy.regression;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Bill;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.BillPage;

public class BillResourceIT {
	private static final String BASE_URI = "http://localhost:8080";
	private static Client client;
	private static WebTarget billTarget;
	private static WebTarget articleTarget;
	private static WebTarget basketTarget;

	@BeforeClass
	public static void setUp() {
		client = ClientBuilder.newClient();
		WebTarget target = client.target(BASE_URI);
		Response response = target.request(MediaType.WILDCARD).header("Content-Type", MediaType.TEXT_PLAIN).get();
		Link articlesLink = response.getLink("tos:articles");
		Link basketsLink = response.getLink("tos:baskets");
		Link billsLink = response.getLink("tos:bills");
		response.close();
		articleTarget = client.target(BASE_URI + articlesLink.getUri().toString());
		basketTarget = client.target(BASE_URI + basketsLink.getUri().toString());
		billTarget = client.target(BASE_URI + billsLink.getUri().toString());
	}

	@Test
	public void testCreateSelfLinkDelete() {
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
		Bill getBillResponse = client.target(payResponse.getLocation()).request().get(Bill.class);
		Assert.assertNotNull(getBillResponse.getLinks());
		Bill getBillSelfResponse = client.target(getBillResponse.getLinks().getLinkForRel("self").getHref()).request()
				.get(Bill.class);
		Assert.assertNotNull(getBillSelfResponse);
		Assert.assertNotNull(getBillSelfResponse.getLinks().getLinkForRel("tos:basket"));
		Assert.assertEquals(getBasketResponse.getLinks().getLinkForRel("self").getHref(),
				getBillSelfResponse.getLinks().getLinkForRel("tos:basket").getHref());

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
	public void testDelete() {
		// create basket and bill
		Response createBasketResponse = basketTarget.request().post(null);
		createBasketResponse.close();
		Basket getBasketResponse = client.target(createBasketResponse.getLocation()).request().get(Basket.class);
		Response payResponse = client.target(getBasketResponse.getLinks().getLinkForRel("payment").getHref()).request()
				.post(null);
		payResponse.close();
		Assert.assertEquals(Status.CREATED.getStatusCode(), payResponse.getStatus());

		// delete bill
		Response deleteBillResponse = client.target(payResponse.getLocation()).request().delete();
		deleteBillResponse.close();
		Assert.assertEquals(Status.OK.getStatusCode(), deleteBillResponse.getStatus());
		Response getAfterDeleteBill = client.target(payResponse.getLocation()).request().get();
		getAfterDeleteBill.close();
		Assert.assertEquals(Status.NOT_FOUND.getStatusCode(), getAfterDeleteBill.getStatus());

		// delete basket
		Response deleteBasketResponse = client.target(createBasketResponse.getLocation()).request().delete();
		deleteBasketResponse.close();
		Assert.assertEquals(Status.OK.getStatusCode(), deleteBasketResponse.getStatus());
	}

	@Test
	public void testPaging() {
		List<URI> createdBaskets = new ArrayList<>();
		List<URI> createdBills = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			// create basket and bill
			Response createBasketResponse = basketTarget.request().post(null);
			createBasketResponse.close();
			Assert.assertEquals(Status.CREATED.getStatusCode(), createBasketResponse.getStatus());
			createdBaskets.add(createBasketResponse.getLocation());
			Basket getBasketResponse = client.target(createBasketResponse.getLocation()).request().get(Basket.class);
			Response payResponse = client.target(getBasketResponse.getLinks().getLinkForRel("payment").getHref())
					.request().post(null);
			payResponse.close();
			Assert.assertEquals(Status.CREATED.getStatusCode(), payResponse.getStatus());
			createdBills.add(payResponse.getLocation());
		}
		BillPage firstPage = billTarget.request().get(new GenericType<BillPage>() {
		});
		Assert.assertNotNull(firstPage.getLinks().getLinkForRel("self"));
		Assert.assertNotNull(firstPage.getLinks().getLinkForRel("next"));
		Assert.assertNull(firstPage.getLinks().getLinkForRel("prev"));
		BillPage secondPage = client.target(firstPage.getLinks().getLinkForRel("next").getHref()).request()
				.get(new GenericType<BillPage>() {
				});
		Assert.assertNotNull(secondPage.getLinks().getLinkForRel("next"));
		Assert.assertNotNull(secondPage.getLinks().getLinkForRel("prev"));
		Assert.assertEquals(10, secondPage.getEntries().size());

		AtomLink next = secondPage.getLinks().getLinkForRel("next");
		BillPage nextResponse = null;
		while (next != null) {
			nextResponse = client.target(next.getHref()).request().get(new GenericType<BillPage>() {
			});
			next = nextResponse.getLinks().getLinkForRel("next");
		}

		Assert.assertNull(nextResponse.getLinks().getLinkForRel("next"));

		for (int i = 0; i < createdBills.size(); i++) {
			Response delete = client.target(createdBills.get(i)).request().delete();
			delete.close();
			Assert.assertEquals(Status.OK.getStatusCode(), delete.getStatus());
			delete = client.target(createdBaskets.get(i)).request().delete();
			delete.close();
			Assert.assertEquals(Status.OK.getStatusCode(), delete.getStatus());
		}
	}

}
