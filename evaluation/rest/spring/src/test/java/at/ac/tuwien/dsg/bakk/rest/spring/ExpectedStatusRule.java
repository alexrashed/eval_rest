package at.ac.tuwien.dsg.bakk.rest.spring;

import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.web.client.HttpClientErrorException;

/**
 * JUnit rule which allows to define a specific HTTP status for Spring HATEOAS
 * JUnit tests using the {@link ExpectedExceptionStatus} annotation.
 * 
 * @author Alexander Rashed, 1325897, alexander.rashed@tuwien.ac.at
 */
public class ExpectedStatusRule implements TestRule {
	@Override
	public Statement apply(Statement base, Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				ExpectedExceptionStatus expectedStatus = description.getAnnotation(ExpectedExceptionStatus.class);
				HttpClientErrorException exception = null;
				try {
					base.evaluate();
				} catch (HttpClientErrorException e) {
					exception = e;
				}
				if (expectedStatus != null && expectedStatus.value() != null) {
					Assert.assertNotNull(exception);
					Assert.assertEquals(expectedStatus.value(), exception.getStatusCode());
				} else if (exception != null) {
					throw exception;
				}
			}
		};
	}
}
