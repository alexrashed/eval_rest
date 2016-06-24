package at.ac.tuwien.dsg.bakk.rest.spring.regression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import at.ac.tuwien.dsg.bakk.rest.spring.BaseIT;
import at.ac.tuwien.dsg.bakk.rest.spring.ExpectedExceptionStatus;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Basket;

public class ArticleControllerIT extends BaseIT {

	@Test
	public void testSelfLink() {
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		URI newArticleURI = template.postForLocation(rootLinks.get("tos:articles").getHref(), newArticle);
		assertNotNull(newArticleURI);

		Article linkedArticle = template.getForEntity(newArticleURI, Article.class).getBody();
		assertNotNull(linkedArticle.getLink(Link.REL_SELF));

		template.delete(newArticleURI);
	}

	@Test
	public void testCreate() {
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		URI newArticleURI = template.postForLocation(rootLinks.get("tos:articles").getHref(), newArticle);
		assertNotNull(newArticleURI);

		Article createdArticle = template.getForEntity(newArticleURI, Article.class).getBody();
		assertNotNull(createdArticle.getLink(Link.REL_SELF));
		assertEquals(createdArticle.getName(), newArticle.getName());
		assertEquals(createdArticle.getDescription(), newArticle.getDescription());
		assertEquals(createdArticle.getPrice(), newArticle.getPrice());

		template.delete(newArticleURI);
	}

	@Test
	@ExpectedExceptionStatus(HttpStatus.NOT_FOUND)
	public void testDelete() {
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		URI newArticleURI = template.postForLocation(rootLinks.get("tos:articles").getHref(), newArticle);
		assertNotNull(newArticleURI);
		Article createdArticle = template.getForEntity(newArticleURI, Article.class).getBody();
		assertNotNull(createdArticle);
		template.delete(newArticleURI);
		template.getForEntity(newArticleURI, null);
	}

	@Test
	public void testPaging() {
		Collection<URI> created = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			Article newArticle = new Article("Created" + i, "created..." + i, new BigDecimal(i));
			URI newArticleURI = template.postForLocation(rootLinks.get("tos:articles").getHref(), newArticle);
			assertNotNull(newArticleURI);
			created.add(newArticleURI);
		}
		PagedResources<Article> firstPage = template.exchange(rootLinks.get("tos:articles").getHref(), HttpMethod.GET,
				null, new ParameterizedTypeReference<PagedResources<Article>>() {
				}).getBody();
		assertNotNull(firstPage.getLink(Link.REL_NEXT));
		assertNotNull(firstPage.getLink(Link.REL_SELF));

		PagedResources<Article> secondPage = template.exchange(firstPage.getLink(Link.REL_NEXT).getHref(),
				HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<Article>>() {
				}).getBody();
		assertNotNull(secondPage.getLink(Link.REL_NEXT));
		assertNotNull(secondPage.getLink(Link.REL_PREVIOUS));
		assertNotNull(secondPage.getLink(Link.REL_SELF));
		assertEquals(10, secondPage.getContent().size());

		Link next = secondPage.getLink(Link.REL_NEXT);
		PagedResources<Article> nextResponse = null;
		while (next != null) {
			nextResponse = template.exchange(next.getHref(), HttpMethod.GET, null,
					new ParameterizedTypeReference<PagedResources<Article>>() {
					}).getBody();
			next = nextResponse.getLink(Link.REL_NEXT);
		}

		List<Article> lastPage = new ArrayList<>(nextResponse.getContent());
		assertEquals("Created99", lastPage.get(9).getName());

		for (URI uri : created) {
			template.delete(uri);
		}
	}

	@Test
	public void testUpdateArticle() {
		// create the article
		Article newArticle = new Article("Created", "created...", new BigDecimal(13));
		URI newArticleURI = template.postForLocation(rootLinks.get("tos:articles").getHref(), newArticle);
		assertNotNull(newArticleURI);
		Article createdArticle = template.getForEntity(newArticleURI, Article.class).getBody();
		assertNotNull(createdArticle);

		// change the values
		newArticle.setName("ChangedName");
		newArticle.setPrice(new BigDecimal(1337));
		newArticle.setDescription("ChangedDescription");

		// update the article
		template.put(newArticleURI, newArticle);

		// get the article again
		Article updatedArticle = template.getForEntity(newArticleURI, Article.class).getBody();
		assertNotNull(updatedArticle);
		assertEquals(newArticle.getName(), updatedArticle.getName());
		assertEquals(newArticle.getDescription(), updatedArticle.getDescription());
		assertEquals(newArticle.getPrice(), updatedArticle.getPrice());

		// delete the article
		template.delete(newArticleURI);
	}

	@Test
	public void testAddToBasket() {
		// create the article
		Article newArticle = new Article("Article for basket", "f00", new BigDecimal(13));
		URI articleURI = template.postForLocation(rootLinks.get("tos:articles").getHref(), newArticle);
		assertNotNull(articleURI);

		// create a basket
		template.postForLocation(rootLinks.get("tos:baskets").getHref(), new Basket());

		// read the created article
		Article createdArticle = template.getForEntity(articleURI, Article.class).getBody();
		assertNotNull(createdArticle);

		// find the link to add it to the basket
		Link addToBasketURI = createdArticle.getLink("tos:addToBasket");
		assertNotNull(addToBasketURI);
		URI basketURI = template.postForLocation(addToBasketURI.getHref(), null);

		// check the basket
		Basket basket = template.getForEntity(basketURI, Basket.class).getBody();
		assertNotNull(basket.getArticles());
		assertFalse(basket.getArticles().isEmpty());
		assertEquals("Article for basket", basket.getArticles().iterator().next().getArticle().getName());
		assertEquals(new Long(1L), basket.getArticles().iterator().next().getAmount());

		// delete everything
		template.delete(basketURI);
		template.delete(articleURI);
	}
}
