package at.ac.tuwien.dsg.bakk.rest.jaxrs.beans;

import java.math.BigDecimal;
import java.util.Collection;

import javax.ws.rs.core.Link;

/**
 * Bean representing a single article of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class ClientArticle extends ClientBaseBean {
	private String name;
	private String description;
	private BigDecimal price;

	public ClientArticle(String name, String description, BigDecimal price, Collection<Link> links) {
		super(links);
		this.name = name;
		this.description = description;
		this.price = price;
	}

	public ClientArticle() {
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
}
