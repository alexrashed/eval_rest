package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * Entity representing a wishlist of a user in the shop.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@Entity
public class WishlistEntity extends BaseEntity {

	@OneToMany
	private List<ArticleEntity> articles;

	public WishlistEntity(Long id, List<ArticleEntity> articles) {
		super();
		this.articles = articles;
	}

	public WishlistEntity(List<ArticleEntity> articles) {
		this(null, articles);
	}

	public WishlistEntity() {
		super();
	}

	public List<ArticleEntity> getArticles() {
		if (articles == null) {
			articles = new ArrayList<>();
		}
		return articles;
	}

	public void setArticles(List<ArticleEntity> articles) {
		this.articles = articles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((articles == null) ? 0 : articles.hashCode());
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
		WishlistEntity other = (WishlistEntity) obj;
		if (articles == null) {
			if (other.articles != null)
				return false;
		} else if (!articles.equals(other.articles))
			return false;
		return true;
	}
}
