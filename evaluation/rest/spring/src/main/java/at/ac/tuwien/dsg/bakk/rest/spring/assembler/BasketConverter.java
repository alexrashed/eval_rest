package at.ac.tuwien.dsg.bakk.rest.spring.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import at.ac.tuwien.dsg.bakk.rest.spring.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.BasketEntry;
import at.ac.tuwien.dsg.bakk.rest.spring.controller.BasketController;
import at.ac.tuwien.dsg.bakk.rest.spring.controller.BillController;
import model.ArticleEntity;
import model.BasketEntity;

/**
 * Assembler converting the entity to the resource bean transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class BasketConverter extends ResourceConverterSupport<BasketEntity, Basket> {

	private ResourceConverterSupport<ArticleEntity, Article> articleConverter;

	public BasketConverter(ResourceConverterSupport<ArticleEntity, Article> articleConverter) {
		super(BasketController.class, Basket.class);
		this.articleConverter = articleConverter;
	}

	@Override
	public Basket toResource(BasketEntity entity) {
		Basket basket = createResourceWithId(entity.getId(), entity);
		basket.setName(entity.getName());
		if (entity.getArticlesToAmount() != null && !entity.getArticlesToAmount().isEmpty()) {
			Collection<BasketEntry> articles = new ArrayList<>();
			for (Entry<ArticleEntity, Long> entry : entity.getArticlesToAmount().entrySet()) {
				articles.add(new BasketEntry(articleConverter.toResource(entry.getKey()), entry.getValue()));
			}
			basket.setArticles(articles);
		}
		if (entity.getBill() != null) {
			basket.add(linkTo(methodOn(BillController.class).get(entity.getBill().getId())).withRel("bill"));
		} else {
			basket.add(linkTo(methodOn(BasketController.class).payBasket(entity.getId())).withRel("payment"));
		}
		return basket;
	}

	@Override
	public BasketEntity fromResource(Basket resource, Long id) {
		// we don't convert the articles
		return new BasketEntity(id, null, null, null);
	}

}