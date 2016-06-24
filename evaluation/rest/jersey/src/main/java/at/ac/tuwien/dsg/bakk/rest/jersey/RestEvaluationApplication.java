package at.ac.tuwien.dsg.bakk.rest.jersey;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class RestEvaluationApplication extends ResourceConfig {
	public RestEvaluationApplication() {
		register(DeclarativeLinkingFeature.class);
		register(ArticleResource.class);
		register(RootResource.class);
		register(RenamedBasketResource.class);
		register(BillResource.class);
	}
}