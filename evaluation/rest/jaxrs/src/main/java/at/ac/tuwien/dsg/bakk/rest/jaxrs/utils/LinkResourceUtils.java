package at.ac.tuwien.dsg.bakk.rest.jaxrs.utils;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;

import at.ac.tuwien.dsg.bakk.rest.base.ParseException;
import at.ac.tuwien.dsg.bakk.rest.base.ResourceUtils;
import at.ac.tuwien.dsg.bakk.rest.jaxrs.beans.BaseBean;
import model.BaseEntity;

public class LinkResourceUtils {

	public static <T extends BaseBean> Response createGetResponse(T bean) {
		ResponseBuilder builder = Response.ok(bean);
		if (bean.getLinks() != null) {
			builder.links(bean.getLinks().toArray(new Link[0]));
		}
		return builder.build();
	}

	public static <T extends BaseBean, V extends BaseEntity> T createBean(V sourceObject, Class<T> targetClazz,
			Class<?> resourceClazz, String method) throws ParseException {
		T result = ResourceUtils.createBean(sourceObject, targetClazz);
		Link self = Link.fromUriBuilder(UriBuilder.fromResource(resourceClazz).path(resourceClazz, method)).rel("self")
				.type(MediaType.APPLICATION_JSON).build(sourceObject.getId());
		result.addLink(self);
		return result;
	}

}
