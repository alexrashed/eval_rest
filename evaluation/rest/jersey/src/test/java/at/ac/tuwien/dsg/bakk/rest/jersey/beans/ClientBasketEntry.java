package at.ac.tuwien.dsg.bakk.rest.jersey.beans;

public class ClientBasketEntry {

	private ClientArticle article;
	private Long amount;

	public ClientBasketEntry(ClientArticle article, Long amount) {
		super();
		this.setArticle(article);
		this.setAmount(amount);
	}

	public ClientBasketEntry() {
		super();
	}

	public ClientArticle getArticle() {
		return article;
	}

	public void setArticle(ClientArticle article) {
		this.article = article;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

}
