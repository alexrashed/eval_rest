package at.ac.tuwien.dsg.bakk.rest.spring.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import at.ac.tuwien.dsg.bakk.rest.spring.assembler.ResourceConverterSupport;
import at.ac.tuwien.dsg.bakk.service.EntityService;
import exceptions.BadRequestException;
import exceptions.NotFoundException;
import model.BaseEntity;

/**
 * Base controller implementation managing the CRUD operations
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public abstract class BaseController<R extends ResourceSupport, E extends BaseEntity> {

	protected ResourceConverterSupport<E, R> converter;
	protected EntityService<E> service;

	public BaseController(ResourceConverterSupport<E, R> converter, EntityService<E> service) {
		super();
		this.converter = converter;
		this.service = service;
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public PagedResources<R> get(@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "1") int number) {
		if (number < 1 || size < 0) {
			throw new BadRequestException("Invalid paging parameters.");
		}
		Iterable<E> entities = service.get((number - 1) * size, size);
		List<R> resources = converter.toResources(entities);
		long totalElements = service.getLimit();
		long totalPages = totalElements / size;
		PageMetadata pageMetadata = new PageMetadata(size, number, totalElements, totalPages);
		PagedResources<R> pagedResources = new PagedResources<>(resources, pageMetadata);

		pagedResources.add(linkTo(methodOn(this.getClass()).get(size, number)).withSelfRel());
		if (size * number < totalElements) {
			pagedResources.add(linkTo(methodOn(this.getClass()).get(size, number + 1)).withRel(Link.REL_NEXT));
		}
		if (number > 1) {
			pagedResources.add(linkTo(methodOn(this.getClass()).get(size, number - 1)).withRel(Link.REL_PREVIOUS));
		}

		return pagedResources;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public R get(@PathVariable Long id) {
		E entity = service.getById(id);
		if (entity == null) {
			throw new NotFoundException("Article not found!");
		}

		R resource = converter.toResource(entity);
		return resource;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Void> create(@RequestBody R resource) {
		if (resource == null) {
			throw new BadRequestException("Entity to create must not be null!");
		}
		E entity = converter.fromResource(resource, null);
		entity = service.createOrUpdate(entity);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(linkTo(methodOn(this.getClass()).get(entity.getId())).toUri());
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody R resource) {
		if (resource == null) {
			throw new BadRequestException("Entity to be created must not be null!");
		}
		E entity = converter.fromResource(resource, id);
		service.createOrUpdate(entity);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		E entity = service.getById(id);
		if (entity == null) {
			throw new NotFoundException("Entity not found!");
		}
		service.delete(entity);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
