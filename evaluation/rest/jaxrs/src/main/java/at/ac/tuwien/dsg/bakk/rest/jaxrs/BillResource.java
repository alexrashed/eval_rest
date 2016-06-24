package at.ac.tuwien.dsg.bakk.rest.jaxrs;

import static at.ac.tuwien.dsg.bakk.rest.jaxrs.utils.LinkResourceUtils.createBean;
import static at.ac.tuwien.dsg.bakk.rest.jaxrs.utils.LinkResourceUtils.createGetResponse;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import at.ac.tuwien.dsg.bakk.rest.base.PagingParameters;
import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.Bill;
import at.ac.tuwien.dsg.bakk.service.BillService;
import model.BillEntity;

@Path("/bills")
@Produces(MediaType.APPLICATION_JSON)
public class BillResource {

	private BillService billService = new BillService();

	@GET
	public Response getBills(@BeanParam PagingParameters paging) {
		Collection<Bill> result = new ArrayList<>();
		billService.get(paging.getOffset(), paging.getLimit())
				.forEach(e -> result.add(createBean(e, Bill.class, BillResource.class, "getBill")));

		Collection<Link> links = new ArrayList<>();

		// add the self link
		links.add(Link.fromResource(this.getClass()).rel("self").build());

		// add the next link
		if (paging.getOffset() + paging.getLimit() < billService.getLimit()) {
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

		GenericEntity<Collection<Bill>> entity = new GenericEntity<Collection<Bill>>(result) {
		};
		return Response.ok(entity).links(links.toArray(new Link[0])).build();
	}

	@GET
	@Path("/{id}")
	public Response getBill(@PathParam("id") Long id) {
		BillEntity bill = billService.getById(id);
		if (bill == null) {
			throw new NotFoundException();
		}

		Bill result = createBean(bill, Bill.class, BillResource.class, "getBill");
		result.addLink(Link
				.fromUriBuilder(UriBuilder.fromResource(RenamedBasketResource.class).path(RenamedBasketResource.class, "getBasket"))
				.rel("tos:basket").type(MediaType.APPLICATION_JSON).build(bill.getBasket().getId()));
		return createGetResponse(result);
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) {
		BillEntity bill = billService.getById(id);
		if (bill == null) {
			throw new NotFoundException("Bill not found!");
		}
		billService.delete(bill);
		return Response.ok().build();
	}
}