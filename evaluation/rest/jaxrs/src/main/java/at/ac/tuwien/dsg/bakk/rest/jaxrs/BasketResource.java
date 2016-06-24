package at.ac.tuwien.dsg.bakk.rest.jaxrs;

import static at.ac.tuwien.dsg.bakk.rest.jaxrs.utils.LinkResourceUtils.createBean;
import static at.ac.tuwien.dsg.bakk.rest.jaxrs.utils.LinkResourceUtils.createGetResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import at.ac.tuwien.dsg.bakk.rest.base.PagingParameters;
import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.BasketEntry;
import at.ac.tuwien.dsg.bakk.service.BasketService;
import at.ac.tuwien.dsg.bakk.service.BillService;
import model.ArticleEntity;
import model.BasketEntity;
import model.BillEntity;

@Path("/baskets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BasketResource {

	private BasketService basketService = new BasketService();
	private BillService billService = new BillService();

	@GET
	public Response getBaskets(@BeanParam PagingParameters paging) {
		Collection<Basket> result = new ArrayList<>();
		basketService.get(paging.getOffset(), paging.getLimit())
				.forEach(e -> result.add(createBean(e, Basket.class, BasketResource.class, "getBasket")));

		Collection<Link> links = new ArrayList<>();

		// add the self link
		links.add(Link.fromResource(this.getClass()).rel("self").build());

		// add the next link
		if (paging.getOffset() + paging.getLimit() < basketService.getLimit()) {
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

		GenericEntity<Collection<Basket>> entity = new GenericEntity<Collection<Basket>>(result) {
		};
		return Response.ok(entity).links(links.toArray(new Link[0])).build();
	}

	@POST
	public Response createBasket(String name) {
		BasketEntity entity = new BasketEntity();
		entity.setName(name);
		BasketEntity created = basketService.createOrUpdate(entity);
		URI link = UriBuilder.fromResource(BasketResource.class).path(BasketResource.class, "getBasket")
				.build(created.getId());
		return Response.created(link).build();
	}

	@GET
	@Path("/{id}")
	public Response getBasket(@PathParam("id") Long id) {
		BasketEntity basket = basketService.getById(id);
		if (basket == null) {
			throw new NotFoundException();
		}
		Basket bean = createBean(basket, Basket.class, this.getClass(), "getBasket");
		if (basket.getArticlesToAmount() != null) {
			Collection<BasketEntry> articles = new ArrayList<>();
			for (Entry<ArticleEntity, Long> entry : basket.getArticlesToAmount().entrySet()) {
				articles.add(
						new BasketEntry(createBean(entry.getKey(), Article.class, ArticleResource.class, "getArticle"),
								entry.getValue()));
			}
			bean.setArticles(articles);
		}
		if (basket.getBill() == null) {
			Link payLink = Link
					.fromUriBuilder(
							UriBuilder.fromResource(BasketResource.class).path(BasketResource.class, "payBasket"))
					.title("Pay the basket").rel("payment").build(basket.getId());
			bean.addLink(payLink);
		} else {
			Link billLink = Link
					.fromUriBuilder(UriBuilder.fromResource(BillResource.class).path(BillResource.class, "getBill"))
					.title("Bill").rel("tos:bill").build(basket.getBill().getId());
			bean.addLink(billLink);
		}
		return createGetResponse(bean);
	}

	@POST
	@Path("/{basketId}")
	public Response payBasket(@PathParam("basketId") Long id) {
		BasketEntity basket = basketService.getById(id);
		if (basket == null) {
			throw new NotFoundException();
		}
		if (basket.getBill() != null) {
			throw new BadRequestException("Basket already payed!");
		}
		BillEntity bill = new BillEntity(basket);
		bill = billService.createOrUpdate(bill);
		basket.setBill(bill);
		basketService.createOrUpdate(basket);

		URI link = UriBuilder.fromResource(BillResource.class).path(BillResource.class, "getBill").build(bill.getId());
		return Response.created(link).build();
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) {
		BasketEntity basket = basketService.getById(id);
		if (basket == null) {
			throw new NotFoundException("Basket not found!");
		}
		basketService.delete(basket);
		return Response.ok().build();
	}
}