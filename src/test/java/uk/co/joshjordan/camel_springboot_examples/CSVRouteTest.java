package uk.co.joshjordan.camel_springboot_examples;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
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
public class CSVRouteTest {
    @Autowired
    CamelContext context;

    @Autowired
    ProducerTemplate producerTemplate;

    @EndpointInject("mock:result")
    protected MockEndpoint mockEndpoint;

    @Test
    public void testMovingFileByMockingFromEndpoint() throws Exception {

        //Create the mock
        String expectedBody = "OutboundPlayer(_name=Josh, _clubAndPosition= Cardiff  GK)";
        mockEndpoint.expectedBodiesReceived(expectedBody);
        mockEndpoint.expectedMinimumMessageCount(1);

        //Tweak the route definition
        AdviceWith.adviceWith(context, "CSVFileRouteId", routeBuilder -> {
            routeBuilder.replaceFromWith("direct:mockStart"); //replaces the from with a direct endpoint that we can use later on
            routeBuilder.weaveByToUri("file:*").replace().to(mockEndpoint); //replaces the .to because file:* matches the pattern of the .to route destination
        });

        //Start the context and validate test
        context.start();
        producerTemplate.sendBody("direct:mockStart", "name, club, position, handed\n" +
                "Josh, Cardiff, GK, R"); // We use a producerTemplate to send to the directEndpoint which we created in the routeBuilder lamda
        mockEndpoint.assertIsSatisfied();

    }
}
