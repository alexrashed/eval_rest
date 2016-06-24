package at.ac.tuwien.dsg.bakk.rest.jersey.beans;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;

import at.ac.tuwien.dsg.bakk.rest.jersey.BillResource;
import at.ac.tuwien.dsg.bakk.service.BillService;
import model.BillEntity;

/**
 * Bean representing a page with a set of bills. It is using the Jersey
 * Declarative Linking feature.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "bills")
@InjectLinks({
		@InjectLink(resource = BillResource.class, method = "getBills", style = Style.ABSOLUTE, bindings = {
				@Binding(name = "offset", value = "${instance.offset}"),
				@Binding(name = "limit", value = "${instance.limit}") }, rel = "self"),
		@InjectLink(resource = BillResource.class, style = Style.ABSOLUTE, method = "getBills", condition = "${instance.offset + instance.limit < instance.modelLimit}", bindings = {
				@Binding(name = "offset", value = "${instance.offset + instance.limit}"),
				@Binding(name = "limit", value = "${instance.limit}") }, rel = "next"),
		@InjectLink(resource = BillResource.class, style = Style.ABSOLUTE, method = "getBills", condition = "${instance.offset - instance.limit >= 0}", bindings = {
				@Binding(name = "offset", value = "${instance.offset - instance.limit}"),
				@Binding(name = "limit", value = "${instance.limit}") }, rel = "prev") })
public class BillPage {
	@XmlElement(name = "bill")
	private List<Bill> bills;

	@XmlTransient
	private final int offset;
	@XmlTransient
	private final int limit;

	@XmlTransient
	private BillService billsModel;

	@InjectLinks({
			@InjectLink(resource = BillResource.class, method = "getBills", style = Style.ABSOLUTE, bindings = {
					@Binding(name = "offset", value = "${instance.offset}"),
					@Binding(name = "limit", value = "${instance.limit}") }, rel = "self"),
			@InjectLink(resource = BillResource.class, style = Style.ABSOLUTE, method = "getBills", condition = "${instance.offset + instance.limit < instance.modelLimit}", bindings = {
					@Binding(name = "offset", value = "${instance.offset + instance.limit}"),
					@Binding(name = "limit", value = "${instance.limit}") }, rel = "next"),
			@InjectLink(resource = BillResource.class, style = Style.ABSOLUTE, method = "getBills", condition = "${instance.offset - instance.limit >= 0}", bindings = {
					@Binding(name = "offset", value = "${instance.offset - instance.limit}"),
					@Binding(name = "limit", value = "${instance.limit}") }, rel = "prev") })
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	private List<Link> links;

	public BillPage() {
		offset = 0;
		limit = 10;
	}

	public BillPage(BillService billsModel, int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
		this.billsModel = billsModel;

		setBills(new ArrayList<>());
		List<BillEntity> list = billsModel.get(offset, limit);
		for (BillEntity entity : list) {
			Long basketId = entity.getBasket() != null ? entity.getBasket().getId() : null;
			Bill bill = new Bill(entity.getId(), basketId);
			getBills().add(bill);
			// add the self link
			Link self = Link
					.fromUriBuilder(UriBuilder.fromResource(BillResource.class).path(BillResource.class, "getBill"))
					.rel("self").build(bill.getId());
			bill.getLinks().add(self);
		}
	}

	public List<Bill> getBills() {
		return bills;
	}

	public void setBills(List<Bill> bills) {
		this.bills = bills;
	}

	public int getOffset() {
		return offset;
	}

	public int getLimit() {
		return limit;
	}

	public Long getModelLimit() {
		return billsModel.getLimit();
	}

	public List<Link> getLinks() {
		return links;
	}

	public List<Link> getLinks(String rel) {
		List<Link> foundLinks = new ArrayList<>();
		for (Link link : links) {
			if (link.getRel().equals(rel)) {
				foundLinks.add(link);
			}
		}
		return foundLinks;
	}

	public Link getLink(String rel) {
		List<Link> links = getLinks(rel);
		return links != null && !links.isEmpty() ? links.get(0) : null;
	}

	public boolean hasLink(String rel) {
		return getLink(rel) != null;
	}
}
