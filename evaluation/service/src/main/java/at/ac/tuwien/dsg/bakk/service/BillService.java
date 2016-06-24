package at.ac.tuwien.dsg.bakk.service;

import model.BillEntity;

/**
 * Service managing the billing entities.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class BillService extends EntityService<BillEntity> {

	public BillService() {
		super(BillEntity.class);
	}

}
