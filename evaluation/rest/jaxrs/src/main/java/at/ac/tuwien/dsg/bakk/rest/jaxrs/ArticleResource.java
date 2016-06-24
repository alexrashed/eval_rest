package at.ac.tuwien.dsg.bakk.rest.jaxrs;

import static at.ac.tuwien.dsg.bakk.rest.jaxrs.utils.LinkResourceUtils.createBean;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import at.ac.tuwien.dsg.bakk.rest.base.PagingParameters;
import at.ac.tuwien.dsg.bakk.rest.base.ResourceUtils;
import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.Article;
import at.ac.tuwien.dsg.bakk.service.ArticleService;
import at.ac.tuwien.dsg.bakk.service.BasketService;
import model.ArticleEntity;
import model.BasketEntity;

@Path("/renamedArticles")
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {

	private ArticleService articleService = new ArticleService();
	private BasketService basketService = new BasketService();

	@GET
	public Response getArticles(@BeanParam PagingParameters paging) {
		Collection<Article> result = new ArrayList<>();
		articleService.get(paging.getOffset(), paging.getLimit())
				.forEach(e -> result.add(createBean(e, Article.class, ArticleResource.class, "getArticle")));

		Collection<Link> links = new ArrayList<>();

		// add the next link
		if (paging.getOffset() + paging.getLimit() < articleService.getLimit()) {
			PagingParameters next = new PagingParameters();
			next.setOffset(paging.getOffset() + paging.getLimit());
			next.setLimit(paging.getLimit());
			Link nextLink = Link.fromUriBuilder(UriBuilder.fromResource(this.getClass())
					.queryParam("offset", next.getOffset()).queryParam("limit", next.getLimit())).rel("next").build();
			links.add(nextLink);
		}

		// add prev link
		if (paging.getOffset() > 0) {
			PagingParameters prev = new PagingParameters();
			prev.setOffset((paging.getOffset() - paging.getLimit()) < 0 ? 0 : paging.getOffset() - paging.getLimit());
			prev.setLimit(paging.getLimit());
			Link prevLink = Link.fromUriBuilder(UriBuilder.fromResource(this.getClass())
					.queryParam("offset", prev.getOffset()).queryParam("limit", prev.getLimit())).rel("prev").build();
			links.add(prevLink);
		}

		GenericEntity<Collection<Article>> entity = new GenericEntity<Collection<Article>>(result) {
		};
		return Response.ok(entity).links(links.toArray(new Link[0])).build();
	}

	@POST
	public Response createArticle(Article article) {
		if (article == null) {
			throw new BadRequestException("New article has to contain data!");
		}
		ArticleEntity entity = ResourceUtils.createEntity(article, ArticleEntity.class);
		ArticleEntity created = articleService.createOrUpdate(entity);
		URI link = UriBuilder.fromResource(ArticleResource.class).path(ArticleResource.class, "getArticle")
				.build(created.getId());
		return Response.created(link).build();
	}

	@GET
	@Path("/{id}")
	public Response getArticle(@PathParam("id") Long id) {
		ArticleEntity article = articleService.getById(id);
		if (article == null) {
			throw new NotFoundException();
		}
		Article bean = createBean(article, Article.class, this.getClass(), "getArticle");
		List<BasketEntity> baskets = basketService.getAll();
		for (BasketEntity basket : baskets) {
			if (basket.getBill() == null) {
				Link basketLink = Link
						.fromUriBuilder(UriBuilder.fromResource(ArticleResource.class).path(ArticleResource.class,
								"addArticleToBasket"))
						.title("Add to basket " + basket.getId()).rel("tos:addToBasket")
						.build(article.getId(), basket.getId());
				bean.addLink(basketLink);
			}
		}
		// do not use createGetResponse, we do not want to add the
		// "tos:addToBasket"
		// Links in the header
		Link self = Link
				.fromUriBuilder(
						UriBuilder.fromResource(ArticleResource.class).path(ArticleResource.class, "getArticle"))
				.rel("self").type(MediaType.APPLICATION_JSON).build(article.getId());
		return Response.ok(bean).links(self).build();
	}

	@PUT
	@Path("/{id}")
	public Response updateArticle(@PathParam("id") Long id, Article article) {
		if (article == null) {
			throw new BadRequestException("Article to be created must not be null!");
		}
		ArticleEntity entity = ResourceUtils.createEntity(article, ArticleEntity.class, id);
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