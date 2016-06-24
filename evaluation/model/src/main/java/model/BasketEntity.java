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
	private String name;

	public BasketEntity(Map<ArticleEntity, Long> articlesToAmount, BillEntity bill, String name) {
		this(null, articlesToAmount, bill, name);
	}

	public BasketEntity(Long id, Map<ArticleEntity, Long> articlesToAmount, BillEntity bill, String name) {
		super(id);
		this.articlesToAmount = articlesToAmount;
		this.bill = bill;
		this.setName(name);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((articlesToAmount == null) ? 0 : articlesToAmount.hashCode());
		result = prime * result + ((bill == null) ? 0 : bill.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
