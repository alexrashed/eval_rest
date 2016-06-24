package at.ac.tuwien.dsg.bakk.rest.spring.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import at.ac.tuwien.dsg.bakk.rest.spring.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.spring.controller.ArticleController;
import at.ac.tuwien.dsg.bakk.service.BasketService;
import model.ArticleEntity;
import model.BasketEntity;

/**
 * Assembler converting the entity to the resource bean transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class ArticleConverter extends ResourceConverterSupport<ArticleEntity, Article> {

	private BasketService basketService;

	public ArticleConverter(BasketService basketService) {
		super(ArticleController.class, Article.class);
		this.basketService = basketService;
	}

	@Override
	public Article toResource(ArticleEntity article) {
		Article resource = createResourceWithId(article.getId(), article);
		List<BasketEntity> baskets = basketService.getAll();
		for (BasketEntity basket : baskets) {
			if (basket.getBill() == null) {
				resource.add(
						linkTo(methodOn(ArticleController.class).addArticleToBasket(article.getId(), basket.getId()))
								.withRel("addToBasket"));
			}
		}
		return resource;
	}

	@Override
	public ArticleEntity fromResource(Article resource, Long id) {
		return new ArticleEntity(id, resource.getName(), resource.getPrice());
	}

	@Override
	protected Article instantiateResource(ArticleEntity entity) {
		return new Article(entity.getName(), entity.getPrice());
	}

}