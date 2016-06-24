package at.ac.tuwien.dsg.bakk.rest.spring.controller;

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
import at.ac.tuwien.dsg.bakk.rest.spring.assembler.BasketConverter;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Bill;
import at.ac.tuwien.dsg.bakk.service.BasketService;
import at.ac.tuwien.dsg.bakk.service.BillService;
import exceptions.BadRequestException;
import exceptions.ForbiddenException;
import exceptions.NotFoundException;
import model.BasketEntity;
import model.BillEntity;

/**
 * Controller managing the basket resources.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@Controller
@ExposesResourceFor(Basket.class)
@RequestMapping("/baskets")
public class BasketController extends BaseController<Basket, BasketEntity> {

	@Autowired
	private EntityLinks entityLinks;
	private BillService billService = new BillService();

	public BasketController() {
		super(new BasketConverter(new ArticleConverter(new BasketService())), new BasketService());
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.POST)
	public ResponseEntity<Void> payBasket(@PathVariable Long id) {
		BasketEntity basket = service.getById(id);
		if (basket == null) {
			throw new NotFoundException("Basket not found.");
		}
		if (basket.getBill() != null) {
			throw new BadRequestException("Basket already payed!");
		}
		BillEntity bill = new BillEntity(basket);
		bill = billService.createOrUpdate(bill);
		basket.setBill(bill);
		service.createOrUpdate(basket);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(entityLinks.linkForSingleResource(Bill.class, bill.getId()).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Void> update(Long id, Basket resource) {
		throw new ForbiddenException();
	}

}
