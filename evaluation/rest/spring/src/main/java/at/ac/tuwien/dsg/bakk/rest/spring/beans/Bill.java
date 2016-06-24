package at.ac.tuwien.dsg.bakk.rest.spring.beans;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.hateoas.ResourceSupport;

/**
 * Bean representing a single bill of the shop used to be transferred by the
 * service.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlRootElement
public class Bill extends ResourceSupport {

	public Bill() {
		super();
	}

}
