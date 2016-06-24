package at.ac.tuwien.dsg.bakk.rest.spring.assembler;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

/**
 * Converter support which extends the {@link ResourceAssemblerSupport} and adds
 * the other way around (create DB entities from the resource).
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 *
 * @param <E>
 *            database entity
 * @param <R>
 *            resource bean
 */
public abstract class ResourceConverterSupport<E, R extends ResourceSupport> extends ResourceAssemblerSupport<E, R> {

	/**
	 * Creates a new {@link ResourceConverterSupport} using the given controller
	 * class and resource type.
	 * 
	 * @param controllerClass
	 *            must not be {@literal null}.
	 * @param resourceType
	 *            must not be {@literal null}.
	 */
	public ResourceConverterSupport(Class<?> controllerClass, Class<R> resourceType) {
		super(controllerClass, resourceType);
	}

	/**
	 * Converts a received resource object into the corresponding database
	 * entity.
	 * 
	 * @param resource
	 *            to create a database entity object of
	 * @param id
	 *            may be null when a new element should be stored in the db
	 *            rather than update an existing one
	 * @return created database entity object
	 */
	public abstract E fromResource(R resource, Long id);

}
