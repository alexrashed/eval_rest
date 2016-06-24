package model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Entity representing a basket of a specific user in the shop.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@Entity
public class BasketEntity extends BaseEntity {

	@ElementCollection
	private Map<ArticleEntity, Long> articlesToAmount;
	@OneToOne(mappedBy = "basket")
	private BillEntity bill;

	public BasketEntity(Map<ArticleEntity, Long> articlesToAmount, BillEntity bill) {
		this(null, articlesToAmount, bill);
	}

	public BasketEntity(Long id, Map<ArticleEntity, Long> articlesToAmount, BillEntity bill) {
		super(id);
		this.articlesToAmount = articlesToAmount;
		this.bill = bill;
	}

	public BasketEntity() {
		super();
	}

	public Map<ArticleEntity, Long> getArticlesToAmount() {
		if (articlesToAmount == null) {
			articlesToAmount = new HashMap<>();
		}
		return articlesToAmount;
	}

	public void setArticlesToAmount(Map<ArticleEntity, Long> articlesToAmount) {
		this.articlesToAmount = articlesToAmount;
	}

	public BillEntity getBill() {
		return bill;
	}

	public void setBill(BillEntity bill) {
		this.bill = bill;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((articlesToAmount == null) ? 0 : articlesToAmount.hashCode());
		result = prime * result + ((bill == null) ? 0 : bill.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasketEntity other = (BasketEntity) obj;
		if (articlesToAmount == null) {
			if (other.articlesToAmount != null)
				return false;
		} else if (!articlesToAmount.equals(other.articlesToAmount))
			return false;
		if (bill == null) {
			if (other.bill != null)
				return false;
		} else if (!bill.equals(other.bill))
			return false;
		return true;
	}
}
