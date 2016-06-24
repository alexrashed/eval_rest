package at.ac.tuwien.dsg.bakk.rest.jersey.beans;

import java.math.BigDecimal;
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

import at.ac.tuwien.dsg.bakk.rest.jersey.ArticleResource;

/**
 * Bean representing a single article. It is using the Jersey Declarative
 * Linking feature.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "article")
@InjectLink(resource = ArticleResource.class, method = "getArticle", style = Style.ABSOLUTE, bindings = @Binding(name = "id", value = "${instance.id}") , rel = "self")
public class Article {

	@XmlTransient
	private Long id;
	@XmlElement
	private String name;
	@XmlElement
	private String description;
	@XmlElement
	private BigDecimal price;
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	private List<Link> links;

	public Article() {
		this(null, null, null);
	}

	public Article(Long id, String name, String description, BigDecimal price) {
		super();
		this.setId(id);
		this.name = name;
		this.description = description;
		this.price = price;
		this.links = new ArrayList<>();
	}

	public Article(String name, String description, BigDecimal price) {
		this(null, name, description, price);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
