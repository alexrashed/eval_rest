package at.ac.tuwien.dsg.bakk.rest.jaxrs.beans;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.Link;

/**
 * Bean representing a single basket of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class Basket extends BaseBean {

	private Collection<BasketEntry> articles;
	private String name;

	public Basket(Collection<BasketEntry> articles, String name, Collection<Link> links) {
		super(links);
		this.setName(name);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
