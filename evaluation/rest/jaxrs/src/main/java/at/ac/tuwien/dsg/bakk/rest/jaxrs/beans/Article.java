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
public class Article extends BaseBean {
	private String name;
	private BigDecimal price;

	public Article(String name, BigDecimal price, Collection<Link> links) {
		super(links);
		this.name = name;
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
