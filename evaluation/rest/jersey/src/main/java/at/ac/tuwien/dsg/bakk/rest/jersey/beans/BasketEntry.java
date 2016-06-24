package at.ac.tuwien.dsg.bakk.rest.jersey.beans;

public class BasketEntry {

	private Article article;
	private Long amount;

	public BasketEntry(Article article, Long amount) {
		super();
		this.setArticle(article);
		this.setAmount(amount);
	}

	public BasketEntry() {
		super();
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

}
