package at.ac.tuwien.dsg.bakk.rest.spring.rfmm;

import org.junit.Assert;
import org.junit.Test;

import at.ac.tuwien.dsg.bakk.rest.spring.BaseIT;

/**
 * Integration test executing the tests for Level 3 of the REST Framework
 * Maturity Model.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class Level3IT extends BaseIT {

	@Test
	public void testLevel3_1_Conditions() {
		// No conditions can be assigned to the links, there have to be typical
		// if/else constructs
		Assert.fail("No conditions can be assigned to the links.");
	}

}
