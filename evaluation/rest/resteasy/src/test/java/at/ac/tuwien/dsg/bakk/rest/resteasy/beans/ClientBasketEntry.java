package at.ac.tuwien.dsg.bakk.rest.resteasy.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "basketEntry")
@XmlAccessorType(XmlAccessType.NONE)
public class ClientBasketEntry {
	@XmlElement
	private ClientArticle article;
	@XmlElement
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
