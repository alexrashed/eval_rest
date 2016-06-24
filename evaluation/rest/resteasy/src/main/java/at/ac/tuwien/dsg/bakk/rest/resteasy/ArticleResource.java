package at.ac.tuwien.dsg.bakk.rest.resteasy;

import static at.ac.tuwien.dsg.bakk.rest.base.ResourceUtils.createBean;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinkResources;
import org.jboss.resteasy.links.ParamBinding;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.RESTServiceDiscovery.AtomLink;

import at.ac.tuwien.dsg.bakk.rest.base.PagingParameters;
import at.ac.tuwien.dsg.bakk.rest.base.ResourceUtils;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.ArticlePage;
import at.ac.tuwien.dsg.bakk.service.ArticleService;
import at.ac.tuwien.dsg.bakk.service.BasketService;
import model.ArticleEntity;
import model.BasketEntity;

@Path("/articles")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ArticleResource {

	private ArticleService articleService = new ArticleService();
	private BasketService basketService = new BasketService();

	@Context
	private UriInfo uriInfo;

	@GET
	@AddLinks
	@LinkResources({ @LinkResource(value = Article.class, rel = "list"),
			@LinkResource(value = ArticlePage.class, rel = "prev", constraint = "${this.offset - this.limit >= 0}", queryParameters = {
					@ParamBinding(name = "offset", value = "${this.offset - this.limit}"),
					@ParamBinding(name = "limit", value = "${this.limit}") }),
			@LinkResource(value = ArticlePage.class, rel = "next", constraint = "${this.offset + this.limit < this.modelLimit}", queryParameters = {
					@ParamBinding(name = "offset", value = "${this.offset + this.limit}"),
					@ParamBinding(name = "limit", value = "${this.limit}") }),
			@LinkResource(value = ArticlePage.class, rel = "self", queryParameters = {
					@ParamBinding(name = "offset", value = "${this.offset}"),
					@ParamBinding(name = "limit", value = "${this.limit}") }) })
	public ArticlePage getArticles(@BeanParam PagingParameters paging) {
		List<Article> result = new ArrayList<>();
		articleService.get(paging.getOffset(), paging.getLimit())
				.forEach(e -> result.add(createBean(e, Article.class)));
		return new ArticlePage(result, paging.getOffset(), paging.getLimit(), articleService.getLimit());
	}

	@POST
	@LinkResource(Article.class)
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
	@AddLinks
	@LinkResource(Article.class)
	@Path("/{id}")
	public Article getArticle(@PathParam("id") Long id) {
		ArticleEntity article = articleService.getById(id);
		if (article == null) {
			throw new NotFoundException();
		}
		Article bean = createBean(article, Article.class);
		RESTServiceDiscovery links = new RESTServiceDiscovery();
		bean.setLinks(links);
		List<BasketEntity> baskets = basketService.getAll();
		for (BasketEntity basket : baskets) {
			if (basket.getBill() == null) {
				AtomLink link = new AtomLink(uriInfo.getBaseUriBuilder().path(ArticleResource.class)
						.path(ArticleResource.class, "addArticleToBasket").build(article.getId(), basket.getId())
						.toString(), "tos:addToBasket");

				link.setTitle("Add to basket " + basket.getId());
				links.add(link);
			}
		}

		return bean;
	}

	@PUT
	@LinkResource(Article.class)
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
	@LinkResource(Article.class)
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