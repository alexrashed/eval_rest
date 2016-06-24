package at.ac.tuwien.dsg.bakk.rest.spring.beans;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

/**
 * Bean representing the root entry point. It is implemented as a resource in
 * order to enable the automatic HAL marshalling.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlRootElement
public class ClientRoot extends ResourceSupport {

	public ClientRoot() {
		super();
	}
}
