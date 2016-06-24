package at.ac.tuwien.dsg.bakk.rest.resteasy.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "basketEntry")
@XmlAccessorType(XmlAccessType.NONE)
public class BasketEntry {
	@XmlElement
	private Article article;
	@XmlElement
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
