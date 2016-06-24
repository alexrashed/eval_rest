package at.ac.tuwien.dsg.bakk.rest.jersey.beans;

import java.util.ArrayList;
import java.util.Collection;
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

import at.ac.tuwien.dsg.bakk.rest.jersey.BasketResource;
import at.ac.tuwien.dsg.bakk.rest.jersey.BillResource;

/**
 * Bean representing a single basket of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "basket")
@InjectLink(resource = BasketResource.class, method = "getBasket", style = Style.ABSOLUTE, bindings = @Binding(name = "id", value = "${instance.id}"), rel = "self")
public class Basket {

	@XmlTransient
	private Long id;

	@XmlElement
	private Bill bill;

	@XmlElement(name = "articles")
	private Collection<BasketEntry> articles;

	@InjectLinks({
			@InjectLink(resource = BasketResource.class, method = "getBasket", style = Style.ABSOLUTE, bindings = @Binding(name = "id", value = "${instance.id}"), rel = "self"),
			@InjectLink(resource = BasketResource.class, method = "payBasket", style = Style.ABSOLUTE, bindings = @Binding(name = "id", value = "${instance.id}"), rel = "payment", title = "Pay the basket", condition = "${instance.bill == null}"),
			@InjectLink(resource = BillResource.class, method = "getBill", style = Style.ABSOLUTE, bindings = @Binding(name = "id", value = "${instance.bill.id}"), rel = "tos:bill", title = "Bill", condition = "${instance.bill != null}") })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	private List<Link> links;

	public Basket(Long id, Collection<BasketEntry> articles, Bill bill) {
		super();
		this.setId(id);
		this.articles = articles;
		this.setBill(bill);
		this.links = new ArrayList<>();
	}

	public Basket(Collection<BasketEntry> articles, Bill bill) {
		this(null, articles, bill);
	}

	public Basket(Collection<BasketEntry> articles) {
		this(null, articles, null);
	}

	public Basket() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Collection<BasketEntry> getArticles() {
		if (articles == null) {
			articles = new ArrayList<>();
		}
		return articles;
	}

	public void setArticles(Collection<BasketEntry> articles) {
		this.articles = articles;
	}

	public Bill getBill() {
		return bill;
	}

	public void setBill(Bill bill) {
		this.bill = bill;
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
