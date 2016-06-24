package at.ac.tuwien.dsg.bakk.rest.jaxrs.beans;

import java.util.Collection;

import javax.ws.rs.core.Link;

/**
 * Bean representing a single bill of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class Bill extends BaseBean {

	public Bill(Collection<Link> links) {
		super(links);
	}

	public Bill() {
		super();
	}
}
