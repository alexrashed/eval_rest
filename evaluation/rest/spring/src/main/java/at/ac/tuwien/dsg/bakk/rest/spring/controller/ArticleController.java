package at.ac.tuwien.dsg.bakk.rest.spring.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import at.ac.tuwien.dsg.bakk.rest.spring.assembler.ArticleConverter;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Basket;
import at.ac.tuwien.dsg.bakk.service.ArticleService;
import at.ac.tuwien.dsg.bakk.service.BasketService;
import exceptions.NotFoundException;
import model.ArticleEntity;
import model.BasketEntity;

/**
 * Controller managing the article resources.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@Controller
@ExposesResourceFor(Article.class)
@RequestMapping("/renamedArticles")
public class ArticleController extends BaseController<Article, ArticleEntity> {

	@Autowired
	private EntityLinks entityLinks;
	private BasketService basketService = new BasketService();

	public ArticleController() {
		super(new ArticleConverter(new BasketService()), new ArticleService());
	}

	@RequestMapping(value = "/{articleId}/basket/{basketId}", method = RequestMethod.POST)
	public ResponseEntity<Void> addArticleToBasket(@PathVariable Long articleId, @PathVariable Long basketId) {
		BasketEntity basket = basketService.getById(basketId);
		if (basket == null) {
			throw new NotFoundException("Basket not found!");
		}
		ArticleEntity article = service.getById(articleId);
		if (article == null) {
			throw new NotFoundException("Article not found!");
		}

		Map<ArticleEntity, Long> articlesToAmount = basket.getArticlesToAmount();
		if (articlesToAmount == null) {
			articlesToAmount = new HashMap<>();
			basket.setArticlesToAmount(articlesToAmount);
		}
		Long amount = 0L;
		if (articlesToAmount.containsKey(article)) {
			amount += articlesToAmount.get(article);
		} else {
		}
		amount++;
		articlesToAmount.put(article, amount);
		basketService.createOrUpdate(basket);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(entityLinks.linkForSingleResource(Basket.class, basket.getId()).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

}
