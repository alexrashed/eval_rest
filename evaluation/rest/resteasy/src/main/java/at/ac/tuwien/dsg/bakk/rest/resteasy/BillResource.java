package at.ac.tuwien.dsg.bakk.rest.resteasy;

import static at.ac.tuwien.dsg.bakk.rest.base.ResourceUtils.createBean;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinkResources;
import org.jboss.resteasy.links.ParamBinding;

import at.ac.tuwien.dsg.bakk.rest.base.PagingParameters;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.Bill;
import at.ac.tuwien.dsg.bakk.rest.resteasy.beans.BillPage;
import at.ac.tuwien.dsg.bakk.service.BillService;
import model.BillEntity;

@Path("/renamedBills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BillResource {

	private BillService billService = new BillService();

	@GET
	@AddLinks
	@LinkResources({ @LinkResource(value = Bill.class, rel = "list"),
			@LinkResource(value = BillPage.class, rel = "prev", constraint = "${this.offset - this.limit >= 0}", queryParameters = {
					@ParamBinding(name = "offset", value = "${this.offset - this.limit}"),
					@ParamBinding(name = "limit", value = "${this.limit}") }),
			@LinkResource(value = BillPage.class, rel = "next", constraint = "${this.offset + this.limit < this.modelLimit}", queryParameters = {
					@ParamBinding(name = "offset", value = "${this.offset + this.limit}"),
					@ParamBinding(name = "limit", value = "${this.limit}") }),
			@LinkResource(value = BillPage.class, rel = "self", queryParameters = {
					@ParamBinding(name = "offset", value = "${this.offset}"),
					@ParamBinding(name = "limit", value = "${this.limit}") }) })
	public BillPage getBaskets(@BeanParam PagingParameters paging) {
		List<Bill> result = new ArrayList<>();
		billService.get(paging.getOffset(), paging.getLimit()).forEach(e -> result.add(createBean(e, Bill.class)));
		return new BillPage(result, paging.getOffset(), paging.getLimit(), billService.getLimit());
	}

	@GET
	@AddLinks
	@Path("/{id}")
	@LinkResources({ @LinkResource(Bill.class),
			@LinkResource(value = Basket.class, rel = "tos:bill", pathParameters = "${this.billId}", constraint = "${this.billId != null}") })
	public Bill getBill(@PathParam("id") Long id) {
		BillEntity bill = billService.getById(id);
		if (bill == null) {
			throw new NotFoundException();
		}

		Bill result = createBean(bill, Bill.class);
		result.setBasketId(bill.getBasket() != null ? bill.getBasket().getId() : null);

		return result;
	}

	@DELETE
	@AddLinks
	@Path("/{id}")
	@LinkResource(Bill.class)
	public Response delete(@PathParam("id") Long id) {
		BillEntity bill = billService.getById(id);
		if (bill == null) {
			throw new NotFoundException("Bill not found!");
		}
		billService.delete(bill);
		return Response.ok().build();
	}
}