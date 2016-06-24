package at.ac.tuwien.dsg.bakk.rest.jersey.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import at.ac.tuwien.dsg.bakk.rest.jersey.BillResource;
import at.ac.tuwien.dsg.bakk.rest.jersey.BasketResource;

/**
 * Bean representing a single bill of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "bill")
@InjectLink(resource = BillResource.class, method = "getBill", style = Style.ABSOLUTE, bindings = @Binding(name = "id", value = "${instance.id}"), rel = "self")
public class Bill {

	@XmlTransient
	private Long id;
	@XmlTransient
	private Long basketId;

	@InjectLinks({
			@InjectLink(resource = BillResource.class, method = "getBill", style = Style.ABSOLUTE, bindings = @Binding(name = "id", value = "${instance.id}"), rel = "self"),
			@InjectLink(resource = BasketResource.class, method = "getBasket", style = Style.ABSOLUTE, bindings = @Binding(name = "id", value = "${instance.basketId}"), rel = "tos:basket", condition = "${instance.basketId != null}") })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	private List<Link> links;

	public Bill(Long id, Long basketId) {
		super();
		this.setBasketId(basketId);
		this.setId(id);
		this.links = new ArrayList<>();
	}

	public Bill() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getBasketId() {
		return basketId;
	}

	public void setBasketId(Long basketId) {
		this.basketId = basketId;
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
