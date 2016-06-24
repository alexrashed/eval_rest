package at.ac.tuwien.dsg.bakk.rest.jersey.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
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
import org.glassfish.jersey.linking.InjectLinks;

import at.ac.tuwien.dsg.bakk.rest.jersey.BasketResource;
import at.ac.tuwien.dsg.bakk.service.BasketService;
import model.ArticleEntity;
import model.BasketEntity;
import model.BillEntity;

/**
 * Bean representing a page with a set of baskets. It is using the Jersey
 * Declarative Linking feature.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "baskets")
@InjectLinks({
		@InjectLink(resource = BasketResource.class, method = "getBaskets", style = Style.ABSOLUTE, bindings = {
				@Binding(name = "offset", value = "${instance.offset}"),
				@Binding(name = "limit", value = "${instance.limit}") }, rel = "self"),
		@InjectLink(resource = BasketResource.class, style = Style.ABSOLUTE, method = "getBaskets", condition = "${instance.offset + instance.limit < instance.modelLimit}", bindings = {
				@Binding(name = "offset", value = "${instance.offset + instance.limit}"),
				@Binding(name = "limit", value = "${instance.limit}") }, rel = "next"),
		@InjectLink(resource = BasketResource.class, style = Style.ABSOLUTE, method = "getBaskets", condition = "${instance.offset - instance.limit >= 0}", bindings = {
				@Binding(name = "offset", value = "${instance.offset - instance.limit}"),
				@Binding(name = "limit", value = "${instance.limit}") }, rel = "prev") })
public class BasketPage {
	@XmlElement(name = "basket")
	private List<Basket> baskets;

	@XmlTransient
	private final int offset;
	@XmlTransient
	private final int limit;

	@XmlTransient
	private BasketService basketsModel;

	@InjectLinks({
			@InjectLink(resource = BasketResource.class, method = "getBaskets", style = Style.ABSOLUTE, bindings = {
					@Binding(name = "offset", value = "${instance.offset}"),
					@Binding(name = "limit", value = "${instance.limit}") }, rel = "self"),
			@InjectLink(resource = BasketResource.class, style = Style.ABSOLUTE, method = "getBaskets", condition = "${instance.offset + instance.limit < instance.modelLimit}", bindings = {
					@Binding(name = "offset", value = "${instance.offset + instance.limit}"),
					@Binding(name = "limit", value = "${instance.limit}") }, rel = "next"),
			@InjectLink(resource = BasketResource.class, style = Style.ABSOLUTE, method = "getBaskets", condition = "${instance.offset - instance.limit >= 0}", bindings = {
					@Binding(name = "offset", value = "${instance.offset - instance.limit}"),
					@Binding(name = "limit", value = "${instance.limit}") }, rel = "prev") })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	private List<Link> links;

	public BasketPage() {
		offset = 0;
		limit = 10;
	}

	public BasketPage(BasketService basketsModel, int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
		this.basketsModel = basketsModel;

		setBaskets(new ArrayList<>());
		List<BasketEntity> list = basketsModel.get(offset, limit);
		for (BasketEntity entity : list) {
			List<BasketEntry> basketEntries = null;
			if (entity.getArticlesToAmount() != null) {
				basketEntries = new ArrayList<>();
				for (Entry<ArticleEntity, Long> entry : entity.getArticlesToAmount().entrySet()) {
					ArticleEntity articleEntity = entry.getKey();
					Article article = new Article(articleEntity.getId(), articleEntity.getName(),
							articleEntity.getDescription(), articleEntity.getPrice());
					basketEntries.add(new BasketEntry(article, entry.getValue()));
				}
			}
			BillEntity billEntity = entity.getBill();
			Bill bill = null;
			if (billEntity != null) {
				Long basketId = billEntity.getBasket() != null ? billEntity.getBasket().getId() : null;
				bill = new Bill(billEntity.getId(), basketId);
			}
			Basket basket = new Basket(entity.getId(), basketEntries, bill);
			getBaskets().add(basket);
			// add the self link
			Link self = Link
					.fromUriBuilder(
							UriBuilder.fromResource(BasketResource.class).path(BasketResource.class, "getBasket"))
					.rel("self").build(basket.getId());
			basket.getLinks().add(self);
		}
	}

	public List<Basket> getBaskets() {
		return baskets;
	}

	public void setBaskets(List<Basket> baskets) {
		this.baskets = baskets;
	}

	public int getOffset() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}

	public Long getModelLimit() {
		return basketsModel.getLimit();
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
