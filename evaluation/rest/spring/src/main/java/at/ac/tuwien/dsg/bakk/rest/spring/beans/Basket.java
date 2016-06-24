package at.ac.tuwien.dsg.bakk.rest.spring.beans;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

/**
 * Bean representing a single basket of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlRootElement
public class Basket extends ResourceSupport {
	private Collection<BasketEntry> articles;
	private String name;

	public Basket(Collection<BasketEntry> articles, String name) {
		super();
		this.articles = articles;
		this.setName(name);
	}

	public Basket(String name) {
		this(null, name);
	}

	public Basket() {
		this(null);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
