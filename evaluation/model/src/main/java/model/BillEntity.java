package model;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Entity representing a bill of a specific user in the shop.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@Entity
public class BillEntity extends BaseEntity {

	@OneToOne
	private BasketEntity basket;

	public BillEntity() {
		super();
	}

	public BillEntity(Long id, BasketEntity basket) {
		super(id);
		this.setBasket(basket);
	}

	public BillEntity(BasketEntity basket) {
		this(null, basket);
	}

	public BasketEntity getBasket() {
		return basket;
	}

	public void setBasket(BasketEntity basket) {
		this.basket = basket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((basket == null) ? 0 : basket.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BillEntity other = (BillEntity) obj;
		if (basket == null) {
			if (other.basket != null)
				return false;
		} else if (!basket.equals(other.basket))
			return false;
		return true;
	}
}
