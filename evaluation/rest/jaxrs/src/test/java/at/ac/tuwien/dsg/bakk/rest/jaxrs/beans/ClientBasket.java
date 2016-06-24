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
public class ClientBasket extends ClientBaseBean {

	private Collection<ClientBasketEntry> articles;

	public ClientBasket(Collection<ClientBasketEntry> articles, Collection<Link> links) {
		super(links);
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
}
