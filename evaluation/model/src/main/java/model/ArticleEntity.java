package model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Entity representing the available articles in the shop.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@Entity
public class ArticleEntity extends BaseEntity {

	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String description;
	@Column(nullable = false)
	private BigDecimal price;

	public ArticleEntity(String name, String description, BigDecimal price) {
		this(null, name, description, price);
	}

	public ArticleEntity(Long id, String name, String description, BigDecimal price) {
		super(id);
		this.name = name;
		this.description = description;
		this.price = price;
	}

	public ArticleEntity() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((price == null) ? 0 : price.hashCode());
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
		ArticleEntity other = (ArticleEntity) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (price == null) {
			if (other.price != null)
				return false;
		} else if (!price.equals(other.price))
			return false;
		return true;
	}

}
