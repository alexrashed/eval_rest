package at.ac.tuwien.dsg.bakk.rest.jaxrs.beans;

import java.util.Collection;

import javax.ws.rs.core.Link;

/**
 * Bean representing a single bill of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class ClientBill extends ClientBaseBean {

	public ClientBill(Collection<Link> links) {
		super(links);
	}

	public ClientBill() {
		super();
	}
}
