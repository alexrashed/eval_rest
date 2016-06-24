package at.ac.tuwien.dsg.bakk.rest.spring.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import at.ac.tuwien.dsg.bakk.rest.spring.beans.Root;

/**
 * Controller providing the entry links for a client.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
@Controller
@ExposesResourceFor(Root.class)
@RequestMapping("/")
public class RootController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Root get() {
		Root root = new Root();
		root.add(linkTo(ArticleController.class).withRel("articles"));
		root.add(linkTo(BasketController.class).withRel("baskets"));
		root.add(linkTo(BillController.class).withRel("bills"));
		return root;
	}

}