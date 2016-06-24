package at.ac.tuwien.dsg.bakk.rest.resteasy.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;
import org.jboss.resteasy.links.RESTServiceDiscovery;

/**
 * Page implementations for bills. It has to be a separate subclass due to the
 * automatic, type driven link injection (otherwise duplicated links would be
 * injected).
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlRootElement(name = "billPage")
@XmlSeeAlso({ Bill.class })
@XmlAccessorType(XmlAccessType.NONE)
@Mapped(namespaceMap = @XmlNsMap(jsonName = "atom", namespace = "http://www.w3.org/2005/Atom"))
public class BillPage {

	@XmlTransient
	private int offset;
	@XmlTransient
	private int limit;
	@XmlTransient
	private long modelLimit;
	@XmlElement
	private List<Bill> entries = new ArrayList<>();
	@XmlElement
	private RESTServiceDiscovery links;

	public BillPage(List<Bill> entries, int offset, int limit, long modelLimit) {
		super();
		this.setOffset(offset);
		this.setLimit(limit);
		this.setModelLimit(modelLimit);
		this.setEntries(entries);
	}

	public BillPage() {
		super();
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public long getModelLimit() {
		return modelLimit;
	}

	public void setModelLimit(long modelLimit) {
		this.modelLimit = modelLimit;
	}

	public List<Bill> getEntries() {
		return entries;
	}

	public void setEntries(List<Bill> entries) {
		this.entries = entries;
	}

	public RESTServiceDiscovery getLinks() {
		return links;
	}

	public void setLinks(RESTServiceDiscovery links) {
		this.links = links;
	}
}
