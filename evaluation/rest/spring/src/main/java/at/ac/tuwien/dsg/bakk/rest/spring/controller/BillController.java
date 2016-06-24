package at.ac.tuwien.dsg.bakk.rest.spring.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import at.ac.tuwien.dsg.bakk.rest.spring.assembler.BillConverter;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Bill;
import at.ac.tuwien.dsg.bakk.service.BillService;
import exceptions.ForbiddenException;
import model.BillEntity;

/**
 * Controller managing the bill resources.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@Controller
@ExposesResourceFor(Bill.class)
@RequestMapping("/bills")
public class BillController extends BaseController<Bill, BillEntity> {

	public BillController() {
		super(new BillConverter(), new BillService());
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Void> redirect(@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "1") int number) {
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(linkTo(methodOn(this.getClass()).get(10, 1)).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.TEMPORARY_REDIRECT);
	}

	@Override
	@RequestMapping(path = "/listing", method = RequestMethod.GET)
	@ResponseBody
	public PagedResources<Bill> get(@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "1") int number) {
		return super.get(size, number);
	}

	@Override
	public ResponseEntity<Void> create(Bill resource) {
		throw new ForbiddenException();
	}

}
