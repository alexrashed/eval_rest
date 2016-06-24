package at.ac.tuwien.dsg.bakk.service;

import model.WishlistEntity;

/**
 * Service managing the wishlist entities.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class WishlistService extends EntityService<WishlistEntity> {

	public WishlistService() {
		super(WishlistEntity.class);
	}

}
