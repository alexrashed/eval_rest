package at.ac.tuwien.dsg.bakk.rest.spring.rfmm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.tuwien.dsg.bakk.rest.spring.beans.Article;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Basket;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.ClientArticle;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.ClientBasket;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.ClientBill;
import at.ac.tuwien.dsg.bakk.rest.spring.beans.Root;

/**
 * Integration test executing the tests for Level 4 of the REST Framework
 * Maturity Model.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class Level4IT {

	protected RestTemplate template;
	protected Map<String, Link> rootLinks;

	protected static final String BASE_URI = "http://localhost:8080/";
	protected static final String URI_SCHEME = "tos";
	protected static final String REL_ARTICLES = URI_SCHEME + ":" + "articles";
	protected static final String REL_BASKETS = URI_SCHEME + ":" + "baskets";
	protected static final String REL_BASKET = URI_SCHEME + ":" + "basket";
	protected static final String REL_BILLS = URI_SCHEME + ":" + "bills";
	protected static final String REL_BILL = URI_SCHEME + ":" + "bill";
	protected static final String REL_ADDTOBASKET = URI_SCHEME + ":" + "addToBasket";
	protected static final String REL_PAYMENT = "payment";

	@Before
	public void setUpTest() {
		template = new RestTemplate();
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jackson2HalModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter();
		halConverter.setObjectMapper(mapper);
		halConverter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));
		converters.add(halConverter);
		converters.add(new MappingJackson2HttpMessageConverter());
		template.setMessageConverters(converters);
		ResponseEntity<Root> rootEntity = template.exchange(BASE_URI, HttpMethod.GET, null, Root.class);
		List<Link> links = rootEntity.getBody().getLinks();
		rootLinks = new HashMap<>();
		links.forEach(e -> rootLinks.put(e.getRel(), e));

		// create some articles
		URI newArticleURI = null;
		for (int i = 0; i < 30; i++) {
			Article newArticle = new Article("Article" + i, new BigDecimal(i));
			newArticleURI = template.postForLocation(rootLinks.get(REL_ARTICLES).getHref(), newArticle);
			assertNotNull(newArticleURI);
		}

		// create a basket
		URI basketURI = template.postForLocation(rootLinks.get(REL_BASKETS).getHref(),
				new Basket("Newly introduced name"));
		assertNotNull(basketURI);
	}

	@After
	public void tearDownTest() {
		for (Link link : rootLinks.values()) {
			if (!link.getHref().startsWith(URI_SCHEME)) {
				continue;
			}
			PagedResources<? extends ResourceSupport> page = template.exchange(link.getHref(), HttpMethod.GET, null,
					new ParameterizedTypeReference<PagedResources<? extends ResourceSupport>>() {
					}).getBody();
			do {
				for (ResourceSupport entity : page.getContent()) {
					template.delete(entity.getLink(Link.REL_SELF).getHref());
				}
				page = page.getLink(Link.REL_NEXT) != null
						? template.exchange(page.getLink(Link.REL_NEXT).getHref(), HttpMethod.GET, null,
								new ParameterizedTypeReference<PagedResources<? extends ResourceSupport>>() {
								}).getBody()
						: null;
			} while (page != null);
		}
	}

	@Test
	public void testLevel4_1_TolerantToAddedProperties() {
		// get the first basket page
		PagedResources<ClientBasket> page = template.exchange(rootLinks.get(REL_BASKETS).getHref(), HttpMethod.GET,
				null, new ParameterizedTypeReference<PagedResources<ClientBasket>>() {
				}).getBody();
		// select the first basket and read the details
		ClientBasket basket = page.getContent().iterator().next();
		assertNotNull(basket);
	}

	@Test
	public void testLevel4_2_TolerantToRemovedProperties() {
		// get the first article page
		PagedResources<ClientArticle> page = template.exchange(rootLinks.get(REL_ARTICLES).getHref(), HttpMethod.GET,
				null, new ParameterizedTypeReference<PagedResources<ClientArticle>>() {
				}).getBody();
		// select the first article and read the details
		ClientArticle article = page.getContent().iterator().next();
		assertNotNull(article);
		assertNotNull(article.getName());
		assertNull(article.getDescription());
	}

	@Test
	public void testLevel4_3_StatusCodeHandling() {
		// get to the bills collection URI
		PagedResources<ClientBill> page = template.exchange(rootLinks.get(REL_BILLS).getHref(), HttpMethod.GET, null,
				new ParameterizedTypeReference<PagedResources<ClientBill>>() {
				}).getBody();

		// check if the client followed the redirect to the actual collection
		// URI
		assertNotNull(page.getLink(Link.REL_SELF));
	}

	@Test
	public void testLevel4_4_DefaultHandling() {
		template = new RestTemplate();
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new Jackson2HalModule());
		MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter();
		halConverter.setObjectMapper(mapper);
		halConverter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));
		converters.add(halConverter);
		converters.add(new MappingJackson2HttpMessageConverter());
		template.setMessageConverters(converters);

		// get the first basket page
		PagedResources<ClientBasket> basketPage = template.exchange(rootLinks.get(REL_BASKETS).getHref(),
				HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<ClientBasket>>() {
				}).getBody();
		// select the first basket and read the details
		ClientBasket basket = basketPage.getContent().iterator().next();
		assertNotNull(basket);

		// get the first article page
		PagedResources<ClientArticle> articlePage = template.exchange(rootLinks.get(REL_ARTICLES).getHref(),
				HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<ClientArticle>>() {
				}).getBody();
		// select the first article and read the details
		ClientArticle article = articlePage.getContent().iterator().next();
		assertNotNull(article);
		assertNotNull(article.getName());
		assertNull(article.getDescription());
	}

}
