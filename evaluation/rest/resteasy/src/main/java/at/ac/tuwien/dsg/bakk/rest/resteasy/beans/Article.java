package at.ac.tuwien.dsg.bakk.rest.resteasy.beans;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.jboss.resteasy.links.RESTServiceDiscovery;
import org.jboss.resteasy.links.ResourceID;

/**
 * Bean representing a single article of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Mapped(namespaceMap = @XmlNsMap(jsonName = "atom", namespace = "http://www.w3.org/2005/Atom") )
public class Article {
	@XmlTransient
	@ResourceID
	private Long id;
	@XmlElement
	private String name;
	@XmlElement
	private String description;
	@XmlElement
	private BigDecimal price;

	@XmlElement
	private RESTServiceDiscovery links;

	public Article(String name, String description, BigDecimal price) {
		super();
		this.name = name;
		this.description = description;
		this.price = price;
	}

	public Article() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
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

	public void setLinks(RESTServiceDiscovery links) {
		this.links = links;
	}
}
