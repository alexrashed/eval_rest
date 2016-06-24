package at.ac.tuwien.dsg.bakk.rest.jersey.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Bean representing a page with a set of baskets. It is using the Jersey
 * Declarative Linking feature.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "baskets")
public class ClientBasketPage {
	@XmlElement(name = "basket")
	private List<ClientBasket> baskets;

	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	private List<Link> links;

	public List<ClientBasket> getBaskets() {
		return baskets;
	}

	public void setBaskets(List<ClientBasket> baskets) {
		this.baskets = baskets;
	}

	public List<Link> getLinks() {
		return links;
	}

	public List<Link> getLinks(String rel) {
		List<Link> foundLinks = new ArrayList<>();
		for (Link link : links) {
			if (link.getRel().equals(rel)) {
				foundLinks.add(link);
			}
		}
		return foundLinks;
	}

	public Link getLink(String rel) {
		List<Link> links = getLinks(rel);
		return links != null && !links.isEmpty() ? links.get(0) : null;
	}

	public boolean hasLink(String rel) {
		return getLink(rel) != null;
	}
}
