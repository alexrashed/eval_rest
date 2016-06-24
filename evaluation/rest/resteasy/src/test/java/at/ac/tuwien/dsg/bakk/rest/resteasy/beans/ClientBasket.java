package at.ac.tuwien.dsg.bakk.rest.resteasy.beans;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.jboss.resteasy.links.ParentResource;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.ResourceID;

/**
 * Bean representing a single basket of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlRootElement(name = "basket")
@XmlAccessorType(XmlAccessType.NONE)
@Mapped(namespaceMap = @XmlNsMap(jsonName = "atom", namespace = "http://www.w3.org/2005/Atom"))
public class ClientBasket {
	@XmlTransient
	@ResourceID
	private Long id;
	@XmlTransient
	@ParentResource
	private Long billId;
	@XmlElement
	private Collection<ClientBasketEntry> articles;
	@XmlElement
	private RESTServiceDiscovery links;

	public ClientBasket(Collection<ClientBasketEntry> articles) {
		super();
		this.articles = articles;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RESTServiceDiscovery getLinks() {
		return links;
	}

	public Long getBillId() {
		return billId;
	}

	public void setBillId(Long billId) {
		this.billId = billId;
	}
}
