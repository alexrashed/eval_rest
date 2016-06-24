package at.ac.tuwien.dsg.bakk.rest.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.http.MediaType;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configuration enabling the hypermedia support.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@Configuration
@ComponentScan
@EnableWebMvc
@EnableEntityLinks
@EnablePluginRegistries(RelProvider.class)
@EnableHypermediaSupport(type = HypermediaType.HAL)
class ApplicationConfig extends WebMvcConfigurerAdapter {

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}

	@Bean
	public CurieProvider curieProvider() {
		return new DefaultCurieProvider("tos", new UriTemplate("https://dsg.tuwien.ac.at/bakk/rest/spring/rels/{rel}"));
	}
}
