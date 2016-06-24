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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Bean representing a single basket of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "basket")
public class ClientBasket {
	@XmlElement
	private ClientBill bill;
	@XmlElement(name = "articles")
	private Collection<ClientBasketEntry> articles;
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	private List<Link> links;

	public ClientBasket(Collection<ClientBasketEntry> articles, ClientBill bill) {
		this.articles = articles;
		this.bill = bill;
	}

	public ClientBasket(Collection<ClientBasketEntry> articles) {
		this(articles, null);
	}

	public ClientBasket() {
		super();
	}

	public Collection<ClientBasketEntry> getArticles() {
		if (articles == null) {
			articles = new ArrayList<>();
		}
		return articles;
	}

	public void setArticles(Collection<ClientBasketEntry> articles) {
		this.articles = articles;
	}

	public ClientBill getBill() {
		return bill;
	}

	public void setBill(ClientBill bill) {
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
