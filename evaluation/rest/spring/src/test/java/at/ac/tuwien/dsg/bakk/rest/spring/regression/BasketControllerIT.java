package at.ac.tuwien.dsg.bakk.rest.spring.regression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import at.ac.tuwien.dsg.bakk.rest.spring.BaseIT;
import at.ac.tuwien.dsg.bakk.rest.spring.ExpectedExceptionStatus;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Bill;

public class BasketControllerIT extends BaseIT {

	@Test
	public void testCreateSelfLinkDelete() {
		URI newBasketURI = template.postForLocation(rootLinks.get("tos:baskets").getHref(), new Basket());
		assertNotNull(newBasketURI);

		Basket linkedBasket = template.getForEntity(newBasketURI, Basket.class).getBody();
		assertNotNull(linkedBasket.getLink(Link.REL_SELF));

		template.delete(linkedBasket.getLink(Link.REL_SELF).getHref());
	}

	@Test
	@ExpectedExceptionStatus(HttpStatus.NOT_FOUND)
	public void testDelete() {
		URI newBasketURI = template.postForLocation(rootLinks.get("tos:baskets").getHref(), new Basket());
		assertNotNull(newBasketURI);
		Basket createdBasket = template.getForEntity(newBasketURI, Basket.class).getBody();
		assertNotNull(createdBasket);
		template.delete(newBasketURI);
		template.getForEntity(newBasketURI, null);
	}

	@Test
	public void testPaging() {
		Collection<URI> created = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			URI newBasketURI = template.postForLocation(rootLinks.get("tos:baskets").getHref(), new Basket());
			assertNotNull(newBasketURI);
			created.add(newBasketURI);
		}
		PagedResources<Basket> firstPage = template.exchange(rootLinks.get("tos:baskets").getHref(), HttpMethod.GET,
				null, new ParameterizedTypeReference<PagedResources<Basket>>() {
				}).getBody();
		assertNotNull(firstPage.getLink(Link.REL_NEXT));
		assertNotNull(firstPage.getLink(Link.REL_SELF));

		PagedResources<Basket> secondPage = template.exchange(firstPage.getLink(Link.REL_NEXT).getHref(),
				HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<Basket>>() {
				}).getBody();
		assertNotNull(secondPage.getLink(Link.REL_NEXT));
		assertNotNull(secondPage.getLink(Link.REL_PREVIOUS));
		assertNotNull(secondPage.getLink(Link.REL_SELF));
		assertEquals(10, secondPage.getContent().size());

		Link next = secondPage.getLink(Link.REL_NEXT);
		PagedResources<Basket> nextResponse = null;
		while (next != null) {
			nextResponse = template.exchange(next.getHref(), HttpMethod.GET, null,
					new ParameterizedTypeReference<PagedResources<Basket>>() {
					}).getBody();
			next = nextResponse.getLink(Link.REL_NEXT);
		}

		assertNull(nextResponse.getLink(Link.REL_NEXT));
		List<Basket> lastPage = new ArrayList<>(nextResponse.getContent());
		assertEquals(10, lastPage.size());

		for (URI uri : created) {
			template.delete(uri);
		}
	}

	@Test
	public void testPayBasket() {
		// create a new article
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		URI articleURI = template.postForLocation(rootLinks.get("tos:articles").getHref(), newArticle);
		assertNotNull(articleURI);

		// create a new basket
		template.postForLocation(rootLinks.get("tos:baskets").getHref(), new Basket());

		// get the article
		Article createdArticle = template.getForEntity(articleURI, Article.class).getBody();

		// find the link to add the article to the basket
		Link addToBasketURI = createdArticle.getLink("tos:addToBasket");
		assertNotNull(addToBasketURI);
		URI basketURI = template.postForLocation(addToBasketURI.getHref(), null);

		// pay the basket
		Basket linkedBasket = template.getForEntity(basketURI, Basket.class).getBody();
		assertNotNull(linkedBasket.getLink("payment"));
		URI billURI = template.postForLocation(linkedBasket.getLink("payment").getHref(), null);

		// find the bill
		ResponseEntity<Bill> billEntity = template.getForEntity(billURI, Bill.class);
		assertNotNull(billEntity);
		assertNotNull(billEntity.getBody());

		// Traverse from the basket to the bill and back, to test the
		// Traversion-API
		ResponseEntity<Basket> actual = new Traverson(basketURI, MediaTypes.HAL_JSON).follow("tos:bill").follow("self")
				.follow("tos:basket").follow("self").toEntity(Basket.class);
		Basket basket = actual.getBody();
		assertNotNull(basket);
		assertEquals(basketURI.toASCIIString(), basket.getLink("self").getHref());

		// delete bill, basket and article
		template.delete(billURI);
		template.delete(basketURI);
		template.delete(articleURI);
	}
}
