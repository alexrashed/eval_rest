package at.ac.tuwien.dsg.bakk.rest.spring.controller;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
@RequestMapping("/renamedBills")
public class BillController extends BaseController<Bill, BillEntity> {

	public BillController() {
		super(new BillConverter(), new BillService());
	}

	@Override
	public ResponseEntity<Void> create(Bill resource) {
		throw new ForbiddenException();
	}

}
