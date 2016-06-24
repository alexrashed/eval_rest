package at.ac.tuwien.dsg.bakk.rest.spring.regression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import at.ac.tuwien.dsg.bakk.rest.spring.BaseIT;
import at.ac.tuwien.dsg.bakk.rest.spring.ExpectedExceptionStatus;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Bill;

public class BillControllerIT extends BaseIT {

	@Test
	public void testSelfLink() {
		URI basketURI = template.postForLocation(rootLinks.get("tos:baskets").getHref(), new Basket());
		Basket basket = template.getForEntity(basketURI, Basket.class).getBody();
		URI billURI = template.postForLocation(basket.getLink("payment").getHref(), null);
		assertNotNull(billURI);

		Bill bill = template.getForEntity(billURI, Bill.class).getBody();
		assertNotNull(bill.getLink(Link.REL_SELF));

		template.delete(billURI);
		template.delete(basketURI);
	}

	@Test
	@ExpectedExceptionStatus(HttpStatus.NOT_FOUND)
	public void testDelete() {
		URI basketURI = template.postForLocation(rootLinks.get("tos:baskets").getHref(), new Basket());
		Basket basket = template.getForEntity(basketURI, Basket.class).getBody();
		URI billURI = template.postForLocation(basket.getLink("payment").getHref(), null);
		assertNotNull(billURI);

		Bill bill = template.getForEntity(billURI, Bill.class).getBody();
		assertNotNull(bill.getLink(Link.REL_SELF));

		template.delete(billURI);
		template.delete(basketURI);
		template.getForEntity(basketURI, null);
	}

	@Test
	public void testPaging() {
		List<URI> createdBaskets = new ArrayList<>();
		List<URI> createdBills = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			URI basketURI = template.postForLocation(rootLinks.get("tos:baskets").getHref(), new Basket());
			assertNotNull(basketURI);
			createdBaskets.add(basketURI);
			Basket basket = template.getForEntity(basketURI, Basket.class).getBody();
			URI billURI = template.postForLocation(basket.getLink("payment").getHref(), new Bill());
			assertNotNull(billURI);
			createdBills.add(billURI);
		}
		PagedResources<Bill> firstPage = template.exchange(rootLinks.get("tos:bills").getHref(), HttpMethod.GET, null,
				new ParameterizedTypeReference<PagedResources<Bill>>() {
				}).getBody();
		assertNotNull(firstPage.getLink(Link.REL_NEXT));
		assertNotNull(firstPage.getLink(Link.REL_SELF));

		PagedResources<Bill> secondPage = template.exchange(firstPage.getLink(Link.REL_NEXT).getHref(), HttpMethod.GET,
				null, new ParameterizedTypeReference<PagedResources<Bill>>() {
				}).getBody();
		assertNotNull(secondPage.getLink(Link.REL_NEXT));
		assertNotNull(secondPage.getLink(Link.REL_PREVIOUS));
		assertNotNull(secondPage.getLink(Link.REL_SELF));
		assertEquals(10, secondPage.getContent().size());

		Link next = secondPage.getLink(Link.REL_NEXT);
		PagedResources<Bill> nextResponse = null;
		while (next != null) {
			nextResponse = template.exchange(next.getHref(), HttpMethod.GET, null,
					new ParameterizedTypeReference<PagedResources<Bill>>() {
					}).getBody();
			next = nextResponse.getLink(Link.REL_NEXT);
		}

		assertNull(nextResponse.getLink(Link.REL_NEXT));
		List<Bill> lastPage = new ArrayList<>(nextResponse.getContent());
		assertEquals(10, lastPage.size());

		for (int i = 0; i < createdBaskets.size(); i++) {
			template.delete(createdBills.get(i));
			template.delete(createdBaskets.get(i));
		}
	}
}
