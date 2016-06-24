package at.ac.tuwien.dsg.bakk.rest.resteasy.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.resteasy.links.RESTServiceDiscovery;

public abstract class ClientPage<T> {
	@XmlTransient
	private int offset;
	@XmlTransient
	private int limit;
	@XmlTransient
	private long modelLimit;
	@XmlElement
	private List<T> entries = new ArrayList<>();
	@XmlElement
	private RESTServiceDiscovery links;

	public ClientPage(List<T> entries, int offset, int limit, long modelLimit) {
		super();
		this.setOffset(offset);
		this.setLimit(limit);
		this.setModelLimit(modelLimit);
		this.setEntries(entries);
	}

	public ClientPage() {
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

	@XmlTransient
	public List<T> getEntries() {
		return entries;
	}

	public void setEntries(List<T> entries) {
		this.entries = entries;
	}

	@XmlTransient
	public RESTServiceDiscovery getLinks() {
		return links;
	}

	public void setLinks(RESTServiceDiscovery links) {
		this.links = links;
	}

}