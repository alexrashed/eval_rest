package at.ac.tuwien.dsg.bakk.rest.jaxrs.beans;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.Link;

/**
 * Base implementation for beans used to be transferred by the service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class BaseBean {
	private Collection<Link> links;

	public BaseBean(Collection<Link> links) {
		this.setLinks(links);
	}

	public BaseBean() {

	}

	public Collection<Link> getLinks() {
		return links;
	}

	public void setLinks(Collection<Link> links) {
		this.links = links;
	}

	public void addLink(Link link) {
		if (links == null) {
			links = new ArrayList<>();
		}
		links.add(link);
	}

	public void addLinks(Collection<Link> links) {
		if (links == null) {
			links = new ArrayList<>();
		}
		links.addAll(links);
	}

	public Link getLink(String rel) {
		if (links == null || rel == null) {
			return null;
		}

		for (Link link : links) {
			if (rel.equals(link.getRel())) {
				return link;
			}
		}
		return null;
	}
}
