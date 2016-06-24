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
public class ClientBasket extends ResourceSupport {
	private Collection<ClientBasketEntry> articles;

	public ClientBasket(Collection<ClientBasketEntry> articles) {
		super();
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
