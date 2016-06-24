package at.ac.tuwien.dsg.bakk.rest.jersey;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.ac.tuwien.dsg.bakk.rest.jersey.beans.Bill;
import at.ac.tuwien.dsg.bakk.rest.jersey.beans.BillPage;
import at.ac.tuwien.dsg.bakk.service.BillService;
import model.BillEntity;

@Path("/renamedBills")
// XXX JSON does not work with the injected links because of the following bug:
// https://java.net/jira/browse/JERSEY-2618
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class BillResource {

	private BillService billService = new BillService();

	@GET
	public BillPage getBills(@QueryParam("offset") @DefaultValue("0") int offset,
			@QueryParam("limit") @DefaultValue("10") int limit) {
		return new BillPage(billService, offset, limit);
	}

	@GET
	@Path("/{id}")
	public Bill getBill(@PathParam("id") Long id) {
		BillEntity bill = billService.getById(id);
		if (bill == null) {
			throw new NotFoundException();
		}
		Long basketId = bill.getBasket() != null ? bill.getBasket().getId() : null;
		return new Bill(bill.getId(), basketId);
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