package at.ac.tuwien.dsg.bakk.rest.spring.rfmm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import at.ac.tuwien.dsg.bakk.rest.spring.BaseIT;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Bill;

/**
 * Integration test executing the tests for Level 5 of the REST Framework
 * Maturity Model.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class Level5IT extends BaseIT {

	@Before
	public void setUpTest() {
		// create some articles
		URI newArticleURI = null;
		for (int i = 0; i < 30; i++) {
			Article newArticle = new Article("Article" + i, "created with love...", new BigDecimal(i));
			newArticleURI = template.postForLocation(rootLinks.get(REL_ARTICLES).getHref(), newArticle);
			assertNotNull(newArticleURI);
		}

		// create a basket
		URI basketURI = template.postForLocation(rootLinks.get(REL_BASKETS).getHref(), new Basket());
		assertNotNull(basketURI);
	}

	@After
	public void tearDownTest() {
		for (Link link : rootLinks.values()) {
			if (!link.getHref().startsWith(URI_SCHEME)) {
				continue;
			}
			PagedResources<? extends ResourceSupport> page = template.exchange(link.getHref(), HttpMethod.GET, null,
					new ParameterizedTypeReference<PagedResources<? extends ResourceSupport>>() {
					}).getBody();
			do {
				for (ResourceSupport entity : page.getContent()) {
					template.delete(entity.getLink(Link.REL_SELF).getHref());
				}
				page = page.getLink(Link.REL_NEXT) != null
						? template.exchange(page.getLink(Link.REL_NEXT).getHref(), HttpMethod.GET, null,
								new ParameterizedTypeReference<PagedResources<? extends ResourceSupport>>() {
								}).getBody()
						: null;
			} while (page != null);
		}
	}

	@Test
	public void testLevel5_1_LinkTraversion() {
		// get the first article page
		PagedResources<Article> page = template.exchange(rootLinks.get(REL_ARTICLES).getHref(), HttpMethod.GET, null,
				new ParameterizedTypeReference<PagedResources<Article>>() {
				}).getBody();
		// select the first article
		Article article = page.getContent().iterator().next();

		// add the article to the basket
		URI basketURI = template.postForLocation(article.getLink(REL_ADDTOBASKET).getHref(), null);
		ResponseEntity<Basket> basket = template.getForEntity(basketURI, Basket.class);

		// pay the basket
		URI billURI = template.postForLocation(basket.getBody().getLink(REL_PAYMENT).getHref(), null);
		ResponseEntity<Bill> bill = template.getForEntity(billURI, Bill.class);

		// ensure that the bill is not null
		assertNotNull(bill.getBody());

		// execute the traversion
		ResponseEntity<Basket> traversed = new Traverson(basketURI, MediaTypes.HAL_JSON).follow("tos:bill")
				.follow("self").follow("tos:basket").follow("self").toEntity(Basket.class);
		Basket traversedBasket = traversed.getBody();
		assertNotNull(traversedBasket);
		assertEquals(basketURI.toASCIIString(), traversedBasket.getLink("self").getHref());
	}

}
