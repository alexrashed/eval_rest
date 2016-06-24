package at.ac.tuwien.dsg.bakk.rest.resteasy;

import static at.ac.tuwien.dsg.bakk.rest.base.ResourceUtils.createBean;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinkResources;
import org.jboss.resteasy.links.ParamBinding;

import at.ac.tuwien.dsg.bakk.rest.base.PagingParameters;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.BasketEntry;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.BasketPage;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Bill;
import at.ac.tuwien.dsg.bakk.service.BasketService;
import at.ac.tuwien.dsg.bakk.service.BillService;
import model.ArticleEntity;
import model.BasketEntity;
import model.BillEntity;

@Path("/baskets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RenamedBasketResource {

	private BasketService basketService = new BasketService();
	private BillService billService = new BillService();

	@GET
	@AddLinks
	@LinkResources({ @LinkResource(value = Basket.class, rel = "list"),
			@LinkResource(value = BasketPage.class, rel = "prev", constraint = "${this.offset - this.limit >= 0}", queryParameters = {
					@ParamBinding(name = "offset", value = "${this.offset - this.limit}"),
					@ParamBinding(name = "limit", value = "${this.limit}") }),
			@LinkResource(value = BasketPage.class, rel = "next", constraint = "${this.offset + this.limit < this.modelLimit}", queryParameters = {
					@ParamBinding(name = "offset", value = "${this.offset + this.limit}"),
					@ParamBinding(name = "limit", value = "${this.limit}") }),
			@LinkResource(value = BasketPage.class, rel = "self", queryParameters = {
					@ParamBinding(name = "offset", value = "${this.offset}"),
					@ParamBinding(name = "limit", value = "${this.limit}") }) })
	public BasketPage getBaskets(@BeanParam PagingParameters paging) {
		List<Basket> result = new ArrayList<>();
		basketService.get(paging.getOffset(), paging.getLimit()).forEach(e -> result.add(createBean(e, Basket.class)));
		return new BasketPage(result, paging.getOffset(), paging.getLimit(), basketService.getLimit());
	}

	@POST
	@AddLinks
	@LinkResource(Basket.class)
	public Response createBasket() {
		BasketEntity entity = new BasketEntity();
		BasketEntity created = basketService.createOrUpdate(entity);
		URI link = UriBuilder.fromResource(RenamedBasketResource.class).path(RenamedBasketResource.class, "getBasket")
				.build(created.getId());
		return Response.created(link).build();
	}

	@GET
	@Path("/{id}")
	@AddLinks
	@LinkResources({ @LinkResource(Basket.class),
			@LinkResource(value = Bill.class, rel = "tos:basket", pathParameters = "${this.basketId}", constraint = "${this.basketId != null}") })
	public Basket getBasket(@PathParam("id") Long id) {
		BasketEntity basket = basketService.getById(id);
		if (basket == null) {
			throw new NotFoundException();
		}
		Basket bean = createBean(basket, Basket.class);
		bean.setBillId(basket.getBill() != null ? basket.getBill().getId() : null);
		if (basket.getArticlesToAmount() != null) {
			Collection<BasketEntry> articles = new ArrayList<>();
			for (Entry<ArticleEntity, Long> entry : basket.getArticlesToAmount().entrySet()) {
				articles.add(new BasketEntry(createBean(entry.getKey(), Article.class), entry.getValue()));
			}
			bean.setArticles(articles);
		}
		return bean;
	}

	@POST
	@Path("/{basketId}")
	@LinkResource(value = Basket.class, rel = "payment", constraint = "${this.billId == null}")
	public Response renamedPayBasket(@PathParam("basketId") Long id) {
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
	@LinkResource(Basket.class)
	public Response delete(@PathParam("id") Long id) {
		BasketEntity basket = basketService.getById(id);
		if (basket == null) {
			throw new NotFoundException("Basket not found!");
		}
		basketService.delete(basket);
		return Response.ok().build();
	}
}