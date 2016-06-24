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

	public Basket(Collection<BasketEntry> articles) {
		super();
		this.articles = articles;
	}

	public Basket() {
		super();
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
}
