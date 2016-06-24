package at.ac.tuwien.dsg.bakk.rest.jersey;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import at.ac.tuwien.dsg.bakk.rest.jersey.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.ArticlePage;
import at.ac.tuwien.dsg.bakk.service.ArticleService;
import at.ac.tuwien.dsg.bakk.service.BasketService;
import model.ArticleEntity;
import model.BasketEntity;

@Path("/renamedArticles")
// XXX JSON does not work with the injected links because of the following bug:
// https://java.net/jira/browse/JERSEY-2618
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class ArticleResource {

	private ArticleService articleService = new ArticleService();
	private BasketService basketService = new BasketService();

	@GET
	public ArticlePage getArticles(@QueryParam("offset") @DefaultValue("0") int offset,
			@QueryParam("limit") @DefaultValue("10") int limit) {
		return new ArticlePage(articleService, offset, limit);
	}

	@POST
	public Response createArticle(Article article) {
		if (article == null) {
			throw new BadRequestException("New article has to contain data!");
		}
		ArticleEntity entity = new ArticleEntity(article.getName(), article.getDescription(), article.getPrice());
		ArticleEntity created = articleService.createOrUpdate(entity);
		URI link = UriBuilder.fromResource(ArticleResource.class).path(ArticleResource.class, "getArticle")
				.build(created.getId());
		return Response.created(link).build();
	}

	// XXX It is not possible to add a dynamic amount of links (f.e. for every
	// non-paid basket) using the Jersey Declarative Linking feature. Therefore
	// we have to do it the old fashioned way.
	@GET
	@Path("/{id}")
	public Article getArticle(@PathParam("id") Long id) {
		ArticleEntity article = articleService.getById(id);
		if (article == null) {
			throw new NotFoundException();
		}
		Article bean = new Article(article.getId(), article.getName(), article.getDescription(), article.getPrice());
		List<BasketEntity> baskets = basketService.getAll();
		for (BasketEntity basket : baskets) {
			if (basket.getBill() == null) {
				Link basketLink = Link
						.fromUriBuilder(UriBuilder.fromResource(ArticleResource.class).path(ArticleResource.class,
								"addArticleToBasket"))
						.title("Add to basket " + basket.getId()).rel("tos:addToBasket")
						.build(article.getId(), basket.getId());
				bean.getLinks().add(basketLink);
			}
		}
		// do not use createGetResponse, we do not want to add the
		// "tos:addToBasket"
		// Links in the header
		Link self = Link
				.fromUriBuilder(
						UriBuilder.fromResource(ArticleResource.class).path(ArticleResource.class, "getArticle"))
				.rel("self").type(MediaType.APPLICATION_JSON).build(article.getId());
		bean.getLinks().add(self);
		return bean;
	}

	@PUT
	@Path("/{id}")
	public Response updateArticle(@PathParam("id") Long id, Article article) {
		if (article == null) {
			throw new BadRequestException("Article to be created must not be null!");
		}
		ArticleEntity entity = new ArticleEntity(id, article.getName(), article.getDescription(), article.getPrice());
		articleService.createOrUpdate(entity);
		return Response.ok().build();
	}

	@POST
	@Path("/{articleId}/basket/{basketId}")
	public Response addArticleToBasket(@PathParam("articleId") Long articleId, @PathParam("basketId") Long basketId) {
		BasketEntity basket = basketService.getById(basketId);
		if (basket == null) {
			throw new NotFoundException("Basket not found!");
		}
		ArticleEntity article = articleService.getById(articleId);
		if (article == null) {
			throw new NotFoundException("Article not found!");
		}

		Map<ArticleEntity, Long> articlesToAmount = basket.getArticlesToAmount();
		if (articlesToAmount == null) {
			articlesToAmount = new HashMap<>();
			basket.setArticlesToAmount(articlesToAmount);
		}
		Long amount = 0L;
		if (articlesToAmount.containsKey(article)) {
			amount += articlesToAmount.get(article);
		}
		amount++;
		articlesToAmount.put(article, amount);
		basketService.createOrUpdate(basket);
		URI basketURI = UriBuilder.fromResource(BasketResource.class).path(BasketResource.class, "getBasket")
				.build(basket.getId());
		return Response.seeOther(basketURI).build();
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) {
		ArticleEntity article = articleService.getById(id);
		if (article == null) {
			throw new NotFoundException("Article not found!");
		}
		articleService.delete(article);
		return Response.ok().build();
	}
}