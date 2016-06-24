package at.ac.tuwien.dsg.bakk.rest.resteasy.beans;

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
 * Bean representing a single bill of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Mapped(namespaceMap = @XmlNsMap(jsonName = "atom", namespace = "http://www.w3.org/2005/Atom"))
public class Bill {
	@XmlTransient
	@ResourceID
	private Long id;
	@XmlTransient
	@ParentResource
	private Long basketId;
	@XmlElement
	private RESTServiceDiscovery links;

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

	public RESTServiceDiscovery getLinks() {
		return links;
	}

	public void setLinks(RESTServiceDiscovery links) {
		this.links = links;
	}

}
