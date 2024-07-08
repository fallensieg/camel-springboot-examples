package uk.co.joshjordan.camel_springboot_examples;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith
public class CamelSpringbootExamplesApplicationTests {

	@Autowired
	CamelContext context;

	@EndpointInject("mock:result")
	protected MockEndpoint mockEndpoint;

	@Test
	public void testBasicTimer() throws Exception{
		String expectedBody = "Hello Camel";

		mockEndpoint.expectedBodiesReceived(expectedBody); // We expect the body of the message to be the value of expectedBody
		mockEndpoint.expectedMinimumMessageCount(1); //We expect at least one message

		AdviceWith.adviceWith(context, "basicTimerRouteId", routeBuilder -> {
			//This essentailly adds the mockendpoint to the end of the basicTimer route
			routeBuilder.weaveAddLast().to(mockEndpoint);
		});

		context.start();
		mockEndpoint.assertIsSatisfied();
	}


}
