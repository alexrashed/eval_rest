package at.ac.tuwien.dsg.bakk.rest.spring.beans;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

/**
 * Bean representing a single article of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlRootElement
public class Article extends ResourceSupport {
	private String name;
	private BigDecimal price;

	public Article(String name, BigDecimal price) {
		super();
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
