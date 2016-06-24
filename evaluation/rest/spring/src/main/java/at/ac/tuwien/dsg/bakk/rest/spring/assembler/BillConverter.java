package at.ac.tuwien.dsg.bakk.rest.spring.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import at.ac.tuwien.dsg.bakk.rest.spring.beans.Bill;
import at.ac.tuwien.dsg.bakk.rest.spring.controller.RenamedBasketController;
import at.ac.tuwien.dsg.bakk.rest.spring.controller.BillController;
import model.BillEntity;

/**
 * Assembler converting the entity to the resource bean transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class BillConverter extends ResourceConverterSupport<BillEntity, Bill> {

	public BillConverter() {
		super(BillController.class, Bill.class);
	}

	@Override
	public Bill toResource(BillEntity entity) {
		return createResourceWithId(entity.getId(), entity);
	}

	@Override
	public BillEntity fromResource(Bill resource, Long id) {
		// we don't convert the basket
		return new BillEntity(id, null);
	}

	@Override
	protected Bill instantiateResource(BillEntity entity) {
		Bill bill = new Bill();

		if (entity.getBasket() != null) {
			bill.add(linkTo(methodOn(RenamedBasketController.class).get(entity.getBasket().getId())).withRel("basket"));
		}
		return bill;
	}

}