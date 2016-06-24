package at.ac.tuwien.dsg.bakk.rest.jersey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;

@Path("/")
public class RootResource {

	@GET
	public Response getRoot() {
		Link[] links = { Link.fromResource(ArticleResource.class).rel("tos:articles").title("Articles").build(),
				Link.fromResource(RenamedBasketResource.class).rel("tos:baskets").title("Baskets").build(),
				Link.fromResource(BillResource.class).rel("tos:bills").title("Bills").build() };
		return Response.ok().links(links).build();
	}

}
