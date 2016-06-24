package at.ac.tuwien.dsg.bakk.rest.spring;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.ac.tuwien.dsg.bakk.rest.spring.beans.Root;

/**
 * Base implementation for Spring HATEOAS integration tests. It configured the
 * RestTemplate and fetches the initial links.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class BaseIT {

	protected static RestTemplate template;
	protected static Map<String, Link> rootLinks;

	protected static final String BASE_URI = "http://localhost:8080/";
	protected static final String URI_SCHEME = "tos";
	protected static final String REL_ARTICLES = URI_SCHEME + ":" + "articles";
	protected static final String REL_BASKETS = URI_SCHEME + ":" + "baskets";
	protected static final String REL_BASKET = URI_SCHEME + ":" + "basket";
	protected static final String REL_BILLS = URI_SCHEME + ":" + "bills";
	protected static final String REL_BILL = URI_SCHEME + ":" + "bill";
	protected static final String REL_ADDTOBASKET = URI_SCHEME + ":" + "addToBasket";
	protected static final String REL_PAYMENT = "payment";

	@Rule
	public ExpectedStatusRule statusRule = new ExpectedStatusRule();

	@BeforeClass
	public static void setUpClass() {
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
	}
}
