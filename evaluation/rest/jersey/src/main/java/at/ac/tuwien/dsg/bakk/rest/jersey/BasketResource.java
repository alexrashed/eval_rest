package at.ac.tuwien.dsg.bakk.rest.jersey;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import at.ac.tuwien.dsg.bakk.rest.jersey.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.BasketEntry;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.BasketPage;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.Bill;
import at.ac.tuwien.dsg.bakk.service.BasketService;
import at.ac.tuwien.dsg.bakk.service.BillService;
import model.ArticleEntity;
import model.BasketEntity;
import model.BillEntity;

@Path("/renamedBaskets")
// XXX JSON does not work with the injected links because of the following bug:
// https://java.net/jira/browse/JERSEY-2618
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class BasketResource {

	private BasketService basketService = new BasketService();
	private BillService billService = new BillService();

	@GET
	public BasketPage getBaskets(@QueryParam("offset") @DefaultValue("0") int offset,
			@QueryParam("limit") @DefaultValue("10") int limit) {
		return new BasketPage(basketService, offset, limit);
	}

	@POST
	public Response createBasket() {
		BasketEntity entity = new BasketEntity();
		BasketEntity created = basketService.createOrUpdate(entity);
		URI link = UriBuilder.fromResource(BasketResource.class).path(BasketResource.class, "getBasket")
				.build(created.getId());
		return Response.created(link).build();
	}

	@GET
	@Path("/{id}")
	public Basket getBasket(@PathParam("id") Long id) {
		BasketEntity basket = basketService.getById(id);
		if (basket == null) {
			throw new NotFoundException();
		}
		List<BasketEntry> basketEntries = null;
		if (basket.getArticlesToAmount() != null) {
			basketEntries = new ArrayList<>();
			for (Entry<ArticleEntity, Long> entry : basket.getArticlesToAmount().entrySet()) {
				ArticleEntity articleEntity = entry.getKey();
				Article article = new Article(articleEntity.getId(), articleEntity.getName(),
						articleEntity.getDescription(), articleEntity.getPrice());
				basketEntries.add(new BasketEntry(article, entry.getValue()));
			}
		}
		BillEntity billEntity = basket.getBill();
		Bill bill = null;
		if (billEntity != null) {
			Long basketId = billEntity.getBasket() != null ? billEntity.getBasket().getId() : null;
			bill = new Bill(billEntity.getId(), basketId);
		}

		return new Basket(basket.getId(), basketEntries, bill);
	}

	@POST
	@Path("/{id}")
	public Response payBasket(@PathParam("id") Long id) {
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