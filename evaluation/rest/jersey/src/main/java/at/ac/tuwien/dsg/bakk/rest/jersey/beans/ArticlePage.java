package at.ac.tuwien.dsg.bakk.rest.jersey.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import at.ac.tuwien.dsg.bakk.rest.jersey.ArticleResource;
import at.ac.tuwien.dsg.bakk.service.ArticleService;
import model.ArticleEntity;

/**
 * Bean representing a page with a set of articles. It is using the Jersey
 * Declarative Linking feature.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "articles")
@InjectLinks({
		@InjectLink(resource = ArticleResource.class, method = "getArticles", style = Style.ABSOLUTE, bindings = {
				@Binding(name = "offset", value = "${instance.offset}"),
				@Binding(name = "limit", value = "${instance.limit}") }, rel = "self"),
		@InjectLink(resource = ArticleResource.class, style = Style.ABSOLUTE, method = "getArticles", condition = "${instance.offset + instance.limit < instance.modelLimit}", bindings = {
				@Binding(name = "offset", value = "${instance.offset + instance.limit}"),
				@Binding(name = "limit", value = "${instance.limit}") }, rel = "next"),
		@InjectLink(resource = ArticleResource.class, style = Style.ABSOLUTE, method = "getArticles", condition = "${instance.offset - instance.limit >= 0}", bindings = {
				@Binding(name = "offset", value = "${instance.offset - instance.limit}"),
				@Binding(name = "limit", value = "${instance.limit}") }, rel = "prev") })
public class ArticlePage {
	@XmlElement(name = "article")
	private List<Article> articles;

	@XmlTransient
	private final int offset;
	@XmlTransient
	private final int limit;

	@XmlTransient
	private ArticleService articlesModel;

	@InjectLinks({
			@InjectLink(resource = ArticleResource.class, method = "getArticles", style = Style.ABSOLUTE, bindings = {
					@Binding(name = "offset", value = "${instance.offset}"),
					@Binding(name = "limit", value = "${instance.limit}") }, rel = "self"),
			@InjectLink(resource = ArticleResource.class, style = Style.ABSOLUTE, method = "getArticles", condition = "${instance.offset + instance.limit < instance.modelLimit}", bindings = {
					@Binding(name = "offset", value = "${instance.offset + instance.limit}"),
					@Binding(name = "limit", value = "${instance.limit}") }, rel = "next"),
			@InjectLink(resource = ArticleResource.class, style = Style.ABSOLUTE, method = "getArticles", condition = "${instance.offset - instance.limit >= 0}", bindings = {
					@Binding(name = "offset", value = "${instance.offset - instance.limit}"),
					@Binding(name = "limit", value = "${instance.limit}") }, rel = "prev") })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	private List<Link> links;

	public ArticlePage() {
		offset = 0;
		limit = 10;
	}

	public ArticlePage(ArticleService articlesModel, int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
		this.articlesModel = articlesModel;

		setArticles(new ArrayList<>());
		List<ArticleEntity> list = articlesModel.get(offset, limit);
		for (ArticleEntity entity : list) {
			Article article = new Article(entity.getId(), entity.getName(), entity.getPrice());
			getArticles().add(article);
			// add the self link
			Link self = Link
					.fromUriBuilder(
							UriBuilder.fromResource(ArticleResource.class).path(ArticleResource.class, "getArticle"))
					.rel("self").build(article.getId());
			article.getLinks().add(self);
		}
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public int getOffset() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}

	public Long getModelLimit() {
		return articlesModel.getLimit();
	}

	public List<Link> getLinks() {
		return links;
	}

	public List<Link> getLinks(String rel) {
		List<Link> foundLinks = new ArrayList<>();
		for (Link link : links) {
			if (link.getRel().equals(rel)) {
				foundLinks.add(link);
			}
		}
		return foundLinks;
	}

	public Link getLink(String rel) {
		List<Link> links = getLinks(rel);
		return links != null && !links.isEmpty() ? links.get(0) : null;
	}

	public boolean hasLink(String rel) {
		return getLink(rel) != null;
	}
}
