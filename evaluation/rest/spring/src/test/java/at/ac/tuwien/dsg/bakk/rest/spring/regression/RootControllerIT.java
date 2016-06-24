package at.ac.tuwien.dsg.bakk.rest.spring.regression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import at.ac.tuwien.dsg.bakk.rest.spring.BaseIT;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Bill;

public class RootControllerIT extends BaseIT {

	@Test
	public void testRootResourceLinks() {
		assertNotNull(rootLinks.get("tos:articles"));
		assertNotNull(rootLinks.get("tos:baskets"));
		assertNotNull(rootLinks.get("tos:bills"));

		ResponseEntity<PagedResources<Article>> articles = template.exchange(rootLinks.get("tos:articles").getHref(),
				HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<Article>>() {
				});
		assertEquals(HttpStatus.OK, articles.getStatusCode());

		ResponseEntity<PagedResources<Basket>> baskets = template.exchange(rootLinks.get("tos:baskets").getHref(),
				HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<Basket>>() {
				});
		assertEquals(HttpStatus.OK, baskets.getStatusCode());

		ResponseEntity<PagedResources<Bill>> bills = template.exchange(rootLinks.get("tos:bills").getHref(),
				HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<Bill>>() {
				});
		assertEquals(HttpStatus.OK, bills.getStatusCode());
	}

}
